package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.wfxr.geolocator.utils.contains
import com.infomatiq.jsi.Rectangle
import com.infomatiq.jsi.rtree.RTree

interface IBoundary {
    val mbr: BoundingBox
    val vertexes: List<WGSPoint>
    fun contains(lat: Double, lon: Double): Boolean
}

class Boundary : IBoundary {
    internal constructor(vertexes: List<WGSPoint>, lazy: Boolean) {
        this.vertexes = vertexes
        this.rtree = RTree(4, 55)
        if (!lazy) init()
    }

    internal fun init() {
        val xMin = vertexes.minBy { it.x }?.x ?: 0.0
        val xMax = vertexes.maxBy { it.x }?.x ?: 0.0
        val yMin = vertexes.minBy { it.y }?.y ?: 0.0
        val yMax = vertexes.maxBy { it.y }?.y ?: 0.0
        mbr = BoundingBox(xMin, xMax, yMin, yMax)
        vertexes.zipWithNext { a, b -> Rectangle(a.lat, a.lon, b.lat, b.lon) }
            .forEachIndexed { index, rectangle -> rtree.add(rectangle, index) }
    }

    constructor(vertexes: List<WGSPoint>) : this(vertexes, false)

    override val vertexes: List<WGSPoint>
    override lateinit var mbr: BoundingBox
    private val rtree: RTree

    private fun rTreeContains(lat: Double, lon: Double): Boolean {
        if (!mbr.contains(lat, lon)) return false
        var count = 0
        rtree.intersectsRightHalfLine(lat, lon) { i ->
            val (lat1, lon1) = vertexes[i]
            val (lat2, lon2) = vertexes[i + 1]
            if (lon1 == lon2) {
                ++count
            } else {
                val m = (lat1 - lat2) * (lon - lon2)
                val n = (lon1 - lon2) * (lat - lat2)

                if (lon1 > lon2 && m >= n) ++count
                if (lon1 < lon2 && m <= n) ++count
            }
            true
        }
        return count % 2 == 1
    }

    override fun contains(lat: Double, lon: Double) = rTreeContains(lat, lon)
}
