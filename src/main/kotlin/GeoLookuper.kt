import ch.hsr.geohash.BoundingBox
import ch.hsr.geohash.GeoHash

class GeoLookuper(districts: List<District>, private val precision: Int = 4) {
    private val bbox = BoundingBox(districts.minBy { it.bBox.minLat }!!.bBox.minLat,
                                   districts.maxBy { it.bBox.maxLat }!!.bBox.maxLat,
                                   districts.minBy { it.bBox.minLon }!!.bBox.minLon,
                                   districts.maxBy { it.bBox.maxLon }!!.bBox.maxLon)

    private val geoHashMapping: Map<GeoHash, List<District>> = HashMap<GeoHash, MutableList<District>>().let { rs ->
        val cornerSW = GeoHash.withCharacterPrecision(bbox.minLat, bbox.minLon, precision)
        val cornerNE = GeoHash.withCharacterPrecision(bbox.maxLat, bbox.maxLon, precision)
        val cornerSE = GeoHash.withCharacterPrecision(bbox.minLat, bbox.maxLon, precision)

        var geoIter = cornerSW
        var beg = cornerSW
        var end = cornerSE
        while (true) {
            districts
                .filter { it.bBox.intersects(geoIter.boundingBox) }
                .takeIf { it.isNotEmpty() }
                ?.toMutableList()?.let { it ->
                    rs.merge(geoIter, it) { old, new ->
                        old.addAll(new).let { old }
                    }
                }

            if (geoIter != end) {
                geoIter = geoIter.easternNeighbour
            } else {
                if (end == cornerNE) break
                beg = beg.northernNeighbour
                end = end.northernNeighbour
                geoIter = beg
            }
        }
        rs
    }

    private fun possibleDistricts(p: WGSPoint): List<District> {
        val geoHash = GeoHash.withCharacterPrecision(p.lat, p.lon, precision)
        return geoHashMapping[geoHash] ?: listOf()
    }

    fun locate(p: WGSPoint) = possibleDistricts(p).find { it.boundary.contains(p) }

    fun locateFast(p: WGSPoint): District? {
        val candidates = possibleDistricts(p)
        if (candidates.size == 1) return candidates.first()
        return candidates.find { it.boundary.contains(p) }
    }
}

