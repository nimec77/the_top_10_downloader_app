package ru.talkinglessons.thetop10downloaderapp

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import ru.talkinglessons.thetop10downloaderapp.entity.FeedEntry
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ParseApplications {
    private val tag = "ParseApplications"

    val applications = ArrayList<FeedEntry>()

    fun parse(xmlData: String): Boolean {
//        Log.d(tag, "parse called with $xmlData")
        var status = true
        var inEntry = false
        var gotImage = false
        var textValue = ""

        try {
            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()
            xpp.setInput(xmlData.reader())
            var eventType = xpp.eventType
            var currentRecord = FeedEntry()
            while (eventType != XmlPullParser.END_DOCUMENT) {
                val tagName = xpp.name?.toLowerCase(Locale.ROOT)
                when (eventType) {
                    XmlPullParser.START_TAG -> {
//                        Log.d(tag, "parse: Starting tag for $tagName")
                        if (tagName == "entry") {
                            inEntry = true
                        } else if ((tagName == "image") && inEntry) {
                            val imageResolution = xpp.getAttributeValue(null, "height")
                            if (imageResolution.isNotEmpty()) {
                                gotImage = imageResolution == "53"
                            }
                        }
                    }

                    XmlPullParser.TEXT -> textValue = xpp.text

                    XmlPullParser.END_TAG -> {
                        Log.d(tag, "parse: Ending tag for $tagName")
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()
                                }
                                "name" -> currentRecord.name = textValue
                                "artist" -> currentRecord.artist = textValue
                                "releasedate" -> currentRecord.releaseDate = textValue
                                "summary" -> currentRecord.summary = textValue
                                "image" -> if (gotImage) currentRecord.imageURL = textValue
                            }
                        }
                    }
                }
                eventType = xpp.next()
            }
//            for (app in applications) {
//                Log.d(tag, "********************")
//                Log.d(tag, app.toString())
//            }
        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }
}