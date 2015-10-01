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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Base Velocity Screen.  The buildTemplate() assumes the template
 * parameter has been set in the PipelineData object.  This provides the
 * ability to execute several templates from one Screen.
 *
 * <p>
 *
 * If you need more specific behavior in your application, extend this
 * class and override the doBuildTemplate() method.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class VelocityScreen
    extends TemplateScreen
{
    /** The prefix for lookup up screen pages */
    private final String prefix = getPrefix() + "/";

    /** Injected service instance */
    @TurbineService
    protected VelocityService velocity;

    /** Injected service instance */
    @TurbineService
    protected TemplateService templateService;

    /** Injected configuration instance */
    @TurbineConfiguration
    protected Configuration conf;

    /**
     * Velocity Screens extending this class should override this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param pipelineData Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(PipelineData pipelineData,
                                   Context context)
            throws Exception
    {
        // empty
    }

    /**
     * Needs to be implemented to make TemplateScreen like us.  The
     * actual method that you should override is the one with the
     * context in the parameter list.
     *
     * @param pipelineData Turbine information.
     * @exception Exception, a generic exception.
     */
    @Override
    protected void doBuildTemplate(PipelineData pipelineData)
            throws Exception
    {
        doBuildTemplate(pipelineData, velocity.getContext(pipelineData));
    }

    /**
     * This builds the Velocity template.
     *
     * @param pipelineData Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    @Override
    public ConcreteElement buildTemplate(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        String screenData = null;

        Context context = velocity.getContext(pipelineData);

        String screenTemplate = data.getTemplateInfo().getScreenTemplate();
        String templateName
            = templateService.getScreenTemplateName(screenTemplate);

        // The Template Service could not find the Screen
        if (StringUtils.isEmpty(templateName))
        {
            log.error("Screen " + screenTemplate + " not found!");
            throw new Exception("Could not find screen for " + screenTemplate);
        }

        try
        {
            // if a layout has been defined return the results, otherwise
            // send the results directly to the output stream.
            if (getLayout(pipelineData) == null)
            {
                velocity.handleRequest(context,
                        prefix + templateName,
                        data.getResponse().getOutputStream());
            }
            else
            {
                screenData =
                    velocity.handleRequest(context, prefix + templateName);
            }
        }
        catch (Exception e)
        {
            // If there is an error, build a $processingException and
            // attempt to call the error.vm template in the screens
            // directory.
            context.put (TurbineConstants.PROCESSING_EXCEPTION_PLACEHOLDER, e.toString());
            context.put (TurbineConstants.STACK_TRACE_PLACEHOLDER, ExceptionUtils.getStackTrace(e));

            templateName = conf.getString(TurbineConstants.TEMPLATE_ERROR_KEY,
                           TurbineConstants.TEMPLATE_ERROR_VM);

            screenData = velocity.handleRequest(context, prefix + templateName);
        }

        // package the response in an ECS element
        StringElement output = new StringElement();
        output.setFilterState(false);

        if (screenData != null)
        {
            output.addElement(screenData);
        }
        return output;
    }
}
