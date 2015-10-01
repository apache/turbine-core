package org.apache.turbine.modules.layouts;


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


import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.jsp.util.JspNavigation;
import org.apache.turbine.services.jsp.util.JspScreenPlaceholder;
import org.apache.turbine.util.RunData;

/**
 * This Layout module allows JSP templates to be used as layouts. Since
 * dynamic content is supposed to be primarily located in screens and
 * navigations there should be relatively few reasons to subclass this Layout.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class JspLayout
    extends Layout
{
    /** The prefix for lookup up layout pages */
    private String prefix = Layout.PREFIX + "/";

    /** Injected service instance */
    @TurbineService
    private JspService jspService;

    /**
     * Method called by LayoutLoader.
     *
     * @param pipelineData PipelineData
     * @throws Exception generic exception
     */
    @Override
    public void doBuild(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        data.getResponse().setContentType("text/html");
        data.declareDirectResponse();

        // variable to reference the screen in the layout template
        data.getRequest()
            .setAttribute(TurbineConstants.SCREEN_PLACEHOLDER,
                          new JspScreenPlaceholder(data));

        // variable to reference the navigations in the layout template
        data.getRequest().setAttribute(
            TurbineConstants.NAVIGATION_PLACEHOLDER,
            new JspNavigation(data));

        // Grab the layout template set in the TemplatePage.
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        jspService.handleRequest(pipelineData, prefix + templateName, true);
    }
}
