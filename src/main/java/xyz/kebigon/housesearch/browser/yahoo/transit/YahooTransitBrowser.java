package xyz.kebigon.housesearch.browser.yahoo.transit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import xyz.kebigon.housesearch.browser.Browser;
import xyz.kebigon.housesearch.domain.Route;
import xyz.kebigon.housesearch.file.RoutesCache;

public class YahooTransitBrowser extends Browser
{
    private final RoutesCache cache;

    public YahooTransitBrowser() throws JsonParseException, JsonMappingException, IOException
    {
        final File file = new File("cache.json");
        cache = RoutesCache.load(file);
    }

    @Override
    public void close() throws IOException
    {
        super.close();

        final File file = new File("cache.json");
        cache.save(file);
    }

    public Collection<Route> search(String from, String to)
    {
        final String cacheKey = from + "-" + to;

        Collection<Route> routes = cache.get(cacheKey);
        if (routes != null)
            return routes;

        final String url = YahooTransitSearchURLBuilder.build(from, to);

        navigateTo(url);

        routes = new HashSet<Route>();

        try
        {
            for (final WebElement element : findElements("//ul[@class='routeList']/li/dl/dd/ul"))
                routes.add(createRoute(from, to, element));

            click("//ul[@id='tabflt']/li/a[@data-rapid_p='2']");

            for (final WebElement element : findElements("//ul[@class='routeList']/li/dl/dd/ul"))
                routes.add(createRoute(from, to, element));

            click("//ul[@id='tabflt']/li/a[@data-rapid_p='3']");

            for (final WebElement element : findElements("//ul[@class='routeList']/li/dl/dd/ul"))
                routes.add(createRoute(from, to, element));
        }
        catch (final NoSuchElementException e)
        {
        }

        cache.put(cacheKey, routes);

        return routes;
    }

    private static final By TIME_XPATH = By.xpath("./li[@class='time']/span[@class='small']");
    private static final By FARE_XPATH = By.xpath("(./li[@class='fare']/div[@class='mark'] | ./li[@class='fare'])[1]");
    private static final By TRANSFER_XPATH = By.xpath("(./li[@class='transfer']/div[@class='mark'] | ./li[@class='transfer'])[1]");

    private static Route createRoute(String from, String to, WebElement element)
    {
        final int time = parseTime(element.findElement(TIME_XPATH).getText());
        final int fare = parseFare(element.findElement(FARE_XPATH).getText());
        final int transfer = parseTransfert(element.findElement(TRANSFER_XPATH).getText());

        return new Route(from, to, time, fare, transfer);
    }

    // 1時間47分 -> 107
    private static int parseTime(String timeString)
    {
        final int hourIndex = timeString.indexOf("時間");
        final int minuteIndex = timeString.indexOf('分');

        if (hourIndex != -1)
        {
            final int hour = Integer.parseInt(timeString.substring(0, hourIndex));
            final int minutes = Integer.parseInt(timeString.substring(hourIndex + 2, minuteIndex));

            return hour * 60 + minutes;
        }
        else
            return Integer.parseInt(timeString.substring(0, minuteIndex));
    }

    // 1,329円 -> 1329
    private static int parseFare(String fareString)
    {
        return Integer.parseInt(fareString.replace(",", "").replace("円", ""));
    }

    // 乗換：2回
    // 0回
    private static int parseTransfert(String transfertString)
    {
        if (transfertString.startsWith("乗換："))
            return Integer.parseInt(transfertString.substring(3, transfertString.length() - 1));
        else
            return Integer.parseInt(transfertString.substring(0, transfertString.length() - 1));
    }
}
