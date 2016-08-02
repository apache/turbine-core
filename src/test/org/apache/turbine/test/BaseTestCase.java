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


import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.PropertyConfigurator;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;
import org.junit.BeforeClass;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
    static File log4jFile = new File("conf/test/Log4j.properties");

    @BeforeClass
    public static void baseInit()
            throws Exception
    {

        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
            p.setProperty(TurbineConstants.APPLICATION_ROOT_KEY, new File(".").getAbsolutePath());
            PropertyConfigurator.configure(p);

        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Could not open Log4J configuration file "
                    + log4jFile);
        }
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

    protected Map<String,Object> attributes = new HashMap<String,Object>();
    protected int maxInactiveInterval = 0;

    @SuppressWarnings("boxing")
    protected HttpServletRequest getMockRequest()
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        doAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String key = (String) invocation.getArguments()[0];
                return attributes.get(key);
            }
        }).when(session).getAttribute(anyString());

        doAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                String key = (String) invocation.getArguments()[0];
                Object value = invocation.getArguments()[1];
                attributes.put(key, value);
                return null;
            }
        }).when(session).setAttribute(anyString(), any());

        when(session.getMaxInactiveInterval()).thenReturn(maxInactiveInterval);

        doAnswer(new Answer<Integer>()
        {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable
            {
                return Integer.valueOf(maxInactiveInterval);
            }
        }).when(session).getMaxInactiveInterval();

        doAnswer(new Answer<Object>()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Integer value = (Integer) invocation.getArguments()[0];
                maxInactiveInterval = value.intValue();
                return null;
            }
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

        Vector<String> v = new Vector<String>();
        when(request.getParameterNames()).thenReturn(v.elements());
        return request;
    }
}

