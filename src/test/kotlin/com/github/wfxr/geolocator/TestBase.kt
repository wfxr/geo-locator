package com.github.wfxr.geolocator

import com.github.wfxr.geolocator.utils.loadDistrictsGaode
import java.nio.file.Paths

internal abstract class TestBase {
    companion object {
        val districts = loadDistrictsGaode(Paths.get("scripts/districts"))
    }

    abstract val geoLocator: IGeoLocator
}