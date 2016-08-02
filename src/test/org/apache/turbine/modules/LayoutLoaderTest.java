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

package org.apache.turbine.modules;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.modules.layouts.TestVelocityOnlyLayout;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class LayoutLoaderTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private ServletConfig config = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

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
    }

    /*
     * @see TestCase#tearDown()
     */
    @AfterClass
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    @Test
    public void testPipelineDataContainsRunData()
    {
        try
        {
            RunData data = getRunData(request, response, config);
            PipelineData pipelineData = data;
            data.setLayout("TestVelocityOnlyLayout");
            int numberOfCalls = TestVelocityOnlyLayout.numberOfCalls;
            try
            {
                LayoutLoader.getInstance().exec(pipelineData, data.getLayout());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Should not have thrown an exception.");
            }
            assertEquals(numberOfCalls + 1, TestVelocityOnlyLayout.numberOfCalls);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }

    @Test
    public void testDoBuildWithRunData()
    {
        try
        {
            RunData data = getRunData(request, response, config);
            data.setLayout("TestVelocityOnlyLayout");
            int numberOfCalls = TestVelocityOnlyLayout.numberOfCalls;
            try
            {
                LayoutLoader.getInstance().exec(data, data.getLayout());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                fail("Should not have thrown an exception.");
            }
            assertEquals(numberOfCalls + 1, TestVelocityOnlyLayout.numberOfCalls);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }
}
