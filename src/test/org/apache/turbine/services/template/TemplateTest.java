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
 * Tests all the various template mappings for Screen and Layout
 * templates of the template service.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
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

