package org.apache.turbine;

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

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletResponse;
import org.apache.turbine.util.TurbineConfig;

import com.mockobjects.servlet.MockHttpServletRequest;

/**
 * This testcase verifies that TurbineConfig can be used to startup Turbine in a
 * non servlet environment properly.
 * 
 * @author <a href="mailto:epugh@opensourceconnections.com">Eric Pugh </a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux </a>
 * @version $Id$
 */
public class TurbineTest extends BaseTestCase
{

    public TurbineTest(String name) throws Exception
    {
        super(name);
    }

    public void testTurbineAndFirstGet() throws Exception
    {
        TurbineConfig tc = new TurbineConfig(".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();

        ServletConfig config = (ServletConfig) tc;
        ServletContext context = config.getServletContext();
        assertNotNull(Turbine.getDefaultServerData());
        assertEquals("", Turbine.getServerName());
        assertEquals("80", Turbine.getServerPort());
        assertEquals("", Turbine.getScriptName());
        Turbine t = tc.getTurbine();

        MockHttpServletRequest request = getMockRequest();
        EnhancedMockHttpServletResponse resp = new EnhancedMockHttpServletResponse();

        t.doGet(request, resp);

        assertEquals("8080", Turbine.getServerPort());
        t.destroy();
    }

    public void testDefaultInputEncoding() throws Exception
    {
        TurbineConfig tc = new TurbineConfig(".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
        Turbine t = tc.getTurbine();
        assertNotNull(t.getDefaultInputEncoding());
        assertEquals(TurbineConstants.PARAMETER_ENCODING_DEFAULT, t.getDefaultInputEncoding());
        t.destroy();
    }
    
    public void testNonDefaultEncoding() throws ServletException 
    {
        TurbineConfig tc = new TurbineConfig(".",
                "/conf/test/CompleteTurbineResourcesWithEncoding.properties");
        tc.initialize();
        Turbine t = tc.getTurbine();
        assertNotNull(t.getDefaultInputEncoding());
        assertEquals("UTF-8", t.getDefaultInputEncoding());
    }

}
