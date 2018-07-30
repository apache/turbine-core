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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
    private TurbineConfig tc = null;

    @Before
    public void setUp() throws Exception
    {
        tc = new TurbineConfig(".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @After
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    @Test
    public void testTurbineAndFirstGet() throws Exception
    {
        assertNotNull(Turbine.getDefaultServerData());
        assertEquals("", Turbine.getServerName());
        assertEquals("80", Turbine.getServerPort());
        assertEquals("", Turbine.getScriptName());
        Turbine t = tc.getTurbine();

        HttpServletRequest request = getMockRequest();
        HttpServletResponse resp = Mockito.mock(HttpServletResponse.class);

        t.doGet(request, resp);

        assertEquals("8080", Turbine.getServerPort());
        t.destroy();
    }

    @Test
    public void testDefaultInputEncoding() throws Exception
    {
        Turbine t = tc.getTurbine();
        assertNotNull(t.getDefaultInputEncoding());
        assertEquals(TurbineConstants.PARAMETER_ENCODING_DEFAULT, t.getDefaultInputEncoding());
        t.destroy();
    }

    @Test
    public void testNonDefaultEncoding()
    {
        Turbine t = tc.getTurbine();
        t.getConfiguration().setProperty(TurbineConstants.PARAMETER_ENCODING_KEY, "UTF-8");
        assertNotNull(t.getDefaultInputEncoding());
        assertEquals("UTF-8", t.getDefaultInputEncoding());
    }
}
