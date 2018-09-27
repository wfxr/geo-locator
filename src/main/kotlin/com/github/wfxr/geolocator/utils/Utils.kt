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
import java.nio.file.Path
import kotlin.streams.toList

fun loadDistrictsGaode(path: Path) =
        path.toFile()
            .walk()
            .filter { it.isFile }
            .map(::loadDistrictGaode)
            .flatten()
            .toList()

fun loadDistrictsGaodeParallel(path: Path) =
        path.toFile()
            .walk()
            .filter { it.isFile }
            .toList().parallelStream()
            .map(::loadDistrictGaode)
            .flatMap { it.stream() }
            .toList()

private fun loadDistrictGaode(file: File) =
        try {
            val root = JsonParser().parse(file.bufferedReader()).obj
            val adcode = root["adcode"].string.toInt()
            val name = root["name"].string.trim()
            val center = root["center"].string.parseAsPointOrNull()
            val regions = root["polyline"].string.split("|").map { it.parseAsPoints() }
            regions.map { District(adcode, name, center, Boundary(it)) }
        } catch (e: Exception) {
            println(file.name)
            throw e
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
