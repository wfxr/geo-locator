package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.wfxr.geolocator.utils.contains

interface IBoundary {
    val bBox: BoundingBox
    fun contains(lat: Double, lon: Double): Boolean
}

data class Boundary(private val V: List<WGSPoint>) : IBoundary {
    override val bBox: BoundingBox

    init {
        val xMin = V.minBy { it.x }?.x ?: 0.0
        val xMax = V.maxBy { it.x }?.x ?: 0.0
        val yMin = V.minBy { it.y }?.y ?: 0.0
        val yMax = V.maxBy { it.y }?.y ?: 0.0
        bBox = BoundingBox(xMin, xMax, yMin, yMax)
    }


    override fun contains(lat: Double, lon: Double): Boolean {
        if (!bBox.contains(lat, lon)) return false

        var res = false
        var i = 0
        var j = V.size - 1
        val x = lat
        val y = lon
        while (i < V.size) {
            if (V[i].y > y != V[j].y > y && x < (V[j].x - V[i].x) * (y - V[i].y) / (V[j].y - V[i].y) + V[i].x)
                res = !res
            j = i++
        }
        return res
    }
}