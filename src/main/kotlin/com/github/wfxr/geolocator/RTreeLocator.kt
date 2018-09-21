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

    override fun locate(lat: Double, lon: Double): District? {
        return tree.search(point(lat, lon))
            .map { it.value() }
            .toBlocking().toIterable()
            .find { it.contains(lat, lon) }
    }

    override fun fastLocate(lat: Double, lon: Double): District? {
        val candidates = tree.search(point(lat, lon))
            .map { it.value() }
            .toBlocking().toIterable().toList()
        if (candidates.size == 1) return candidates.first()
        return candidates.find { it.contains(lat, lon) }
    }
}