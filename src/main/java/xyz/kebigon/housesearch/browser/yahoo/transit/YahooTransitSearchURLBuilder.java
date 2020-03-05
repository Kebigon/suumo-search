package xyz.kebigon.housesearch.browser.yahoo.transit;

import java.time.LocalDate;

public class YahooTransitSearchURLBuilder
{
    private static final String SEARCH_URI = "https://transit.yahoo.co.jp/search/result";

    public static String build(String from, String to)
    {
        LocalDate today = LocalDate.now();
        switch (today.getDayOfWeek())
        {
            case SUNDAY:
                today = today.plusDays(1);
                break;
            case SATURDAY:
                today = today.plusDays(2);
                break;
            default:
                break;
        }

        final StringBuilder builder = new StringBuilder(SEARCH_URI);
        builder.append("?from=").append(from);
        builder.append("&to=").append(to);
        builder.append("&y=").append(today.getYear());
        builder.append("&m=").append(String.format("%02d", today.getMonthValue()));
        builder.append("&d=").append(String.format("%02d", today.getDayOfMonth()));
        builder.append("&hh=10&m2=0&m1=0"); // 10:00
        builder.append("&type=4");

        return builder.toString();
    }
}
