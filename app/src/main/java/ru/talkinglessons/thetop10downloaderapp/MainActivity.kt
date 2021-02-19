package ru.talkinglessons.thetop10downloaderapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import ru.talkinglessons.thetop10downloaderapp.adapter.FeedAdapter
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

const val TAG = "TheTop10"

class MainActivity : AppCompatActivity() {

    private val ioScope = CoroutineScope(Dispatchers.IO)

    private var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: called")
        downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate: done")
    }

    private fun downloadUrl(feedUrl: String) {
        ioScope.launch {
            downloadData(
                this@MainActivity.baseContext,
                xmlListView,
                feedUrl,
            )

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(R.id.mnu10)?.isChecked = true
        } else {
            menu?.findItem(R.id.mnu25)?.isCheckable = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.mnuFree -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"

            R.id.mnuPaid -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"

            R.id.mnuSongs -> feedUrl =
                "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"

            R.id.mnu10, R.id.mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        downloadUrl(feedUrl.format(feedLimit))
        return true
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
            val feedAdapter =
                FeedAdapter(context, R.layout.list_record, parseApplications.applications)
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