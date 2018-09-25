package com.github.wfxr.geolocator

import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.Geometries.point
import com.github.davidmoten.rtree.geometry.Rectangle
import com.github.davidmoten.rtree.internal.EntryDefault
import com.github.wfxr.geolocator.utils.toRectangle

class RTreeLocator(districts: List<District>) : IGeoLocator {
    private val tree: RTree<District, Rectangle> = RTree
        .star()
        .create<District, Rectangle>()
        .add(districts.map { EntryDefault(it, it.mbr.toRectangle()) })

    private fun possibleDistricts(lat: Double, lon: Double) =
            tree.search(point(lat, lon))
                .map { it.value() }
                .toBlocking()
                .toIterable()

    override fun locate(lat: Double, lon: Double) =
            possibleDistricts(lat, lon).find { it.contains(lat, lon) }

    override fun fastLocate(lat: Double, lon: Double): District? {
        val candidates = possibleDistricts(lat, lon).toList()
        var remain = candidates.size
        candidates.forEach { if (remain == 1 || it.contains(lat, lon)) return it else --remain }
        return null
    }
}