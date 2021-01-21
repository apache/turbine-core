package org.apache.turbine.services.urlmapper.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

public class URLMappingContainerTest
{
    URLMappingContainer container;

    @Before
    public void setUp() throws Exception
    {
        try (InputStream reader = new FileInputStream("conf/turbine-url-mapping.xml"))
        {
            JAXBContext jaxb = JAXBContext.newInstance(URLMappingContainer.class);
            Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            container = (URLMappingContainer) unmarshaller.unmarshal(reader);
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
