package org.apache.turbine.services.jsp;


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


import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Facade class for the Jsp Service.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public abstract class TurbineJsp
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a JspService implementation instance
     */
    protected static JspService getService()
    {
        return (JspService) TurbineServices
            .getInstance().getService(JspService.SERVICE_NAME);
    }

    /**
     * Adds some convenience objects to the request.  For example an instance
     * of JspLink which can be used to generate links to other templates.
     *
     * @deprecated Use the PipelineData version.
     * @param data the turbine rundata object
     */
    public static void addDefaultObjects(RunData data)
    {
        getService().addDefaultObjects(data);
    }


    /**
     * Adds some convenience objects to the request.  For example an instance
     * of JspLink which can be used to generate links to other templates.
     *
     * @param data the turbine pipelinedData object
     */
    public static void addDefaultObjects(PipelineData pipelineData)
    {
        //Map runDataMap = (Map) pipelineData.get(RunData.class);
        //RunData data = (RunData)runDataMap.get(RunData.class);
        RunData runData = (RunData)pipelineData;
        addDefaultObjects(runData);
    }

    /**
     * executes the JSP given by templateName.
     *
     * @deprecated Use the PipelineData version.
     * @param data A RunData Object
     * @param templateName The template to execute
     * @param isForward whether to perform a forward or include.
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    public static void handleRequest(RunData data, String templateName, boolean isForward)
        throws TurbineException
    {
        getService().handleRequest(data, templateName, isForward);
    }

    /**
     * executes the JSP given by templateName.
     *
     * @deprecated Use the PipelineData version.
     * @param data A RunData Object
     * @param templateName The template to execute
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    public static void handleRequest(RunData data, String templateName)
        throws TurbineException
    {
        getService().handleRequest(data, templateName);
    }

    /**
     * executes the JSP given by templateName.
     *
     * @param data A RunData Object
     * @param templateName The template to execute
     * @param isForward whether to perform a forward or include.
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    public static void handleRequest(PipelineData pipelineData, String templateName, boolean isForward)
        throws TurbineException
    {
        //Map runDataMap = (Map) pipelineData.get(RunData.class);
        //RunData data = (RunData)runDataMap.get(RunData.class);
        RunData runData = (RunData)pipelineData;
        handleRequest(runData, templateName, isForward);
    }

    /**
     * executes the JSP given by templateName.
     *
     * @param data A RunData Object
     * @param templateName The template to execute
     *
     * @throws TurbineException If a problem occured while executing the JSP
     */
    public static void handleRequest(PipelineData pipelineData, String templateName)
        throws TurbineException
    {
        if(!(pipelineData instanceof RunData)){
            throw new RuntimeException("Can't cast to rundata from pipeline data.");
        }
        handleRequest((RunData)pipelineData, templateName);
    }


    /**
     * Returns the default buffer size of the JspService
     *
     * @return The default buffer size.
     */
    public static int getDefaultBufferSize()
    {
        return getService().getDefaultBufferSize();
    }

    /**
     * Searchs for a template in the default.template path[s] and
     * returns the template name with a relative path which is required
     * by <a href="http://java.sun.com/products/servlet/2.3/javadoc/javax/servlet/ServletContext.html#getRequestDispatcher(java.lang.String)">javax.servlet.RequestDispatcher</a>
     *
     * @param template The name of the template to search for.
     *
     * @return the template with a relative path
     */
    public static String getRelativeTemplateName(String template)
    {
        return getService().getRelativeTemplateName(template);
    }
}
