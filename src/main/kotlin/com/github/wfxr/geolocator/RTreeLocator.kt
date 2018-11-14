package com.github.wfxr.geolocator

import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.Geometries.point
import com.github.davidmoten.rtree.geometry.Rectangle
import com.github.davidmoten.rtree.internal.EntryDefault
import com.github.wfxr.geolocator.utils.toRectangle

class RTreeLocator(districts: List<Region>) : IGeoLocator {
    private val tree: RTree<Region, Rectangle> = RTree
        .star()
        .create<Region, Rectangle>()
        .add(districts.map { EntryDefault(it, it.mbr.toRectangle()) })

    private fun possibleRegions(lat: Double, lon: Double) =
            tree.search(point(lat, lon))
                .map { it.value() }
                .toBlocking()
                .toIterable()

    override fun locate(lat: Double, lon: Double) =
            possibleRegions(lat, lon).find { it.contains(lat, lon) }

    override fun fastLocate(lat: Double, lon: Double): Region? {
        val candidates = possibleRegions(lat, lon).toList()
        var remain = candidates.size
        candidates.forEach { if (remain == 1 || it.contains(lat, lon)) return it else --remain }
        return null
    }
}