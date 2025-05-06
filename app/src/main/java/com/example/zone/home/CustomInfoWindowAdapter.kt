package com.example.zone.home

import android.app.Activity
import android.view.View
import android.widget.TextView
import com.example.zone.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Activity) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        return null // Use default window frame
    }

    override fun getInfoContents(marker: Marker): View {
        val view = context.layoutInflater.inflate(R.layout.custom_info_window, null, false)

        val eventTitle = view.findViewById<TextView>(R.id.title)
        val eventTime = view.findViewById<TextView>(R.id.event_time)
        val address = view.findViewById<TextView>(R.id.address)

        // Parse the snippet to extract time and address
        val snippetParts = marker.snippet?.split("\n") ?: listOf()

        eventTitle.text = marker.title

        if (snippetParts.isNotEmpty() && snippetParts[0].startsWith("Time: ")) {
            eventTime.text = snippetParts[0]
        }

        if (snippetParts.size > 1 && snippetParts[1].startsWith("Address: ")) {
            address.text = snippetParts[1]
        }

        return view
    }
}