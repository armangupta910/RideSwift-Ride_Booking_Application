package com.example.rideswift

import android.content.ContentValues
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import kotlin.math.min

class viewSeprately : AppCompatActivity() {
    var selectedmarket:String = ""
    private var data1:MutableList<HashMap<String,String>> = mutableListOf()
    private var data2:MutableList<HashMap<String,String>> = mutableListOf()
    private var masterData:MutableList<HashMap<String,String>> = mutableListOf()
    lateinit var recycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_seprately)

        findViewById<ImageView>(R.id.goBack).setOnClickListener {
            startActivity(Intent(this,homePage::class.java))
        }
        val dropdownButton: EditText = findViewById(R.id.dropdown)

        dropdownButton.setOnClickListener { view ->
            // Creating the instance of PopupMenu
            val popupMenu = PopupMenu(this, view)

            // Inflating the Popup using xml file
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            // Registering popup with OnMenuItemClickListener
            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem): Boolean {
                    when (item.itemId) {
                        R.id.amazon -> {
                            // Handle option 1 click
                            selectedmarket = "Amazon"
                            dropdownButton.setText("Amazon")
                            return true
                        }

                        R.id.flipkart -> {
                            // Handle option 2 click
                            selectedmarket = "Flipkart"
                            dropdownButton.setText("Flipkart")
                            return true
                        }
                        // Add more cases for other menu items
                        else -> return false
                    }
                }
            })

            // Showing the popup menu
            popupMenu.show()
        }

        findViewById<Button>(R.id.go).setOnClickListener {
            masterData.clear()
            val data:String = findViewById<EditText>(R.id.url).text.toString()
            var url:String = ""
            if(selectedmarket == "Amazon"){
                url = "https://www.amazon.in/s?k=" + data + "&crid=1DM0CQLCR4RGS&sprefix=" + data + "%2Caps%2C263&ref=nb_sb_noss_1"
            }
            if(selectedmarket == "Flipkart"){
                url = "https://www.flipkart.com/search?q=" + data + "&otracker=search&otracker1=search&marketplace=FLIPKART&as-show=on&as=off"
            }
            FetchHtmlTask(selectedmarket).execute(url)

            recycler = findViewById<RecyclerView>(R.id.recycler)
            val adap = recycler(this,masterData,"Matser User")
            recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            recycler.adapter = adap
            recycler.adapter?.notifyDataSetChanged()
        }
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
                Log.d(ContentValues.TAG,"Ghusa Amazon")
                val document: Document = Jsoup.parse(result)
                val selectedSpans: Elements = document.select("span.a-size-base-plus.a-color-base.a-text-normal")
                val selected: Elements = document.select("span.a-size-medium.a-color-base.a-text-normal")
                for(i in selected){
                    selectedSpans.add(i)
                }
                val selectedPrices: Elements = document.select("span.a-price-whole")
                val selectedRatings: Elements = document.select("span.a-icon-alt")
                val imageURLS: Elements = document.select("img.s-image")
                val productLinks: Elements = document.select("a.a-link-normal.s-underline-text.s-underline-link-text.s-link-style.a-text-normal")

                data1.clear()

                for(i in 1..selectedSpans.size){
                    val hashi = hashMapOf<String,String>(
                        "Name" to selectedSpans[i-1].text(),
                        "Price" to selectedPrices[min(i-1,selectedPrices.size-1)].text(),
                        "Ratings" to selectedRatings[min(i-1,selectedRatings.size-1)].text(),
                        "Platform" to "Amazon",
                        "ImageURL" to imageURLS[min(i-1,imageURLS.size-1)].attr("src").toString(),
                        "ProductLink" to productLinks[min(i-1,productLinks.size-1)].attr("href").toString()
                    )
//                    Log.d(TAG,"${imageURLS}")
                    data1.add(hashi)
                }
                Log.d(ContentValues.TAG,"Data1: ${data1}")
                recycler.adapter?.notifyDataSetChanged()



            }
            if(plat == "Flipkart"){
                Log.d(ContentValues.TAG,"Ghusa Flipkart")
                val document: Document = Jsoup.parse(result)
                val selectedSpans: Elements = document.select("a.s1Q9rs")
                Log.d(ContentValues.TAG,"${selectedSpans.size}")
                var selected: Elements = document.select("a._4rR01T")
                for(i in selected){
                    selectedSpans.add(i)
                }
                val selectedPrices: Elements = document.select("div._30jeq3")
                selected = document.select("div._30jeq3._1_WHN1")
                for(i in selected){
                    selectedPrices.add(i)
                }
                Log.d(ContentValues.TAG,"${selectedPrices.size}")
                val selectedRatings: Elements = document.select("div._3LWZlK")
                val imageURLS: Elements = document.select("img._396cs4")
                val productLinks: Elements = document.select("a")

                data2.clear()

                for(i in 1..selectedSpans.size){
                    Log.d(ContentValues.TAG,"Ghusa")
                    val hashi = hashMapOf<String,String>(
                        "Name" to selectedSpans[i-1].text(),
                        "Price" to selectedPrices[min(i-1,selectedPrices.size-1)].text(),
                        "Ratings" to selectedRatings[min(i-1,selectedRatings.size-1)].text() + " out of 5 stars.",
                        "Platform" to "Flipkart",
                        "ImageURL" to imageURLS[min(i-1,imageURLS.size-1)].attr("src").toString(),
//                        "ProductLink" to ""
                        "ProductLink" to productLinks[min(i-1,productLinks.size-1)].attr("href").toString()
                    )
//                    Log.d(TAG,"${imageURLS}")
                    data2.add(hashi)
                }

                Log.d(ContentValues.TAG,"Data2: ${data2}")
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