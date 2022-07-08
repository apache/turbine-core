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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
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
public class DefaultSetEncodingValveTest extends BaseTestCase
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
        when(request.getCharacterEncoding()).thenReturn(null);
    }

    /**
     * Tests the Valve.
     */
    @Test
    public void testDefaults() throws Exception
    {
        // reset
        Turbine.getConfiguration().setProperty(TurbineConstants.PARAMETER_ENCODING_KEY,
                TurbineConstants.PARAMETER_ENCODING_DEFAULT);

        PipelineData pipelineData = getPipelineData(request, response, config);

        Pipeline pipeline = new TurbinePipeline();

        DefaultSetEncodingValve valve = new DefaultSetEncodingValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);

        RunData runData = (RunData) pipelineData;
        assertEquals(TurbineConstants.PARAMETER_ENCODING_DEFAULT, runData.getCharset().name());
    }

    /**
     * Tests the Valve.
     */
    @Test
    public void testEncodingSet() throws Exception
    {
        Turbine.getConfiguration().setProperty(TurbineConstants.PARAMETER_ENCODING_KEY, "UTF-8");
        PipelineData pipelineData = getPipelineData(request, response, config);

        Pipeline pipeline = new TurbinePipeline();

        DefaultSetEncodingValve valve = new DefaultSetEncodingValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);
        RunData runData = (RunData) pipelineData;

        assertEquals("UTF-8", runData.getCharset().name());
    }

    @AfterAll
    public static void destroy()
    {
        tc.dispose();
    }

}
