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


import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Tests the class mapping of the Template Service for screen,
 * layout and navigation.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class ClassTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;

    public ClassTest(String name)
            throws Exception
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        ts = (TemplateService) TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
    }

    public void testTemplateDefaults()
    {
        // Test if the Default-Values for the Screen, Layout and Navigation classes
        assertEquals("Default Page failed",           TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultPage());
        assertEquals("Default Screen failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultScreen());
        assertEquals("Default Layout failed",         TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultLayout());
        assertEquals("Default Navigation failed",     TemplateService.DEFAULT_TEMPLATE_VALUE, ts.getDefaultNavigation());
    }

    public void testVelocityDefaults()
    {
        // Test if all the Velocity based Defaults for Page, Screen, Layout, Navigation
        assertEquals("Default Page failed",           "VelocityPage",       ts.getDefaultPageName("foo.vm"));
        assertEquals("Default Screen failed",         "VelocityScreen",     ts.getDefaultScreenName("foo.vm"));
        assertEquals("Default Layout failed",         "VelocityOnlyLayout", ts.getDefaultLayoutName("foo.vm"));
        assertEquals("Default Navigation failed",     "VelocityNavigation", ts.getDefaultNavigationName("foo.vm"));
    }

    // Here comes the fun

    public void testNonExistingTemplate()
        throws Exception
    {
        //
        // Try a non existing Template. This should render with the default screen class,
        // use the default Layout class and Navigation. It should be rendered with the
        // default Layout Template but the Screen Template itself must not exist.
        String templateName = "DoesNotExistPage.vm";
        assertEquals("Screen translation failed",         "VelocityScreen",     ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "VelocityOnlyLayout", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "VelocityNavigation", ts.getNavigationName(templateName));
    }

    public void testNonExistingSublevelTemplate()
        throws Exception
    {
        //
        // Try a non existing Template in a sub-path. This should render with the default screen class,
        // use the default Layout class and Navigation.
        String templateName = "this,template,DoesNotExistPage.vm";
        assertEquals("Screen translation failed",         "VelocityScreen",     ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "VelocityOnlyLayout", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "VelocityNavigation", ts.getNavigationName(templateName));
    }

    public void testExistingTemplate()
        throws Exception
    {
        //
        // Try an existing Template without any backing class. Should also return the default classes
        String templateName = "ExistPage.vm";
        assertEquals("Screen translation failed",         "VelocityScreen",     ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "VelocityOnlyLayout", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "VelocityNavigation", ts.getNavigationName(templateName));
    }

    public void testExistingSublevelTemplate()
        throws Exception
    {
        //
        // Try an existing Sublevel Template without any backing class. Should also return the default classes
        String templateName = "existing,Page.vm";
        assertEquals("Screen translation failed",         "VelocityScreen",     ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "VelocityOnlyLayout", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "VelocityNavigation", ts.getNavigationName(templateName));
    }

    // Now we start checking existing classes.

    public void testExistingClass()
        throws Exception
    {
        //
        // Now we have a class backed template. It has a separate Class for Screen, Navigation and
        // Layout. It should find the matching class names in the screens, navigations and layout
        // packages.
        String templateName = "ExistPageWithClass.vm";
        assertEquals("Screen translation failed",         "ExistPageWithClass", ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "ExistPageWithClass", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "ExistPageWithClass", ts.getNavigationName(templateName));
    }

    public void testExistingSublevelClass()
        throws Exception
    {
        //
        // Now we have a class backed template. It has a separate Class for Screen, Navigation and
        // Layout. It should find the matching class names in the screens, navigations and layout
        // packages. For a twist, the classes are in a subpackage, so they should also find the
        // classes in the sub packages.
        String templateName = "existing,PageWithClass.vm";
        assertEquals("Screen translation failed",         "existing.PageWithClass", ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "existing.PageWithClass", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "existing.PageWithClass", ts.getNavigationName(templateName));
    }

    public void testDefaultClass()
        throws Exception
    {
        //
        // We look for a specific Template but it has no class. It has, however
        // a Default class in its package. So the Loader should find the default
        String templateName = "existing,dflt,PageWithClass.vm";
        assertEquals("Screen translation failed",         "existing.dflt.Default", ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "existing.dflt.Default", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "existing.dflt.Default", ts.getNavigationName(templateName));
    }

    public void testDefaultSublevelClass()
        throws Exception
    {
        //
        // We look for a specific Template but it has no class. It has, however
        // a Default class in an upper package. So the Loader should find this.
        String templateName = "existing,dflt,onelevel,twolevel,threelevel,PageWithClass.vm";
        assertEquals("Screen translation failed",         "existing.dflt.Default", ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "existing.dflt.Default", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "existing.dflt.Default", ts.getNavigationName(templateName));
    }

    public void testIgnoreExistingClass()
        throws Exception
    {
        //
        // This is a test, whether matching classes in upper level packages are ignored.
        // We're looking for classes which don't exist. We have, however, matching names
        // in an upper package. This should still match the Default classes, and not these.
        String templateName = "sublevel,ExistPageWithClass.vm";
        assertEquals("Screen translation failed",         "VelocityScreen",     ts.getScreenName(templateName));
        assertEquals("Layout translation failed",         "VelocityOnlyLayout", ts.getLayoutName(templateName));
        assertEquals("Navigation translation failed",     "VelocityNavigation", ts.getNavigationName(templateName));
    }


}
