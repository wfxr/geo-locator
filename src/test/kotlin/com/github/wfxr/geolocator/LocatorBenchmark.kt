package com.github.wfxr.geolocator

import org.apache.commons.lang3.Validate
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*

internal abstract class LocatorBenchmarkBase : TestBase() {
    companion object {
        private const val COUNT = 100_000_0
        private val rand = Random(COUNT.toLong())

        @Suppress("unused")
        @JvmStatic
        fun geoRange() = listOf(
            Arguments.of("全球区域", Pair(-90.0, 90.0), Pair(-180.0, 180.0)),
            Arguments.of("中国外矩形区域", Pair(0.0, 73.0), Pair(55.0, 138.0)),
            Arguments.of("中国内部区域1", Pair(30.0, 41.0), Pair(109.0, 119.0)),
            Arguments.of("中国内部区域2", Pair(26.0, 39.0), Pair(100.0, 116.0)))

        fun randomDouble(startInclusive: Double, endInclusive: Double): Double {
            Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.")
            return if (startInclusive == endInclusive) startInclusive
            else startInclusive + (endInclusive - startInclusive) * rand.nextDouble()
        }
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(COUNT) {
            val lat = randomDouble(latRange.first, latRange.second)
            val lon = randomDouble(lonRange.first, lonRange.second)
            geoLocator.locate(lat, lon)
        }
    }


    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchFastLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(COUNT) {
            val lat = randomDouble(latRange.first, latRange.second)
            val lon = randomDouble(lonRange.first, lonRange.second)
            geoLocator.fastLocate(lat, lon)
        }
    }
}

internal class HashingLocatorBenchmark : LocatorBenchmarkBase() {
    companion object {
        val GeoLocator = HashingLocator(districts, 4)
    }

    override val geoLocator = GeoLocator
}

internal class RTreeLocatorBenchmark : LocatorBenchmarkBase() {
    companion object {
        val GeoLocator = RTreeLocator(districts)
    }

    override val geoLocator = GeoLocator
}
