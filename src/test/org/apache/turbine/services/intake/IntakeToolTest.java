package org.apache.turbine.services.intake;

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

import java.io.File;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.fulcrum.intake.IntakeService;
import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockHttpSession;
import com.mockobjects.servlet.MockServletConfig;

/**
 * Unit test for Intake Tool, wrapping the Fulcrum Intake service.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class IntakeToolTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private IntakeTool intakeTool;

    @Before
    public void initTool() throws Exception
    {
        intakeTool = new IntakeTool();
        AnnotationProcessor.process(intakeTool);
        intakeTool.init(getRunData());
    }

    @Test
    public void testGet() throws Exception
    {
        File file = new File("./target/appData.ser");
        assertTrue("Make sure serialized data file exists:" + file, file.exists());
        Group group = intakeTool.get("LoginGroup", "loginGroupKey");
        assertNotNull(group);
        assertEquals("loginGroupKey", group.getGID());
        assertEquals("LoginGroup", group.getIntakeGroupName());
    }

    /**
     * Make sure refresh DOESN'T do anything
     *
     * @throws Exception
     */
    @Test
    public void testRefresh() throws Exception
    {
        int numberOfGroups = intakeTool.getGroups().size();
        intakeTool.refresh();
        assertEquals(numberOfGroups, intakeTool.getGroups().size());
    }

    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        EnhancedMockHttpServletRequest request = new EnhancedMockHttpServletRequest();
        request.setupServerName("bob");
        request.setupGetProtocol("http");
        request.setupScheme("scheme");
        request.setupPathInfo("damn");
        request.setupGetServletPath("damn2");
        request.setupGetContextPath("wow");
        request.setupGetContentType("html/text");
        request.setupAddHeader("Content-type", "html/text");
        request.setupAddHeader("Accept-Language", "en-US");
        Vector<String> v = new Vector<String>();
        request.setupGetParameterNames(v.elements());
        MockHttpSession session = new MockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, null);
        request.setSession(session);
        HttpServletResponse response = new MockHttpServletResponse();
        ServletConfig config = new MockServletConfig();
        RunData runData = rds.getRunData(request, response, config);
        assertEquals("Verify we are using Fulcrum parameter parser", DefaultParameterParser.class, runData.getParameters()
            .getClass());
        return runData;
    }

    @BeforeClass
    public static void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
        TurbineServices.getInstance().getService(IntakeService.class.getName());
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
