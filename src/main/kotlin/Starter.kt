import java.nio.file.Paths
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.IntStream
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    {
        val districts = gaodeDistrictsLoader(Paths.get("/home/wenxuan/work/boundary/region"))
        val geoLocator = GeoLocator(districts, 4)
        println(geoLocator.stat)
        println(geoLocator.locate(WGSPoint(36.8092847021, 103.4912109375))) // 中国甘肃省永登县
        println(geoLocator.locate(WGSPoint(30.7135039904, 101.0302734375))) // 中国四川省甘孜藏族自治州道孚县
        println(geoLocator.locate(WGSPoint(46.2558468185, 126.6064453125))) // 中国黑龙江省绥化市兰西县
        println(geoLocator.locate(WGSPoint(35.2456190942, 81.2329101563)))  // 中国西藏自治区阿里地区日土县

        val count = AtomicInteger(0)
        val rand = Random()
        val time = measureTimeMillis {
            IntStream.range(0, 8).parallel().forEach {
                repeat(125_000) {
                    val lat = rand.nextDouble() * 16 + 25
                    val lon = rand.nextDouble() * 33 + 87
                    val district = geoLocator.locateFast(WGSPoint(lat, lon))
                    if (district != null) count.incrementAndGet()
                }
            }
        }

        println("ms: $time\ncount: $count")
    }()
}