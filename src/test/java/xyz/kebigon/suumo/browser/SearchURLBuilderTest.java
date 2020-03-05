package xyz.kebigon.suumo.browser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import xyz.kebigon.housesearch.browser.suumo.SuumoSearchURLBuilder;
import xyz.kebigon.housesearch.domain.AnounceType;
import xyz.kebigon.housesearch.domain.Area;
import xyz.kebigon.housesearch.domain.SearchConditions;

public class SearchURLBuilderTest
{
	@Test
	public void testSearch()
	{
		final SearchConditions conditions = new SearchConditions();
		conditions.setArea(Area.KANTO);
		conditions.setType(AnounceType.USED_HOUSE);
		conditions.setMaxPrice(20000000l);
		conditions.setMaxAge(10);
		conditions.setMinLandSurface(100);
		conditions.setMinHouseSurface(80);

		assertEquals("https://suumo.jp/jj/bukken/ichiran/JJ012FC001?ar=030&bs=021&pc=100&kr=A&kt=2000&cn=10&tb=100&hb=80",
				SuumoSearchURLBuilder.build(conditions));
	}

	@Test
	public void testExactMatch()
	{
		final SearchConditions conditions = new SearchConditions();
		conditions.setArea(Area.KANTO);
		conditions.setType(AnounceType.USED_HOUSE);
		conditions.setMinPrice(6780000l);
		conditions.setMaxPrice(12340000l);
		conditions.setMinAge(11);
		conditions.setMaxAge(27);
		conditions.setMinLandSurface(82);
		conditions.setMaxLandSurface(125);
		conditions.setMinHouseSurface(73);
		conditions.setMaxHouseSurface(86);

		assertEquals("https://suumo.jp/jj/bukken/ichiran/JJ012FC001?ar=030&bs=021&pc=100&kr=A&kb=500&kt=1500&cnb=10&cn=30&tb=80&tt=130&hb=70&ht=90",
				SuumoSearchURLBuilder.build(conditions));
	}
}
