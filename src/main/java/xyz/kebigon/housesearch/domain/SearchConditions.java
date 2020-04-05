package xyz.kebigon.housesearch.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class SearchConditions
{
	private String name;
	private Area area;
	private AnounceType type;
	@JsonProperty("price.min")
	private Long minPrice;
	@JsonProperty("price.max")
	private Long maxPrice;
	@JsonProperty("age.min")
	private Integer minAge;
	@JsonProperty("age.max")
	private Integer maxAge;
	@JsonProperty("surface.land.min")
	private Integer minLandSurface;
	@JsonProperty("surface.land.max")
	private Integer maxLandSurface;
	@JsonProperty("surface.house.min")
	private Integer minHouseSurface;
	@JsonProperty("surface.house.max")
	private Integer maxHouseSurface;
	@JsonProperty("station.walktime.max")
	private Integer maxWalkTimeToStation;
	private String expression;

	public static SearchConditions[] load() throws IOException
	{
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(SearchConditions.class.getClassLoader().getResourceAsStream("searches.json"), SearchConditions[].class);
	}

	@JsonProperty("expression.file")
	private void expressionProp(String expressionFile) throws IOException
	{
		if (StringUtils.isEmpty(expressionFile))
		{
			expression = null;
			return;
		}

		expression = "";
		try (final BufferedReader reader = new BufferedReader(
				new InputStreamReader(SearchConditions.class.getClassLoader().getResourceAsStream(expressionFile))))
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
	}
}
