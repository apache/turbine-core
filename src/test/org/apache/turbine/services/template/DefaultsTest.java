package org.apache.turbine.services.template;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTurbineTest;

/**
 * Tests all the various defaults for the Template Service.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DefaultsTest
    extends BaseTurbineTest
{
	private TemplateService ts = null;

    public DefaultsTest(String name)
            throws Exception
    {
        super(name, "/conf/test/TemplateService.properties");

        ts = (TemplateService) TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
    }

    public static Test suite()
    {
        return new TestSuite(DefaultsTest.class);
    }

    public void testDefaults()
    {
        // Test if the caching property was loaded correctly.
        assertEquals("isCaching failed!",             ts.isCaching(), false);

        // Test if the default values for Template and Extension were loaded correctly
        assertEquals("Default Extension failed",      ts.getDefaultExtension(), "");
        assertEquals("Default Template failed",       ts.getDefaultTemplate(), TemplateService.DEFAULT_TEMPLATE_VALUE);
    }

    public void testTemplateDefaults()
    {
        // Test if the Default-Values for the Screen, Layout and Navigation classes and the Layout Template are correct.
        assertEquals("Default Page failed",           TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultPage());
        assertEquals("Default Screen failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultScreen());
        assertEquals("Default Layout failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayout());
        assertEquals("Default Navigation failed",     TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultNavigation());
        assertEquals("Default LayoutTemplate failed", TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayoutTemplate());
    }

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

