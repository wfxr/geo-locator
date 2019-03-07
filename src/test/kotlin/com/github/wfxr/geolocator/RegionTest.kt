package com.github.wfxr.geolocator

import ch.hsr.geohash.GeoHash
import com.github.wfxr.geolocator.utils.GeoHashRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RegionTest {
    @Test
    fun testNonClosedCurve() {
        val exception = assertThrows<IllegalArgumentException> {
            Region(listOf(WGSPoint(0.0, 0.0), WGSPoint(1.0, 1.0)), Unit)
        }
        assertEquals("region curve not closed", exception.message)
    }
}

internal class GeoHashRangeTest {
    @Test
    fun testSingleHash() {
        val p = GeoHash.withCharacterPrecision(0.0, 0.0, 3)
        val range = GeoHashRange(p, p)
        val hashes = range.toList()
        assertEquals(1, hashes.size)
        assertEquals(p, hashes.first())
    }
}

