package org.apache.turbine.modules;
/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.turbine.Turbine;
import org.apache.turbine.modules.actions.VelocityActionDoesNothing;
import org.apache.turbine.modules.actions.VelocityActionThrowsException;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.DefaultPipelineData;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.template.TemplateService;
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
	private static TemplateService ts = null;
	private MockServletConfig config = null;
	private EnhancedMockHttpServletRequest request = null;
	private EnhancedMockHttpSession session = null;
	private HttpServletResponse response = null;
	private static ServletConfig sc = null;
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
		sc = config;
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
		PipelineData pipelineData = new DefaultPipelineData();
		Map runDataMap = new HashMap();
		runDataMap.put(RunData.class, data);
		pipelineData.put(RunData.class, runDataMap);
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
