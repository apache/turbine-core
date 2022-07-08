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


import org.apache.commons.configuration2.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests startup of the Velocity Service and translation of various
 * path patterns.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 *
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PathConverterTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;

    @TurbineService
    private VelocityService vs = null;

    @BeforeAll
    public void setUp() throws Exception {
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        AnnotationProcessor.process(this);
        //vs = (VelocityService) TurbineServices.getInstance().getService(VelocityService.SERVICE_NAME);
        assertNotNull(vs);
    }

    @AfterAll
    public void destroy() throws Exception {
        vs.shutdown();
        tc.dispose();
    }

    @Test
    void testService()
        throws Exception
    {
        // Can we start the service?
        assertNotNull(vs, "Could not load Service!");
    }

    @Test
     void testPathTranslation()
        throws Exception
    {
        Configuration conf = vs.getConfiguration();
        VelocityEngine ve = new VelocityEngine();
        ((TurbineVelocityService) vs).setVelocityProperties(ve, conf);

        String rootPath = Turbine.getRealPath("");

        String test1 = (String) ve.getProperty("test1.resource.loader.path");
        assertNotNull( test1, "No Test1 Property found");
        assertEquals( String.join(File.separator, rootPath, "relative", "path"), test1,
                "Test1 Path translation failed");

        String test2 = (String) ve.getProperty("test2.resource.loader.path");
        assertNotNull( test2, "No Test2 Property found");
        assertEquals(String.join(File.separator, rootPath, "absolute", "path"), test2,
                "Test2 Path translation failed");

        String test3 = (String) ve.getProperty("test3.resource.loader.path");
        assertNotNull( test3, "No Test3 Property found");
        assertEquals(
                rootPath +File.separator+"jar-file.jar!/", test3,
                "Test3 Path translation failed");

        String test4 = (String) ve.getProperty("test4.resource.loader.path");
        assertNotNull( test4, "No Test4 Property found");
        assertEquals(rootPath +File.separator+"jar-file.jar!/with/some/extensions" , test4,
                "Test4 Path translation failed");

        String test5 = (String) ve.getProperty("test5.resource.loader.path");
        assertNotNull( test5,"No Test5 Property found");
        assertEquals(rootPath
                +File.separator+"jar-file.jar" , test5,
                "Test5 Path translation failed");

        String test6 = (String) ve.getProperty("test6.resource.loader.path");
        assertNotNull(test6, "No Test6 Property found");
        assertEquals("jar:http://jar.on.website/" , test6,
                "Test6 Path translation failed");

        String test7 = (String) ve.getProperty("test7.resource.loader.path");
        assertNotNull(test7, "No Test7 Property found");
        assertEquals(String.join(File.separator, rootPath, "file", "system", "reference"), test7,
                "Test7 Path translation failed");

        String test8 = (String) ve.getProperty("test8.resource.loader.path");
        assertNotNull(test8, "No Test8 Property found");
        assertEquals("http://reference.on.website/", test8,
                "Test8 Path translation failed");
    }
}
