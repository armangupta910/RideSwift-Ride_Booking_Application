package com.example.rideswift

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okio.utf8Size
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import kotlin.math.min

class homePage : AppCompatActivity() {

    lateinit var phone:String
    lateinit var email:String
    lateinit var username:String
    private lateinit var profileURL:String
    private var data1:MutableList<HashMap<String,String>> = mutableListOf()
    private var data2:MutableList<HashMap<String,String>> = mutableListOf()
    private var masterData:MutableList<HashMap<String,String>> = mutableListOf()
    lateinit var recycler:RecyclerView
    var ref1 = 1
    var ref2 = 1

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
//        val chipGroup: ChipGroup = findViewById(R.id.chipGroup)
        val chip1 = Chip(this)
        chip1.text = "Amazon"
        chip1.setChipBackgroundColorResource(R.color.yellow)

        chip1.setOnClickListener {
            if(ref1==1){
                Toast.makeText(this,"Chip 1 Clicked",Toast.LENGTH_SHORT).show()
                chip1.setChipBackgroundColorResource(R.color.white)
                if(ref2 == 1){
                    Log.d(TAG,"MasterData: ${masterData}")
                    masterData = data2
                    Log.d(TAG,"MasterData: ${masterData}")
                    recycler.adapter?.notifyDataSetChanged()
                }
                else{
                    Log.d(TAG,"MasterData: ${masterData}")
                    masterData = mutableListOf()
                    Log.d(TAG,"MasterData: ${masterData}")
                    recycler.adapter?.notifyDataSetChanged()
                }

                ref1 = 0
            }
            else{
                chip1.setChipBackgroundColorResource(R.color.yellow)
                Log.d(TAG,"MasterData: ${masterData}")
                masterData.addAll(data1)
                Log.d(TAG,"MasterData: ${masterData}")
                recycler.adapter?.notifyDataSetChanged()
                ref1 = 1
            }
        }

//        chip1.isCheckable = true
//        chip1.setOnCheckedChangeListener { _, isChecked ->
//            Toast.makeText(this,"Chip 1 Clicked",Toast.LENGTH_SHORT).show()
//        }
        val chip2 = Chip(this)
        chip2.setChipBackgroundColorResource(R.color.yellow)

        chip2.setOnClickListener {
            if(ref2==1){
                chip2.setChipBackgroundColorResource(R.color.white)
                if(ref1 == 1){
                    masterData = data1
                    recycler.adapter?.notifyDataSetChanged()
                }
                else{
                    masterData = mutableListOf()
                    recycler.adapter?.notifyDataSetChanged()
                }
                ref2 = 0
            }
            else{
                masterData.addAll(data2)
                recycler.adapter?.notifyDataSetChanged()
                chip2.setChipBackgroundColorResource(R.color.yellow)
                ref2 = 1
            }
        }
//        chip2.isCheckable = true
//        chip2.setOnCheckedChangeListener { _, isChecked ->
//            Toast.makeText(this,"Chip 2 Clicked",Toast.LENGTH_SHORT).show()
//        }
        chip2.text = "Flipkart"

//        chipGroup.addView(chip1)
//        chipGroup.addView(chip2)
//        val db = Firebase.firestore
//        var ref1:Int =0;
//        db.collection("MasterData").get().addOnSuccessListener {
//            for(i in it.documents){
//                if(i.id == FirebaseAuth.getInstance().currentUser!!.uid.toString()){
//                    ref1 = 1
//                }
//            }
//        }
//        if(ref1==0){
//            db.collection("MasterData").document(FirebaseAuth.getInstance().currentUser!!.uid.toString()).set(demo)
//        }


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
//        email = intent.getStringExtra("Email").toString()
//        username = intent.getStringExtra("Username").toString()
//        profileURL = intent.getStringExtra("ProfileURL").toString()

//        val image:ImageView = findViewById(R.id.profileimage)
//        Glide.with(this).load(profileURL).centerCrop().into(image)

//        findViewById<TextView>(R.id.data).setText(email+" "+username)

//        val bottom:View = findViewById(R.id.bottom_sheet)
//        val signout = bottom.findViewById<Button>(R.id.signout)
//        signout.setOnClickListener {
//            signout()
//        }

        val nav = findViewById<NavigationView>(R.id.navigation)

        nav.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signout -> {
                    // Handle menu item 1 click
                    signout()
                    true
                }
                R.id.marketplaces -> {
                    startActivity(Intent(this,viewSeprately::class.java))
                    true
                }
                R.id.home -> {
                    startActivity(Intent(this,homePage::class.java))
                    finish()
                    true
                }
                R.id.help -> {
//                    startActivity(Intent(this,homePage::class.java))
                    val customDialog = Dialog(this)

                    // Set the content view to the custom layout
                    customDialog.setContentView(R.layout.custom_dialog)

                    customDialog.show()

                    customDialog.findViewById<Button>(R.id.exit).setOnClickListener {
                        customDialog.dismiss()
                    }
                    true
                }
                R.id.signout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,splashscreen::class.java))
                    true
                }
                R.id.pinned -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this,pinneditems::class.java))
                    true
                }
                // Add more cases as needed
                else -> false
            }
        }

//        val data:MutableList<HashMap<String,String>> = mutableListOf(
//            hashMapOf(
//                "Name" to "Arman",
//                "Ratings" to "5 Star",
//                "Platform" to "Amazon",
//                "Price" to "500$"
//            ),
//        )

        findViewById<ImageView>(R.id.searchButton).setOnClickListener {
            var query = findViewById<EditText>(R.id.searchBox).text.toString()

            val query1 = query.replace(" ","+")
            val query2 = query.replace(" ","%20")

            if(query.length !=0 ){
                val link1 = "https://www.amazon.in/s?k=" + query1 + "&crid=1DM0CQLCR4RGS&sprefix=" + query1 + "%2Caps%2C263&ref=nb_sb_noss_1"
                val link2 = "https://www.flipkart.com/search?q=" + query2 + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off"
                masterData.clear()
                FetchHtmlTask("Amazon").execute(link1)
                FetchHtmlTask("Flipkart").execute(link2)
                Log.d(TAG,"Master Data: ${masterData}")
                recycler.adapter?.notifyDataSetChanged()
            }


        }

        val url1 = "https://www.amazon.in/s?k=books&i=digital-text&crid=3OQ8PO913S4FY&sprefix=book%2Cdigital-text%2C204&ref=nb_sb_noss_1"
        val url2 = "https://www.flipkart.com/search?q=books&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off"
        masterData.clear()
        FetchHtmlTask("Amazon").execute(url1)
        FetchHtmlTask("Flipkart").execute(url2)

        Log.d(TAG,"Data 1 Pehle: ${data1}")
//        if(ref1 == 1){
//            masterdata.addAll(data1)
//        }
//        if(ref2 == 1){
//            masterdata.addAll(data2)
//        }
        Log.d(TAG,"${data1}")
        Log.d(TAG,"${data2}")
        recycler = findViewById<RecyclerView>(R.id.recycler)
        val adap = recycler(this,masterData,"Matser User")
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        recycler.adapter = adap
        recycler.adapter?.notifyDataSetChanged()

    }

    private inner class FetchHtmlTask(platform:String) : AsyncTask<String, Void, String>() {

        val plat = platform
        override fun doInBackground(vararg params: String?): String {
            val url = params[0]
            data1.clear()
            data2.clear()

            try {
                // Connect to the URL and get the HTML content
                val document: Document = Jsoup.connect(url).get()

                // Get the HTML content as a string
                return document.outerHtml()
            } catch (e: IOException) {
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            // Print or process the HTML content here


            // Example: Select and print text from span elements
            if(plat == "Amazon"){
                Log.d(TAG,"Ghusa Amazon")
                val document: Document = Jsoup.parse(result)
                val selectedSpans: Elements = document.select("span.a-size-base-plus.a-color-base.a-text-normal")
                val selected:Elements = document.select("span.a-size-medium.a-color-base.a-text-normal")
                for(i in selected){
                    selectedSpans.add(i)
                }
                val selectedPrices: Elements = document.select("span.a-price-whole")
                val selectedRatings: Elements = document.select("span.a-icon-alt")
                val imageURLS: Elements = document.select("img.s-image")
                val productLinks:Elements = document.select("a.a-link-normal.s-underline-text.s-underline-link-text.s-link-style.a-text-normal")

                data1.clear()

                for(i in 1..selectedSpans.size){
                    val hashi = hashMapOf<String,String>(
                        "Name" to selectedSpans[i-1].text(),
                        "Price" to selectedPrices[min(i-1,selectedPrices.size-1)].text(),
                        "Ratings" to selectedRatings[min(i-1,selectedRatings.size-1)].text(),
                        "Platform" to "Amazon",
                        "ImageURL" to imageURLS[min(i-1,imageURLS.size-1)].attr("src").toString(),
                        "ProductLink" to "https://amazon.in" + productLinks[min(i-1,productLinks.size-1)].attr("href").toString()
                    )
//                    Log.d(TAG,"${imageURLS}")
                    data1.add(hashi)
                }
                Log.d(TAG,"Data1: ${data1}")
                if(ref1 == 1){
                    masterData.addAll(data1)
                }
                recycler.adapter?.notifyDataSetChanged()



            }
            if(plat == "Flipkart"){
                Log.d(TAG,"Ghusa Flipkart")
                val document: Document = Jsoup.parse(result)
                val selectedSpans: Elements = document.select("a.s1Q9rs")
                Log.d(TAG,"${selectedSpans.size}")
                var selected:Elements = document.select("a._4rR01T")
                for(i in selected){
                    selectedSpans.add(i)
                }
                val selectedPrices: Elements = document.select("div._30jeq3")
                selected = document.select("div._30jeq3._1_WHN1")
                for(i in selected){
                    selectedPrices.add(i)
                }
                Log.d(TAG,"${selectedPrices.size}")
                val selectedRatings: Elements = document.select("div._3LWZlK")
                val imageURLS: Elements = document.select("img._396cs4")
                val productLinks:Elements = document.select("a")

                data2.clear()

                for(i in 1..selectedSpans.size){
                    Log.d(TAG,"Ghusa")
                    val hashi = hashMapOf<String,String>(
                        "Name" to selectedSpans[i-1].text(),
                        "Price" to selectedPrices[min(i-1,selectedPrices.size-1)].text(),
                        "Ratings" to selectedRatings[min(i-1,selectedRatings.size-1)].text() + " out of 5 stars.",
                        "Platform" to "Flipkart",
                        "ImageURL" to imageURLS[min(i-1,imageURLS.size-1)].attr("src").toString(),
//                        "ProductLink" to ""
                        "ProductLink" to "https://flipkart.com" + productLinks[min(i-1,productLinks.size-1)].attr("href").toString()
                    )
//                    Log.d(TAG,"${imageURLS}")
                    data2.add(hashi)
                }

                Log.d(TAG,"Data2: ${data2}")
                if(ref2 == 1){
                    masterData.addAll(data2)
                }
                recycler.adapter?.notifyDataSetChanged()


            }


//            for (span: Element in selectedSpans) {
//                val spanText: String = span.text()
//                println("Extracted Text: $spanText")
//            }
//
//            for (span: Element in selectedPrices) {
//                val spanText: String = span.text()
//                println("Extracted Text: $spanText")
//            }
        }
    }
}