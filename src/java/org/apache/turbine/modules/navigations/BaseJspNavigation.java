package org.apache.turbine.modules.navigations;


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


import org.apache.ecs.ConcreteElement;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Navigation;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.util.RunData;

/**
 * Base JSP navigation that should be subclassed by Navigation that want to
 * use JSP.  Subclasses should override the doBuildTemplate() method.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class BaseJspNavigation
        extends TemplateNavigation
{
    /** The prefix for lookup up navigation pages */
    private final String prefix = Navigation.PREFIX + "/";

    /** Injected service instance */
    @TurbineService
    private JspService jspService;

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
        // empty
    }

    /**
     * Method that sets up beans and forward the request to the JSP.
     *
     * @param pipelineData the PipelineData object
     * @return null - the JSP sends the information
     * @throws Exception a generic exception.
     */
    @Override
    public ConcreteElement buildTemplate(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        // get the name of the JSP we want to use
        String templateName = data.getTemplateInfo().getNavigationTemplate();

        // navigations are used by a layout
        jspService.handleRequest(pipelineData, prefix + templateName);
        return null;
    }
}
