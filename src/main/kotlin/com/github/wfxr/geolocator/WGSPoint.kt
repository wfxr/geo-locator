package com.github.wfxr.geolocator

data class WGSPoint(val lat: Double, val lon: Double) {
    inline val x get() = lat
    inline val y get() = lon
}
