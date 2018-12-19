package com.github.wfxr.geolocator.example

import com.github.wfxr.geolocator.HashingLocator
import com.github.wfxr.geolocator.utils.loadRegionsParallel
import java.lang.System.currentTimeMillis
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    println("loading districts data...")
    val timeStart = currentTimeMillis()

    //    val hashLevel = 5
    //    val geoLocator = Files.newInputStream(Paths.get("scripts/hashing-locator-level-$hashLevel.dat")).use {
    //        HashingLocator.deserialize(it, true)
    //    }
    //    val timeLocatorDeserialized = currentTimeMillis()
    //    println("time used: ${timeLocatorDeserialized - timeStart}")

    val districts = loadRegionsParallel(Paths.get("scripts/districts"))
    val timeRegionsLoaded = currentTimeMillis()
    println("time loading districts: ${timeRegionsLoaded - timeStart}")
    val geoLocator = HashingLocator(districts)
    val timeLocatorLoaded = currentTimeMillis()
    println("time loading locator:   ${timeLocatorLoaded - timeRegionsLoaded}")

    println("locate test: ${geoLocator.stat}")

    println(geoLocator.locate(36.8092847021, 103.4912109375)?.tag) // 中国甘肃省永登县
    println(geoLocator.locate(30.7135039904, 101.0302734375)?.tag) // 中国四川省甘孜藏族自治州道孚县
    println(geoLocator.locate(46.2558468185, 126.6064453125)?.tag) // 中国黑龙江省绥化市兰西县
    println(geoLocator.locate(35.2456190942, 81.2329101563)?.tag)  // 中国西藏自治区阿里地区日土县
    println(geoLocator.locate(19.5409200000, 109.5919300000)?.tag) // 中国海南省儋州市
    println(geoLocator.locate(31.2140750000, 121.4346470000)?.tag) // 中国上海市上海市长宁区
}