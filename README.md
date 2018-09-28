# Geo Locator Library

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Jitpack](https://jitpack.io/v/wfxr/geo-locator.svg)](https://jitpack.io/#wfxr/geo-locator)

## Overview

This is a library for fast locating district by GPS coordinates.

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
    <version>2.6.0</version>
</dependency>
```

## Usage

### Java
``` java
List<District> districts  = UtilsKt.loadDistrictsParallel(Paths.get("path/to/districts/dir"));
IGeoLocator    geoLocator = new HashingLocator(districts);
System.out.println(geoLocator.locate(36.8092847021, 103.4912109375));
```

### Kotlin
``` kotlin
val districts  = loadDistrictsParallel(Paths.get("path/to/districts/dir"))
val geoLocator = HashingLocator(districts)
println(geoLocator.locate(36.8092847021, 103.4912109375))
```
