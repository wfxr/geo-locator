import ch.hsr.geohash.BoundingBox

class Boundary(private val regions: List<Point>) {
    val bBox: BoundingBox = regions.let { vertices ->
        val xMin = vertices.minBy { it.x }?.x ?: 0.0
        val xMax = vertices.maxBy { it.x }?.x ?: 0.0
        val yMin = vertices.minBy { it.y }?.y ?: 0.0
        val yMax = vertices.maxBy { it.y }?.y ?: 0.0
        BoundingBox(xMin, xMax, yMin, yMax)
    }

    fun contains(p: WGSPoint): Boolean {
        if (!bBox.contains(p)) return false

        var res = false
        var i = 0
        var j = regions.size - 1
        while (i < regions.size) {
            if (regions[i].y > p.y != regions[j].y > p.y && p.x < (regions[j].x - regions[i].x) * (p.y - regions[i].y) / (regions[j].y - regions[i].y) + regions[i].x) {
                res = !res
            }
            j = i++
        }
        return res
    }
}