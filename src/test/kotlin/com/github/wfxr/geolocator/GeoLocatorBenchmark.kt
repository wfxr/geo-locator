package com.github.wfxr.geolocator

import com.github.wfxr.geolocator.utils.loadDistrictsGaode
import org.apache.commons.lang3.Validate
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.*

@Suppress("unused_parameter")
internal class GeoLocatorBenchmark {
    companion object {
        val geoLocator: GeoLocator = GeoLocator(loadDistrictsGaode(Paths.get("scripts/districts")))
        private val rand = Random()

        @Suppress("unused")
        @JvmStatic
        fun geoRange() = listOf(
            Arguments.of("全球区域", Pair(-90.0, 90.0), Pair(-180.0, 180.0)),
            Arguments.of("中国外矩形区域", Pair(0.0, 73.0), Pair(55.0, 138.0)),
            Arguments.of("中国内部区域", Pair(30.0, 41.0), Pair(109.0, 119.0)))

        fun randomDouble(startInclusive: Double, endInclusive: Double): Double {
            Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.")
            return if (startInclusive == endInclusive) startInclusive
            else startInclusive + (endInclusive - startInclusive) * rand.nextDouble()
        }
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(1_000_000) {
            val lat = randomDouble(latRange.first, latRange.second)
            val lon = randomDouble(lonRange.first, lonRange.second)
            geoLocator.locate(WGSPoint(lat, lon))
        }
    }


    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchFastLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(1_000_000) {
            val lat = randomDouble(latRange.first, latRange.second)
            val lon = randomDouble(lonRange.first, lonRange.second)
            geoLocator.fastLocate(WGSPoint(lat, lon))
        }
    }
}