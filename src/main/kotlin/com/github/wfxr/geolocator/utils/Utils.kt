package com.github.wfxr.geolocator.utils

import ch.hsr.geohash.BoundingBox
import com.github.davidmoten.rtree.geometry.Geometries.rectangle
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.github.wfxr.geolocator.Boundary
import com.github.wfxr.geolocator.District
import com.github.wfxr.geolocator.WGSPoint
import com.google.gson.JsonParser
import java.io.Reader
import java.nio.file.Path
import kotlin.streams.toList

fun loadDistricts(items: List<String>) =
        items.map { loadDistrict(it.reader().buffered()) }
            .flatten()

fun loadDistrictsParallel(items: List<String>): List<District> =
        items.parallelStream()
            .map { loadDistrict(it.reader().buffered()) }
            .flatMap { it.stream() }
            .toList()

fun loadDistricts(path: Path) =
        path.toFile()
            .walk()
            .filter { it.isFile }
            .map { loadDistrict(it.bufferedReader()) }
            .flatten()
            .toList()

fun loadDistrictsParallel(path: Path): List<District> =
        path.toFile()
            .walk()
            .filter { it.isFile }
            .toList().parallelStream()
            .map { loadDistrict(it.bufferedReader()) }
            .flatMap { it.stream() }
            .toList()

private fun loadDistrict(reader: Reader): List<District> {
    val root = JsonParser().parse(reader).obj
    val adcode = root["adcode"].string.toInt()
    val name = root["name"].string.trim()
    val center = root["center"].string.parseAsPointOrNull()
    val regions = root["polyline"].string.split("|").map { it.parseAsPoints() }
    return regions.map { District(adcode, name, center, Boundary(it)) }
}

private fun String.parseAsPoint() = this.split(",").map { it.toDouble() }.let { WGSPoint(it[1], it[0]) }
private fun String.parseAsPoints() = this.split(";").map { it.parseAsPoint() }
private fun String.parseAsPointOrNull() = try {
    this.split(",").map { it.toDouble() }.let { WGSPoint(it[1], it[0]) }
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
