package xyz.kebigon.housesearch.browser.suumo;

import xyz.kebigon.housesearch.domain.AnounceType;
import xyz.kebigon.housesearch.domain.Area;
import xyz.kebigon.housesearch.domain.SearchConditions;

public class SuumoSearchURLBuilder
{
    private static final String SEARCH_URI = "https://suumo.jp/jj/bukken/ichiran/JJ012FC001";
    private static final String MIN_PRICE_FIELD = "kb";
    private static final String MAX_PRICE_FIELD = "kt";
    private static final String MIN_AGE_FIELD = "cnb";
    private static final String MAX_AGE_FIELD = "cn";
    private static final String MIN_LAND_SURFACE_FIELD = "tb";
    private static final String MAX_LAND_SURFACE_FIELD = "tt";
    private static final String MIN_HOUSE_SURFACE_FIELD = "hb";
    private static final String MAX_HOUSE_SURFACE_FIELD = "ht";
    private static final String MAX_WALK_TIME_TO_STATION_FIELD = "et";

    public static String build(SearchConditions conditions)
    {
        final Area area = conditions.getArea();
        if (area == null)
            throw new RuntimeException();

        final AnounceType type = conditions.getType();
        if (type == null)
            throw new RuntimeException();

        final StringBuilder builder = new StringBuilder(SEARCH_URI);
        builder.append("?ar=").append(area.getCode());
        builder.append("&bs=").append(type.getCode());
        builder.append("&pc=100"); // 100 results per page
        builder.append("&kr=A"); // Buying the land with the house

        // Price
        encodeMinPrice(builder, conditions.getMinPrice());
        encodeMaxPrice(builder, conditions.getMaxPrice());
        // Age
        encodeMinAge(builder, conditions.getMinAge());
        encodeMaxAge(builder, conditions.getMaxAge());
        // LandSurface
        encodeMinLandSurface(builder, conditions.getMinLandSurface());
        encodeMaxLandSurface(builder, conditions.getMaxLandSurface());
        // HouseSurface
        encodeMinHouseSurface(builder, conditions.getMinHouseSurface());
        encodeMaxHouseSurface(builder, conditions.getMaxHouseSurface());
        // WalkTimeToStation
        encodeMaxWalkTimeToStation(builder, conditions.getMaxWalkTimeToStation());

        return builder.toString();
    }

    private static void encodeMinPrice(StringBuilder builder, Long minPrice)
    {
        if (minPrice == null || minPrice < 5000000)
            return; // 下限なし

        builder.append('&').append(MIN_PRICE_FIELD).append('=');

        if (5000000 <= minPrice && minPrice < 10000000)
            builder.append(500); // 500万円以上
        else if (10000000 <= minPrice && minPrice < 15000000)
            builder.append(1000); // 1000万円以上
        else
            builder.append(1500); // 1500万円以上
    }

    private static void encodeMaxPrice(StringBuilder builder, Long maxPrice)
    {
        if (maxPrice == null || 120000000 < maxPrice)
            return; // 上限なし

        builder.append('&').append(MAX_PRICE_FIELD).append('=');

        if (100000000 < maxPrice && maxPrice <= 120000000)
            builder.append(12000); // 1億2千万円未満
        else if (90000000 < maxPrice && maxPrice <= 100000000)
            builder.append(10000); // 1億円未満
        else if (80000000 < maxPrice && maxPrice <= 90000000)
            builder.append(9000); // 9000万円未満
        else if (75000000 < maxPrice && maxPrice <= 80000000)
            builder.append(8000); // 8000万円未満
        else if (70000000 < maxPrice && maxPrice <= 75000000)
            builder.append(7500); // 7500万円未満
        else if (65000000 < maxPrice && maxPrice <= 70000000)
            builder.append(7000); // 7000万円未満
        else if (60000000 < maxPrice && maxPrice <= 65000000)
            builder.append(6500); // 6500万円未満
        else if (55000000 < maxPrice && maxPrice <= 60000000)
            builder.append(6000); // 6000万円未満
        else if (50000000 < maxPrice && maxPrice <= 55000000)
            builder.append(5500); // 5500万円未満
        else if (45000000 < maxPrice && maxPrice <= 50000000)
            builder.append(5000); // 5000万円未満
        else if (40000000 < maxPrice && maxPrice <= 45000000)
            builder.append(4500); // 4500万円未満
        else if (35000000 < maxPrice && maxPrice <= 40000000)
            builder.append(4000); // 4000万円未満
        else if (30000000 < maxPrice && maxPrice <= 35000000)
            builder.append(3500); // 3500万円未満
        else if (25000000 < maxPrice && maxPrice <= 30000000)
            builder.append(3000); // 3000万円未満
        else if (20000000 < maxPrice && maxPrice <= 25000000)
            builder.append(2500); // 2500万円未満
        else if (15000000 < maxPrice && maxPrice <= 20000000)
            builder.append(2000); // 2000万円未満
        else if (10000000 < maxPrice && maxPrice <= 15000000)
            builder.append(1500); // 1500万円未満
        else if (5000000 < maxPrice && maxPrice <= 10000000)
            builder.append(1000); // 1000万円未満
        else
            builder.append(500); // 500万円未満
    }

    private static void encodeMinAge(StringBuilder builder, Integer minAge)
    {
        if (minAge == null || minAge < 3)
            return; // 下限なし

        builder.append('&').append(MIN_AGE_FIELD).append('=');

        if (3 <= minAge && minAge < 5)
            builder.append(3); // 3年以上
        else if (5 <= minAge && minAge < 7)
            builder.append(5); // 5年以上
        else if (7 <= minAge && minAge < 10)
            builder.append(7); // 7年以上
        else if (10 <= minAge && minAge < 15)
            builder.append(10); // 10年以上
        else
            builder.append(15); // 15年以上
    }

    private static void encodeMaxAge(StringBuilder builder, Integer maxAge)
    {
        if (maxAge == null || 30 < maxAge)
            return; // 上限なし

        builder.append('&').append(MAX_AGE_FIELD).append('=');

        if (25 < maxAge && maxAge <= 30)
            builder.append(30); // 30年以内
        else if (20 < maxAge && maxAge <= 25)
            builder.append(25); // 25年以内
        else if (15 < maxAge && maxAge <= 20)
            builder.append(20); // 20年以内
        else if (10 < maxAge && maxAge <= 15)
            builder.append(15); // 15年以内
        else if (7 < maxAge && maxAge <= 10)
            builder.append(10); // 10年以内
        else if (5 < maxAge && maxAge <= 7)
            builder.append(7); // 7年以内
        else if (3 < maxAge && maxAge <= 5)
            builder.append(5); // 5年以内
        else
            builder.append(3); // 3年以内
    }

    private static void encodeMinLandSurface(StringBuilder builder, Integer minLandSurface)
    {
        encodeMinSurface(builder, MIN_LAND_SURFACE_FIELD, minLandSurface);
    }

    private static void encodeMaxLandSurface(StringBuilder builder, Integer maxLandSurface)
    {
        encodeMaxSurface(builder, MAX_LAND_SURFACE_FIELD, maxLandSurface);
    }

    private static void encodeMinHouseSurface(StringBuilder builder, Integer minHouseSurface)
    {
        encodeMinSurface(builder, MIN_HOUSE_SURFACE_FIELD, minHouseSurface);
    }

    private static void encodeMaxHouseSurface(StringBuilder builder, Integer maxHouseSurface)
    {
        encodeMaxSurface(builder, MAX_HOUSE_SURFACE_FIELD, maxHouseSurface);
    }

    private static void encodeMinSurface(StringBuilder builder, String field, Integer minLandSurface)
    {
        if (minLandSurface == null || minLandSurface < 60)
            return; // 下限なし

        builder.append('&').append(field).append('=');

        if (60 <= minLandSurface && minLandSurface < 70)
            builder.append(60); // 60m2以上
        else if (70 <= minLandSurface && minLandSurface < 80)
            builder.append(70); // 70m2以上
        else if (80 <= minLandSurface && minLandSurface < 90)
            builder.append(80); // 80m2以上
        else if (90 <= minLandSurface && minLandSurface < 100)
            builder.append(90); // 90m2以上
        else if (100 <= minLandSurface && minLandSurface < 110)
            builder.append(100); // 100m2以上
        else if (110 <= minLandSurface && minLandSurface < 120)
            builder.append(110); // 110m2以上
        else if (120 <= minLandSurface && minLandSurface < 130)
            builder.append(120); // 120m2以上
        else if (130 <= minLandSurface && minLandSurface < 140)
            builder.append(130); // 130m2以上
        else if (140 <= minLandSurface && minLandSurface < 150)
            builder.append(140); // 140m2以上
        else
            builder.append(150); // 150m2以上
    }

    private static void encodeMaxSurface(StringBuilder builder, String field, Integer maxLandSurface)
    {
        if (maxLandSurface == null || 150 < maxLandSurface)
            return; // 上限なし

        builder.append('&').append(field).append('=');

        if (140 < maxLandSurface && maxLandSurface <= 150)
            builder.append(150); // 150m2未満
        else if (130 < maxLandSurface && maxLandSurface <= 140)
            builder.append(140); // 140m2未満
        else if (120 < maxLandSurface && maxLandSurface <= 130)
            builder.append(130); // 130m2未満
        else if (110 < maxLandSurface && maxLandSurface <= 120)
            builder.append(120); // 120m2未満
        else if (100 < maxLandSurface && maxLandSurface <= 110)
            builder.append(110); // 110m2未満
        else if (90 < maxLandSurface && maxLandSurface <= 100)
            builder.append(100); // 100m2未満
        else if (80 < maxLandSurface && maxLandSurface <= 90)
            builder.append(90); // 90m2未満
        else if (70 < maxLandSurface && maxLandSurface <= 80)
            builder.append(80); // 80m2未満
        else if (60 < maxLandSurface && maxLandSurface <= 70)
            builder.append(70); // 70m2未満
        else
            builder.append(60); // 60m2未満
    }

    public static void encodeMaxWalkTimeToStation(StringBuilder builder, Integer maxWalkTimeToStation)
    {
        if (maxWalkTimeToStation == null || 20 < maxWalkTimeToStation)
            return; // 指定なし

        builder.append('&').append(MAX_WALK_TIME_TO_STATION_FIELD).append('=');

        if (15 < maxWalkTimeToStation && maxWalkTimeToStation <= 20)
            builder.append(20); // 20分以内
        else if (10 < maxWalkTimeToStation && maxWalkTimeToStation <= 15)
            builder.append(15); // 15分以内
        else if (7 < maxWalkTimeToStation && maxWalkTimeToStation <= 10)
            builder.append(10); // 10分以内
        else if (5 < maxWalkTimeToStation && maxWalkTimeToStation <= 7)
            builder.append(7); // 7分以内
        else if (3 < maxWalkTimeToStation && maxWalkTimeToStation <= 5)
            builder.append(5); // 5分以内
        else if (1 < maxWalkTimeToStation && maxWalkTimeToStation <= 3)
            builder.append(3); // 3分以内
        else
            builder.append(1); // 1分以内
    }
}
