package com.example.rideswift

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class bottomSheet : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_sheet)

        findViewById<Button>(R.id.signout).setOnClickListener {
            Toast.makeText(this,"Clicked",Toast.LENGTH_SHORT).show()
            FirebaseAuth.getInstance().signOut()
            val token=getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor=token.edit()
            editor.putString("data","")
            editor.commit()
            startActivity(Intent(this,splashscreen::class.java))
        }
    }
}