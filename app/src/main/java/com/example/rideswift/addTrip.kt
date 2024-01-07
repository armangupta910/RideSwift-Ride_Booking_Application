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

class addTrip : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {
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
        textView = findViewById(R.id.datetimepicker)
        textView.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this, this, year, month,day)
            datePickerDialog.show()
        }

        findViewById<Button>(R.id.addTrip).setOnClickListener {
            val pickup = findViewById<EditText>(R.id.pick).text.toString()
            val dropoff = findViewById<EditText>(R.id.drop).text.toString()
            val vehicle = findViewById<EditText>(R.id.vehicle).text.toString()
            val extra = findViewById<EditText>(R.id.extra).text.toString()
            val db = Firebase.firestore
            var demo: MutableMap<String, MutableList<HashMap<String, String>>> = mutableMapOf(FirebaseAuth.getInstance().currentUser!!.uid.toString() to mutableListOf<HashMap<String,String>>())
            db.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).get().addOnSuccessListener {
                demo = it.data as MutableMap<String, MutableList<HashMap<String, String>>>
                Log.d(TAG,"This is the value of Demo right now: ${demo}")
                var list: MutableList<HashMap<String, String>>? = demo.get(FirebaseAuth.getInstance().currentUser!!.uid.toString()) as MutableList<HashMap<String, String>>
                Log.d(TAG,"This is the value of List right now: ${list}")
                val temphashmap:HashMap<String,String> = hashMapOf(
                    "Pickup" to pickup,
                    "Dropoff" to dropoff,
                    "Vehicle" to vehicle,
                    "Extra" to extra,
                    "Date" to myDay.toString() + "-" + (myMonth + 1).toString() + "-" + myYear.toString(),
                    "Time" to myHour.toString() + ":" + myMinute.toString()
                )
                list!!.add(temphashmap)
                demo[FirebaseAuth.getInstance().currentUser!!.uid.toString()] = list
                db.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).set(demo).addOnSuccessListener {
                    Log.d(TAG,"This is the value of Demo right now in the Databse is: ${demo}")
                    Toast.makeText(this,"Trip added Successfully",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,homePage::class.java))
                }

            }.addOnFailureListener {
                Toast.makeText(this,"Trip could not be Added! Try Again!",Toast.LENGTH_SHORT).show()
                Log.d(TAG,"This is the value of Demo right now: ${demo}")
            }
        }
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = view!!.dayOfMonth
        myYear = view.year
        myMonth = view.month
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this, this, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        var finale2:String = myDay.toString() + "-" + (myMonth + 1) + "-" + myYear + "  " + myHour + ":" + myMinute
        textView.setText(finale2)
    }
}