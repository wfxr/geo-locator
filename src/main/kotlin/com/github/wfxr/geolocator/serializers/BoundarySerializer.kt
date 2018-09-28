package com.github.wfxr.geolocator.serializers

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.Serializer
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.wfxr.geolocator.Boundary
import com.github.wfxr.geolocator.WGSPoint

class BoundarySerializer : Serializer<Boundary>() {
    override fun write(kryo: Kryo, output: Output, boundary: Boundary) {
        kryo.writeObject(output, boundary.vertexes)
    }

    @Suppress("UNCHECKED_CAST")
    override fun read(kryo: Kryo, input: Input, type: Class<Boundary>): Boundary {
        return Boundary(kryo.readObject(input, ArrayList::class.java) as ArrayList<WGSPoint>, false)
    }
}