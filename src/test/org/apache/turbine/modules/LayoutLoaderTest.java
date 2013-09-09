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

import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.turbine.modules.layouts.TestVelocityOnlyLayout;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletConfig;


/**
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class LayoutLoaderTest extends BaseTestCase {
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
		Vector<String> v = new Vector<String>();
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
	 * Constructor for LayoutLoaderTest.
	 * @param arg0
	 */
	public LayoutLoaderTest(String arg0) throws Exception {
		super(arg0);
	}

	public void testPipelineDataContainsRunData()
	{
	    try
	    {
		    RunData data = getRunData(request,response,config);
            PipelineData pipelineData = data;
			data.setLayout("TestVelocityOnlyLayout");
			int numberOfCalls = TestVelocityOnlyLayout.numberOfCalls;
			try {
				LayoutLoader.getInstance().exec(pipelineData, data.getLayout());
			} catch (Exception e) {
			    e.printStackTrace();
			    Assert.fail("Should not have thrown an exception.");
			}
			assertEquals(numberOfCalls+1,TestVelocityOnlyLayout.numberOfCalls);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        Assert.fail("Should not have thrown an exception.");
	    }
	}

	public void testDoBuildWithRunData()
	{
	    try
	    {
		    RunData data = getRunData(request,response,config);
			data.setLayout("TestVelocityOnlyLayout");
			int numberOfCalls = TestVelocityOnlyLayout.numberOfCalls;
			try {
				LayoutLoader.getInstance().exec(data, data.getLayout());
			} catch (Exception e) {
			    e.printStackTrace();
			    Assert.fail("Should not have thrown an exception.");
			}
			assertEquals(numberOfCalls+1,TestVelocityOnlyLayout.numberOfCalls);
	    }
	    catch (Exception e)
	    {
	        e.printStackTrace();
	        Assert.fail("Should not have thrown an exception.");
	    }
	}
}
