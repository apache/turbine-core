package org.apache.turbine.test;


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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;

import org.apache.log4j.PropertyConfigurator;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;

import com.mockobjects.servlet.MockHttpServletRequest;

/**
 * Base functionality to be extended by all Apache Turbine test cases.  Test
 * case implementations are used to automate testing via JUnit.
 *
 * @author <a href="mailto:celkins@scardini.com">Christopher Elkins</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class BaseTestCase
        extends TestCase
{
    File log4jFile = new File("conf/test/Log4j.properties");

    public BaseTestCase(String name)
            throws Exception
    {
        super(name);

        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
            p.setProperty(Turbine.APPLICATION_ROOT_KEY, new File(".").getAbsolutePath());
            PropertyConfigurator.configure(p);

        }
        catch (FileNotFoundException fnf)
        {
            System.err.println("Could not open Log4J configuration file "
                    + log4jFile);
        }


    }

    protected RunData getRunData(HttpServletRequest request,HttpServletResponse response,ServletConfig config) throws Exception {
        RunDataService rds =
            (RunDataService) TurbineServices.getInstance().getService(
                    RunDataService.SERVICE_NAME);
        RunData runData = rds.getRunData(request, response, config);
        return runData;
    }
    protected PipelineData getPipelineData(HttpServletRequest request,HttpServletResponse response,ServletConfig config) throws Exception {
       RunData runData = getRunData(request,response,config);
       return runData;
    }


    protected MockHttpServletRequest getMockRequest(){
        EnhancedMockHttpServletRequest request = new EnhancedMockHttpServletRequest();
        EnhancedMockHttpSession session = new EnhancedMockHttpSession();
        session.setupGetAttribute(User.SESSION_KEY, null);
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
        request.setSession(session);
        return request;

    }
}

