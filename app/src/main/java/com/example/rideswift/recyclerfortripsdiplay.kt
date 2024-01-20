package com.example.rideswift

import android.app.Activity
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rideswift.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class recycler(val valconti:Context,val data:MutableList<HashMap<String,String>>,val owner:String):RecyclerView.Adapter<recycler.view_holder>(){
    class view_holder(itemView:View):RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.name)
        val ratings = itemView.findViewById<TextView>(R.id.ratings)
        val platform = itemView.findViewById<TextView>(R.id.platform)
        val price = itemView.findViewById<TextView>(R.id.price)
        val image = itemView.findViewById<ImageView>(R.id.image)
        val goweb = itemView.findViewById<Button>(R.id.goweb)
        val pin = itemView.findViewById<Button>(R.id.pin)
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):view_holder{
        var itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.cardforrecycler,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount():Int{
        return data.size
    }

    override fun onBindViewHolder(holder:view_holder,position:Int){
        holder.name.setText("Name: " + data[position].get("Name"))
        holder.ratings.setText("Ratings: " + data[position].get("Ratings"))
        holder.platform.setText("Platform: " + data[position].get("Platform"))
        var x = data[position].get("Price")
        if(x=="0"){
            x="Free"
        }
        else{
            x = "Rs. " + x
        }
        holder.price.setText("Price: " + x)

        holder.goweb.text = "Go to " + data[position].get("Platform")

        Glide.with(valconti).load(data[position].get("ImageURL"))
            .into(holder.image)

        holder.pin.setOnClickListener {
            val data1:HashMap<String,String> = hashMapOf()
            var server: HashMap<String,HashMap<String,HashMap<String,String>>> = hashMapOf()
            Firebase.firestore.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).get().addOnSuccessListener {
                server = it.data as HashMap<String, HashMap<String, HashMap<String, String>>>

                var temp:HashMap<String,HashMap<String,String>> = server.get("Pinned Products") as HashMap<String, HashMap<String, String>>

                temp[data[position].get("Name").toString()] = hashMapOf(
                    "Name" to data[position].get("Name").toString(),
                    "Ratings" to data[position].get("Ratings").toString(),
                    "Paltform" to data[position].get("Platform").toString(),
                    "Price" to data[position].get("Price").toString(),
                    "Image" to data[position].get("ImageURL").toString(),
                    "Web" to data[position].get("ProductLink").toString()
                )
                server["Pinned Products"] = temp
                Firebase.firestore.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).set(server).addOnSuccessListener {
                    Toast.makeText(valconti,"Product Pinned!!!",Toast.LENGTH_SHORT).show()
                }


            }
        }

        holder.goweb.setOnClickListener {
            val url = data[position].get("ProductLink").toString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

            valconti.startActivity(intent)
        }
    }
}
