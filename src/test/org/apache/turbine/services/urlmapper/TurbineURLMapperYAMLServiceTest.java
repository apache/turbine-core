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

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.log.Log;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.TemplateURI;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Tag("yaml")
public class TurbineURLMapperYAMLServiceTest extends BaseTestCase
{

    private static TurbineConfig tc = null;

    private static URLMapperService urlMapper = null;

    private RunData data;

    private Log log = Log.getLog(TurbineURLMapperYAMLServiceTest.class);

    @BeforeAll
    public static void setUp() throws Exception
    {
        tc = new TurbineConfig( ".", "/conf/test/TurbineURLMapperYAMLServiceTest.properties" );
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
    public void testMapToAnotherURL() throws Exception
    {

        PipelineData pipelineData = data;

        assertNotNull( urlMapper );

        TemplateURI uri = new TemplateURI( pipelineData.getRunData() );
        uri.addPathInfo( "id", 1234 );
        uri.addPathInfo( "role", "guest" );
        uri.addPathInfo( "language", "de" );

        String unMappedURL = uri.getAbsoluteLink(); // scheme://bob/wow/damn2/id/1234/role/guest
        log.info( unMappedURL );

        String expectedRawURL = "scheme://bob/wow/damn2/id/1234/role/guest/language/de";
        urlMapper.mapToURL( uri );
        urlMapper.mapToURL( uri ); // should be idempotent
        // raw url
        assertEquals( expectedRawURL, unMappedURL );

        String mappedLink = uri.getRelativeLink(); // wow/damn2/id/1234/role/guest
        log.info( mappedLink );
        String expectedMappedURL = "/wow/1234/guest/de";
        assertEquals( expectedMappedURL, mappedLink );

        ParameterParser pp = pipelineData.get( Turbine.class, ParameterParser.class );
        assertNotNull( pp );
        assertTrue( pp.keySet().isEmpty() );
        urlMapper.mapFromURL( mappedLink, pp );

        assertEquals( 5, pp.keySet().size() );
        assertEquals( 1234, pp.getInt( "id" ) );
        assertEquals( "guest", pp.getString( "role" ) );
        assertEquals( "de", pp.getString( "language" ) );
        assertEquals( "html", pp.getString( "media-type" ) );

        TemplateURI uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.setTemplate( "default.vm" );
        uri2.addPathInfo( pp );
        // this is an artifical url
        // "scheme://bob/wow/damn2/template/default.vm/media-type/html/role/guest/id/1234/language/de",
        // but entries may be reversed.
        assertTrue( uri2.getAbsoluteLink().startsWith( "scheme://bob/wow/damn2/template/default.vm/") );
        uri2.getPathInfo().stream()
            .filter( entry -> entry.getKey().equals("role") )
            .findFirst()
            .get().getValue().equals( "guest" );
        uri2.getPathInfo().stream()
        .filter( entry -> entry.getKey().equals("language") )
        .findFirst()
        .get().getValue().equals( "de" );
        urlMapper.mapToURL( uri2 );
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );
    }

    @Test
    public void testOverrideShortURL() throws Exception
    {

        PipelineData pipelineData = data;

        assertNotNull( urlMapper );

        ParameterParser pp = pipelineData.get( Turbine.class, ParameterParser.class );
        assertNotNull( pp );
        assertTrue( pp.keySet().isEmpty() );

        pp.add( "role", "admin" ); // will not be overridden
        urlMapper.mapFromURL( "/app/register", pp );

        log.info( "parameters: {0}", pp );
        assertEquals( 4, pp.keySet().size() );
        assertEquals( "random-id-123-abc", pp.getString( "js_pane" ) );
        assertEquals( "admin", pp.getString( "role" ) );
        assertEquals( "html", pp.getString( "media-type" ) );
        assertEquals( "Registerone.vm", pp.getString( "template" ) );

        TemplateURI uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.setTemplate( "Registerone.vm" );
        pp.remove( "role" );
        pp.add( "role", "anon" );
        uri2.addPathInfo( pp );

        // this is an artifical url, as the exact sequence could not be reconstructed as
        // ParameterParser uses expicitely a random access table
        assertEquals(
                "scheme://bob/wow/damn2/template/Registerone.vm/media-type/html/js_pane/random-id-123-abc/role/anon",
                uri2.getAbsoluteLink() );
        urlMapper.mapToURL( uri2 );
        String expectedMappedURL = "/wow/damn2/register";
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

        pp.clear();
        pp.add( "role", "admin" );// will be overridden
        urlMapper.mapFromURL( "/app/contact", pp );
        log.info( "parameters: {0}", pp );
        assertEquals( 4, pp.keySet().size() );
        assertEquals( "anon", pp.getString( "role" ) );
        assertEquals( "another-random-id-876-dfg", pp.getString( "js_pane" ) );

        uri2 = new TemplateURI( pipelineData.getRunData() );
        uri2.clearResponse();
        uri2.addPathInfo( pp );

        // this is an artifical url
        assertEquals( "scheme://bob/wow/damn2/page/Contact/media-type/html/js_pane/another-random-id-876-dfg/role/anon",
                uri2.getAbsoluteLink() );
        urlMapper.mapToURL( uri2 );
        expectedMappedURL = "/wow/damn2/contact";
        assertEquals( expectedMappedURL, uri2.getRelativeLink() );

    }

//	/**
//	 * 		Not implemented Test for MappedTemplateLink:
//	 * - To work with <i>MappedTemplateLink</i>, we need access to the urlmapperservice in order to
//	 * - simulate a request without pipeline (setting velocity context and initializing the service):
//	 */
//   @Test
//   public void testMappedURILink() {
//   	MappedTemplateLink ml = MappedTemplateLink.class.getDeclaredConstructor().newInstance();
//   	assertNotNull(ml);
//   	ml.setUrlMapperService(urlMapper);
//   	ml.init(data);
//   }

}
