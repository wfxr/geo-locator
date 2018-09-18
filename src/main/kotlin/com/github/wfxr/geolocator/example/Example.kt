package com.github.wfxr.geolocator.example

import com.github.wfxr.geolocator.HashingLocator
import com.github.wfxr.geolocator.utils.loadDistrictsGaode
import java.nio.file.Paths

fun main(args: Array<String>) {
    println("loading districts data...")
    val districts = loadDistrictsGaode(Paths.get("scripts/districts"))
    val geoLocator = HashingLocator(districts, 4)
    println("done\n")

    println(geoLocator.locate(36.8092847021, 103.4912109375)) // 中国甘肃省永登县
    println(geoLocator.locate(30.7135039904, 101.0302734375)) // 中国四川省甘孜藏族自治州道孚县
    println(geoLocator.locate(46.2558468185, 126.6064453125)) // 中国黑龙江省绥化市兰西县
    println(geoLocator.locate(35.2456190942, 81.2329101563))  // 中国西藏自治区阿里地区日土县
}