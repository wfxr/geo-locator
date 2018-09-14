package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash
import com.github.wfxr.geolocator.utils.GeoHashRange

class GeoLocator(districts: List<District>, private val precision: Int = 4) {
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

    private fun possibleDistricts(p: WGSPoint): List<District> {
        val geoHash = GeoHash.withCharacterPrecision(p.lat, p.lon, precision)
        return geoHashMapping[geoHash] ?: listOf()
    }

    fun locate(p: WGSPoint) = possibleDistricts(p).find { it.contains(p) }

    fun fastLocate(p: WGSPoint): District? {
        val candidates = possibleDistricts(p)
        if (candidates.size == 1) return candidates.first()
        return candidates.find { it.contains(p) }
    }

    fun locate(lat: Double, lon: Double) = locate(WGSPoint(lat, lon))
    fun fastLocate(lat: Double, lon: Double) = fastLocate(WGSPoint(lat, lon))

    data class Stat(val sole: Int, val all: Int, val max: Int, val avg: Double)
}
