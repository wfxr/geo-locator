package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.wfxr.geolocator.utils.contains
import com.infomatiq.jsi.Rectangle
import com.infomatiq.jsi.rtree.RTree
import org.apache.commons.lang3.Validate

class Region<T> {
    internal constructor(vertexes: List<WGSPoint>, tag: T, lazy: Boolean) {
        Validate.isTrue(vertexes.first() == vertexes.last(), "region curve not closed")
        this.vertexes = vertexes
        this.rtree = RTree(4, 55)
        this.tag = tag
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

    constructor(vertexes: List<WGSPoint>, tag: T) : this(vertexes, tag, false)

    val tag: T
    val vertexes: List<WGSPoint>
    lateinit var mbr: BoundingBox
    private val rtree: RTree

    // TODO: Fix bug on Point (440305, 22.5240300000, 113.9247900000, "中国广东省深圳市南山区")
    @Suppress("unused")
    private fun rTreeContains(x: Double, y: Double): Boolean {
        if (!mbr.contains(x, y)) return false
        var count = 0
        rtree.intersectsRightHalfLine(x, y) { i ->
            val (x1, y1) = vertexes[i]
            val (x2, y2) = vertexes[i + 1]
            val m = (x1 - x2) * (y - y2)
            val n = (y1 - y2) * (x - x2)
            if ((y1 > y2 && m >= n || y1 < y2 && m <= n) && y != y2 && y1 != y2) ++count
            true
        }
        return count % 2 == 1
    }

    @Suppress("unused")
    private fun iterateContains(x: Double, y: Double): Boolean {
        if (!mbr.contains(x, y)) return false

        var res = 0
        var i = 0
        var j = vertexes.size - 1
        while (i < vertexes.size) {
            if (vertexes[i].y > y != vertexes[j].y > y && x < (vertexes[j].x - vertexes[i].x) * (y - vertexes[i].y) / (vertexes[j].y - vertexes[i].y) + vertexes[i].x)
                ++res
            j = i++
        }
        return res % 2 == 1
    }

    fun contains(lat: Double, lon: Double) = iterateContains(lat, lon)
}
