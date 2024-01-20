package com.example.rideswift

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class splashscreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        var token1=getSharedPreferences("data", Context.MODE_PRIVATE)
        if(token1.getString("data","")==""){
            startActivity(Intent(this,mainPage::class.java))
            finish()
        }
        else{
            val intent = Intent(this,homePage::class.java)
            val email:String = FirebaseAuth.getInstance().currentUser!!.email.toString()
            val username:String = FirebaseAuth.getInstance().currentUser!!.displayName.toString()
            val imageURL:String = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
            intent.putExtra("Email",email)
            intent.putExtra("Username",username)
            intent.putExtra("ProfileURL",imageURL)

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