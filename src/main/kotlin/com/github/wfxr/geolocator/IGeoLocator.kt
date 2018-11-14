package com.github.wfxr.geolocator

interface IGeoLocator {
    fun locate(lat: Double, lon: Double): Region?
    fun fastLocate(lat: Double, lon: Double): Region?

    fun locate(p: WGSPoint) = locate(p.lat, p.lon)
    fun fastLocate(p: WGSPoint) = fastLocate(p.lat, p.lon)
}