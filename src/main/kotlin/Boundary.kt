import ch.hsr.geohash.BoundingBox

class Boundary(private val regions: List<List<Point>>) {
    val bBox: BoundingBox = regions.let { vertices ->
        val xMin = vertices.flatMap { it }.minBy { it.x }?.x ?: 0.0
        val xMax = vertices.flatMap { it }.maxBy { it.x }?.x ?: 0.0
        val yMin = vertices.flatMap { it }.minBy { it.y }?.y ?: 0.0
        val yMax = vertices.flatMap { it }.maxBy { it.y }?.y ?: 0.0
        BoundingBox(xMin, xMax, yMin, yMax)
    }

    fun contains(p: WGSPoint): Boolean {
        if (!bBox.contains(p)) return false

        regions.forEach { V ->
            var res = false
            var i = 0
            var j = V.size - 1
            while (i < V.size) {
                if (V[i].y > p.y != V[j].y > p.y && p.x < (V[j].x - V[i].x) * (p.y - V[i].y) / (V[j].y - V[i].y) + V[i].x) {
                    res = !res
                }
                j = i++
            }
            if (res) return res
        }

        return false
    }
}
