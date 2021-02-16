package ru.talkinglessons.thetop10downloaderapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import ru.talkinglessons.thetop10downloaderapp.R
import ru.talkinglessons.thetop10downloaderapp.entity.FeedEntry

class FeedAdapter(
    context: Context,
    private val resource: Int,
    private val applications: List<FeedEntry>
) :
    ArrayAdapter<FeedAdapter>(context, resource) {
    private val tag = "FeedAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(tag, "getCount() called")
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        Log.d(tag, "getView() called")

        val view = convertView ?: inflater.inflate(resource, parent, false)

        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvArtists: TextView = view.findViewById(R.id.tvArtist)
        val tvSummary: TextView = view.findViewById(R.id.tvSummary)

        val currentApp = applications[position]

        tvName.text = currentApp.name
        tvArtists.text = currentApp.artist
        tvSummary.text = currentApp.summary

        return view
    }
}