import ch.hsr.geohash.BoundingBox
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import com.google.gson.JsonParser
import java.nio.file.Path

fun loadDistrictsGaode(path: Path) =
        path.toFile().walk().filter { it.isFile }.map { file ->
            try {
                val root = JsonParser().parse(file.bufferedReader()).obj
                val adcode = root["adcode"].string.trim()
                val name = root["name"].string.trim()
                val center = root["center"].string.parseAsPointOrNull()
                val regions = root["polyline"].string.split("|").map { it.parseAsPoints() }
                regions.map {
                    val boundary = Boundary(it)
                    District(adcode, name, center, boundary)
                }
            } catch (e: Exception) {
                println(file.name)
                throw e
            }
        }.flatten().toList()

private fun String.parseAsPoint() = this.split(",").map { it.toDouble() }.let { Point(it[1], it[0]) }
private fun String.parseAsPoints() = this.split(";").map { it.parseAsPoint() }
private fun String.parseAsPointOrNull() = try {
    this.split(",").map { it.toDouble() }.let { Point(it[1], it[0]) }
} catch (e: Exception) {
    null
}

fun BoundingBox.contains(p: WGSPoint) =
        p.lat >= minLat && p.lon >= minLon && p.lat <= maxLat && p.lon <= maxLon

@Suppress("unused")
private fun distHaversineDEG(a: Point, b: Point) = distHaversineDEG(a.x, a.y, b.x, b.y)

private fun distHaversineDEG(latA: Double, lonA: Double, latB: Double, lonB: Double) =
        distHaversineRAD(toRAD(latA), toRAD(lonA), toRAD(latB), toRAD(lonB))

private fun distHaversineRAD(latA: Double, lonA: Double, latB: Double, lonB: Double): Double {
    if (latA == latB && lonA == lonB) return 0.0
    val hsinX = Math.sin((lonA - lonB) * 0.5)
    val hsinY = Math.sin((latA - latB) * 0.5)
    var h = hsinY * hsinY + Math.cos(latA) * Math.cos(latB) * hsinX * hsinX
    if (h > 1) h = 1.0
    return 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h))
}

private fun toRAD(degrees: Double) = degrees * DEG_PER_RAD

private const val DEG_PER_RAD = Math.PI / 180
