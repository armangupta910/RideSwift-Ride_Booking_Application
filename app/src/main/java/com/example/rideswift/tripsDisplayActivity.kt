package com.example.rideswift

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class tripsDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips_display)

        val data:MutableList<HashMap<String,String>> = mutableListOf()
        val db=Firebase.firestore

        db.collection("MasterData").get().addOnSuccessListener {
            for(i in it){
//                if(i.id.toString() != FirebaseAuth.getInstance().currentUser!!.uid.toString()){
                    val demo:MutableList<HashMap<String,String>> = i.data.get(i.id.toString()) as MutableList<HashMap<String, String>>
                    data.addAll(demo)
//                }
            }
            val recycler = findViewById<RecyclerView>(R.id.recycler)
            val adap = recycler(this,data,"Matser User")
            recycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
            recycler.adapter = adap
        }
    }
}