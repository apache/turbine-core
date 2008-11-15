package org.apache.turbine.util.uri;

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

import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.fulcrum.parser.ParameterParser;
import org.apache.fulcrum.parser.ParserService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.TurbineConfig;

/**
 * Testing of the TurbineURI class
 * 
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class TurbineURITest extends BaseTestCase
{
    private TurbineURI turi;

    private ParserService parserService;

    private static TurbineConfig tc = null;

    /**
     * Constructor for test.
     * 
     * @param testName
     *            name of the test being executed
     */
    public TurbineURITest(String testName) throws Exception
    {
        super(testName);

        // Setup configuration
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    /**
     * Performs any initialization that must happen before each test is run.
     */
    protected void setUp()
    {
        ServerData sd = new ServerData("www.testserver.com",
                URIConstants.HTTP_PORT, URIConstants.HTTP, "/servlet/turbine",
                "/context");
        turi = new TurbineURI(sd);

        parserService = (ParserService)TurbineServices.getInstance().getService(ParserService.ROLE);
    }

    /**
     * Clean up after each test is run.
     */
    protected void tearDown()
    {
        if (tc != null) 
        {
            tc.dispose();
        }
        
        turi = null;
    }

    public void testAddRemove()
    {
        assertEquals("TurbineURI should not have a pathInfo", false, turi
                .hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi
                .hasQueryData());
        turi.addPathInfo("test", "x");
        assertEquals("TurbineURI must have a pathInfo", true, turi
                .hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi
                .hasQueryData());
        turi.removePathInfo("test");
        assertEquals("TurbineURI must not have a pathInfo", false, turi
                .hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi
                .hasQueryData());

        assertEquals("TurbineURI should not have a queryData", false, turi
                .hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi
                .hasPathInfo());
        turi.addQueryData("test", "x");
        assertEquals("TurbineURI must have a queryData", true, turi
                .hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi
                .hasPathInfo());
        turi.removeQueryData("test");
        assertEquals("TurbineURI must not have a queryData", false, turi
                .hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi
                .hasPathInfo());
    }

    public void testEmptyAndNullQueryData()
    {
        // Check empty String
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        turi.addQueryData("test", "");
        assertEquals("/context/servlet/turbine?test=", turi.getRelativeLink());
        turi.removeQueryData("test");

        // Check null
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        turi.addQueryData("test", null);
        assertEquals("/context/servlet/turbine?test=null", turi
                .getRelativeLink());
        turi.removeQueryData("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

    public void testEmptyAndNullPathInfo()
    {
        // Check empty String
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        turi.addPathInfo("test", "");
        // Kind of susspect result - might result in "//" in the URL.
        assertEquals("/context/servlet/turbine/test/", turi.getRelativeLink());
        turi.removePathInfo("test");

        // Check null
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        turi.addPathInfo("test", null);
        assertEquals("/context/servlet/turbine/test/null", turi
                .getRelativeLink());
        turi.removePathInfo("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

    public void testAddEmptyParameterParser()
    {
        ParameterParser pp = new DefaultParameterParser();
        turi.add(1, pp); // 1 = query data
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

    public void testAddParameterParser() throws InstantiationException
    {
        ParameterParser pp = (ParameterParser) parserService.getParser(DefaultParameterParser.class);
        pp.add("test", "");
        turi.add(1, pp); // 1 = query data
        assertEquals("/context/servlet/turbine?test=", turi.getRelativeLink());
        turi.removeQueryData("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        
        parserService.putParser(pp);
        pp = (ParameterParser) parserService.getParser(DefaultParameterParser.class);
        pp.add("test", (String) null);
        turi.add(1, pp); // 1 = query data
        // Should make the following work so as to be consistent with directly
        // added values.
        // assertEquals("/context/servlet/turbine?test=null",
        // turi.getRelativeLink());
        turi.removeQueryData("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());

        // TRB-8
        //
        // This is commented out for now as it results in a ClassCastException.
        // The 2_3 branch parser changes need to be merged into the fulcrum
        // code.
        //
        // pp = new DefaultParameterParser();
        // DiskFileItemFactory factory = new DiskFileItemFactory(10240, null);
        // FileItem test = factory.createItem("upload-field",
        // "application/octet-stream", false, null);
        // pp.append("upload-field", test);
        // // The following causes a ClassCastException with or without the
        // TRB-8 fix.
        // turi.add(1, pp); // 1 = query data
        // assertEquals("/context/servlet/turbine?upload-field=",
        // turi.getRelativeLink());
        // turi.removeQueryData("upload-field");
        // assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

}
