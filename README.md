# geo-locator

Locate the district by gps.

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
    <version>1.3.0</version>
</dependency>
```

## Usage
```java
List<District> districts  = UtilsKt.loadDistrictsGaode(Paths.get("path/to/districts/dir"));
// GeoLocator     geoLocator = new RTreeLocator(districts);
GeoLocator     geoLocator = new HashingLocator(districts);
System.out.println(geoLocator.locate(36.8092847021, 103.4912109375)); // 中国甘肃省永登县
```
