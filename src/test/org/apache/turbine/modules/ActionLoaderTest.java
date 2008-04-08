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


import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.turbine.Turbine;
import org.apache.turbine.modules.actions.VelocityActionDoesNothing;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.DefaultPipelineData;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletConfig;
/**
 * This test case is to verify whether exceptions in Velocity actions are
 * properly bubbled up when action.event.bubbleexception=true.  Or, if
 * action.event.bubbleexception=false, then the exceptions should be
 * logged and sunk.
 *
 * @author     <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class ActionLoaderTest extends BaseTestCase {
	private static TurbineConfig tc = null;
	private MockServletConfig config = null;
	private EnhancedMockHttpServletRequest request = null;
	private EnhancedMockHttpSession session = null;
	private HttpServletResponse response = null;

    /*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
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
		Vector v = new Vector();
		request.setupGetParameterNames(v.elements());
		session = new EnhancedMockHttpSession();
		response = new MockHttpServletResponse();
		session.setupGetAttribute(User.SESSION_KEY, null);
		request.setSession(session);
		tc =
			new TurbineConfig(
				".",
				"/conf/test/CompleteTurbineResources.properties");
		tc.initialize();
	}
	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		if (tc != null) {
			tc.dispose();
		}
	}
	/**
	 * Constructor for VelocityErrorScreenTest.
	 * @param arg0
	 */
	public ActionLoaderTest(String arg0) throws Exception {
		super(arg0);
	}
	/**
	 * This unit test verifies that if your standard doPerform is called,
	 * and it throws an Exception, the exception is bubbled up out of the ActionLoader...
	 *
	 * @throws Exception If something goes wrong with the unit test
	 */
	public void testDoPerformBubblesException() throws Exception {
		RunData data = getRunData(request,response,config);
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
		data.setAction("VelocityActionThrowsException");
		try {
			ActionLoader.getInstance().exec(data, data.getAction());
			fail("Should have thrown an exception");
		} catch (Exception e) {
			//good
		}

		try {
			ActionLoader.getInstance().exec(pipelineData, data.getAction());
			fail("Should have thrown an exception");
		} catch (Exception e) {
			//good
		}
	}
	/**
	   * This unit test verifies that if an Action Event doEventSubmit_ is called, and it throws an Exception, the
	   * exception is bubbled up out of the ActionLoader...
	   *
	   * @throws Exception If something goes wrong with the unit test
	   */
	public void testActionEventBubblesException() throws Exception {
		// can't seem to figure out how to setup the Mock Request with the right parameters...
		request.setupAddParameter("eventSubmit_doCauseexception", "foo");
		RunData data = getRunData(request,response,config);
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
		data.setAction("VelocityActionThrowsException");
		data.getParameters().add("eventSubmit_doCauseexception", "foo");
		assertTrue(
			data.getParameters().containsKey("eventSubmit_doCauseexception"));
		try {
			ActionLoader.getInstance().exec(data, data.getAction());
			fail("Should have bubbled out an exception thrown by the action.");
		} catch (Exception e) {
			//good
		}
		try {
			ActionLoader.getInstance().exec(pipelineData, data.getAction());
			fail("Should have bubbled out an exception thrown by the action.");
		} catch (Exception e) {
			//good
		}
	}

	/**
	 * This unit test verifies that if your standard doPerform is called,
	 * and it throws an Exception, if the action.event.bubbleexception
     * property is set to false then the exception is NOT bubbled up
	 *
	 * @throws Exception If something goes wrong with the unit test
	 */
	public void testDoPerformDoesntBubbleException() throws Exception {
		Turbine.getConfiguration().setProperty("action.event.bubbleexception",Boolean.FALSE);
		assertFalse(Turbine.getConfiguration().getBoolean("action.event.bubbleexception"));
		RunData data = getRunData(request,response,config);
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
		data.setAction("VelocityActionThrowsException");
		try {
			ActionLoader.getInstance().exec(data, data.getAction());

		} catch (Exception e) {
			fail("Should NOT have thrown an exception:" + e.getMessage());
		}
		try {
			ActionLoader.getInstance().exec(pipelineData, data.getAction());

		} catch (Exception e) {
			fail("Should NOT have thrown an exception:" + e.getMessage());
		}
	}
	/**
     * This unit test verifies that if an Action Event doEventSubmit_ is called,
     * and it throws an Exception, if the action.event.bubbleexception
     * property is set to false then the exception is NOT bubbled up
     *
     * @throws Exception If something goes wrong with the unit test
     */
	public void testActionEventDoesntBubbleException() throws Exception {
		// can't seem to figure out how to setup the Mock Request with the right parameters...
		Turbine.getConfiguration().setProperty("action.event.bubbleexception",Boolean.FALSE);
		request.setupAddParameter("eventSubmit_doCauseexception", "foo");
		RunData data = getRunData(request,response,config);
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
		data.setAction("VelocityActionThrowsException");
		data.getParameters().add("eventSubmit_doCauseexception", "foo");
		assertTrue(
			data.getParameters().containsKey("eventSubmit_doCauseexception"));

		try {
			ActionLoader.getInstance().exec(data, data.getAction());
		} catch (Exception e) {
			fail("Should NOT have thrown an exception:" + e.getMessage());
		}
		try {
			ActionLoader.getInstance().exec(pipelineData, data.getAction());
		} catch (Exception e) {
			fail("Should NOT have thrown an exception:" + e.getMessage());
		}
	}
	public void testNonexistentActionCausesError() throws Exception {
	    RunData data = getRunData(request,response,config);
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
		data.setAction("ImaginaryAction");
		try {
			ActionLoader.getInstance().exec(data, "boo");
			fail("Should have thrown an exception");
		} catch (Exception e) {
			//good
		}
		try {
			ActionLoader.getInstance().exec(pipelineData, "boo");
			fail("Should have thrown an exception");
		} catch (Exception e) {
			//good
		}
	}

	public void testDoPerformWithRunData() throws Exception
	{
	    RunData data = getRunData(request,response,config);
		data.setAction("VelocityActionDoesNothing");
		try {
			ActionLoader.getInstance().exec(data, data.getAction());
		} catch (Exception e) {
		    e.printStackTrace();
		    Assert.fail("Should not have thrown an exception.");
		}

	}

	public void testDoPerformWithPipelineData() throws Exception
	{
	    RunData data = getRunData(request,response,config);
        PipelineData pipelineData = data;
		data.setAction("VelocityActionDoesNothing");
		int numberOfCalls = VelocityActionDoesNothing.numberOfCalls;
		int pipelineDataCalls = VelocityActionDoesNothing.pipelineDataCalls;
		int runDataCalls = VelocityActionDoesNothing.runDataCalls;
		try {
			ActionLoader.getInstance().exec(pipelineData, data.getAction());
		} catch (Exception e) {
		    e.printStackTrace();
		    Assert.fail("Should not have thrown an exception.");
		}
		assertEquals(numberOfCalls+1,VelocityActionDoesNothing.numberOfCalls);
		assertEquals(runDataCalls,VelocityActionDoesNothing.runDataCalls);
		assertEquals(pipelineDataCalls+1,VelocityActionDoesNothing.pipelineDataCalls);

	}

}
