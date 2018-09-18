package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash
import com.github.wfxr.geolocator.utils.GeoHashRange

class HashingLocator(districts: List<District>, private val precision: Int = 4) : IGeoLocator {
    private val bbox = BoundingBox(districts.minBy { it.bBox.minLat }!!.bBox.minLat,
                                   districts.maxBy { it.bBox.maxLat }!!.bBox.maxLat,
                                   districts.minBy { it.bBox.minLon }!!.bBox.minLon,
                                   districts.maxBy { it.bBox.maxLon }!!.bBox.maxLon)

    private val geoHashMapping: Map<GeoHash, List<District>> =
            GeoHashRange(bbox.minLat, bbox.minLon, bbox.maxLat, bbox.maxLon, precision).mapNotNull { geoHash ->
                districts
                    .filter { it.bBox.intersects(geoHash.boundingBox) }
                    .takeIf { it.isNotEmpty() }
                    ?.let { geoHash to it }
            }.toMap()

    val stat: Stat = geoHashMapping.values.let { candidates ->
        val sole = candidates.count { it.size == 1 }
        val max = candidates.maxBy { it.size }!!.size
        val all = candidates.sumBy { it.size }
        val avg = all / candidates.size.toDouble()
        Stat(sole, all, max, avg)
    }

    private fun possibleDistricts(lat: Double, lon: Double): List<District> {
        val geoHash = GeoHash.withCharacterPrecision(lat, lon, precision)
        return geoHashMapping[geoHash] ?: listOf()
    }

    override fun locate(lat: Double, lon: Double) = possibleDistricts(lat, lon).find { it.contains(lat, lon) }

    override fun fastLocate(lat: Double, lon: Double): District? {
        val candidates = possibleDistricts(lat, lon)
        if (candidates.size == 1) return candidates.first()
        return candidates.find { it.contains(lat, lon) }
    }

    data class Stat(val sole: Int, val all: Int, val max: Int, val avg: Double)
}
