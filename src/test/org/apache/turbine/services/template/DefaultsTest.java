package org.apache.turbine.services.template;


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
import static org.junit.Assert.assertFalse;


import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests all the various defaults for the Template Service.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DefaultsTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;


    @BeforeClass
    public static void setUp() throws Exception {
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        ts = (TemplateService) TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
    }

    @AfterClass
    public static void destroy() throws Exception {
        ts.shutdown();
        tc.dispose();
    }

    @Test
    public void testDefaults()
    {
        // Test if the caching property was loaded correctly. (key:module.cache)
        assertFalse("isCaching failed!", ts.isCaching());

        // Test if the default values for Template and Extension were loaded correctly
        assertEquals("Default Extension failed",      ts.getDefaultExtension(), "");
        assertEquals("Default Template failed",       ts.getDefaultTemplate(), TemplateService.DEFAULT_TEMPLATE_VALUE);
    }

    @Test
    public void testTemplateDefaults()
    {
        // Test if the Default-Values for the Screen, Layout and Navigation classes and the Layout Template are correct.
        assertEquals("Default Page failed",           TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultPage());
        assertEquals("Default Screen failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultScreen());
        assertEquals("Default Layout failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayout());
        assertEquals("Default Navigation failed",     TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultNavigation());
        assertEquals("Default LayoutTemplate failed", TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayoutTemplate());
    }

    @Test
    public void testVelocityDefaults()
    {
        // Test if all the Velocity based Defaults for Page, Screen, Layout, Navigation and Layout Template
        assertEquals("Default Page failed",           "VelocityPage",       ts.getDefaultPageName("foo.vm"));
        assertEquals("Default Screen failed",         "VelocityScreen",     ts.getDefaultScreenName("foo.vm"));
        assertEquals("Default Layout failed",         "VelocityOnlyLayout", ts.getDefaultLayoutName("foo.vm"));
        assertEquals("Default Navigation failed",     "VelocityNavigation", ts.getDefaultNavigationName("foo.vm"));
        assertEquals("Default LayoutTemplate failed", "Default.vm",         ts.getDefaultLayoutTemplateName("foo.vm"));
    }
}

