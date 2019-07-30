package com.github.wfxr.geolocator

interface IGeoLocator<T> {
    fun locate(lat: Double, lon: Double): Region<T>?
    fun fastLocate(lat: Double, lon: Double): Region<T>?

    fun locate(p: WGSPoint) = locate(p.lat, p.lon)
    fun fastLocate(p: WGSPoint) = fastLocate(p.lat, p.lon)

    fun locateAll(lat: Double, lon: Double): List<Region<T>>
    fun locateAll(p: WGSPoint) = locateAll(p.lat, p.lon)
}