package org.apache.turbine.services.velocity;

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
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.collections.ExtendedProperties;

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.services.velocity.TurbineVelocityService;

import org.apache.turbine.util.TurbineConfig;

import org.apache.turbine.Turbine;

/**
 * Tests startup of the Velocity Service and translation of various
 * path patterns.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class PathConverterTest
    extends TestCase
{
    private static TurbineConfig tc = null;
    private static VelocityService vs = null;

    public PathConverterTest(String name)
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        vs = (VelocityService) TurbineServices.getInstance().getService(VelocityService.SERVICE_NAME);
    }

    public static Test suite()
    {
        return new TestSuite(PathConverterTest.class);
    }

    public void testService()
        throws Exception
    {

        // Can we start the service?
        assertFalse("Could not load Service!", vs == null);
    }

    public void testPathTranslation()
        throws Exception
    {
        Configuration conf = vs.getConfiguration();
        ExtendedProperties ep = ((TurbineVelocityService) vs).createVelocityProperties(conf);

        String rootPath = Turbine.getRealPath("");

        String [] test1 = ep.getStringArray("test1.resource.loader.path");
        assertEquals("No Test1 Property found", 1, test1.length);
        assertEquals("Test1 Path translation failed", rootPath + "/relative/path" , test1[0]);

        String [] test2 = ep.getStringArray("test2.resource.loader.path");
        assertEquals("No Test2 Property found", 1, test2.length);
        assertEquals("Test2 Path translation failed", rootPath + "/absolute/path" , test2[0]);

        String [] test3 = ep.getStringArray("test3.resource.loader.path");
        assertEquals("No Test3 Property found", 1, test2.length);
        assertEquals("Test3 Path translation failed", rootPath +"/jar-file.jar!/", test3[0]);

        String [] test4 = ep.getStringArray("test4.resource.loader.path");
        assertEquals("No Test4 Property found", 1, test4.length);
        assertEquals("Test4 Path translation failed", rootPath + "/jar-file.jar!/with/some/extensions" , test4[0]);

        String [] test5 = ep.getStringArray("test5.resource.loader.path");
        assertEquals("No Test5 Property found", 1, test5.length);
        assertEquals("Test5 Path translation failed", rootPath + "/jar-file.jar" , test5[0]);

        String [] test6 = ep.getStringArray("test6.resource.loader.path");
        assertEquals("No Test6 Property found", 1, test6.length);
        assertEquals("Test6 Path translation failed", "jar:http://jar.on.website/" , test6[0]);

        String [] test7 = ep.getStringArray("test7.resource.loader.path");
        assertEquals("No Test7 Property found", 1, test7.length);
        assertEquals("Test7 Path translation failed", rootPath + "/file/system/reference" , test7[0]);

        String [] test8 = ep.getStringArray("test8.resource.loader.path");
        assertEquals("No Test8 Property found", 1, test8.length);
        assertEquals("Test8 Path translation failed", "http://reference.on.website/" , test8[0]);

    }
}
