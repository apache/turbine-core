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

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
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
    private static String fileSeperator = System.getProperty("file.separator");

    
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
        ExtendedProperties ep = ((TurbineVelocityService) vs).createVelocityProperties(conf);

        String rootPath = Turbine.getRealPath("");

        String [] test1 = ep.getStringArray("test1.resource.loader.path");
        assertEquals("No Test1 Property found", 1, test1.length);
        assertEquals("Test1 Path translation failed", rootPath
                +fileSeperator+"relative"+fileSeperator+"path" , test1[0]);

        String [] test2 = ep.getStringArray("test2.resource.loader.path");
        assertEquals("No Test2 Property found", 1, test2.length);
        assertEquals("Test2 Path translation failed", rootPath
                +fileSeperator+"absolute"+fileSeperator+"path" , test2[0]);

        String [] test3 = ep.getStringArray("test3.resource.loader.path");
        assertEquals("No Test3 Property found", 1, test2.length);
        assertEquals("Test3 Path translation failed", rootPath
                +fileSeperator+"jar-file.jar!/", test3[0]);

        String [] test4 = ep.getStringArray("test4.resource.loader.path");
        assertEquals("No Test4 Property found", 1, test4.length);
        assertEquals("Test4 Path translation failed", rootPath
                +fileSeperator+"jar-file.jar!/with/some/extensions" , test4[0]);

        String [] test5 = ep.getStringArray("test5.resource.loader.path");
        assertEquals("No Test5 Property found", 1, test5.length);
        assertEquals("Test5 Path translation failed", rootPath
                +fileSeperator+"jar-file.jar" , test5[0]);

        String [] test6 = ep.getStringArray("test6.resource.loader.path");
        assertEquals("No Test6 Property found", 1, test6.length);
        assertEquals("Test6 Path translation failed", "jar:http://jar.on.website/" , test6[0]);

        String [] test7 = ep.getStringArray("test7.resource.loader.path");
        assertEquals("No Test7 Property found", 1, test7.length);
        assertEquals("Test7 Path translation failed", rootPath
                +fileSeperator+"file"+fileSeperator
                +"system"+fileSeperator+"reference" , test7[0]);

        String [] test8 = ep.getStringArray("test8.resource.loader.path");
        assertEquals("No Test8 Property found", 1, test8.length);
        assertEquals("Test8 Path translation failed", "http://reference.on.website/" , test8[0]);

    }
}
