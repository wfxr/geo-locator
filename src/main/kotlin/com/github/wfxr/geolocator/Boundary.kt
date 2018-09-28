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

class Boundary(private val V: List<WGSPoint>) : IBoundary {
    override val mbr: BoundingBox
    private val tree: RTree
    override val vertexes get() = V

    init {
        val xMin = V.minBy { it.x }?.x ?: 0.0
        val xMax = V.maxBy { it.x }?.x ?: 0.0
        val yMin = V.minBy { it.y }?.y ?: 0.0
        val yMax = V.maxBy { it.y }?.y ?: 0.0
        mbr = BoundingBox(xMin, xMax, yMin, yMax)
        tree = RTree(4, 55)

        V.zipWithNext { a, b -> Rectangle(a.lat, a.lon, b.lat, b.lon) }
            .forEachIndexed { index, rectangle -> tree.add(rectangle, index) }
    }

    private fun rTreeContains(lat: Double, lon: Double): Boolean {
        if (!mbr.contains(lat, lon)) return false
        var count = 0
        tree.intersectsRightHalfLine(lat, lon) { i ->
            val (lat1, lon1) = V[i]
            val (lat2, lon2) = V[i + 1]
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

    @Suppress("ConvertTwoComparisonsToRangeCheck")
    private fun lineIntersects(A: WGSPoint, B: WGSPoint, C: WGSPoint, D: WGSPoint): Boolean {
        val dxAB = B.x - A.x
        val dyAB = B.y - A.y
        val dxCD = D.x - C.x
        val dyCD = D.y - C.y

        val s = (-dyAB * (A.x - C.x) + dxAB * (A.y - C.y)) / (-dxCD * dyAB + dxAB * dyCD)
        if (s >= 0 && s <= 1) return true

        val t = (dxCD * (A.y - C.y) - dyCD * (A.x - C.x)) / (-dxCD * dyAB + dxAB * dyCD)
        if (t >= 0 && t <= 1) return true

        return false
    }

    private fun iterateContains(lat: Double, lon: Double): Boolean {
        if (!mbr.contains(lat, lon)) return false

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

    override fun contains(lat: Double, lon: Double) = rTreeContains(lat, lon)
}
