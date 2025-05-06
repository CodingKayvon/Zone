package com.example.zone.home

import com.google.android.gms.maps.model.LatLng

data class EventData(
    val name: String,
    val time: String,
    val address: String,
    val latLng: LatLng
)