import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    {
        val districts = gaodeDistrictsLoader(Paths.get("/home/wenxuan/work/boundary/region"))
        val geoLookuper = GeoLookuper(districts)
        println(geoLookuper.locate(WGSPoint(36.8092847021, 103.4912109375))) // 中国甘肃省永登县
        println(geoLookuper.locate(WGSPoint(30.7135039904, 101.0302734375))) // 中国四川省甘孜藏族自治州道孚县
        println(geoLookuper.locate(WGSPoint(46.2558468185, 126.6064453125))) // 中国黑龙江省绥化市兰西县
        println(geoLookuper.locate(WGSPoint(35.2456190942, 81.2329101563)))  // 中国西藏自治区阿里地区日土县

        val count = AtomicInteger(0)
        val rand = Random()
        val time = measureTimeMillis {
            IntStream.range(0, 8).parallel().forEach {
                repeat(1_250_000) {
                    val lat = rand.nextDouble() * 73
                    val lon = rand.nextDouble() * 83 + 55
                    val district = geoLookuper.locate(WGSPoint(lat, lon))
                    if (district != null) count.incrementAndGet()
                }
            }
        }

        println("ms: $time\ncount: $count")
    }()
}