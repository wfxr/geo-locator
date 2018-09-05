import ch.hsr.geohash.BoundingBox

data class District(val adcode: String, val name: String, val center: Point?, val boundary: Boundary) {
    val bBox: BoundingBox get() = boundary.bBox
}
