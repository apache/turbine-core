package org.apache.turbine.modules.screens;

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

import org.apache.commons.lang3.StringUtils;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.RunData;

/**
 * Base JSP Screen that should be subclassed by screens that want to
 * use JSP.  Subclasses should override the doBuildTemplate() method.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author Frank Y. Kim
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class BaseJspScreen
        extends TemplateScreen
{
    /** The prefix for lookup up screen pages */
    private static final String prefix = PREFIX + "/";

    /** Injected service instance */
    @TurbineService
    private JspService jspService;

    /** Injected service instance */
    @TurbineService
    private TemplateService templateService;

    /**
     * Method that sets up beans and forward the request to the JSP.
     *
     * @param pipelineData Turbine information.
     * @return null - the JSP sends the information.
     * @throws Exception a generic exception.
     */
    @Override
    public String buildTemplate(PipelineData pipelineData)
            throws Exception
    {
        RunData data = pipelineData.getRunData();
        String screenTemplate = data.getTemplateInfo().getScreenTemplate();
        // get the name of the template we want to use
        String templateName
            = templateService.getScreenTemplateName(screenTemplate);

        // The Template Service could not find the Screen
        if (StringUtils.isEmpty(templateName))
        {
            log.error("Screen " + screenTemplate + " not found!");
            throw new Exception("Could not find screen for " + screenTemplate);
        }

        // let service know whether we are using a layout
        jspService.handleRequest(pipelineData, prefix + templateName,
                                 getLayout(pipelineData) == null);

        return null;
    }

    /**
     * Method to be overridden by subclasses to include data in beans, etc.
     *
     * @param pipelineData the PipelineData object
     * @throws Exception a generic exception.
     */
    @Override
    protected void doBuildTemplate(PipelineData pipelineData)
        throws Exception
    {
        // abstract method
    }
}
