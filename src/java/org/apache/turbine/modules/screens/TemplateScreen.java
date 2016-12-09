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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.annotation.TurbineLoader;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * Template Screen.
 *
 * Base Template Screens should extend this class and override the
 * buildTemplate() method.  Users of the particular service can then
 * override the doBuildTemplate() for any specific pre-processing.
 * You can also override the doBuild() method in order to add extra
 * functionality to your system, but you need to make sure to at least
 * duplicate the existing functionality in order for things to work.
 * Look at the code for the doBuild() method to get an idea of what is
 * going on there (it is quite simple really).
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class TemplateScreen
    extends Screen
{
    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());

    /** Injected service instance */
    @TurbineService
    private TemplateService templateService;

    /** Injected loader instance */
    @TurbineLoader( Screen.class )
    private ScreenLoader screenLoader;

    /**
     * This method should be overridden by subclasses that wish to add
     * specific business logic.
     * @param pipelineData Turbine information.
     * @throws Exception A generic exception.
     */
    protected abstract void doBuildTemplate(PipelineData pipelineData)
            throws Exception;

    /**
     * This method should be implemented by Base template classes.  It
     * should contain the specific template service code to generate
     * the template.
     * @param pipelineData Turbine information.
     * @return the content of the screen
     * @throws Exception A generic exception.
     */
    public abstract String buildTemplate(PipelineData pipelineData)
            throws Exception;

    /**
     * This method can be overridden to write code that executes when
     * the template has been built (called from a finally clause, so
     * executes regardless of whether an exception is thrown or not)
     *
     * @param pipelineData Turbine information
     */
    protected void doPostBuildTemplate(PipelineData pipelineData)
    {
        // empty
    }

    /**
     * This method is called by the Screenloader to construct the
     * Screen.
     *
     * @param pipelineData Turbine information.
     * @return the content of the screen
     * @throws Exception A generic exception.
     */
    @Override
    protected String doBuild(PipelineData pipelineData)
            throws Exception
    {
        String out = null;

        try
        {
            doBuildTemplate(pipelineData);
            out = buildTemplate(pipelineData);
        }
        finally
        {
            doPostBuildTemplate(pipelineData);
        }

        return out;
    }

    /**
     * This method is used when you want to short circuit a Screen and
     * change the template that will be executed next. <b>Note that the current
     * context will be applied to the next template that is executed.
     * If you want to have the context executed for the next screen,
     * to be the same one as the next screen, then you should use the
     * TemplateScreen.doRedirect() method.</b>
     *
     * @param pipelineData Turbine information.
     * @param template The name of the next template.
     */
    public static void setTemplate(PipelineData pipelineData, String template)
    {
        RunData data = (RunData)pipelineData;
        TemplateInfo ti = data.getTemplateInfo();
        TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);

        ti.setScreenTemplate(template);
        try
        {
            // We have do call getScreenTemplate because of the path
            // separator.
            ti.setLayoutTemplate(templateService.getLayoutTemplateName(ti.getScreenTemplate()));
        }
        catch (Exception e)
        {
            // Nothing to do.
        }
    }

    /**
     * You can call this within a Screen to cause an internal redirect
     * to happen.  It essentially allows you to stop execution in one
     * Screen and instantly execute another Screen.  Don't worry, this
     * does not do a HTTP redirect and also if you have anything added
     * in the Context, it will get carried over.
     *
     * <p>
     *
     * This class is useful if you have a Screen that submits to
     * another Screen and you want it to do error validation before
     * executing the other Screen.  If there is an error, you can
     * doRedirect() back to the original Screen.
     *
     * @param pipelineData Turbine information.
     * @param screen Name of screen to redirect to.
     * @param template Name of template.
     * @throws Exception A generic exception.
     */
    public void doRedirect(PipelineData pipelineData, String screen, String template)
            throws Exception
    {
        log.debug("doRedirect(data, " + screen + ", " + template + ")");
        setTemplate(pipelineData, template);
        screenLoader.exec(pipelineData, screen);
    }

    /**
     * You can call this within a Screen to cause an internal redirect
     * to happen.  It essentially allows you to stop execution in one
     * Screen and instantly execute another Screen.  Don't worry, this
     * does not do a HTTP redirect and also if you have anything added
     * in the Context, it will get carried over.
     *
     * <p>
     *
     * This class is useful if you have a Screen that submits to
     * another Screen and you want it to do error validation before
     * executing the other Screen.  If there is an error, you can
     * doRedirect() back to the original Screen.
     *
     * @param pipelineData Turbine information.
     * @param template Name of template.
     * @throws Exception A generic exception.
     */
    public void doRedirect(PipelineData pipelineData, String template)
            throws Exception
    {
        doRedirect(pipelineData, templateService.getScreenName(template), template);
    }
}
