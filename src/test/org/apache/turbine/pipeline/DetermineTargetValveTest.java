package org.apache.turbine.pipeline;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.EnhancedMockHttpServletRequest;
import org.apache.turbine.test.EnhancedMockHttpSession;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.uri.URIConstants;

import com.mockobjects.servlet.MockHttpServletResponse;
import com.mockobjects.servlet.MockServletConfig;

/**
 * Tests TurbinePipeline.
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DetermineTargetValveTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;
    private MockServletConfig config = null;
    private EnhancedMockHttpServletRequest request = null;
    private EnhancedMockHttpSession session = null;
    private HttpServletResponse response = null;
    private static ServletConfig sc = null;
    /**
     * Constructor
     */
    public DetermineTargetValveTest(String testName) throws Exception
    {
        super(testName);
    }

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
    
    /**
     * Tests the Valve.
     */
    public void testScreenSet() throws Exception
    {
        Vector v = new Vector();
        v.add(URIConstants.CGI_SCREEN_PARAM);
        request.setupGetParameterNames(v.elements());
        
        request.setupAddParameter(URIConstants.CGI_SCREEN_PARAM,"TestScreen");
        
        RunData runData = getRunData(request,response,config);
        
        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;
        DetermineTargetValve valve = new DetermineTargetValve();
        pipeline.addValve(valve);

        pipeline.invoke(pipelineData);
        assertEquals("TestScreen",runData.getScreen());


    }
    public void testScreenNotSet() throws Exception
    {
        Vector v = new Vector();
        v.add(URIConstants.CGI_SCREEN_PARAM);
        request.setupGetParameterNames(v.elements());
        
        String screens[] = new String[1];
        screens[0]=null;
        request.setupAddParameter(URIConstants.CGI_SCREEN_PARAM,screens);
        
        RunData runData = getRunData(request,response,config);
        
        Pipeline pipeline = new TurbinePipeline();
        PipelineData pipelineData = runData;

        DetermineTargetValve valve = new DetermineTargetValve();
        pipeline.addValve(valve);

        pipeline.invoke(pipelineData);
        assertEquals("",runData.getScreen());


    }
    
   
}
