package com.github.wfxr.geolocator.example;

import com.github.wfxr.geolocator.AdTag;
import com.github.wfxr.geolocator.Region;
import com.github.wfxr.geolocator.HashingLocator;
import com.github.wfxr.geolocator.IGeoLocator;
import com.github.wfxr.geolocator.utils.UtilsKt;

import java.nio.file.Paths;
import java.util.List;

public class Basic {
    public static void main(String[] args) {
        System.out.println("loading districts data...");
        List<Region<AdTag>> regions    = UtilsKt.loadRegionsParallel(Paths.get("scripts/districts"));
        IGeoLocator<AdTag>  geoLocator = new HashingLocator<>(regions);
        System.out.println("done\n");

        Region<AdTag> region = geoLocator.locate(36.8092847021, 103.4912109375); // 中国甘肃省永登县
        if (region != null) {
            System.out.println(region.getTag());
        } else {
            System.out.println("Not found");
        }
    }
}
