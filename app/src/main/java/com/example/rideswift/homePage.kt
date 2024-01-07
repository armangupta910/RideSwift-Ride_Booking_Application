package com.example.rideswift

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class homePage : AppCompatActivity() {

    lateinit var phone:String
    lateinit var email:String
    lateinit var username:String
    private lateinit var profileURL:String

    private fun signout(){
        Toast.makeText(this,"Signing Out",Toast.LENGTH_SHORT).show()
        FirebaseAuth.getInstance().signOut()
        val token=getSharedPreferences("data", Context.MODE_PRIVATE)
        val editor=token.edit()
        editor.putString("data","")
        editor.commit()
        startActivity(Intent(this,splashscreen::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val demo:MutableMap<String,MutableList<HashMap<String,String>>> = mutableMapOf(FirebaseAuth.getInstance().currentUser!!.uid.toString() to mutableListOf<HashMap<String,String>>(
            hashMapOf("0" to "1")
        ))
        val db = Firebase.firestore
        var ref1:Int =0;
        db.collection("MasterData").get().addOnSuccessListener {
            for(i in it.documents){
                if(i.id == FirebaseAuth.getInstance().currentUser!!.uid.toString()){
                    ref1 = 1
                }
            }
        }
        if(ref1==0){
            db.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).set(demo)
        }


        //Checking for the incomplete Authentication
        val google = intent.getStringExtra("Google").toString()
        val auth = FirebaseAuth.getInstance()
        var ph:String = "123"
        val ref:String = "1234567890123"
        auth.currentUser!!.reload()
        ph= auth.currentUser!!.phoneNumber.toString()
        Log.d(TAG,"Phone Number found is: ${ph}")
        if(ph.length != ref.length){
            customDialogforPhoneVerification(this).show(supportFragmentManager,"MyCustomDialog")
//            customDialogforPhoneVerification().dialog!!.setCancelable(false)
        }

        val navigation = findViewById<NavigationView>(R.id.navigation)
        val drawer = findViewById<DrawerLayout>(R.id.drawer)

        val butt = findViewById<ImageView>(R.id.opendrawer)

        butt.setOnClickListener {
            drawer.openDrawer(navigation)
        }

//        phone = intent.getStringExtra("Phone").toString()
        email = intent.getStringExtra("Email").toString()
        username = intent.getStringExtra("Username").toString()
        profileURL = intent.getStringExtra("ProfileURL").toString()

        val image:ImageView = findViewById(R.id.profileimage)
        Glide.with(this).load(profileURL).centerCrop().into(image)

        findViewById<TextView>(R.id.data).setText(email+" "+username)

        val bottom:View = findViewById(R.id.bottom_sheet)
        val signout = bottom.findViewById<Button>(R.id.signout)
        signout.setOnClickListener {
            signout()
        }

        val nav = findViewById<NavigationView>(R.id.navigation)

        nav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signout -> {
                    // Handle menu item 1 click
                    signout()
                    true
                }
                R.id.support -> {
                    startActivity(Intent(this,addTrip::class.java))
                    true
                }
                // Add more cases as needed
                else -> false
            }
        }


    }
}