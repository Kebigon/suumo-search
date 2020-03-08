package xyz.kebigon.housesearch.mail;

import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

import javax.mail.Session;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import xyz.kebigon.housesearch.HouseSearchApplication;
import xyz.kebigon.housesearch.domain.Posting;
import xyz.kebigon.housesearch.domain.Route;

public class EmailSender
{
    private final Session mailSession;

    public EmailSender() throws IOException
    {
        final Properties mailProperties = new Properties();
        mailProperties.load(HouseSearchApplication.class.getClassLoader().getResourceAsStream("mail.cfg"));
        mailSession = Session.getInstance(mailProperties);
    }

    public void send(Collection<Posting> postings) throws EmailException
    {
        // Send email separately
        if (postings.size() <= 3)
        {
            for (final Posting posting : postings)
            {
                final StringBuilder builder = new StringBuilder();
                appendPosting(posting, builder);
                send(posting.getAge() + "yo house found near " + posting.getStation() + " station for " + (posting.getPrice() / 10000) + "万円",
                        builder.toString());
            }
        }
        // Send one email with all results
        else
        {
            final StringBuilder builder = new StringBuilder();
            for (final Posting posting : postings)
                appendPosting(posting, builder);
            send(postings.size() + " houses found", builder.toString());
        }
    }

    private void send(String title, String content) throws EmailException
    {
        final Email email = new HtmlEmail();
        email.setMailSession(mailSession);
        email.setSubject(title);
        email.setCharset("UTF-8");
        email.setMsg(content);

        email.setFrom(mailSession.getProperty("housesearch.mail.to").trim());
        for (final String address : mailSession.getProperty("housesearch.mail.to").split(","))
            email.addTo(address.trim());

        email.setFrom(mailSession.getProperty("housesearch.mail.bcc").trim());
        for (final String address : mailSession.getProperty("housesearch.mail.bcc").split(","))
            email.addBcc(address.trim());

        email.send();
    }

    private static void appendPosting(Posting posting, StringBuilder builder)
    {
        builder.append("<p><a href=\"").append(posting.getUrl()).append("\">");
        builder.append(posting.getAge()).append("yo house near ").append(posting.getStation()).append(" station for ").append(posting.getPrice() / 10000)
                .append("万円");
        builder.append("</a>");
        builder.append("<br>House surface: ").append(Math.round(posting.getHouseSurface())).append("m2 / ")
                .append(Math.round(posting.getHouseSurface() * 0.3025)).append("坪");
        builder.append("<br>Land surface: ").append(Math.round(posting.getLandSurface())).append("m2 / ").append(Math.round(posting.getLandSurface() * 0.3025))
                .append("坪");

        appendRoutes(posting, builder);

        builder.append("</p>");
    }

    private static void appendRoutes(Posting posting, StringBuilder builder)
    {
        final Route fastestRoute = posting.getFastestRoute();
        final Route cheapestRoute = posting.getCheapestRoute();
        final Route easiestRoute = posting.getEasiestRoute();

        // No route have been analyzed
        if (fastestRoute == null && cheapestRoute == null && easiestRoute == null)
            return;

        builder.append("<br>Routes: ");

        if (fastestRoute == cheapestRoute)
            if (fastestRoute == easiestRoute)
                appendRoute("Best", posting, fastestRoute, builder);
            else
            {
                appendRoute("Fastest & Cheapest", posting, fastestRoute, builder);
                appendRoute("Easiest", posting, easiestRoute, builder);
            }
        else if (fastestRoute == easiestRoute)
        {
            appendRoute("Fastest & Easiest", posting, fastestRoute, builder);
            appendRoute("Cheapest", posting, cheapestRoute, builder);
        }
        else
        {
            appendRoute("Fastest", posting, fastestRoute, builder);

            if (cheapestRoute == easiestRoute)
                appendRoute("Cheapest & Easiest", posting, cheapestRoute, builder);
            else
            {
                appendRoute("Cheapest", posting, cheapestRoute, builder);
                appendRoute("Easiest", posting, easiestRoute, builder);
            }
        }
    }

    private static void appendRoute(String label, Posting property, Route route, StringBuilder builder)
    {
        if (route == null)
            return;

        builder.append("<br>- ").append(label).append(": ");
        builder.append(route.getTime() + property.getWalkTimeToStation()).append("min to ").append(route.getTo());
        builder.append(" (walk: ").append(property.getWalkTimeToStation()).append("min, train: ").append(route.getTime()).append("min)");
        builder.append(" / ").append(route.getFare()).append("円");
        builder.append(" / ").append(route.getTransfer()).append(" transfer");
    }
}
