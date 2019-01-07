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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.actions.LoginUser;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
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
public class DefaultLoginValveTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private ServletConfig config = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;
    private HttpSession session = null;
    private SecurityService securityService = null;

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
        session = request.getSession();

        // User must exist
        securityService = (SecurityService)TurbineServices.getInstance().getService(SecurityService.SERVICE_NAME);
        if (!securityService.accountExists("username"))
        {
            User user = securityService.getUserInstance();
            user.setName("username");
            securityService.addUser(user, "password");
        }
    }

    /**
     * Tests the Valve.
     */
    @Test 
    public void testDefaults() throws Exception
    {
        Vector<String> v = new Vector<String>();
        v.add(LoginUser.CGI_USERNAME);
        v.add(LoginUser.CGI_PASSWORD);
        when(request.getParameterNames()).thenReturn(v.elements());

        when(request.getParameterValues(LoginUser.CGI_USERNAME)).thenReturn(new String[] { "username" });
        when(request.getParameterValues(LoginUser.CGI_PASSWORD)).thenReturn(new String[] { "password" });

        RunData runData = getRunData(request,response,config);
        runData.setAction(TurbineConstants.ACTION_LOGIN_DEFAULT);

        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;

        DefaultLoginValve valve = new DefaultLoginValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username",user.getName());
        assertTrue(user.hasLoggedIn());
    }

    /**
     * Tests the LogoutAction.
     */
    @Test 
    public void testLogout() throws Exception
    {
        User user = securityService.getUser("username");
        user.setHasLoggedIn(Boolean.TRUE);
        session.setAttribute(User.SESSION_KEY, user);

        RunData runData = getRunData(request,response,config);
        runData.setAction(TurbineConstants.ACTION_LOGOUT_DEFAULT);

        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;

        DefaultLoginValve valve = new DefaultLoginValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);
        user = runData.getUser();
        assertNotNull(user);
        assertTrue(securityService.isAnonymousUser(user));
        assertFalse(user.hasLoggedIn());
    }

    @AfterClass
    public static void destroy()
    {
        tc.dispose();
    }
}
