@Suppress("MemberVisibilityCanBePrivate")
class BoundingBox(val xMin: Double, val xMax: Double, val yMin: Double, val yMax: Double) {
    fun contains(point: Point) = !(point.x < xMin || point.x > xMax || point.y < yMin || point.y > yMax)
}