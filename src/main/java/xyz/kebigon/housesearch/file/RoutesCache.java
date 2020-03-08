package xyz.kebigon.housesearch.file;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.domain.Route;

@Slf4j
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutesCache
{
    private static final File ROUTES_CACHE_FILE = new File("var/state/routes-cache.json");
    static
    {
        ROUTES_CACHE_FILE.getParentFile().mkdirs();
    }

    private Map<String, Collection<Route>> cache = new HashMap<>();

    public Collection<Route> get(String cacheKey)
    {
        return cache.get(cacheKey);
    }

    public void put(String cacheKey, Collection<Route> routes)
    {
        cache.put(cacheKey, routes);
    }

    public void save() throws JsonGenerationException, JsonMappingException, IOException
    {
        log.info("Saving {} routes to {}", cache.size(), ROUTES_CACHE_FILE.getAbsolutePath());
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(ROUTES_CACHE_FILE, this);
    }

    public static RoutesCache load() throws JsonParseException, JsonMappingException, IOException
    {
        if (ROUTES_CACHE_FILE.exists())
        {
            log.info("Loading routes cache from {}", ROUTES_CACHE_FILE.getAbsolutePath());

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(ROUTES_CACHE_FILE, RoutesCache.class);
        }
        else
            return new RoutesCache();
    }
}
