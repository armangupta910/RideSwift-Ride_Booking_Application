package com.example.rideswift

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.HttpAuthHandler
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class addTrip : AppCompatActivity() {
    lateinit var textView: TextView
    lateinit var button: Button
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    private var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_trip)

        findViewById<Button>(R.id.proceed).setOnClickListener {
            val name = findViewById<EditText>(R.id.name).text.toString()
            val username = findViewById<EditText>(R.id.username).text.toString()

            val demo: HashMap <String,HashMap <String,HashMap <String,String>>> = hashMapOf(
                FirebaseAuth.getInstance().currentUser!!.uid.toString() to hashMapOf<String,HashMap <String,String>>(
                    "Personal Details" to hashMapOf<String,String>(
                        "Name" to name,
                        "Username" to username
                    )
                )
            )

            Firebase.firestore.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).set(demo).addOnSuccessListener {
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,homePage::class.java))
            }

        }


    }
}
