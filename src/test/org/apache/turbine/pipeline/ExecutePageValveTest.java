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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.security.model.turbine.entity.impl.TurbineUserImpl;
import org.apache.turbine.modules.actions.VelocityActionDoesNothing;
import org.apache.turbine.modules.actions.VelocitySecureActionDoesNothing;
import org.apache.turbine.om.security.DefaultUserImpl;
import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.URIConstants;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests ExecutePageValve.
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class ExecutePageValveTest extends BaseTestCase
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
        ServletOutputStream sos = mock(ServletOutputStream.class);

        when(response.getOutputStream()).thenReturn(sos);
    }

    @Test public void testValve() throws Exception
    {
        Vector<String> v = new Vector<>();
        v.add(URIConstants.CGI_TEMPLATE_PARAM);
        when(request.getParameterNames()).thenReturn(v.elements());

        when(request.getParameterValues(URIConstants.CGI_TEMPLATE_PARAM)).thenReturn(new String[] { "Index.vm" });

        RunData runData = getRunData(request, response, config);
        runData.setScreenTemplate("ExistPageWithLayout.vm");
        User tu = new DefaultUserImpl(new TurbineUserImpl());
        tu.setName("username");
        tu.setHasLoggedIn(Boolean.TRUE);
        String actionName = VelocityActionDoesNothing.class.getName();
        actionName = actionName.substring(actionName.lastIndexOf(".")+1);
        runData.setAction(actionName);
        runData.setUser(tu);

        Pipeline pipeline = new TurbinePipeline();

        PipelineData pipelineData = runData;
        ExecutePageValve valve = new ExecutePageValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        int numberOfCalls = VelocityActionDoesNothing.numberOfCalls;
        pipeline.invoke(pipelineData);
        assertEquals(numberOfCalls +1,VelocityActionDoesNothing.numberOfCalls, "Assert action was called");
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username", user.getName());
        assertTrue(user.hasLoggedIn());
    }

    @Test public void testValveWithSecureAction() throws Exception
    {
        Vector<String> v = new Vector<>();
        v.add(URIConstants.CGI_TEMPLATE_PARAM);
        when(request.getParameterNames()).thenReturn(v.elements());

        when(request.getParameterValues(URIConstants.CGI_TEMPLATE_PARAM)).thenReturn(new String[] { "Index.vm" });

        RunData runData = getRunData(request, response, config);
        runData.setScreenTemplate("ExistPageWithLayout.vm");
        User tu = new DefaultUserImpl(new TurbineUserImpl());
        tu.setName("username");
        tu.setHasLoggedIn(Boolean.TRUE);
        String actionName = VelocitySecureActionDoesNothing.class.getName();
        actionName = actionName.substring(actionName.lastIndexOf(".")+1);
        runData.setAction(actionName);
        runData.setUser(tu);

        Pipeline pipeline = new TurbinePipeline();

        PipelineData pipelineData = runData;
        ExecutePageValve valve = new ExecutePageValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        int numberOfCalls = VelocitySecureActionDoesNothing.numberOfCalls;
        int isAuthorizedCalls = VelocitySecureActionDoesNothing.isAuthorizedCalls;
        pipeline.invoke(pipelineData);
        assertEquals(numberOfCalls +1,VelocitySecureActionDoesNothing.numberOfCalls, "Assert action was called");
        assertEquals(isAuthorizedCalls +1,VelocitySecureActionDoesNothing.isAuthorizedCalls, "Assert authorization was called");
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username", user.getName());
        assertTrue(user.hasLoggedIn());
    }

    @AfterAll
    public static void destroy()
    {
        tc.dispose();
    }
}
