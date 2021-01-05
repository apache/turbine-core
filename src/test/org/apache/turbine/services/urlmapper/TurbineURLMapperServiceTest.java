package org.apache.turbine.services.urlmapper;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.TemplateURI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TurbineURLMapperServiceTest extends BaseTestCase
{
    private TurbineConfig tc = null;

    private URLMapperService urlMapper = null;

    @Before
    public void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TurbineURLMapperServiceTest.properties");
        tc.initialize();

        urlMapper = (URLMapperService)TurbineServices.getInstance().getService(URLMapperService.SERVICE_NAME);
    }

    @After
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    @Test
    public void testMapToURL() throws Exception
    {
        assertNotNull(urlMapper);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        PipelineData pipelineData = getPipelineData(request, response, tc.getTurbine().getServletConfig());
        assertNotNull(pipelineData);

        TemplateURI uri = new TemplateURI(pipelineData.getRunData());
        uri.clearResponse(); // avoid encoding on mocked HTTPServletResponse
        uri.addPathInfo("bookId", 123);
        uri.setTemplate("Book.vm");
        uri.addQueryData("detail", 0);

        urlMapper.mapToURL(uri);
        assertEquals("/wow/book/123", uri.getRelativeLink());
        assertTrue(uri.getPathInfo().isEmpty());
        assertTrue(uri.getQueryData().isEmpty());

        uri = new TemplateURI(pipelineData.getRunData());
        uri.clearResponse(); // avoid encoding on mocked HTTPServletResponse
        uri.addPathInfo("bookId", 123);
        uri.setTemplate("Book.vm");
        uri.addQueryData("detail", 1);
        uri.addQueryData("detail", 2);
        uri.addQueryData("view", "collapsed");

        urlMapper.mapToURL(uri);
        assertEquals("/wow/book/123/1?view=collapsed", uri.getRelativeLink());
        assertTrue(uri.getPathInfo().isEmpty());
        assertEquals(1, uri.getQueryData().size());
    }

    @Test
    public void testMapFromURL() throws Exception
    {
        assertNotNull(urlMapper);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        PipelineData pipelineData = getPipelineData(request, response, tc.getTurbine().getServletConfig());
        assertNotNull(pipelineData);
        ParameterParser pp = pipelineData.get(Turbine.class, ParameterParser.class);
        assertNotNull(pp);
        assertTrue(pp.keySet().isEmpty());

        urlMapper.mapFromURL("/app/book/123/4", pp);

        assertEquals(3, pp.keySet().size());
        assertEquals(123, pp.getInt("bookId"));
        assertEquals("Book.vm", pp.getString("template"));
        assertEquals(4, pp.getInt("detail"));
    }

}
