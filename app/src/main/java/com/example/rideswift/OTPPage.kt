package com.example.rideswift

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.chaos.view.PinView
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPPage : AppCompatActivity() {

    lateinit var auth:FirebaseAuth
    lateinit var phone:String
    private lateinit var token: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otppage)

        auth = FirebaseAuth.getInstance()
        val storedVerificationId= intent.getStringExtra("storedVerificationId")
        phone = intent.getStringExtra("Phone Number").toString()

        findViewById<Button>(R.id.verifyOTP).setOnClickListener {
            val otp:String = findViewById<PinView>(R.id.pinview).text.toString()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else{
                Toast.makeText(this,"Enter OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this , homePage::class.java)
                    saveOnDatabase()
                    val token=getSharedPreferences("data", Context.MODE_PRIVATE)
                    val editor=token.edit()
                    editor.putString("data",phone)
                    editor.commit()
                    startActivity(intent)
                    finish()
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Toast.makeText(this,"Invalid OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun saveOnDatabase(){
        val db = Firebase.firestore
        val UID = auth.currentUser?.uid.toString()
        val user = hashMapOf<String,String>(
            "Phone" to phone,
            "Email" to ""
        )
        db.collection("Users").document(UID).set(user).addOnSuccessListener {
            Log.d(TAG,"Sign in Successfull with Number: ${phone}")
        }
    }

}