package com.example.rideswift

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rideswift.R
import org.w3c.dom.Text

class recycler(val valconti:Context,val data:MutableList<HashMap<String,String>>,val owner:String):RecyclerView.Adapter<recycler.view_holder>(){
    class view_holder(itemView:View):RecyclerView.ViewHolder(itemView){
        val source = itemView.findViewById<TextView>(R.id.source)
        val desti = itemView.findViewById<TextView>(R.id.destination)
        val date = itemView.findViewById<TextView>(R.id.date)
        val time = itemView.findViewById<TextView>(R.id.time)
        val vehicle = itemView.findViewById<TextView>(R.id.vehicle)
        val chat = itemView.findViewById<Button>(R.id.chat)
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):view_holder{
        var itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.cardforrecycler,parent,false)
        return view_holder(itemView)
    }

    override fun getItemCount():Int{
        return data.size
    }

    override fun onBindViewHolder(holder:view_holder,position:Int){
        holder.source.setText(data[position].get("Pickup"))
        holder.desti.setText(data[position].get("Dropoff"))
        holder.date.setText(data[position].get("Date"))
        holder.time.setText(data[position].get("Time"))
        holder.vehicle.setText(data[position].get("Vehicle"))

    }
}
