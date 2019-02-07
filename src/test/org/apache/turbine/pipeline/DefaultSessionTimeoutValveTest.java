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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.URIConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id: DefaultSessionTimeoutValveTest.java 1606111 2014-06-27
 *          14:46:47Z gk $
 */
public class DefaultSessionTimeoutValveTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private ServletConfig config = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    @BeforeAll
    public static void init()
    {
        tc = new TurbineConfig(
                ".",
                "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @BeforeEach
    public void setUpBefore() throws Exception
    {
        config = mock(ServletConfig.class);
        request = getMockRequest();
        response = mock(HttpServletResponse.class);
    }

    /**
     * Tests the Valve.
     */
    @Test
    public void testDefaults() throws Exception
    {
        // reset
        Turbine.getConfiguration().setProperty(TurbineConstants.SESSION_TIMEOUT_KEY,
                Integer.valueOf(TurbineConstants.SESSION_TIMEOUT_DEFAULT));

        Vector<String> v = new Vector<String>();
        v.add(URIConstants.CGI_ACTION_PARAM);
        when(request.getParameterNames()).thenReturn(v.elements());
        when(request.getParameterValues(URIConstants.CGI_ACTION_PARAM)).thenReturn(new String[] { "TestAction" });

        PipelineData pipelineData = getPipelineData(request, response, config);

        Pipeline pipeline = new TurbinePipeline();

        DefaultSessionTimeoutValve valve = new DefaultSessionTimeoutValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);

        RunData runData = (RunData) pipelineData;
        assertEquals(0, runData.getSession().getMaxInactiveInterval());
    }

    /**
     * Tests the Valve.
     */
    @Test
    public void testTimeoutSet() throws Exception
    {
        Turbine.getConfiguration().setProperty(TurbineConstants.SESSION_TIMEOUT_KEY, "3600");
        PipelineData pipelineData = getPipelineData(request, response, config);

        Pipeline pipeline = new TurbinePipeline();

        DefaultSessionTimeoutValve valve = new DefaultSessionTimeoutValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);
        RunData runData = (RunData) pipelineData;

        assertEquals(3600, runData.getSession().getMaxInactiveInterval());
    }

    @AfterAll
    public static void destroy()
    {
        tc.dispose();
    }

}
