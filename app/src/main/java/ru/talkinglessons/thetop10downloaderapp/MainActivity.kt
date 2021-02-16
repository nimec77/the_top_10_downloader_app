package ru.talkinglessons.thetop10downloaderapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import ru.talkinglessons.thetop10downloaderapp.entity.FeedEntry
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.talkinglessons.thetop10downloaderapp.adapter.FeedAdapter

const val TAG = "TheTop10"

class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: called")
        ioScope.launch {
            downloadData(
                this@MainActivity.baseContext,
                xmlListView,
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml",
            )

        }
        Log.d(TAG, "onCreate: done")
    }

    private suspend fun downloadData(context: Context, listView: ListView, url: String) {
        Log.d(TAG, "downloadData: start with $url")
        val rssFeed = downloadXML(url)
        if (rssFeed.isEmpty()) {
            Log.e(TAG, "downloadData: Error downloading")
        }
        val parseApplications = ParseApplications()
        parseApplications.parse(rssFeed)

        withContext(Dispatchers.Main) {
            val feedAdapter = FeedAdapter(context, R.layout.list_record, parseApplications.applications)
            listView.adapter = feedAdapter
        }
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