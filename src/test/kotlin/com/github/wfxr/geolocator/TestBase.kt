package com.github.wfxr.geolocator

import com.github.wfxr.geolocator.utils.loadDistrictsParallel
import java.nio.file.Paths

internal abstract class TestBase {
    companion object {
        val districts = loadDistrictsParallel(Paths.get("scripts/districts"))
    }

    abstract val geoLocator: IGeoLocator
}