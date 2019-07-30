package com.github.wfxr.geolocator.utils

import ch.hsr.geohash.BoundingBox
import com.github.davidmoten.rtree.geometry.Geometries.rectangle
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.github.wfxr.geolocator.AdTag
import com.github.wfxr.geolocator.Region
import com.github.wfxr.geolocator.WGSPoint
import com.google.gson.JsonParser
import java.io.File
import java.io.Reader
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.streams.asStream
import kotlin.streams.toList

fun loadRegions(path: Path) = loadRegions(path.toFile())
fun loadRegionsParallel(path: Path) = loadRegionsParallel(path.toFile())

fun loadRegions(items: List<String>) =
        loadRegionsFromReaders(items.stream().map { it.reader() })

fun loadRegionsParallel(items: List<String>) =
        loadRegionsFromReaders(items.parallelStream().map { it.reader() })

fun loadRegions(file: File): List<Region<AdTag>> {
    require(file.exists()) { "File($file) not exist" }
    val readers = file.walk().asStream().filter { it.isFile }.map { it.reader() }
    return loadRegionsFromReaders(readers)
}

fun loadRegionsParallel(file: File): List<Region<AdTag>> {
    require(file.exists()) { "File($file) not exist" }
    val readers = file.walk().toList().parallelStream().filter { it.isFile }.map { it.reader() }
    return loadRegionsFromReaders(readers)
}

fun loadRegionsFromReaders(readers: Stream<out Reader>): List<Region<AdTag>> =
        readers.flatMap { loadRegion(it).stream() }.toList()

private fun loadRegion(reader: Reader): List<Region<AdTag>> {
    val root = JsonParser().parse(reader.buffered()).obj
    val adcode = root["adcode"].string.toInt()
    val name = root["name"].string.trim()
    val center = root["center"].string.tryParseAsPoint()
    val boundaries = root["polyline"].string.split("|").map { it.parseAsPoints() }
    return boundaries.map { Region(it, AdTag(adcode, name, center)) }
}

private fun String.parseAsPoints() = split(";").map { it.parseAsPoint() }
private fun String.parseAsPoint() = split(",").map { it.toDouble() }.let { (lon, lat) -> WGSPoint(lat, lon) }
private fun String.tryParseAsPoint() = try {
    parseAsPoint()
} catch (e: Exception) {
    null
}

fun BoundingBox.toRectangle() = rectangle(minLat, minLon, maxLat, maxLon)!!

fun BoundingBox.contains(lat: Double, lon: Double) =
        lat >= minLat && lon >= minLon && lat <= maxLat && lon <= maxLon

fun BoundingBox.vertexIn(district: Region<*>) =
        district.contains(minLat, minLon) ||
        district.contains(minLat, maxLon) ||
        district.contains(maxLat, maxLon) ||
        district.contains(maxLat, minLon)

fun Region<*>.vertexIn(mbr: BoundingBox) = vertexes.any { mbr.contains(it.lat, it.lon) }

fun Region<*>.intersects(box: BoundingBox) = box.intersects(this.mbr)
