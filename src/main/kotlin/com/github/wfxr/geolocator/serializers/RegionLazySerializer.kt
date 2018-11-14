package com.github.wfxr.geolocator.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.wfxr.geolocator.Region
import com.github.wfxr.geolocator.WGSPoint

class RegionLazySerializer : Serializer<Region>() {
    override fun write(kryo: Kryo, output: Output, region: Region) {
        kryo.writeObject(output, region.vertexes)
        kryo.writeClassAndObject(output, region.tag)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read(kryo: Kryo, input: Input, type: Class<Region>): Region {
        val vertexes = kryo.readObject(input, ArrayList::class.java) as ArrayList<WGSPoint>
        val tag = kryo.readClassAndObject(input)
        return Region(vertexes, tag, true)
    }
}
