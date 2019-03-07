package com.github.wfxr.geolocator.utils

import ch.hsr.geohash.GeoHash
import org.apache.commons.lang3.Validate

class GeoHashRange(private val SW: GeoHash, private val NE: GeoHash) : ClosedRange<GeoHash>, Iterable<GeoHash> {
    init {
        Validate.isTrue(SW.characterPrecision == NE.characterPrecision,
                        "Precision for range must be equal")
        Validate.isTrue(SW.point.latitude <= NE.point.latitude && SW.point.longitude <= NE.point.longitude,
                        "SW should not be norther or easter than NE")
    }

    constructor(minLat: Double, minLon: Double, maxLat: Double, maxLon: Double, characterPrecision: Int) :
            this(GeoHash.withCharacterPrecision(minLat, minLon, characterPrecision),
                 GeoHash.withCharacterPrecision(maxLat, maxLon, characterPrecision))

    override val start: GeoHash get() = SW
    override val endInclusive: GeoHash get() = NE
    override fun iterator() = GeoHashIterator(start, endInclusive)
}

class GeoHashIterator(SW: GeoHash, NE: GeoHash) : Iterator<GeoHash> {
    private var currStart = SW
    private var curr = currStart
    private val end = NE.easternNeighbour

    override fun hasNext() = curr != end

    override fun next(): GeoHash {
        val rs = curr
        curr = if (curr.point.longitude >= end.point.longitude) {
            currStart = currStart.northernNeighbour
            currStart
        } else
            curr.easternNeighbour
        return rs
    }
}
