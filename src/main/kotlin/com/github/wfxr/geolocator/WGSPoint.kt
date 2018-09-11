package com.github.wfxr.geolocator

data class WGSPoint(val lat: Double, val lon: Double) {
    val x get() = lat
    val y get() = lon
}
