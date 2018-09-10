import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths

@Suppress("unused_parameter")
internal class GeoLocatorTest {
    companion object {
        val geoLocator: GeoLocator = GeoLocator(loadDistrictsGaode(Paths.get("scripts/districts")))

        @Suppress("unused")
        @JvmStatic
        fun districtSample() = listOf(
            Arguments.of("210624", WGSPoint(40.6473035625, 124.8486328125), "辽宁省丹东市宽甸满族自治县"),
            Arguments.of("232723", WGSPoint(53.1467703309, 122.1240234375), "黑龙江省大兴安岭地区漠河县"),
            Arguments.of("350213", WGSPoint(24.5671083526, 118.3007812500), "中国福建省厦门市翔安区"),
            Arguments.of("460202", WGSPoint(18.2710861096, 109.7314453125), "海南省三亚市海棠区"),
            Arguments.of("532823", WGSPoint(21.2688997200, 101.5356445313), "云南省西双版纳傣族自治州勐腊县"),
            Arguments.of("620121", WGSPoint(36.8092847021, 103.4912109375), "甘肃省永登县"),
            Arguments.of("513326", WGSPoint(30.7135039904, 101.0302734375), "四川省甘孜藏族自治州道孚县"),
            Arguments.of("231222", WGSPoint(46.2558468185, 126.6064453125), "黑龙江省绥化市兰西县"),

            Arguments.of("653022", WGSPoint(39.2322531417, 74.2675781250), "新疆维吾尔自治区克孜勒苏柯尔克孜自治州阿克陶县"),
            Arguments.of("542524", WGSPoint(35.2456190942, 81.2329101563), "西藏自治区阿里地区日土县"),

            Arguments.of("110108", WGSPoint(40.0008432815, 116.3681101799), "北京市北京市海淀区"),
            Arguments.of("110105", WGSPoint(40.0010240919, 116.3713932037), "北京市北京市朝阳区"),

            Arguments.of("110108", WGSPoint(39.8981479502, 116.3225555420), "北京市北京市海淀区"),
            Arguments.of("110106", WGSPoint(39.8944274320, 116.3180494308), "北京市北京市丰台区"),
            Arguments.of("110102", WGSPoint(39.8938018383, 116.3265466690), "北京市北京市西城区"))
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

