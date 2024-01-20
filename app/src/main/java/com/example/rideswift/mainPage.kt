package com.example.rideswift

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.chaos.view.PinView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class mainPage : AppCompatActivity() {

    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var auth: FirebaseAuth
    lateinit var storedVerificationId:String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var phone:String
    lateinit var email:String
    lateinit var username:String
    lateinit var profileURL:String
    private lateinit var token: SharedPreferences

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code: Int = 123
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_main)

        auth = FirebaseAuth.getInstance()

        //Google Sign In
        FirebaseApp.initializeApp(this)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient.revokeAccess()

        firebaseAuth = FirebaseAuth.getInstance()

        findViewById<ImageView>(R.id.google).setOnClickListener { view: View? ->
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            signInGoogle()
        }


        val butt = findViewById<Button>(R.id.submit)
        val pinview = findViewById<EditText>(R.id.phone)
        butt.setOnClickListener {
            phone = pinview.text.toString()
            sendOTP(phone)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, homePage::class.java))
                finish()
                Log.d("GFG" , "onVerificationCompleted Success")
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("GFG" , "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("GFG","onCodeSent: $verificationId")
                storedVerificationId = verificationId
                resendToken = token

                // Start a new activity using intent
                // also send the storedVerificationId using intent
                // we will use this id to send the otp back to firebase
                val intent = Intent(applicationContext,OTPPage::class.java)
                intent.putExtra("storedVerificationId",storedVerificationId)
                intent.putExtra("Phone Number", phone)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                email = account.email.toString()
                username = account.displayName.toString()
                profileURL = account.photoUrl.toString()
                val intent = Intent(this, homePage::class.java)
//                intent.putExtra("Phone Number",phone)
                intent.putExtra("Google","1")
                intent.putExtra("Email",email)
                intent.putExtra("Username",username)
                intent.putExtra("ProfileURL",profileURL)

                val token=getSharedPreferences("data", Context.MODE_PRIVATE)
                val editor=token.edit()
                editor.putString("data",email)
                editor.commit()
                var ref = 0
                val db = Firebase.firestore
                db.collection("MasterData").get().addOnSuccessListener {
                    for (i in it){
                        if(i.id == FirebaseAuth.getInstance().currentUser!!.uid.toString()){
                            ref = 1
                            startActivity(intent)
                            finish()
                        }
                    }
                    if(ref == 0){
                        startActivity(Intent(this,addTrip::class.java))
                        finish()
                    }
                }


            }
        }
    }


    private fun sendOTP(phone: String){
        val options = PhoneAuthOptions.newBuilder().setPhoneNumber("+91" + phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(this,)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("Arman","Auth Started")
    }

}