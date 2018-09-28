package com.github.wfxr.geolocator.utils

import ch.hsr.geohash.BoundingBox
import com.github.davidmoten.rtree.geometry.Geometries.rectangle
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.github.wfxr.geolocator.Boundary
import com.github.wfxr.geolocator.District
import com.github.wfxr.geolocator.WGSPoint
import com.google.gson.JsonParser
import java.io.File
import java.io.Reader
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.streams.asStream
import kotlin.streams.toList

fun loadDistricts(path: Path) = loadDistricts(path.toFile())
fun loadDistrictsParallel(path: Path) = loadDistrictsParallel(path.toFile())

fun loadDistricts(items: List<String>) =
        loadDistrictsFromReaders(items.stream().map { it.reader() })

fun loadDistrictsParallel(items: List<String>) =
        loadDistrictsFromReaders(items.parallelStream().map { it.reader() })

fun loadDistricts(file: File): List<District> {
    require(file.exists()) { "File($file) not exist" }
    val readers = file.walk().asStream().filter { it.isFile }.map { it.reader() }
    return loadDistrictsFromReaders(readers)
}

fun loadDistrictsParallel(file: File): List<District> {
    require(file.exists()) { "File($file) not exist" }
    val readers = file.walk().toList().parallelStream().filter { it.isFile }.map { it.reader() }
    return loadDistrictsFromReaders(readers)
}

fun loadDistrictsFromReaders(readers: Stream<out Reader>) =
        readers.flatMap { loadDistrict(it).stream() }.toList()

private fun loadDistrict(reader: Reader): List<District> {
    val root = JsonParser().parse(reader.buffered()).obj
    val adcode = root["adcode"].string.toInt()
    val name = root["name"].string.trim()
    val center = root["center"].string.tryParseAsPoint()
    val regions = root["polyline"].string.split("|").map { it.parseAsPoints() }
    return regions.map { District(adcode, name, center, Boundary(it)) }
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

fun BoundingBox.vertexIn(district: District) =
        district.contains(minLat, minLon) ||
        district.contains(minLat, maxLon) ||
        district.contains(maxLat, maxLon) ||
        district.contains(maxLat, minLon)

fun District.vertexIn(mbr: BoundingBox) = vertexes.any { mbr.contains(it.lat, it.lon) }

fun District.intersects(box: BoundingBox) =
        box.intersects(this.mbr) && (box.vertexIn(this) || this.vertexIn(box))
