package ru.talkinglessons.thetop10downloaderapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URI
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
                downloadData("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
//            Log.d(TAG, downloadedData)
        }
        Log.d(TAG, "onCreate: done")
    }

    private fun downloadData(url: String): String {
        Log.d(TAG, "downloadData: start with $url")
        val rssFeed = downloadXML(url)
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

//            val inputStream = connection.inputStream
//            val inputStreamReader = InputStreamReader(inputStream)
//            val reader = BufferedReader(inputStreamReader)
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val inputBuffer = CharArray(500)
            var charsRead = 0
            while (charsRead >= 0) {
                charsRead = reader.read(inputBuffer)
                if (charsRead >= 0) {
                    xmlResult.append(String(inputBuffer, 0, charsRead))
                }
            }
            reader.close()

            Log.d(TAG, "Received ${xmlResult.length} bytes")

            return xmlResult.toString()
        } catch (e: MalformedURLException) {
            Log.e(TAG, "downloadXML: Invalid URL ${e.message}")
        } catch (e: IOException) {
            Log.e(TAG, "downloadXML: IO Exception reading data: ${e.message}")
        } catch (e: SecurityException) {
            Log.e(TAG, "downloadXML: Security exception. Needs permissions? ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "downloadXML: Unknown error")
        }
        return ""
    }
}