package com.github.wfxr.geolocator

import com.github.wfxr.geolocator.utils.loadDistrictsGaodeParallel
import java.nio.file.Paths

internal abstract class TestBase {
    companion object {
        val districts = loadDistrictsGaodeParallel(Paths.get("scripts/districts"))
    }

    abstract val geoLocator: IGeoLocator
}