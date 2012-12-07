package org.apache.jetspeed.modules.screens;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.ecs.ConcreteElement;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.screens.VelocityScreen;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * VelocityCachedScreen was pre-Turbine 2.3.3 VelocityDirectScreen, now with method added for {@link PipelineData}.
 * 
 * <p>
 */
public class VelocityCachedScreen
    extends VelocityScreen
{
    /** The prefix for lookup up screen pages */
    private String prefix = getPrefix() + "/";

    /**
     * This builds the Velocity template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data)
        throws Exception
    {
        Context context = TurbineVelocity.getContext(data);

        String screenTemplate = data.getTemplateInfo().getScreenTemplate();
        String templateName
            = TurbineTemplate.getScreenTemplateName(screenTemplate);

        // The Template Service could not find the Screen
        if (StringUtils.isEmpty(templateName))
        {
            log.error("Screen " + screenTemplate + " not found!");
            throw new Exception("Could not find screen for " + screenTemplate);
        }

        try
        {
            TurbineVelocity.handleRequest(context,
                                          prefix + templateName,
                                          data.getOut());

        }
        catch (Exception e)
        {
            // If there is an error, build a $processingException and
            // attempt to call the error.vm template in the screens
            // directory.
            context.put (TurbineConstants.PROCESSING_EXCEPTION_PLACEHOLDER, e.toString());
            context.put (TurbineConstants.STACK_TRACE_PLACEHOLDER, ExceptionUtils.getStackTrace(e));

            templateName = Turbine.getConfiguration()
                .getString(TurbineConstants.TEMPLATE_ERROR_KEY,
                           TurbineConstants.TEMPLATE_ERROR_VM);

            TurbineVelocity.handleRequest(context,
                    prefix + templateName,
                    data.getOut());
        }

        return null;
    }
    
    /**
     * This builds the Velocity template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        Context context = TurbineVelocity.getContext(pipelineData);

        String screenTemplate = data.getTemplateInfo().getScreenTemplate();
        String templateName
            = TurbineTemplate.getScreenTemplateName(screenTemplate);

        // The Template Service could not find the Screen
        if (StringUtils.isEmpty(templateName))
        {
            log.error("Screen " + screenTemplate + " not found!");
            throw new Exception("Could not find screen for " + screenTemplate);
        }

        try
        {
            TurbineVelocity.handleRequest(context,
                                          prefix + templateName,
                                          data.getOut());

        }
        catch (Exception e)
        {
            // If there is an error, build a $processingException and
            // attempt to call the error.vm template in the screens
            // directory.
            context.put (TurbineConstants.PROCESSING_EXCEPTION_PLACEHOLDER, e.toString());
            context.put (TurbineConstants.STACK_TRACE_PLACEHOLDER, ExceptionUtils.getStackTrace(e));

            templateName = Turbine.getConfiguration()
                .getString(TurbineConstants.TEMPLATE_ERROR_KEY,
                           TurbineConstants.TEMPLATE_ERROR_VM);

            TurbineVelocity.handleRequest(context,
                    prefix + templateName,
                    data.getOut());
        }

        return null;
    }
}


