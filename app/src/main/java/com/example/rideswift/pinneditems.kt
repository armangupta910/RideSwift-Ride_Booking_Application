package com.example.rideswift

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class pinneditems : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pinneditems)

        var data:MutableList<HashMap<String,String>> = mutableListOf()


        val db=Firebase.firestore
        db.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).get().addOnSuccessListener {
            val demodata:HashMap<String,HashMap<String,String>>
            demodata = it.data!!.get("Pinned Products") as HashMap<String, HashMap<String, String>>
            for(i in demodata){
                data.add(i.value)
            }
            val recycler = findViewById<RecyclerView>(R.id.recycler)
            val adap = recycler(this,data,"Matser User")
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            recycler.adapter = adap
            recycler.adapter?.notifyDataSetChanged()
        }
    }
}