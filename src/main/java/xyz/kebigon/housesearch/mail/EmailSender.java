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
import xyz.kebigon.housesearch.domain.SearchConditions;

public class EmailSender
{
	private static final String ONE_POSTING_TITLE = "[%1$s] %2$syo house found near %3$s station for %4$s万円";
	private static final String SEVERAL_POSTINGS_TITLE = "[%1$s] %2$s houses found";

	private final Session mailSession;

	public EmailSender() throws IOException
	{
		final Properties mailProperties = new Properties();
		mailProperties.load(HouseSearchApplication.class.getClassLoader().getResourceAsStream("mail.cfg"));
		mailSession = Session.getInstance(mailProperties);
	}

	public void send(Collection<Posting> postings, SearchConditions conditions) throws EmailException
	{
		// Send email separately
		if (postings.size() <= 3)
		{
			for (final Posting posting : postings)
			{
				final String title = String.format(ONE_POSTING_TITLE, conditions.getName(), posting.getAge(), posting.getStation(), posting.getPrice() / 10000);

				final StringBuilder builder = new StringBuilder();
				appendPosting(posting, builder);
				send(title, builder.toString());
			}
		}
		// Send one email with all results
		else
		{
			final String title = String.format(SEVERAL_POSTINGS_TITLE, conditions.getName(), postings.size());

			final StringBuilder builder = new StringBuilder();
			for (final Posting posting : postings)
				appendPosting(posting, builder);
			send(title, builder.toString());
		}
	}

	private void send(String title, String content) throws EmailException
	{
		final Email email = new HtmlEmail();
		email.setMailSession(mailSession);
		email.setSubject(title);
		email.setCharset("UTF-8");
		email.setMsg(content);

		String from = mailSession.getProperty("housesearch.mail.from");
		String to = mailSession.getProperty("housesearch.mail.to");
		String bcc = mailSession.getProperty("housesearch.mail.bcc");

		if (from != null && !(from = from.trim()).isEmpty())
			email.setFrom(from);

		if (to != null && !(to = to.trim()).isEmpty())
			for (final String address : to.split(","))
				email.addTo(address.trim());

		if (bcc != null && !(bcc = bcc.trim()).isEmpty())
			for (final String address : bcc.split(","))
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
