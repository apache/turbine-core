package org.apache.turbine.services.urlmapper.model;


import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Tag("yaml")
public class YamlURLMappingContainerTest
{
    private static URLMappingContainer container;

    @BeforeAll
    public static void setUp() throws Exception
    {
        try (InputStream reader = new FileInputStream("conf/turbine-url-mapping.yml"))
        {
        	ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        	// List<URLMapEntry> urlList = 
        	// mapper.readValue(reader, mapper.getTypeFactory().constructCollectionType(List.class, URLMapEntry.class));//            	
        	container = mapper.readValue(reader, URLMappingContainer.class);
        }
    }

    @Test
    public void testGetName()
    {
        assertNotNull(container);
        assertEquals("default", container.getName());
    }

    @Test
    public void testGetMapEntries()
    {
        assertNotNull(container);

        List<URLMapEntry> mapEntries = container.getMapEntries();
        assertNotNull(mapEntries);
        assertNotEquals(0, mapEntries.size());

        URLMapEntry entry = mapEntries.get(0);
        assertNotNull(entry);

        Pattern pattern = entry.getUrlPattern();
        assertNotNull(pattern);
        assertTrue(pattern.matcher("/app/book/123").matches());

        Map<String, String> implicit = entry.getImplicitParameters();
        assertNotNull(implicit);
        assertEquals(2, implicit.size());
        assertEquals("Book.vm", implicit.get("template"));
        assertEquals("0", implicit.get("detail"));
    }

}
