# Geo Locator Library

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://wfxr.mit-license.org/2018)
[![Jitpack](https://jitpack.io/v/wfxr/geo-locator.svg)](https://jitpack.io/#wfxr/geo-locator)

## Overview

This is a library for fast locating region by GPS coordinates.

## Install

Add `JitPack` repository：
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
Add dependency：
```xml
<dependency>
    <groupId>com.github.wfxr</groupId>
    <artifactId>geo-locator</artifactId>
    <version>LATEST-VERSION</version>
</dependency>
```

## Usage

### Java
``` java
List<Region<AdTag>> regions = UtilsKt.loadRegionsParallel(Paths.get("path/to/regions/dir"));
IGeoLocator<AdTag>  locator = new HashingLocator<>(regions);
Region<Adtag>        region = locator.locate(36.8092847021, 103.4912109375);
if (region != null) {
    System.out.println(region.getTag());
} else {
    System.out.println("Not found");
}
```

### Kotlin
``` kotlin
val regions = loadRegionsParallel(Paths.get("path/to/regions/dir"))
val locator = HashingLocator(regions)
val  region = locator.locate(36.8092847021, 103.4912109375)
println(region?.tag ?: "Not found")
```

## License

[MIT](https://wfxr.mit-license.org/2018) (c) Wenxuan Zhang
