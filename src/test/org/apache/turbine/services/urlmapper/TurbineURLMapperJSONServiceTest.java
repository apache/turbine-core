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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.TemplateURI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class TurbineURLMapperJSONServiceTest extends BaseTestCase
{

    private static TurbineConfig tc = null;

    private static URLMapperService urlMapper = null;

    private RunData data;

    Logger log = LogManager.getLogger();

    @BeforeAll
    public static void setUp() throws Exception
    {
        tc = new TurbineConfig( ".", "/conf/test/TurbineURLMapperJSONServiceTest.properties" );
        tc.initialize();

        urlMapper = (URLMapperService) TurbineServices.getInstance().getService( URLMapperService.SERVICE_NAME );
    }

    @AfterAll
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    @BeforeEach
    public void init() throws Exception
    {

        ServletConfig config = tc.getTurbine().getServletConfig();
        // mock(ServletConfig.class);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = mock( HttpServletResponse.class );

        data = getRunData( request, response, config );

        Mockito.when( response.encodeURL( Mockito.anyString() ) )
                .thenAnswer( invocation -> invocation.getArgument( 0 ) );
    }

    @Test
    public void testIgnoreParameterForShortURL() throws Exception
    {

        PipelineData pipelineData = data;

        assertNotNull( urlMapper );

        ParameterParser pp = pipelineData.get( Turbine.class, ParameterParser.class );
        assertNotNull( pp );
        assertTrue( pp.keySet().isEmpty() );
        pp.clear();

        urlMapper.mapFromURL( "/app/context/contact", pp );

        log.info( "parameters: {}", pp );
        assertEquals( 2, pp.keySet().size() );
        assertEquals( "anon", pp.getString( "role" ) );
        assertEquals( "Contact", pp.getString( "page" ) );

        TemplateURI uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.addPathInfo( pp );

        // this is an artifical url
        assertEquals( "scheme://bob/wow/damn2/page/Contact/role/anon", uri2.getAbsoluteLink() );

        uri2.addPathInfo( "language", "en" );
        assertEquals( "scheme://bob/wow/damn2/page/Contact/role/anon/language/en", uri2.getAbsoluteLink() );

        urlMapper.mapToURL( uri2 );
        String expectedMappedURL = "/wow/damn2/contact";
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

        pp.clear();
        urlMapper.mapFromURL( uri2.getRelativeLink(), pp );

        log.info( "parameters: {}", pp );
        assertEquals( 2, pp.keySet().size() );
        assertEquals( "anon", pp.getString( "role" ) );
        assertEquals( "Contact", pp.getString( "page" ) );

        uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.addPathInfo( pp );

        urlMapper.mapToURL( uri2 );
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

    }

    @Test
    public void testNonOptionalParameterForShortURL() throws Exception
    {

        PipelineData pipelineData = data;

        assertNotNull( urlMapper );

        ParameterParser pp = pipelineData.get( Turbine.class, ParameterParser.class );
        assertNotNull( pp );
        assertTrue( pp.keySet().isEmpty() );
        pp.clear();

        urlMapper.mapFromURL( "/wow/damn2/register", pp );

        log.info( "parameters: {}", pp );
        assertEquals( 2, pp.keySet().size() );
        assertEquals( "anon", pp.getString( "role" ) );
        assertEquals( "Register", pp.getString( "page" ) );

        TemplateURI uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.addPathInfo( pp );

        // this is an artifical url
        assertEquals( "scheme://bob/wow/damn2/page/Register/role/anon", uri2.getAbsoluteLink() );

        uri2.addPathInfo( "language", "en" );
        assertEquals( "scheme://bob/wow/damn2/page/Register/role/anon/language/en", uri2.getAbsoluteLink() );

        urlMapper.mapToURL( uri2 );
        String expectedMappedURL = "/wow/damn2/en/register";
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

        pp.clear();
        urlMapper.mapFromURL( uri2.getRelativeLink(), pp );

        log.info( "parameters: {}", pp );
        assertEquals( 3, pp.keySet().size() );
        assertEquals( "anon", pp.getString( "role" ) );
        assertEquals( "Register", pp.getString( "page" ) );
        assertEquals( "en", pp.getString( "language" ) );

        uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.addPathInfo( pp );

        urlMapper.mapToURL( uri2 );
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

    }

}
