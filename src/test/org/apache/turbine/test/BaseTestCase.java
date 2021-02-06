package org.apache.turbine.test;


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


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;
import org.junit.BeforeClass;

/**
 * Base functionality to be extended by all Apache Turbine test cases.  Test
 * case implementations are used to automate testing via JUnit.
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class BaseTestCase
{
    static File log4j2File = new File("conf/test/log4j2.xml");

    @BeforeClass
    public static void baseInit()
            throws Exception
    {
    	// auto load log4j2 file
    }

    protected RunData getRunData(HttpServletRequest request,HttpServletResponse response,ServletConfig config) throws Exception {
        RunDataService rds =
            (RunDataService) TurbineServices.getInstance().getService(
                    RunDataService.SERVICE_NAME);
        RunData runData = rds.getRunData(request, response, config);
        return runData;
    }

    protected PipelineData getPipelineData(HttpServletRequest request,HttpServletResponse response,ServletConfig config) throws Exception {
       RunData runData = getRunData(request,response,config);
       return runData;
    }

    protected Map<String,Object> attributes = new HashMap<>();
    protected int maxInactiveInterval = 0;

    @SuppressWarnings("boxing")
    protected HttpServletRequest getMockRequest()
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            return attributes.get(key);
        }).when(session).getAttribute(anyString());

        doAnswer(invocation -> {
            String key = (String) invocation.getArguments()[0];
            Object value = invocation.getArguments()[1];
            attributes.put(key, value);
            return null;
        }).when(session).setAttribute(anyString(), any());

        when(session.getMaxInactiveInterval()).thenReturn(maxInactiveInterval);

        doAnswer(invocation -> Integer.valueOf(maxInactiveInterval)).when(session).getMaxInactiveInterval();

        doAnswer(invocation -> {
            Integer value = (Integer) invocation.getArguments()[0];
            maxInactiveInterval = value.intValue();
            return null;
        }).when(session).setMaxInactiveInterval(anyInt());

        when(session.isNew()).thenReturn(true);
        when(request.getSession()).thenReturn(session);

        when(request.getServerName()).thenReturn("bob");
        when(request.getProtocol()).thenReturn("http");
        when(request.getScheme()).thenReturn("scheme");
        when(request.getPathInfo()).thenReturn("damn");
        when(request.getServletPath()).thenReturn("damn2");
        when(request.getContextPath()).thenReturn("wow");
        when(request.getContentType()).thenReturn("html/text");

        when(request.getCharacterEncoding()).thenReturn("US-ASCII");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getLocale()).thenReturn(Locale.US);

        when(request.getHeader("Content-type")).thenReturn("html/text");
        when(request.getHeader("Accept-Language")).thenReturn("en-US");

        Vector<String> v = new Vector<>();
        when(request.getParameterNames()).thenReturn(v.elements());
        return request;
    }
}

