package org.apache.turbine.services.urlmapper.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
