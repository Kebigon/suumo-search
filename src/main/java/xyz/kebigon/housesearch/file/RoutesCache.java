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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import xyz.kebigon.housesearch.domain.Route;

@Slf4j
@Data
@NoArgsConstructor
public class RoutesCache
{
    private Map<String, Collection<Route>> cache = new HashMap<>();

    public Collection<Route> get(String cacheKey)
    {
        return cache.get(cacheKey);
    }

    public void put(String cacheKey, Collection<Route> routes)
    {
        cache.put(cacheKey, routes);
    }

    public void save(File file) throws JsonGenerationException, JsonMappingException, IOException
    {
        log.info("Saving {} routes to {}", cache.size(), file);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, this);
    }

    public static RoutesCache load(File file) throws JsonParseException, JsonMappingException, IOException
    {
        if (file.exists())
        {
            log.info("Loading routes cache from {}", file);

            final ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(file, RoutesCache.class);
        }
        else
            return new RoutesCache();
    }
}
