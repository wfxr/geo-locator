open class Point(val x: Double, val y: Double)

class WGSPoint(lat: Double, lon: Double) : Point(lat, lon) {
    val lat get() = x
    val lon get() = y
}
