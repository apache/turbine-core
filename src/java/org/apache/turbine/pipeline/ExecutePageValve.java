package org.apache.turbine.pipeline;


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


import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.PageLoader;
import org.apache.turbine.pipeline.AbstractValve;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.pipeline.ValveContext;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the Page Generation portion of the "Turbine classic"
 * processing pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class ExecutePageValve
    extends AbstractValve
{
    Log log = LogFactory.getLog(ExecutePageValve.class);
    TemplateService templateService;
    /**
     * Creates a new instance.
     */
    public ExecutePageValve()
    {

    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        try
        {
            executePage(pipelineData);
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }

    /**
     * execute the page generation.
     *
     * @param data The run-time data.
     */
    protected void executePage(PipelineData pipelineData)
        throws Exception
    {
        RunData data = (RunData)getRunData(pipelineData);

        // Start the execution phase. DefaultPage will execute the
        // appropriate action as well as get the Layout from the
        // Screen and then execute that. The Layout is then
        // responsible for executing the Navigation and Screen
        // modules.
        //
        // Note that by default, this cannot be overridden from
        // parameters passed in via post/query data. This is for
        // security purposes.  You should really never need more
        // than just the default page.  If you do, add logic to
        // DefaultPage to do what you want.

        // see if this should be static or not...  Or loaded from
        // Turbine.java.
        templateService = TurbineTemplate.getService();

        String defaultPage = (templateService == null)
        ? null :templateService.getDefaultPageName(data);

        if (defaultPage == null)
        {
            /*
             * In this case none of the template services are running.
             * The application may be using ECS for views, or a
             * decendent of RawScreen is trying to produce output.
             * If there is a 'page.default' property in the TR.props
             * then use that, otherwise return DefaultPage which will
             * handle ECS view scenerios and RawScreen scenerios. The
             * app developer can still specify the 'page.default'
             * if they wish but the DefaultPage should work in
             * most cases.
             */
            defaultPage = Turbine.getConfiguration().getString(TurbineConstants.PAGE_DEFAULT_KEY,
                    TurbineConstants.PAGE_DEFAULT_DEFAULT);
        }

        PageLoader.getInstance().exec(pipelineData, defaultPage);

    }
}
