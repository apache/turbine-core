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
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Tests all the various template mappings for Screen and Layout
 * templates of the template service.
 *
 * @version $Id$
 */

public class TemplateTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;

    public TemplateTest(String name)
            throws Exception
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        ts = (TemplateService) TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
    }

    public static Test suite()
    {
        return new TestSuite(TemplateTest.class);
    }

    public void testTemplateDefaults()
    {
        assertEquals("Default LayoutTemplate failed", TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayoutTemplate());
    }

    public void testVelocityDefaults()
    {
        assertEquals("Default LayoutTemplate failed", "Default.vm",         ts.getDefaultLayoutTemplateName("foo.vm"));
    }

    public void testNonExistingTemplate()
        throws Exception
    {
        //
        // Try a non existing Template. This should render with the default screen class,
        // use the default Layout class and Navigation. It should be rendered with the
        // default Layout Template but the Screen Template itself must not exist.
        String templateName = "DoesNotExistPage.vm";
        assertEquals("LayoutTemplate translation failed", "Default.vm",         ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", null,                 ts.getScreenTemplateName(templateName));
    }

    public void testNonExistingSublevelTemplate()
        throws Exception
    {
        //
        // Try a non existing Template in a sub-path. This should render with the default screen class,
        // use the default Layout class and Navigation. It should be rendered with the
        // default Layout Template but the Screen Template itself must not exist.
        String templateName = "this,template,DoesNotExistPage.vm";
        assertEquals("LayoutTemplate translation failed", "Default.vm",         ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", null,                 ts.getScreenTemplateName(templateName));
    }

    public void testExistingTemplate()
        throws Exception
    {
        //
        // Try an existing Template. As we already know, missing classes are found correctly
        // so we test only Layout and Screen template. This should return the "Default" Layout
        // template to render and the Screen Template for the Page to render
        String templateName = "ExistPage.vm";
        assertEquals("LayoutTemplate translation failed", "Default.vm",         ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", "ExistPage.vm",       ts.getScreenTemplateName(templateName));
    }

    public void testExistingSublevelTemplate()
        throws Exception
    {
        //
        // Try an existing Template. As we already know, missing classes are found correctly
        // so we test only Layout and Screen template. This should return the "Default" Layout
        // template to render and the Screen Template for the Page to render. The names returned
        // by the template service are "/" separated so that e.g. Velocity can use this.
        String templateName = "existing,Page.vm";
        assertEquals("LayoutTemplate translation failed", "Default.vm",         ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", "existing/Page.vm",   ts.getScreenTemplateName(templateName));
    }

    public void testExistingLayoutTemplate()
        throws Exception
    {
        //
        // Try an existing Template. This time we have a backing Layout page. So the getLayoutTemplateName
        // method should not return the Default but our Layout page.
        //
        String templateName = "ExistPageWithLayout.vm";
        assertEquals("LayoutTemplate translation failed", "ExistPageWithLayout.vm", ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", "ExistPageWithLayout.vm", ts.getScreenTemplateName(templateName));
    }

    public void testExistingSublevelLayoutTemplate()
        throws Exception
    {
        //
        // Try an existing Template. This time we have a backing Layout page. So the getLayoutTemplateName
        // method should not return the Default but our Layout page.
        //
        String templateName = "existing,ExistSublevelPageWithLayout.vm";
        assertEquals("LayoutTemplate translation failed", "existing/ExistSublevelPageWithLayout.vm", ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", "existing/ExistSublevelPageWithLayout.vm", ts.getScreenTemplateName(templateName));
    }

    public void testExistingDefaultLayoutTemplate()
        throws Exception
    {
        //
        // Try an existing Template in a sublevel. This has an equally named Layout in the root. This
        // test must find the Template itself but the "Default" layout
        //
        String templateName = "existing,ExistPageWithLayout.vm";
        assertEquals("LayoutTemplate translation failed", "Default.vm",                      ts.getLayoutTemplateName(templateName));
        assertEquals("ScreenTemplate translation failed", "existing/ExistPageWithLayout.vm", ts.getScreenTemplateName(templateName));
    }
}

