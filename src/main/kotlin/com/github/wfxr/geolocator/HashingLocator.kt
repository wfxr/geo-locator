package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.wfxr.geolocator.serializers.RegionLazySerializer
import com.github.wfxr.geolocator.utils.GeoHashRange
import com.github.wfxr.geolocator.utils.intersects
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.InputStream
import java.io.OutputStream
import java.util.stream.Collectors


class HashingLocator<T>(private val regions: List<Region<T>>, private val hashLevel: Int) : IGeoLocator<T> {
    override fun locateAll(lat: Double, lon: Double): List<Region<T>> = possibleRegions(lat, lon).filter { it.contains(lat, lon) }

    constructor(districts: List<Region<T>>) : this(districts, 4)

    private val mbr = BoundingBox(regions.minBy { it.mbr.minLat }!!.mbr.minLat,
                                  regions.maxBy { it.mbr.maxLat }!!.mbr.maxLat,
                                  regions.minBy { it.mbr.minLon }!!.mbr.minLon,
                                  regions.maxBy { it.mbr.maxLon }!!.mbr.maxLon)

    private val geoHashCache: Map<GeoHash, List<Region<T>>> = buildGeoHashCacheParallel(regions)

    private fun buildGeoHashCacheParallel(districts: List<Region<T>>): Map<GeoHash, List<Region<T>>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, hashLevel)
                .toList().parallelStream()
                .map { it to possibleRegions(it, districts) }
                .filter { it.second.isNotEmpty() }
                .collect(Collectors.toMap({ it!!.first }, { it!!.second }))

    private fun buildGeoHashCache(districts: List<Region<T>>): Map<GeoHash, List<Region<T>>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, hashLevel)
                .map { it to possibleRegions(it, districts) }
                .filter { it.second.isNotEmpty() }
                .toMap()

    private fun possibleRegions(geoHash: GeoHash, districts: List<Region<T>>) =
            districts.filter { it.intersects(geoHash.boundingBox) }

    val stat: Stat = geoHashCache.values.let { candidates ->
        val sole = candidates.count { it.size == 1 }
        val max = candidates.maxBy { it.size }!!.size
        val all = candidates.sumBy { it.size }
        val avg = all / candidates.size.toDouble()
        Stat(sole, all, max, avg)
    }

    private fun possibleRegions(lat: Double, lon: Double): List<Region<T>> {
        val geoHash = GeoHash.withCharacterPrecision(lat, lon, hashLevel)
        return geoHashCache[geoHash] ?: listOf()
    }

    override fun locate(lat: Double, lon: Double) = possibleRegions(lat, lon).find { it.contains(lat, lon) }

    override fun fastLocate(lat: Double, lon: Double): Region<T>? {
        val candidates = possibleRegions(lat, lon)
        val last = candidates.size - 1
        candidates.forEachIndexed { i, item -> if (i == last || item.contains(lat, lon)) return item }
        return null
    }

    data class Stat(val sole: Int, val all: Int, val max: Int, val avg: Double)

    companion object {
        fun <T> deserialize(inputStream: InputStream, parallel: Boolean = false): HashingLocator<T> {
            val kryo = Kryo()
            kryo.instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())
            kryo.register(Region::class.java, RegionLazySerializer<T>())
            val locator = kryo.readObject(Input(inputStream), HashingLocator::class.java)
            if (parallel) {
                locator.regions.parallelStream().forEach { it.init() }
            } else {
                locator.regions.forEach { it.init() }
            }
            @Suppress("UNCHECKED_CAST")
            return locator as HashingLocator<T>
        }
    }

    fun serialize(outputStream: OutputStream) {
        val kryo = Kryo()
        kryo.instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())
        kryo.register(Region::class.java, RegionLazySerializer<T>())
        val output = Output(outputStream)
        kryo.writeObject(output, this)
        output.flush()
    }
}

