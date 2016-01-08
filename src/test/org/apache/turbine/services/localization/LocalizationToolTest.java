package org.apache.turbine.services.localization;

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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;

/**
 * Unit test for Localization Tool. Verifies that localization works the same using the
 * deprecated Turbine localization service as well as the new Fulcrum Localization
 * component.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class LocalizationToolTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private LocalizationTool lt;

    @Before
    public void initTool() throws Exception
    {
        lt = new LocalizationTool();
        AnnotationProcessor.process(lt);
        lt.init(getRunData());
    }

    @Test
    public void testGet() throws Exception
    {
        assertEquals("value1", lt.get("key1"));
        assertEquals("value3", lt.get("key3"));
    }

    @Test
    public void testGetLocale() throws Exception
    {
        assertNotNull(lt.getLocale());
        assertEquals("US", lt.getLocale().getCountry());
        assertEquals("en", lt.getLocale().getLanguage());
    }

    @Test
    public void testInit() throws Exception
    {
        assertNotNull(lt.getLocale());
    }

    @Test
    public void testRefresh() throws Exception
    {
        assertNotNull(lt.getLocale());
        lt.refresh();
        assertNull(lt.getLocale());
    }

    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        EnhancedMockHttpServletRequest request = new EnhancedMockHttpServletRequest();
        request.setupServerName("bob");
        request.setupGetProtocol("http");
        request.setupScheme("scheme");
        request.setupPathInfo("damn");
        request.setupGetServletPath("damn2");
        request.setupGetContextPath("wow");
        request.setupGetContentType("html/text");
        request.setupAddHeader("Content-type", "html/text");
        request.setupAddHeader("Accept-Language", "en-US");
        Vector<String> v = new Vector<String>();
        request.setupGetParameterNames(v.elements());
        MockHttpSession session = new MockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, null);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        RunData runData = rds.getRunData(request, response, config);
        return runData;
    }

    @BeforeClass
    public static void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
