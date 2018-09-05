class GeoLookuper(private val districts: Map<String, District>) {
    fun locateAdcode(point: Point) =
            districts.values.find { it.boundary.contains(point) }
}
