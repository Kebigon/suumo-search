package xyz.kebigon.housesearch.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;

import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.domain.Posting;

@Slf4j
public class SearchArchive implements Closeable
{
    private static final File ARCHIVE_FILE = new File("var/state/archive");
    static
    {
        ARCHIVE_FILE.getParentFile().mkdirs();
    }

    private final Collection<String> urls = new HashSet<String>();

    public SearchArchive() throws IOException
    {
        if (ARCHIVE_FILE.exists())
        {
            try (final BufferedReader reader = new BufferedReader(new FileReader(ARCHIVE_FILE)))
            {
                String line;
                while ((line = reader.readLine()) != null)
                    urls.add(line);
            }

            log.info("Loaded {} urls from {}", urls.size(), ARCHIVE_FILE.getAbsolutePath());
        }
    }

    public boolean filter(Posting property)
    {
        if (urls.contains(property.getUrl()))
            return false;

        urls.add(property.getUrl());
        return true;
    }

    @Override
    public void close() throws IOException
    {
        try (final Writer writer = new BufferedWriter(new FileWriter(ARCHIVE_FILE)))
        {
            for (final String url : urls)
                writer.write(url + '\n');
        }

        log.info("Saved {} urls to {}", urls.size(), ARCHIVE_FILE.getAbsolutePath());
    }
}
