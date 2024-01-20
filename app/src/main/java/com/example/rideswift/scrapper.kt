package com.example.rideswift

import android.content.ContentValues.TAG
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class scrapper : AppCompatActivity() {
    private lateinit var textview:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrapper)
        textview = findViewById<TextView>(R.id.scrappedText)


        val url = "https://www.amazon.in/s?k=books+zandu&crid=VKEOY47MXXX3&sprefix=books+zandu%2Caps%2C201&ref=nb_sb_noss"
        FetchHtmlTask().execute(url)

    }
    private inner class FetchHtmlTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val url = params[0]

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
            val document: Document = Jsoup.parse(result)
            val selectedSpans: Elements = document.select("span.a-size-medium.a-color-base.a-text-normal")
            val selectedPrices: Elements = document.select("span.a-price-whole")

            for (span: Element in selectedSpans) {
                val spanText: String = span.text()
                println("Extracted Text: $spanText")
            }

            for (span: Element in selectedPrices) {
                val spanText: String = span.text()
                println("Extracted Text: $spanText")
            }
        }
    }



}