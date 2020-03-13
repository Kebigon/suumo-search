package xyz.kebigon.housesearch;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.mail.EmailException;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.browser.suumo.SuumoBrowser;
import xyz.kebigon.housesearch.browser.yahoo.transit.YahooTransitBrowser;
import xyz.kebigon.housesearch.domain.Posting;
import xyz.kebigon.housesearch.domain.SearchConditions;
import xyz.kebigon.housesearch.domain.SearchConditionsValidator;
import xyz.kebigon.housesearch.file.SentPostingsCache;
import xyz.kebigon.housesearch.mail.EmailSender;

@Slf4j
public class HouseSearchApplication
{
    public static void main(String[] args) throws IOException, EmailException
    {
        final SearchConditions conditions = SearchConditions.load();
        final SentPostingsCache sentPostings = SentPostingsCache.load();

        try
        {
            Collection<Posting> postings;

            try (final SuumoBrowser suumo = new SuumoBrowser())
            {
                postings = suumo.search(conditions, sentPostings);
            }

            if (postings.isEmpty())
            {
                log.info("No postings found on Suumo, terminating");
                return;
            }

            if (!StringUtils.isEmpty(conditions.getExpression()))
            {
                try (final YahooTransitBrowser yahooTransit = new YahooTransitBrowser())
                {
                    ApplicationContext.setYahooTransitBrowser(yahooTransit);

                    postings = postings.stream() // Do not parallel here
                            .filter(property -> SearchConditionsValidator.validateExpression(property, conditions)).collect(Collectors.toList());
                }

                if (postings.isEmpty())
                {
                    log.info("No postings left after applying expression filter, terminating");
                    return;
                }
            }

            log.info("=======[ RESULTS ]=======");
            log.info("Found {} postings", postings.size());

            for (final Posting posting : postings)
                log.info("-> {}", posting);

            log.info("Sending email notification");

            final EmailSender sender = new EmailSender();
            sender.send(postings);

            // Register sent postings
            postings.forEach(sentPostings::add);

            log.info("Email notification sent, terminating");
        }
        catch (final Throwable t)
        {
            log.error("Unrecoverable exception", t);
        }
        finally
        {
            sentPostings.save();
        }
    }
}
