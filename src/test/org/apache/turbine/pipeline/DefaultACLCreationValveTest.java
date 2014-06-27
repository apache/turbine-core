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



import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.security.model.turbine.entity.impl.TurbineUserImpl;
import org.apache.turbine.om.security.DefaultUserImpl;
import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletConfig;

import static org.junit.Assert.*;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultACLCreationValveTest extends BaseTestCase
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
    public void setUpBefore() throws Exception {
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
        response = new MockHttpServletResponse();
        request.setSession(session);
    }


    @Test public void testLoggedInUser() throws Exception
    {
        RunData runData = getRunData(request,response,config);
        User tu = new DefaultUserImpl(new TurbineUserImpl());
        tu.setName("username");
        tu.setHasLoggedIn(Boolean.TRUE);
        runData.setAction("TestAction");
        runData.setUser(tu);

        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;

        DefaultACLCreationValve valve = new DefaultACLCreationValve();
        pipeline.addValve(valve);
        pipeline.initialize();

        pipeline.invoke(pipelineData);
        User user = runData.getUser();
        assertNotNull(user);
        assertEquals("username",user.getName());
        assertTrue(user.hasLoggedIn());
        assertNotNull(runData.getACL());
    }
    
    @AfterClass
    public static void destroy() {
        tc.dispose();
    }
}
