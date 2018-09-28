package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash
import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.wfxr.geolocator.serializers.BoundaryLazySerializer
import com.github.wfxr.geolocator.utils.GeoHashRange
import com.github.wfxr.geolocator.utils.intersects
import org.objenesis.strategy.StdInstantiatorStrategy
import java.io.InputStream
import java.io.OutputStream
import java.util.stream.Collectors


class HashingLocator(private val districts: List<District>, private val hashLevel: Int) : IGeoLocator {
    companion object {
        fun deserialize(inputStream: InputStream, parallel: Boolean = false): HashingLocator {
            val kryo = Kryo()
            kryo.instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())
            kryo.register(Boundary::class.java, BoundaryLazySerializer())
            val locator = kryo.readObject(Input(inputStream), HashingLocator::class.java)
            if (parallel) {
                locator.districts.parallelStream().forEach { it.boundary.init() }
            } else {
                locator.districts.forEach { it.boundary.init() }
            }
            return locator
        }
    }


    constructor(districts: List<District>) : this(districts, 4)

    private val mbr = BoundingBox(districts.minBy { it.mbr.minLat }!!.mbr.minLat,
                                  districts.maxBy { it.mbr.maxLat }!!.mbr.maxLat,
                                  districts.minBy { it.mbr.minLon }!!.mbr.minLon,
                                  districts.maxBy { it.mbr.maxLon }!!.mbr.maxLon)

    private val geoHashCache: Map<GeoHash, List<District>> = buildGeoHashCacheParallel(districts)

    private fun buildGeoHashCacheParallel(districts: List<District>): Map<GeoHash, List<District>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, hashLevel)
                .toList().parallelStream()
                .map { it to possibleDistricts(it, districts) }
                .filter { it.second.isNotEmpty() }
                .collect(Collectors.toMap({ it!!.first }, { it!!.second }))

    private fun buildGeoHashCache(districts: List<District>): Map<GeoHash, List<District>> =
            GeoHashRange(mbr.minLat, mbr.minLon, mbr.maxLat, mbr.maxLon, hashLevel)
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
        val geoHash = GeoHash.withCharacterPrecision(lat, lon, hashLevel)
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

fun HashingLocator.serialize(outputStream: OutputStream) {
    val kryo = Kryo()
    kryo.instantiatorStrategy = Kryo.DefaultInstantiatorStrategy(StdInstantiatorStrategy())
    kryo.register(Boundary::class.java, BoundaryLazySerializer())
    val output = Output(outputStream)
    kryo.writeObject(output, this)
    output.flush()
}
