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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.fulcrum.intake.model.Group;
import org.apache.fulcrum.parser.DefaultParameterParser;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pull.PullService;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.context.Context;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit test for Intake Tool, wrapping the Fulcrum Intake service.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 */
public class IntakeToolTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static PullService pullService = null;
    private Context context;
    private IntakeTool intakeTool;

    @Before
    public void initTool() throws Exception
    {
        context = pullService.getGlobalContext();
        assertNotNull(context);

        pullService.populateContext(context, getRunData());
        intakeTool = (IntakeTool) context.get("intake");
    }

    @After
    public void recycle() throws Exception
    {
        pullService.releaseTools(context);
        assertTrue(intakeTool.isDisposed());
        assertTrue(intakeTool.getGroups().isEmpty());
        assertNull(intakeTool.pp);
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

    @Test
    public void testRemove() throws Exception
    {
        File file = new File("./target/appData.ser");
        assertTrue("Make sure serialized data file exists:" + file, file.exists());
        Group group = intakeTool.get("LoginGroup", "loginGroupKey");
        assertNotNull(group);
        assertEquals(1, intakeTool.getGroups().size());
        intakeTool.remove(group);
        assertTrue(intakeTool.getGroups().isEmpty());
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
        intakeTool.refresh(null);
        assertEquals(numberOfGroups, intakeTool.getGroups().size());
    }

    private RunData getRunData() throws Exception
    {
        RunDataService rds = (RunDataService) TurbineServices.getInstance().getService(RunDataService.SERVICE_NAME);
        ServletConfig config = mock(ServletConfig.class);
        HttpServletRequest request = getMockRequest();
        HttpServletResponse response = mock(HttpServletResponse.class);
        RunData runData = rds.getRunData(request, response, config);
        assertInstanceOf(DefaultParameterParser.class, runData.getParameters(),
                "Verify we are using Fulcrum parameter parser");
        return runData;
    }

    @BeforeClass
    public static void setUp() throws Exception
    {
        Map<String, String> initParams = new HashMap<>();
        initParams.put(TurbineConfig.PROPERTIES_PATH_KEY, "/conf/test/CompleteTurbineResources.properties"); // "conf/test/TurbineResources.properties"
        initParams.put(TurbineConstants.LOGGING_ROOT_KEY, "target/test-logs");

        tc = new TurbineConfig(".", initParams);
        tc.initialize();

        pullService = (PullService)TurbineServices.getInstance().getService(PullService.SERVICE_NAME);
        assertNotNull(pullService);
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
