package com.example.rideswift

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.chaos.view.PinView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class customDialogforPhoneVerification(contex: Context): DialogFragment() {
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    val conti:Context = contex
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.round_corner);
        val view = inflater.inflate(R.layout.phoneverifcationdialog, container, false)
        val closeButton = view.findViewById<ImageButton>(R.id.close)
        closeButton.setOnClickListener {
            Toast.makeText(view.context,"You need to Verify a Phone Number to proceed",Toast.LENGTH_SHORT).show()
        }
        view.findViewById<Button>(R.id.getOTP).setOnClickListener {
            val phone = view.findViewById<EditText>(R.id.phone).text.toString()
            sendOTP(phone,view.context)
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            // This method is called when the verification is completed
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Toast.makeText(view.context,"Phone Number verified Successfully",Toast.LENGTH_SHORT).show()
                dismiss()
            }

            // Called when verification is failed add log statement to see the exception
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(view.context,"You've entered the wrong OTP",Toast.LENGTH_SHORT).show()
                Log.d("GFG" , "onVerificationFailed  $e")
            }

            // On code is sent by the firebase this method is called
            // in here we start a new activity where user can enter the OTP
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                //Changing Dialog Content
                val text:TextView = view.findViewById(R.id.textView)
                text.visibility = View.GONE
                val ll:LinearLayout = view.findViewById(R.id.ll1)
                ll.visibility=View.GONE
                val butt:Button = view.findViewById(R.id.getOTP)
                butt.visibility = View.GONE

                val text1:TextView = view.findViewById(R.id.text1)
                text1.visibility = View.VISIBLE
                val pin:PinView = view.findViewById(R.id.pinview)
                pin.visibility = View.VISIBLE
                val text2:TextView = view.findViewById(R.id.text2)
                text1.visibility = View.VISIBLE
                val text3:TextView = view.findViewById(R.id.text3)
                text1.visibility = View.VISIBLE
                val button:Button = view.findViewById(R.id.verifyOTP)
                button.visibility = View.VISIBLE
                //Changed Dialog Content

                button.setOnClickListener {
                    val otp:String = pin.text.toString()
                    if(otp.isNotEmpty()){
                        val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                            verificationId.toString(), otp)
                        FirebaseAuth.getInstance().currentUser!!.linkWithCredential(credential)
                            .addOnCompleteListener(requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    Log.d(TAG, "linkWithCredential:success")
                                    val user = task.result?.user
                                    dismiss()
                                } else {
                                    Log.w(TAG, "linkWithCredential:failure", task.exception)
                                    Toast.makeText(
                                        view.context,
                                        "Authentication failed. Wrong OTP entered",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }

                    }else{
                        Toast.makeText(view.context,"Enter OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return view
    }

    private fun sendOTP(phone: String,contex:Context){
        val options = PhoneAuthOptions.newBuilder().setPhoneNumber("+91" + phone)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(requireActivity(),)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        Log.d("Arman","Auth Started")
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.6).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // Set the dialog to be non-cancellable
        isCancelable = false
        return dialog
    }
}