package com.github.wfxr.geolocator

internal abstract class TestBase {
    companion object {
        val CONCURRENCY = Runtime.getRuntime().availableProcessors() / 2 + 1
    }

    abstract val geoLocator: IGeoLocator
}