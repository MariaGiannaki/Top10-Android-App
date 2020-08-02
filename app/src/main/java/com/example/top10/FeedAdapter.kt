package com.example.top10

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FeedAdapter (context: Context, val resource: Int, val applications: List<FeedEntry>): ArrayAdapter<FeedEntry>(context, resource) {
    private val tag = "FeedAdapter"
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        Log.d(tag, "getCount() called ${applications.size}")
        return applications.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view:View
        val viewHolder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        val currentApp = applications[position]

        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtist.text = currentApp.artist
        viewHolder.tvDescription.text = currentApp.summary

        return view
    }
}

class ViewHolder(v: View) {
    val tvName: TextView = v.findViewById(R.id.tvName)
    val tvArtist: TextView = v.findViewById(R.id.tvArtist)
    val tvDescription:TextView = v.findViewById(R.id.tvSummary)
}