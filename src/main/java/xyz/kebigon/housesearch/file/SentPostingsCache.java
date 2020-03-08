package xyz.kebigon.housesearch.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.domain.Posting;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SentPostingsCache
{
    private static final File SENT_POSTINGS_FILE = new File("var/state/sent-postings.json");
    static
    {
        SENT_POSTINGS_FILE.getParentFile().mkdirs();
    }

    private final Collection<String> urls = new HashSet<String>();

    public void add(Posting posting)
    {
        urls.add(posting.getUrl());
    }

    public boolean notSent(Posting posting)
    {
        return !urls.contains(posting.getUrl());
    }

    public void save() throws JsonGenerationException, JsonMappingException, IOException
    {
        log.info("Saving {} sent postings to {}", urls.size(), SENT_POSTINGS_FILE.getAbsolutePath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(SENT_POSTINGS_FILE, this);
    }

    public static SentPostingsCache load() throws JsonParseException, JsonMappingException, IOException
    {
        if (SENT_POSTINGS_FILE.exists())
        {
            log.info("Loading sent postings cache from {}", SENT_POSTINGS_FILE.getAbsolutePath());

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(SENT_POSTINGS_FILE, SentPostingsCache.class);
        }
        else
            return new SentPostingsCache();
    }
}
