import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val districts = gaodeDistrictsLoader(Paths.get("/home/wenxuan/work/boundary/region"))
    val geoLookuper = GeoLookuper(districts)

    val count = AtomicInteger(0)
    val rand = Random()
    val time = measureTimeMillis {
        IntStream.range(0, 8).parallel().forEach {
            repeat(100_000) {
                val lon = rand.nextDouble() * 73
                val lat = rand.nextDouble() * 83 + 55
                val district = geoLookuper.locateAdcode(Point(lat, lon))
                if (district != null) count.incrementAndGet()
            }
        }
    }

    println("ms: $time\ncount: $count")
}