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

    private fun rTreeContains(x: Double, y: Double): Boolean {
        if (!mbr.contains(x, y)) return false
        var count = 0
        rtree.intersectsRightHalfLine(x, y) { i ->
            val (x1, y1) = vertexes[i]
            val (x2, y2) = vertexes[i + 1]
            if (y1 == y2) {
                ++count
            } else {
                val m = (x1 - x2) * (y - y2)
                val n = (y1 - y2) * (x - x2)
                if (y1 > y2 && m >= n || y1 < y2 && m <= n) ++count
            }
            true
        }
        return count % 2 == 1
    }

    override fun contains(lat: Double, lon: Double) = rTreeContains(lat, lon)
}
