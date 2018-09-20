package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.Geometries
import com.github.davidmoten.rtree.geometry.Line
import com.github.wfxr.geolocator.utils.contains

interface IBoundary {
    val bBox: BoundingBox
    fun contains(lat: Double, lon: Double): Boolean
}

data class Boundary(private val V: List<WGSPoint>) : IBoundary {
    override val bBox: BoundingBox
    private val tree: RTree<Unit, Line>

    init {
        val xMin = V.minBy { it.x }?.x ?: 0.0
        val xMax = V.maxBy { it.x }?.x ?: 0.0
        val yMin = V.minBy { it.y }?.y ?: 0.0
        val yMax = V.maxBy { it.y }?.y ?: 0.0
        bBox = BoundingBox(xMin, xMax, yMin, yMax)

        var rtree = RTree
            .maxChildren(5)
            .loadingFactor(0.4)
            .create<Unit, Line>()
        V.zipWithNext { a, b ->
            rtree = rtree.add(Unit, Geometries.line(a.lat, a.lon, b.lat, b.lon))
        }
        tree = rtree
    }

    private fun rTreeContains(lat: Double, lon: Double): Boolean {
        if (!bBox.contains(lat, lon)) return false
        val line = Geometries.line(lat, lon, lat + 0.000001, bBox.maxLon)
        var count = 0
        tree.search(line).forEach { ++count }
        return count % 2 == 1
    }

    private fun iterateContains(lat: Double, lon: Double): Boolean {
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

    override fun contains(lat: Double, lon: Double) = rTreeContains(lat, lon)
}
