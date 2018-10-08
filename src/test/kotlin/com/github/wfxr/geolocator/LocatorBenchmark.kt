package com.github.wfxr.geolocator

import com.github.wfxr.geolocator.utils.loadDistrictsParallel
import org.apache.commons.lang3.Validate
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

internal abstract class LocatorBenchmarkBase : TestBase() {
    companion object {
        private const val COUNT = 100_000_0
        private val rand = Random(COUNT.toLong())

        @Suppress("unused")
        @JvmStatic
        fun geoRange() = listOf(
            Arguments.of("全球区域", Pair(-90.0, 90.0), Pair(-180.0, 180.0)),
            Arguments.of("中国外矩形区域", Pair(0.0, 73.0), Pair(55.0, 138.0)),
            Arguments.of("中国内部区域A", Pair(30.0, 41.0), Pair(109.0, 119.0)),
            Arguments.of("中国内部区域B", Pair(26.0, 39.0), Pair(100.0, 116.0)))

        private fun randDouble(startInclusive: Double, endInclusive: Double): Double {
            Validate.isTrue(endInclusive >= startInclusive, "Start value must be smaller or equal to end value.")
            return if (startInclusive == endInclusive) startInclusive
            else startInclusive + (endInclusive - startInclusive) * rand.nextDouble()
        }

        private fun randDoubles(from: Double, to: Double, count: Int) =
                DoubleArray(count) { randDouble(from, to) }

        private fun randPoints(latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>, count: Int) =
                Pair(randDoubles(latRange.first, latRange.second, count), randDoubles(lonRange.first, lonRange.second, count))

        val pool = Executors.newFixedThreadPool(4)!!

        private fun bench(remark: String, action: () -> Unit) {
            val time = measureTimeMillis(action)
            println("${time}ms, ${"%.2f/s".format(COUNT * 1000.0 / time)} - $remark")
        }

    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        val (lats, lons) = randPoints(latRange, lonRange, COUNT)
        bench(remark) {
            (0 until COUNT).forEach { geoLocator.locate(lats[it], lons[it]) }
        }
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchFastLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        val (lats, lons) = randPoints(latRange, lonRange, COUNT)
        bench(remark) {
            (0 until COUNT).forEach { geoLocator.fastLocate(lats[it], lons[it]) }
        }
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchConcurrentFastLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        val groupSize = COUNT / CONCURRENCY
        val groups = (1..CONCURRENCY).map { randPoints(latRange, lonRange, groupSize) }

        println("Concurrency: $CONCURRENCY")
        bench(remark) {
            val latch = CountDownLatch(CONCURRENCY)
            groups.forEach { (lats, lons) ->
                pool.execute {
                    (0 until groupSize).forEach { geoLocator.fastLocate(lats[it], lons[it]) }
                    latch.countDown()
                }
            }
            latch.await()
        }
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchConcurrentLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        val groupSize = COUNT / CONCURRENCY
        val groups = (1..CONCURRENCY).map { randPoints(latRange, lonRange, groupSize) }

        println("Concurrency: $CONCURRENCY")
        bench(remark) {
            val latch = CountDownLatch(CONCURRENCY)
            groups.forEach { (lats, lons) ->
                pool.execute {
                    (0 until groupSize).forEach { geoLocator.locate(lats[it], lons[it]) }
                    latch.countDown()
                }
            }
            latch.await()
        }
    }
}

internal class HashingLocatorBenchmark : LocatorBenchmarkBase() {
    companion object {
        private const val LEVEL = 5
        private val path = Paths.get("scripts/hashing-locator-level-$LEVEL.dat")
        val GeoLocator = HashingLocator.deserialize(Files.newInputStream(path), true)
    }

    override val geoLocator = GeoLocator
}

internal class RTreeLocatorBenchmark : LocatorBenchmarkBase() {
    companion object {
        val GeoLocator = RTreeLocator(loadDistrictsParallel(Paths.get("scripts/districts")))
    }
    override val geoLocator = GeoLocator
}
