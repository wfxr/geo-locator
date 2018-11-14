package com.github.wfxr.geolocator.example

import com.github.wfxr.geolocator.HashingLocator
import com.github.wfxr.geolocator.utils.loadRegionsParallel
import java.lang.System.currentTimeMillis
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val timeStart = currentTimeMillis()

    val districts = loadRegionsParallel(Paths.get("scripts/districts"))
    val timeRegionsLoaded = currentTimeMillis()
    println("time loading districts: ${timeRegionsLoaded - timeStart}")

    val hashLevel = 5
    val locator = HashingLocator(districts, hashLevel)
    val timeLocatorLoaded = currentTimeMillis()
    println("time loading locator:   ${timeLocatorLoaded - timeRegionsLoaded}")

    val outputStream = Files.newOutputStream(Paths.get("scripts/hashing-locator-level-$hashLevel.dat"))
    locator.serialize(outputStream)
    outputStream.close()
    val timeLocatorSerialized = currentTimeMillis()
    println("time serializing locator: ${timeLocatorSerialized - timeLocatorLoaded}")
}
