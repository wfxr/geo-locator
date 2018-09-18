package com.github.wfxr.geolocator

import com.github.davidmoten.rtree.RTree
import com.github.davidmoten.rtree.geometry.Geometries
import com.github.davidmoten.rtree.geometry.Geometry

class RTreeLocator(districts: List<District>) : IGeoLocator {
    private val tree: RTree<District, Geometry> = districts.let { districts ->
        var rtree = RTree.star().create<District, Geometry>()
        districts.forEach { it ->
            val lat1 = it.bBox.minLat
            val lon1 = it.bBox.minLon
            val lat2 = it.bBox.maxLat
            val lon2 = it.bBox.maxLon
            rtree = rtree.add(it, Geometries.rectangle(lat1, lon1, lat2, lon2))
        }
        rtree!!
    }

    override fun locate(lat: Double, lon: Double): District? {
        return tree.search(Geometries.point(lat, lon))
            .map { it.value() }
            .toBlocking().toIterable()
            .find { it.contains(lat, lon) }
    }

    override fun fastLocate(lat: Double, lon: Double): District? {
        val candidates = tree.search(Geometries.point(lat, lon))
            .map { it.value() }
            .toBlocking().toIterable().toList()
        if (candidates.size == 1) return candidates.first()
        return candidates.find { it.contains(lat, lon) }
    }
}