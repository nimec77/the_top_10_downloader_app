package ru.talkinglessons.thetop10downloaderapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

const val TAG = "TheTop10"

class MainActivity : AppCompatActivity() {

    //    private val uiScope = CoroutineScope(Dispatchers.Main)
    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: called")
        ioScope.launch {
            val downloadedData =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml"
                    .downloadData()
            Log.d(TAG, downloadedData)
        }
        Log.d(TAG, "onCreate: done")
    }

    private fun String.downloadData(): String {
        Log.d(TAG, "downloadData: start with $this")
        val rssFeed = downloadXML(this)
        if (rssFeed.isEmpty()) {
            Log.e(TAG, "downloadData: Error downloading")
        }
        return rssFeed
    }

    private fun downloadXML(urlPath: String): String {
        val xmlResult = StringBuilder()

        try {
            val url = URL(urlPath)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            val response = connection.responseCode
            Log.d(TAG, "downloadXML: The response code was $response")

            connection.inputStream.buffered().reader().use { xmlResult.append(it.readText()) }
            Log.d(TAG, "Received ${xmlResult.length} bytes")

            return xmlResult.toString()
        } catch (e: Exception) {
            val errorMessage: String = when (e) {
                is MalformedURLException -> "downloadXML: Invalid URL ${e.message}"
                is IOException -> "downloadXML: IO Exception reading data: ${e.message}"
                is SecurityException -> {
                    e.printStackTrace()
                    "downloadXML: Security Exception. Needs permission? ${e.message}"
                }
                else -> "downloadXML: Unknown error: ${e.message}"
            }
            Log.e(TAG, errorMessage)
        }
        return ""
    }
}