package org.apache.turbine.util.uri;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.TestSuite;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.ServerData;
import org.apache.turbine.util.parser.DefaultParameterParser;
import org.apache.turbine.util.parser.ParameterParser;
import org.apache.turbine.util.parser.ParserUtils;

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

    /**
     * Constructor for test.
     *
     * @param testName name of the test being executed
     */
    public TurbineURITest(String testName)
            throws Exception
    {
        super(testName);

        // Setup configuration
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");
        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(ParserUtils.URL_CASE_FOLDING_KEY,
                ParserUtils.URL_CASE_FOLDING_LOWER_VALUE );
        serviceManager.setConfiguration(cfg);

    }

    /**
     * Performs any initialization that must happen before each test is run.
     */
    protected void setUp()
    {
        ServerData sd = new ServerData("www.testserver.com",
                URIConstants.HTTP_PORT, URIConstants.HTTP,
                "/servlet/turbine", "/context");
        turi = new TurbineURI(sd);
    }

    /**
     * Clean up after each test is run.
     */
    protected void tearDown()
    {
        turi = null;
    }

    /**
     * Factory method for creating a TestSuite for this class.
     *
     * @return the test suite
     */
    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite(TurbineURITest.class);
        return suite;
    }

    public void testAddRemove()
    {
        assertEquals("TurbineURI should not have a pathInfo", false, turi.hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi.hasQueryData());
        turi.addPathInfo("test","x");
        assertEquals("TurbineURI must have a pathInfo", true, turi.hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi.hasQueryData());
        turi.removePathInfo("test");
        assertEquals("TurbineURI must not have a pathInfo", false, turi.hasPathInfo());
        assertEquals("TurbineURI must not have a queryData", false, turi.hasQueryData());

        assertEquals("TurbineURI should not have a queryData", false, turi.hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi.hasPathInfo());
        turi.addQueryData("test","x");
        assertEquals("TurbineURI must have a queryData", true, turi.hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi.hasPathInfo());
        turi.removeQueryData("test");
        assertEquals("TurbineURI must not have a queryData", false, turi.hasQueryData());
        assertEquals("TurbineURI must not have a pathInfo", false, turi.hasPathInfo());
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
        assertEquals("/context/servlet/turbine?test=null", turi.getRelativeLink());
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
        assertEquals("/context/servlet/turbine/test/null", turi.getRelativeLink());
        turi.removePathInfo("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

    public void testAddEmptyParameterParser()
    {
        ParameterParser pp = new DefaultParameterParser();
        turi.add(1, pp); // 1 = query data
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }

    public void testAddParameterParser()
    {
        ParameterParser pp = new DefaultParameterParser();
        pp.add("test", "");
        turi.add(1, pp); // 1 = query data
        assertEquals("/context/servlet/turbine?test=", turi.getRelativeLink());
        turi.removeQueryData("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
        
        pp = new DefaultParameterParser();
        pp.add("test", (String) null);
        turi.add(1, pp); // 1 = query data
        // Should make the following work so as to be consistent with directly added values. 
        //assertEquals("/context/servlet/turbine?test=null", turi.getRelativeLink());
        turi.removeQueryData("test");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());

        // TRB-8
        pp = new DefaultParameterParser();
        DefaultFileItemFactory factory = new DefaultFileItemFactory(10240, null);
        FileItem test = factory.createItem("upload-field", "application/octet-stream", false, null);
        pp.add("upload-field", test);
        turi.add(1, pp); // 1 = query data
        assertEquals("/context/servlet/turbine?upload-field=", turi.getRelativeLink());
        turi.removeQueryData("upload-field");
        assertEquals("/context/servlet/turbine", turi.getRelativeLink());
    }
    
}
