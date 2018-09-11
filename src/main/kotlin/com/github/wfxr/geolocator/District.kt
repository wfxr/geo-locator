package com.github.wfxr.geolocator

import ch.hsr.geohash.BoundingBox

data class District(val adcode: Int, val name: String, val center: WGSPoint?, val boundary: Boundary) {
    val bBox: BoundingBox get() = boundary.bBox
    val metro get() = adcode / 10000 * 10000
    val city get() = adcode / 100 * 100
    val district get() = adcode
}
