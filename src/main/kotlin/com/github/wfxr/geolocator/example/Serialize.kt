package com.github.wfxr.geolocator.example

import com.github.wfxr.geolocator.HashingLocator
import com.github.wfxr.geolocator.serialize
import com.github.wfxr.geolocator.utils.loadDistrictsParallel
import java.lang.System.currentTimeMillis
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val timeStart = currentTimeMillis()

    val districts = loadDistrictsParallel(Paths.get("scripts/districts"))
    val timeDistrictsLoaded = currentTimeMillis()
    println("time loading districts: ${timeDistrictsLoaded - timeStart}")

    val hashLevel = 5
    val locator = HashingLocator(districts, hashLevel)
    val timeLocatorLoaded = currentTimeMillis()
    println("time loading locator:   ${timeLocatorLoaded - timeDistrictsLoaded}")

    val outputStream = Files.newOutputStream(Paths.get("scripts/hashing-locator-level-$hashLevel.dat"))
    locator.serialize(outputStream)
    outputStream.close()
    val timeLocatorSerialized = currentTimeMillis()
    println("time serializing locator: ${timeLocatorSerialized - timeLocatorLoaded}")
}
