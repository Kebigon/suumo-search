package xyz.kebigon.housesearch;

import xyz.kebigon.housesearch.browser.yahoo.transit.YahooTransitBrowser;

public class ApplicationContext
{
    private static YahooTransitBrowser yahooTransitBrowser;

    public static void setYahooTransitBrowser(YahooTransitBrowser yahooTransitBrowser)
    {
        ApplicationContext.yahooTransitBrowser = yahooTransitBrowser;
    }

    public static YahooTransitBrowser getYahooTransitBrowser()
    {
        return yahooTransitBrowser;
    }
}
