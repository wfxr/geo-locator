package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.wfxr.geolocator.utils.contains

data class Boundary(private val vertices: List<WGSPoint>) {
    val bBox: BoundingBox

    init {
        val xMin = vertices.minBy { it.x }?.x ?: 0.0
        val xMax = vertices.maxBy { it.x }?.x ?: 0.0
        val yMin = vertices.minBy { it.y }?.y ?: 0.0
        val yMax = vertices.maxBy { it.y }?.y ?: 0.0
        bBox = BoundingBox(xMin, xMax, yMin, yMax)
    }

    fun contains(p: WGSPoint): Boolean {
        if (!bBox.contains(p)) return false

        var res = false
        var i = 0
        var j = vertices.size - 1
        while (i < vertices.size) {
            if (vertices[i].y > p.y != vertices[j].y > p.y && p.x < (vertices[j].x - vertices[i].x) * (p.y - vertices[i].y) / (vertices[j].y - vertices[i].y) + vertices[i].x)
                res = !res
            j = i++
        }
        return res
    }
}