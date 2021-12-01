package org.apache.turbine.services.velocity;


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

import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.app.VelocityEngine;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests startup of the Velocity Service and translation of various
 * path patterns.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class PathConverterTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static VelocityService vs = null;

    @BeforeClass
    public static void setUp() throws Exception {
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        vs = (VelocityService) TurbineServices.getInstance().getService(VelocityService.SERVICE_NAME);
    }

    @AfterClass
    public static void destroy() throws Exception {
        vs.shutdown();
        tc.dispose();
    }

    @Test public void testService()
        throws Exception
    {

        // Can we start the service?
        assertNotNull("Could not load Service!", vs);
    }

    @Test
    public void testPathTranslation()
        throws Exception
    {
        Configuration conf = vs.getConfiguration();
        VelocityEngine ve = new VelocityEngine();
        ((TurbineVelocityService) vs).setVelocityProperties(ve, conf);

        String rootPath = Turbine.getRealPath("");

        String test1 = (String) ve.getProperty("test1.resource.loader.path");
        assertNotNull("No Test1 Property found", test1);
        assertEquals("Test1 Path translation failed",
                String.join(File.separator, rootPath, "relative", "path"), test1);

        String test2 = (String) ve.getProperty("test2.resource.loader.path");
        assertNotNull("No Test2 Property found", test2);
        assertEquals("Test2 Path translation failed",
                String.join(File.separator, rootPath, "absolute", "path"), test2);

        String test3 = (String) ve.getProperty("test3.resource.loader.path");
        assertNotNull("No Test3 Property found", test3);
        assertEquals("Test3 Path translation failed",
                rootPath +File.separator+"jar-file.jar!/", test3);

        String test4 = (String) ve.getProperty("test4.resource.loader.path");
        assertNotNull("No Test4 Property found", test4);
        assertEquals("Test4 Path translation failed", rootPath
                +File.separator+"jar-file.jar!/with/some/extensions" , test4);

        String test5 = (String) ve.getProperty("test5.resource.loader.path");
        assertNotNull("No Test5 Property found", test5);
        assertEquals("Test5 Path translation failed", rootPath
                +File.separator+"jar-file.jar" , test5);

        String test6 = (String) ve.getProperty("test6.resource.loader.path");
        assertNotNull("No Test6 Property found", test6);
        assertEquals("Test6 Path translation failed", "jar:http://jar.on.website/" , test6);

        String test7 = (String) ve.getProperty("test7.resource.loader.path");
        assertNotNull("No Test7 Property found", test7);
        assertEquals("Test7 Path translation failed",
                String.join(File.separator, rootPath, "file", "system", "reference"), test7);

        String test8 = (String) ve.getProperty("test8.resource.loader.path");
        assertNotNull("No Test8 Property found", test8);
        assertEquals("Test8 Path translation failed", "http://reference.on.website/" , test8);
    }
}
