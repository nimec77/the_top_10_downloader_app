package ru.talkinglessons.thetop10downloaderapp

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.talkinglessons.thetop10downloaderapp.entity.FeedEntry
import java.lang.Exception

class ParseApplications {
    private val tag = "ParseApplications"

    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
        Log.d(tag, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {

            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }
}