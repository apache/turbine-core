package org.apache.turbine.pipeline;


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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.URIConstants;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DetermineActionValveTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private ServletConfig config = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    @BeforeClass
    public static void init()
    {
        tc = new TurbineConfig(
                            ".",
                            "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @Before
    public void setUpBefore() throws Exception
    {
        config = mock(ServletConfig.class);
        request = getMockRequest();
        response = mock(HttpServletResponse.class);

        Vector<String> v = new Vector<String>();
        v.add(URIConstants.CGI_ACTION_PARAM);
        when(request.getParameterNames()).thenReturn(v.elements());

        when(request.getParameterValues(URIConstants.CGI_ACTION_PARAM)).thenReturn(new String[] { "TestAction" });
    }

    /**
     * Tests the Valve.
     */
    @Test public void testValve() throws Exception
    {
        RunData runData = getRunData(request,response,config);

        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;

        DetermineActionValve valve = new DetermineActionValve();
        pipeline.addValve(valve);

        pipeline.invoke(pipelineData);
        assertEquals("TestAction",runData.getAction());
    }

    @AfterClass
    public static void destroy() {
        tc.dispose();
    }
}
