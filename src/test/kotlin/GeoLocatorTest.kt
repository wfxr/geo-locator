import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class GeoLocatorTest {
    companion object {
        val geoLocator: GeoLocator = GeoLocator(gaodeDistrictsLoader(Paths.get("/home/wenxuan/work/boundary/region")))

        @Suppress("unused")
        @JvmStatic
        fun districtSample() = listOf(
            Arguments.of("542524", WGSPoint(35.2456190942, 81.2329101563), "中国西藏自治区阿里地区日土县"),
            Arguments.of("620121", WGSPoint(36.8092847021, 103.4912109375), "中国甘肃省永登县"),
            Arguments.of("513326", WGSPoint(30.7135039904, 101.0302734375), "中国四川省甘孜藏族自治州道孚县"),
            Arguments.of("231222", WGSPoint(46.2558468185, 126.6064453125), "中国黑龙江省绥化市兰西县"),

            Arguments.of("110108", WGSPoint(40.0008432815, 116.3681101799), "中国北京市北京市海淀区"),
            Arguments.of("110105", WGSPoint(40.0010240919, 116.3713932037), "中国北京市北京市朝阳区"),

            Arguments.of("110108", WGSPoint(39.8981479502, 116.3225555420), "中国北京市北京市海淀区"),
            Arguments.of("110106", WGSPoint(39.8944274320, 116.3180494308), "中国北京市北京市丰台区"),
            Arguments.of("110102", WGSPoint(39.8938018383, 116.3265466690), "中国北京市北京市西城区"))
    }

    @ParameterizedTest
    @MethodSource("districtSample")
    fun locate(expectAdcode: String, gps: WGSPoint, remark: String) {
        val district = geoLocator.locate(gps)
        assertNotNull(district)
        assertEquals(expectAdcode, district!!.adcode, remark)
    }

    @ParameterizedTest
    @MethodSource("districtSample")
    fun fastLocate(expectAdcode: String, gps: WGSPoint, remark: String) {
        val district = geoLocator.fastLocate(gps)
        assertNotNull(district)
        assertEquals(expectAdcode, district!!.adcode, remark)
    }

}

