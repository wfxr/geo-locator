import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.*

@Suppress("unused_parameter")
internal class GeoLocatorBenchmark {
    companion object {
        val rand = Random()
        val geoLocator: GeoLocator = GeoLocator(loadDistrictsGaode(Paths.get("/home/wenxuan/work/boundary/region")))
        @Suppress("unused")
        @JvmStatic
        fun geoRange() = listOf(
            Arguments.of("全球区域", Pair(-90.0, 90.0), Pair(-180.0, 180.0)),
            Arguments.of("中国外矩形区域", Pair(0.0, 73.0), Pair(55.0, 138.0)),
            Arguments.of("中国内部区域", Pair(30.0, 41.0), Pair(109.0, 119.0)))
    }

    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(1_000_000) {
            val lat = rand.nextDouble() * (latRange.second - latRange.first) + latRange.first
            val lon = rand.nextDouble() * (lonRange.second - lonRange.first) + lonRange.first
            geoLocator.locate(WGSPoint(lat, lon))
        }
    }


    @ParameterizedTest
    @MethodSource("geoRange")
    fun benchFastLocate(remark: String, latRange: Pair<Double, Double>, lonRange: Pair<Double, Double>) {
        repeat(1_000_000) {
            val lat = rand.nextDouble() * (latRange.second - latRange.first) + latRange.first
            val lon = rand.nextDouble() * (lonRange.second - lonRange.first) + lonRange.first
            geoLocator.fastLocate(WGSPoint(lat, lon))
        }
    }
}