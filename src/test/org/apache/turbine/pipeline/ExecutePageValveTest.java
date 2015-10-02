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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.security.model.turbine.entity.impl.TurbineUserImpl;
import org.apache.turbine.modules.actions.VelocityActionDoesNothing;
import org.apache.turbine.modules.actions.VelocitySecureActionDoesNothing;
import org.apache.turbine.om.security.DefaultUserImpl;
import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpServletResponse;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.URIConstants;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockobjects.servlet.MockServletConfig;

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
    private MockServletConfig config = null;
    private EnhancedMockHttpServletRequest request = null;
    private EnhancedMockHttpSession session = null;
    private HttpServletResponse response = null;


    @BeforeClass
    public static void init() {
        tc = new TurbineConfig(
                            ".",
                            "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @Before
    public void setUpBefore() throws Exception
    {
        config = new MockServletConfig();
        config.setupNoParameters();
        request = new EnhancedMockHttpServletRequest();
        request.setupServerName("bob");
        request.setupGetProtocol("http");
        request.setupScheme("scheme");
        request.setupPathInfo("damn");
        request.setupGetServletPath("damn2");
        request.setupGetContextPath("wow");
        request.setupGetContentType("html/text");
        request.setupAddHeader("Content-type", "html/text");
        request.setupAddHeader("Accept-Language", "en-US");

        session = new EnhancedMockHttpSession();
        response = new EnhancedMockHttpServletResponse();

        request.setSession(session);

    }

    @Test public void testValve() throws Exception
    {
        Vector<String> v = new Vector<String>();
        v.add(URIConstants.CGI_TEMPLATE_PARAM);
        request.setupGetParameterNames(v.elements());
        String nulls[] = new String[1];
        nulls[0]="Index.vm";
        request.setupAddParameter(URIConstants.CGI_TEMPLATE_PARAM, nulls);

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
        assertEquals("Assert action was called",numberOfCalls +1,VelocityActionDoesNothing.numberOfCalls);
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username", user.getName());
        assertTrue(user.hasLoggedIn());
    }

    @Test public void testValveWithSecureAction() throws Exception
    {
        Vector<String> v = new Vector<String>();
        v.add(URIConstants.CGI_TEMPLATE_PARAM);
        request.setupGetParameterNames(v.elements());
        String nulls[] = new String[1];
        nulls[0]="Index.vm";
        request.setupAddParameter(URIConstants.CGI_TEMPLATE_PARAM, nulls);

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
        assertEquals("Assert action was called",numberOfCalls +1,VelocitySecureActionDoesNothing.numberOfCalls);
        assertEquals("Assert authorization was called",isAuthorizedCalls +1,VelocitySecureActionDoesNothing.isAuthorizedCalls);
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username", user.getName());
        assertTrue(user.hasLoggedIn());
    }

    @AfterClass
    public static void destroy()
    {
        tc.dispose();
    }
}
