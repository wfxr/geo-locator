package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.Geometries.line
import com.github.davidmoten.rtree.geometry.Line
import com.github.davidmoten.rtree.internal.EntryDefault
import com.github.wfxr.geolocator.utils.contains

interface IBoundary {
    val mbr: BoundingBox
    val vertexes: List<WGSPoint>
    fun contains(lat: Double, lon: Double): Boolean
}

data class Boundary(private val V: List<WGSPoint>) : IBoundary {
    override val mbr: BoundingBox
    private val tree: RTree<Unit, Line>
    override val vertexes get() = V

    init {
        val xMin = V.minBy { it.x }?.x ?: 0.0
        val xMax = V.maxBy { it.x }?.x ?: 0.0
        val yMin = V.minBy { it.y }?.y ?: 0.0
        val yMax = V.maxBy { it.y }?.y ?: 0.0
        mbr = BoundingBox(xMin, xMax, yMin, yMax)

        tree = RTree
            .maxChildren(5)
            .loadingFactor(0.4)
            .create<Unit, Line>()
            .add(V.zipWithNext { a, b -> EntryDefault(Unit, line(a.lat, a.lon, b.lat, b.lon)) })
    }

    private fun rTreeContains(lat: Double, lon: Double): Boolean {
        if (!mbr.contains(lat, lon)) return false
        val line = line(lat, lon, lat + 0.000001, mbr.maxLon)
        var count = 0
        tree.search(line).forEach { ++count }
        return count % 2 == 1
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
