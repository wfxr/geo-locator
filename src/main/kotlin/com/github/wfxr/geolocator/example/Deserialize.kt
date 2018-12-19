package com.github.wfxr.geolocator.example

import com.github.wfxr.geolocator.AdTag
import com.github.wfxr.geolocator.HashingLocator
import java.lang.System.currentTimeMillis
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) {
    val hashLevel = 5
    val inputStream = Files.newInputStream(Paths.get("scripts/hashing-locator-level-$hashLevel.dat"))
    val timeStart = currentTimeMillis()
    HashingLocator.deserialize<AdTag>(inputStream, true)
    inputStream.close()
    val timeLocatorDeserialized = currentTimeMillis()
    println("time used: ${timeLocatorDeserialized - timeStart}")
}
