package org.apache.turbine.modules;

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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.Turbine;
import org.apache.turbine.modules.actions.VelocityActionDoesNothing;
import org.apache.turbine.pipeline.DefaultPipelineData;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.velocity.context.Context;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This test case is to verify whether exceptions in Velocity actions are
 * properly bubbled up when action.event.bubbleexception=true. Or, if
 * action.event.bubbleexception=false, then the exceptions should be logged and
 * sunk.
 *
 * Changes 2014/Jun/26 (gk): removed Constructor with String parameter as no Test VelocityErrorScreenTest is found and JUnit does not allow it.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class ActionLoaderTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private ServletConfig config = null;
    private HttpServletRequest request = null;
    private HttpServletResponse response = null;

    /*
     * @see TestCase#setUp()
     */

    @BeforeClass
    public static void init()
    {
        tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
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

    /**
     * This unit test verifies that if your standard doPerform is called, and it
     * throws an Exception, the exception is bubbled up out of the
     * ActionLoader...
     *
     * @throws Exception
     *             If something goes wrong with the unit test
     */
    @Test
    public void testDoPerformBubblesException() throws Exception
    {
        System.out.println("tcturbine:"+ tc.getTurbine());
    }

    /**
     * This unit test verifies that if an Action Event doEventSubmit_ is called,
     * and it throws an Exception, the exception is bubbled up out of the
     * ActionLoader...
     *
     * @throws Exception
     *             If something goes wrong with the unit test
     */
    @Test
    public void testActionEventBubblesException() throws Exception
    {
        when(request.getParameterValues("eventSubmit_doCauseexception")).thenReturn(new String[] { "foo" });
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = new DefaultPipelineData();
        Map<Class<?>, Object> runDataMap = new HashMap<Class<?>, Object>();
        runDataMap.put(RunData.class, data);
        pipelineData.put(RunData.class, runDataMap);
        data.setAction("VelocityActionThrowsException");
        data.getParameters().add("eventSubmit_doCauseexception", "foo");
        assertTrue(data.getParameters().containsKey("eventSubmit_doCauseexception"));
        try
        {
            ActionLoader.getInstance().exec(data, data.getAction());
            fail("Should have bubbled out an exception thrown by the action.");
        }
        catch (Exception e)
        {
            // good
        }
        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
            fail("Should have bubbled out an exception thrown by the action.");
        }
        catch (Exception e)
        {
            // good
        }
    }

    /**
     * This unit test verifies that if your standard doPerform is called, and it
     * throws an Exception, if the action.event.bubbleexception property is set
     * to false then the exception is NOT bubbled up
     *
     * @throws Exception
     *             If something goes wrong with the unit test
     */
    @Test
    public void testDoPerformDoesntBubbleException() throws Exception
    {
        Turbine.getConfiguration().setProperty("action.event.bubbleexception", Boolean.FALSE);
        assertFalse(Turbine.getConfiguration().getBoolean("action.event.bubbleexception"));
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = new DefaultPipelineData();
        Map<Class<?>, Object> runDataMap = new HashMap<Class<?>, Object>();
        runDataMap.put(RunData.class, data);
        pipelineData.put(RunData.class, runDataMap);
        data.setAction("VelocityActionThrowsException");
        try
        {
            ActionLoader.getInstance().exec(data, data.getAction());
        }
        catch (Exception e)
        {
            fail("Should NOT have thrown an exception:" + e.getMessage());
        }
        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
        }
        catch (Exception e)
        {
            fail("Should NOT have thrown an exception:" + e.getMessage());
        }
    }

    /**
     * This unit test verifies that if an Action Event doEventSubmit_ is called,
     * and it throws an Exception, if the action.event.bubbleexception property
     * is set to false then the exception is NOT bubbled up
     *
     * @throws Exception
     *             If something goes wrong with the unit test
     */
    @Test
    public void testActionEventDoesntBubbleException() throws Exception
    {
        // can't seem to figure out how to setup the Mock Request with the right
        // parameters...
        Turbine.getConfiguration().setProperty("action.event.bubbleexception", Boolean.FALSE);
        when(request.getParameterValues("eventSubmit_doCauseexception")).thenReturn(new String[] { "foo" });
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = new DefaultPipelineData();
        Map<Class<?>, Object> runDataMap = new HashMap<Class<?>, Object>();
        runDataMap.put(RunData.class, data);
        pipelineData.put(RunData.class, runDataMap);
        data.setAction("VelocityActionThrowsException");
        data.getParameters().add("eventSubmit_doCauseexception", "foo");
        assertTrue(data.getParameters().containsKey("eventSubmit_doCauseexception"));

        try
        {
            ActionLoader.getInstance().exec(data, data.getAction());
        }
        catch (Exception e)
        {
            fail("Should NOT have thrown an exception:" + e.getMessage());
        }
        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
        }
        catch (Exception e)
        {
            fail("Should NOT have thrown an exception:" + e.getMessage());
        }
    }

    /**
     * This unit test verifies that if an Action Event doEventSubmit_ is called,
     * a properly annotated method is being called
     *
     * @throws Exception
     *             If something goes wrong with the unit test
     */
    @Test
    public void testActionEventAnnotation() throws Exception
    {
        when(request.getParameterValues("eventSubmit_annotatedEvent")).thenReturn(new String[] { "foo" });
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = data;
        data.setAction("VelocityActionDoesNothing");
        data.getParameters().add("eventSubmit_annotatedEvent", "foo");

        int numberOfCalls = VelocityActionDoesNothing.numberOfCalls;
        int pipelineDataCalls = VelocityActionDoesNothing.pipelineDataCalls;
        int actionEventCalls = VelocityActionDoesNothing.actionEventCalls;
        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
        assertEquals(numberOfCalls + 1, VelocityActionDoesNothing.numberOfCalls);
        assertEquals(pipelineDataCalls, VelocityActionDoesNothing.pipelineDataCalls);
        assertEquals(actionEventCalls + 1, VelocityActionDoesNothing.actionEventCalls);
    }

    @Test
    public void testNonexistentActionCausesError() throws Exception
    {
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = new DefaultPipelineData();
        Map<Class<?>, Object> runDataMap = new HashMap<Class<?>, Object>();
        runDataMap.put(RunData.class, data);
        pipelineData.put(RunData.class, runDataMap);
        data.setAction("ImaginaryAction");
        try
        {
            ActionLoader.getInstance().exec(data, "boo");
            fail("Should have thrown an exception");
        }
        catch (Exception e)
        {
            // good
        }
        try
        {
            ActionLoader.getInstance().exec(pipelineData, "boo");
            fail("Should have thrown an exception");
        }
        catch (Exception e)
        {
            // good
        }
    }

    @Test
    public void testDoPerformWithPipelineData() throws Exception
    {
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = data;
        data.setAction("VelocityActionDoesNothing");
        int numberOfCalls = VelocityActionDoesNothing.numberOfCalls;
        int pipelineDataCalls = VelocityActionDoesNothing.pipelineDataCalls;
        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
        assertEquals(numberOfCalls + 1, VelocityActionDoesNothing.numberOfCalls);
        assertEquals(pipelineDataCalls + 1, VelocityActionDoesNothing.pipelineDataCalls);
    }

    @Test
    public void testDoPerformWithServiceInjection() throws Exception
    {
        RunData data = getRunData(request, response, config);
        PipelineData pipelineData = data;
        data.setAction("VelocityActionWithServiceInjection");

        try
        {
            ActionLoader.getInstance().exec(pipelineData, data.getAction());
            Context context = (Context)
                            data.getTemplateInfo().getTemplateContext(VelocityService.CONTEXT);
            assertTrue( context.get( "mykey" ) != null );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }
}
