package org.apache.turbine.services.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import junit.framework.Test;
import junit.framework.TestSuite;

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

    public static Test suite()
    {
        return new TestSuite(ClassTest.class);
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
