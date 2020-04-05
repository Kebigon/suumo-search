package xyz.kebigon.housesearch.domain;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.ApplicationContext;

@AllArgsConstructor
@Slf4j
public class SearchConditionsValidator
{
	public static boolean validateBasicConditions(Posting posting, SearchConditions conditions)
	{
		final String url = posting.getUrl();
		if (url == null)
		{
			log.debug("Missing url for {}", posting);
			return false;
		}

		final Long price = posting.getPrice();
		if (price == null)
		{
			log.debug("Missing price for {}", posting);
			return false;
		}
		if (conditions.getMinPrice() != null && price < conditions.getMinPrice())
			return false;
		if (conditions.getMaxPrice() != null && price > conditions.getMaxPrice())
			return false;

		final Integer age = posting.getAge();
		if (age == null)
		{
			log.debug("Missing age for {}", posting);
			return false;
		}
		if (conditions.getMinAge() != null && age < conditions.getMinAge())
			return false;
		if (conditions.getMaxAge() != null && age > conditions.getMaxAge())
			return false;

		final Double landSurface = posting.getLandSurface();
		if (landSurface == null)
		{
			log.debug("Missing landSurface for {}", posting);
			return false;
		}
		if (conditions.getMinLandSurface() != null && landSurface < conditions.getMinLandSurface())
			return false;
		if (conditions.getMaxLandSurface() != null && landSurface > conditions.getMaxLandSurface())
			return false;

		final Double houseSurface = posting.getHouseSurface();
		if (houseSurface == null)
		{
			log.debug("Missing houseSurface for {}", posting);
			return false;
		}
		if (conditions.getMinHouseSurface() != null && houseSurface < conditions.getMinHouseSurface())
			return false;
		if (conditions.getMaxHouseSurface() != null && houseSurface > conditions.getMaxHouseSurface())
			return false;

		final Integer walkTimeToStation = posting.getWalkTimeToStation();
		if (walkTimeToStation == null)
		{
			log.debug("Missing walkTimeToStation for {}", posting);
			return false;
		}
		if (conditions.getMaxWalkTimeToStation() != null && walkTimeToStation > conditions.getMaxWalkTimeToStation())
			return false;

		final String station = posting.getStation();
		if (station == null)
		{
			log.debug("Missing station for {}", posting);
			return false;
		}

		return true;
	}

	public static boolean validateExpression(Posting posting, SearchConditions conditions)
	{
		final ExpressionParser expressionParser = new SpelExpressionParser();

		final StandardEvaluationContext context = new StandardEvaluationContext();
		context.setVariable("property", posting);

		try
		{
			context.registerFunction("fastestRoute",
					SearchConditionsValidator.class.getDeclaredMethod("getFastestRoute", new Class[] { Posting.class, String[].class }));
			context.registerFunction("cheapestRoute",
					SearchConditionsValidator.class.getDeclaredMethod("getCheapestRoute", new Class[] { Posting.class, String[].class }));
			context.registerFunction("easiestRoute",
					SearchConditionsValidator.class.getDeclaredMethod("getEasiestRoute", new Class[] { Posting.class, String[].class }));

			context.registerFunction("timeToStation",
					SearchConditionsValidator.class.getDeclaredMethod("timeToStation", new Class[] { Posting.class, String.class }));
			context.registerFunction("fareToStation",
					SearchConditionsValidator.class.getDeclaredMethod("fareToStation", new Class[] { Posting.class, String.class }));
			context.registerFunction("transferToStation",
					SearchConditionsValidator.class.getDeclaredMethod("transferToStation", new Class[] { Posting.class, String.class }));
		}
		catch (NoSuchMethodException | SecurityException e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		final Expression expression = expressionParser.parseExpression(conditions.getExpression());
		return expression.getValue(context, posting, Boolean.class);
	}

	private static Stream<Route> getRoutes(Posting posting, String... stations)
	{
		return Arrays.stream(stations) //
				.flatMap(station -> {
					final Collection<Route> routes = ApplicationContext.getYahooTransitBrowser().search(posting.getStation(), station);
					routes.forEach(posting::updateRoutes);
					return routes.stream();
				});
	}

	private static Route getFastestRoute(Posting posting, String... stations)
	{
		return getRoutes(posting, stations).min(Route.TIME_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);
	}

	private static Route getCheapestRoute(Posting posting, String... stations)
	{
		return getRoutes(posting, stations).min(Route.FARE_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);
	}

	private static Route getEasiestRoute(Posting posting, String... stations)
	{
		return getRoutes(posting, stations).min(Route.TRANSFER_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);
	}

	@SuppressWarnings("unused")
	private static int timeToStation(Posting posting, String station)
	{
		return getFastestRoute(posting, station).getTime() + posting.getWalkTimeToStation();
	}

	@SuppressWarnings("unused")
	private static int fareToStation(Posting posting, String station)
	{
		return getCheapestRoute(posting, station).getFare();
	}

	@SuppressWarnings("unused")
	private static int transferToStation(Posting posting, String station)
	{
		return getEasiestRoute(posting, station).getTransfer();
	}
}
