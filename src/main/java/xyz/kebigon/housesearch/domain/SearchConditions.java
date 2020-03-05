package xyz.kebigon.housesearch.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.springframework.util.StringUtils;

import lombok.Data;

@Data
public class SearchConditions
{
    private Area area;
    private AnounceType type;
    private Long minPrice;
    private Long maxPrice;
    private Integer minAge;
    private Integer maxAge;
    private Integer minLandSurface;
    private Integer maxLandSurface;
    private Integer minHouseSurface;
    private Integer maxHouseSurface;
    private Integer maxWalkTimeToStation;
    private String expression;

    public static SearchConditions load() throws IOException
    {
        final Properties properties = new Properties();
        properties.load(SearchConditions.class.getClassLoader().getResourceAsStream("search-conditions.cfg"));

        final SearchConditions conditions = new SearchConditions();
        conditions.setArea(areaProp(properties, "area"));
        conditions.setType(anounceTypeProp(properties, "type"));
        conditions.setMinPrice(longProp(properties, "price.min"));
        conditions.setMaxPrice(longProp(properties, "price.max"));
        conditions.setMinAge(intProp(properties, "age.min"));
        conditions.setMaxAge(intProp(properties, "age.max"));
        conditions.setMinLandSurface(intProp(properties, "surface.land.min"));
        conditions.setMaxLandSurface(intProp(properties, "surface.land.max"));
        conditions.setMinHouseSurface(intProp(properties, "surface.house.min"));
        conditions.setMaxHouseSurface(intProp(properties, "surface.house.max"));
        conditions.setMaxWalkTimeToStation(intProp(properties, "station.walktime.max"));
        conditions.setExpression(expressionProp(properties, "expression.file"));
        return conditions;
    }

    private static Area areaProp(Properties properties, String key)
    {
        final String property = properties.getProperty(key);
        return StringUtils.isEmpty(property) ? null : Area.valueOf(property);
    }

    private static AnounceType anounceTypeProp(Properties properties, String key)
    {
        final String property = properties.getProperty(key);
        return StringUtils.isEmpty(property) ? null : AnounceType.valueOf(property);
    }

    private static Integer intProp(Properties properties, String key)
    {
        final String property = properties.getProperty(key);
        return StringUtils.isEmpty(property) ? null : Integer.parseInt(property);
    }

    private static Long longProp(Properties properties, String key)
    {
        final String property = properties.getProperty(key);
        return StringUtils.isEmpty(property) ? null : Long.parseLong(property);
    }

    private static String expressionProp(Properties properties, String key) throws IOException
    {
        final String property = properties.getProperty(key);
        if (StringUtils.isEmpty(property))
            return null;

        String expression = "";
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(SearchConditions.class.getClassLoader().getResourceAsStream(property))))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("#"))
                    continue;
                expression += line;
            }
        }

        return expression;
    }
}
