package com.github.wfxr.geolocator

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

@Suppress("unused_parameter")
internal abstract class LocatorTestBase : TestBase() {
    companion object {
        @Suppress("unused")
        @JvmStatic
        fun districtSample() = listOf(
            Arguments.of(210624, 40.6473035625, 124.8486328125, "辽宁省丹东市宽甸满族自治县"),
            Arguments.of(232723, 53.1467703309, 122.1240234375, "黑龙江省大兴安岭地区漠河县"),
            Arguments.of(350213, 24.5671083526, 118.3007812500, "中国福建省厦门市翔安区"),
            Arguments.of(460202, 18.2710861096, 109.7314453125, "海南省三亚市海棠区"),
            Arguments.of(532823, 21.2688997200, 101.5356445313, "云南省西双版纳傣族自治州勐腊县"),
            Arguments.of(620121, 36.8092847021, 103.4912109375, "甘肃省永登县"),
            Arguments.of(513326, 30.7135039904, 101.0302734375, "四川省甘孜藏族自治州道孚县"),
            Arguments.of(231222, 46.2558468185, 126.6064453125, "黑龙江省绥化市兰西县"),

            Arguments.of(653022, 39.2322531417, 74.2675781250, "新疆维吾尔自治区克孜勒苏柯尔克孜自治州阿克陶县"),
            Arguments.of(542524, 35.2456190942, 81.2329101563, "西藏自治区阿里地区日土县"),

            Arguments.of(110108, 40.0008432815, 116.3681101799, "北京市北京市海淀区"),
            Arguments.of(110105, 40.0010240919, 116.3713932037, "北京市北京市朝阳区"),

            Arguments.of(110108, 39.8981479502, 116.3225555420, "北京市北京市海淀区"),
            Arguments.of(110106, 39.8944274320, 116.3180494308, "北京市北京市丰台区"),
            Arguments.of(110102, 39.8938018383, 116.3265466690, "北京市北京市西城区"))
    }

    @ParameterizedTest
    @MethodSource("districtSample")
    fun locate(expectAdcode: Int, lat: Double, lon: Double, remark: String) {
        val district = geoLocator.locate(lat, lon)
        assertNotNull(district)
        assertEquals(expectAdcode, district!!.adcode, remark)
    }

    @ParameterizedTest
    @MethodSource("districtSample")
    fun fastLocate(expectAdcode: Int, lat: Double, lon: Double, remark: String) {
        val district = geoLocator.fastLocate(lat, lon)
        assertNotNull(district)
        assertEquals(expectAdcode, district!!.adcode, remark)
    }
}

internal class HashingLocatorTest : LocatorTestBase() {
    companion object {
        val GeoLocator = HashingLocator(districts)
    }

    override val geoLocator = GeoLocator
}

internal class RTreeLocatorTest : LocatorTestBase() {
    companion object {
        val GeoLocator = RTreeLocator(districts)
    }

    override val geoLocator = GeoLocator
}
