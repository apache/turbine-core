package org.apache.turbine.services.template;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TemplateEngineService;

import org.apache.turbine.services.velocity.VelocityService;

import org.apache.turbine.util.TurbineConfig;

/**
 * Tests all the various defaults for the Template Service.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DefaultsTest
    extends TestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;

    public DefaultsTest(String name)
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

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

