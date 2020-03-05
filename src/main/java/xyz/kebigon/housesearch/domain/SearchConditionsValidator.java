package xyz.kebigon.housesearch.domain;

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
            log.warn("Missing url for {}", posting);
            return false;
        }

        final Long price = posting.getPrice();
        if (price == null)
        {
            log.warn("Missing price for {}", posting);
            return false;
        }
        if (conditions.getMinPrice() != null && price < conditions.getMinPrice())
            return false;
        if (conditions.getMaxPrice() != null && price > conditions.getMaxPrice())
            return false;

        final Integer age = posting.getAge();
        if (age == null)
        {
            log.warn("Missing age for {}", posting);
            return false;
        }
        if (conditions.getMinAge() != null && age < conditions.getMinAge())
            return false;
        if (conditions.getMaxAge() != null && age > conditions.getMaxAge())
            return false;

        final Double landSurface = posting.getLandSurface();
        if (landSurface == null)
        {
            log.warn("Missing landSurface for {}", posting);
            return false;
        }
        if (conditions.getMinLandSurface() != null && landSurface < conditions.getMinLandSurface())
            return false;
        if (conditions.getMaxLandSurface() != null && landSurface > conditions.getMaxLandSurface())
            return false;

        final Double houseSurface = posting.getHouseSurface();
        if (houseSurface == null)
        {
            log.warn("Missing houseSurface for {}", posting);
            return false;
        }
        if (conditions.getMinHouseSurface() != null && houseSurface < conditions.getMinHouseSurface())
            return false;
        if (conditions.getMaxHouseSurface() != null && houseSurface > conditions.getMaxHouseSurface())
            return false;

        final Integer walkTimeToStation = posting.getWalkTimeToStation();
        if (walkTimeToStation == null)
        {
            log.warn("Missing walkTimeToStation for {}", posting);
            return false;
        }
        if (conditions.getMaxWalkTimeToStation() != null && walkTimeToStation > conditions.getMaxWalkTimeToStation())
            return false;

        final String station = posting.getStation();
        if (station == null)
        {
            log.warn("Missing station for {}", posting);
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

    @SuppressWarnings("unused")
    private static int timeToStation(Posting posting, String station)
    {
        final Route route = ApplicationContext.getYahooTransitBrowser() //
                .search(posting.getStation(), station).stream() //
                .min(Route.TIME_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);

        posting.updateRoutes(route);

        return route.getTime() + posting.getWalkTimeToStation();
    }

    @SuppressWarnings("unused")
    private static int fareToStation(Posting posting, String station)
    {
        final Route route = ApplicationContext.getYahooTransitBrowser() //
                .search(posting.getStation(), station).stream() //
                .min(Route.FARE_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);

        posting.updateRoutes(route);

        return route.getFare();
    }

    @SuppressWarnings("unused")
    private static int transferToStation(Posting posting, String station)
    {
        final Route route = ApplicationContext.getYahooTransitBrowser() //
                .search(posting.getStation(), station).stream() //
                .min(Route.TRANSFER_COMPARATOR).orElse(Route.IMPOSSIBLE_ROUTE);

        posting.updateRoutes(route);

        return route.getTransfer();
    }
}
