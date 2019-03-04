package org.apache.turbine.modules.layouts;


import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;

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


import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This Layout module allows Velocity templates
 * to be used as layouts.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public abstract class VelocityLayout implements Layout
{
    /** Logging */
    protected final Logger log = LogManager.getLogger(this.getClass());

    /** The prefix for lookup up layout pages */
    protected static final String prefix = PREFIX + "/";

    /** Injected service instance */
    @TurbineService
    protected VelocityService velocityService;

    /**
     * Method called by LayoutLoader.
     *
     *
     * @param pipelineData PipelineData
     * @throws Exception generic exception
     */
    @Override
    public void doBuild(PipelineData pipelineData)
        throws Exception
    {
        RunData data = pipelineData.getRunData();
        // Get the context needed by Velocity.
        Context context = velocityService.getContext(pipelineData);

        // Provide objects to Velocity context
        populateContext(pipelineData, context);

        // Grab the layout template set in the VelocityPage.
        // If null, then use the default layout template
        // (done by the TemplateInfo object)
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        // Set the locale and content type
        data.getResponse().setLocale(data.getLocale());
        data.getResponse().setContentType(data.getContentType());

        log.debug("Now trying to render layout {}", templateName);

        // Finally, generate the layout template and send it to the browser
        render(pipelineData, context, templateName);
    }

    /**
     * Populate Velocity context
     *
     * @param pipelineData PipelineData
     * @param context the Velocity context
     *
     * @throws Exception if evaluation fails
     */
    protected abstract void populateContext(PipelineData pipelineData, Context context)
        throws Exception;

    /**
     * Render layout
     *
     * @param pipelineData PipelineData
     * @param context the Velocity context
     * @param templateName relative path to Velocity template
     *
     * @throws Exception if rendering fails
     */
    protected void render(PipelineData pipelineData, Context context, String templateName)
        throws Exception
    {
        velocityService.handleRequest(context,
                prefix + templateName,
                pipelineData.get(Turbine.class, HttpServletResponse.class)
                    .getOutputStream());
    }
}
