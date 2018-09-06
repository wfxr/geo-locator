import java.nio.file.Paths;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        List<District> districts  = UtilsKt.loadDistrictsGaode(Paths.get("/home/wenxuan/work/boundary/region"));
        GeoLocator     geoLocator = new GeoLocator(districts, 4);
        System.out.println(geoLocator.getStat());
        System.out.println(geoLocator.locate(new WGSPoint(36.8092847021, 103.4912109375))); // 中国甘肃省永登县
        System.out.println(geoLocator.locate(new WGSPoint(30.7135039904, 101.0302734375))); // 中国四川省甘孜藏族自治州道孚县
        System.out.println(geoLocator.locate(new WGSPoint(46.2558468185, 126.6064453125))); // 中国黑龙江省绥化市兰西县
        System.out.println(geoLocator.locate(new WGSPoint(35.2456190942, 81.2329101563)));  // 中国西藏自治区阿里地区日土县
    }
}
