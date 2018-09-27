package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash
import com.github.wfxr.geolocator.utils.GeoHashRange
import com.github.wfxr.geolocator.utils.intersects
import java.util.stream.Collectors

class HashingLocator(districts: List<District>, private val precision: Int) : IGeoLocator {
    constructor(districts: List<District>) : this(districts, 4)

    private val mbr = BoundingBox(districts.minBy { it.mbr.minLat }!!.mbr.minLat,
                                   districts.maxBy { it.mbr.maxLat }!!.mbr.maxLat,
                                   districts.minBy { it.mbr.minLon }!!.mbr.minLon,
                                   districts.maxBy { it.mbr.maxLon }!!.mbr.maxLon)

    private val geoHashCache: Map<GeoHash, List<District>> = buildGeoHashCacheParallel(districts)

    private fun buildGeoHashCacheParallel(districts: List<District>): Map<GeoHash, List<District>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, precision)
                .toList().parallelStream()
                .map { it to possibleDistricts(it, districts) }
                .filter { it.second.isNotEmpty() }
                .collect(Collectors.toMap({ it!!.first }, { it!!.second }))

    private fun buildGeoHashCache(districts: List<District>): Map<GeoHash, List<District>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, precision)
                .map { it to possibleDistricts(it, districts) }
                .filter { it.second.isNotEmpty() }
                .toMap()

    private fun possibleDistricts(geoHash: GeoHash, districts: List<District>) =
            districts.filter { it.intersects(geoHash.boundingBox) }

    val stat: Stat = geoHashCache.values.let { candidates ->
        val sole = candidates.count { it.size == 1 }
        val max = candidates.maxBy { it.size }!!.size
        val all = candidates.sumBy { it.size }
        val avg = all / candidates.size.toDouble()
        Stat(sole, all, max, avg)
    }

    private fun possibleDistricts(lat: Double, lon: Double): List<District> {
        val geoHash = GeoHash.withCharacterPrecision(lat, lon, precision)
        return geoHashCache[geoHash] ?: listOf()
    }

    override fun locate(lat: Double, lon: Double) = possibleDistricts(lat, lon).find { it.contains(lat, lon) }

    override fun fastLocate(lat: Double, lon: Double): District? {
        val candidates = possibleDistricts(lat, lon)
        var remain = candidates.size
        candidates.forEach { if (remain == 1 || it.contains(lat, lon)) return it else --remain }
        return null
    }

    data class Stat(val sole: Int, val all: Int, val max: Int, val avg: Double)
}
