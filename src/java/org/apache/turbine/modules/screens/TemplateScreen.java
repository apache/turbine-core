package org.apache.turbine.modules.screens;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.modules.Screen;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.pipeline.PipelineData;

import org.apache.turbine.services.template.TurbineTemplate;

import org.apache.turbine.util.RunData;

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

    /**
     * This method should be overidden by subclasses that wish to add
     * specific business logic.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception Exception A generic exception.
     */
    protected abstract void doBuildTemplate(RunData data)
            throws Exception;

    /**
     * This method should be overidden by subclasses that wish to add
     * specific business logic.
     * Should revert to abstract when RunData has gone.
     * @param data Turbine information.
     * @exception Exception A generic exception.
     */
    protected void doBuildTemplate(PipelineData pipelineData)
    throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        doBuildTemplate(data);
    }

    
    /**
     * This method should be implemented by Base template classes.  It
     * should contain the specific template service code to generate
     * the template.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception A generic exception.
     */
    public abstract ConcreteElement buildTemplate(RunData data)
            throws Exception;

    /**
     * This method should be implemented by Base template classes.  It
     * should contain the specific template service code to generate
     * the template.
     * Should revert to abstract when RunData goes.
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception A generic exception.
     */
    public ConcreteElement buildTemplate(PipelineData pipelineData)
    throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        return buildTemplate(data);
    }

    
    /**
     * This method can be overridden to write code that executes when
     * the template has been built (called from a finally clause, so
     * executes regardless of whether an exception is thrown or not)
     * 
     * @deprecated Use PipelineData version instead.
     */
    protected void doPostBuildTemplate(RunData data)
    {
        // empty
    }

    /**
     * This method can be overridden to write code that executes when
     * the template has been built (called from a finally clause, so
     * executes regardless of whether an exception is thrown or not)
     */
    protected void doPostBuildTemplate(PipelineData pipelineData)
    {
        // empty
    }

    
    /**
     * This method is called by the Screenloader to construct the
     * Screen.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception A generic exception.
     */
    protected ConcreteElement doBuild(RunData data)
            throws Exception
    {
        ConcreteElement out = null;

        try
        {
            doBuildTemplate(data);
            out = buildTemplate(data);
        }
        finally
        {
            doPostBuildTemplate(data);
        }

        return out;
    }

    /**
     * This method is called by the Screenloader to construct the
     * Screen.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception A generic exception.
     */
    protected ConcreteElement doBuild(PipelineData pipelineData)
            throws Exception
    {
        ConcreteElement out = null;

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
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param template The name of the next template.
     */
    public static void setTemplate(RunData data, String template)
    {
        data.getTemplateInfo().setScreenTemplate(template);
        try
        {
            // We have do call getScreenTemplate because of the path
            // separator.
            data.getTemplateInfo().setLayoutTemplate(
                    TurbineTemplate.getLayoutTemplateName(
                            data.getTemplateInfo().getScreenTemplate()));
        }
        catch (Exception e)
        {
            // Nothing to do.
        }
    }

    /**
     * This method is used when you want to short circuit a Screen and
     * change the template that will be executed next. <b>Note that the current
     * context will be applied to the next template that is executed.
     * If you want to have the context executed for the next screen,
     * to be the same one as the next screen, then you should use the
     * TemplateScreen.doRedirect() method.</b>
     *
     * @param data Turbine information.
     * @param template The name of the next template.
     */
    public static void setTemplate(PipelineData pipelineData, String template)
    {
        Map runDataMap = (Map) pipelineData.get(RunData.class);
        RunData data = (RunData)runDataMap.get(RunData.class);
        setTemplate(data, template);
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
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param screen Name of screen to redirect to.
     * @param template Name of template.
     * @exception Exception A generic exception.
     */
    public void doRedirect(RunData data, String screen, String template)
            throws Exception
    {
        log.debug("doRedirect(data, " + screen + ", " + template + ")");
        setTemplate(data, template);
        ScreenLoader.getInstance().exec(data, screen);
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
     * @param data Turbine information.
     * @param screen Name of screen to redirect to.
     * @param template Name of template.
     * @exception Exception A generic exception.
     */
    public void doRedirect(PipelineData pipelineData, String screen, String template)
            throws Exception
    {
        RunData data = (RunData) getRunData(pipelineData);
        log.debug("doRedirect(data, " + screen + ", " + template + ")");
        setTemplate(data, template);
        ScreenLoader.getInstance().exec(pipelineData, screen);
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
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param template Name of template.
     * @exception Exception A generic exception.
     */
    public void doRedirect(RunData data, String template)
            throws Exception
    {
        doRedirect(data, TurbineTemplate.getScreenName(template), template);
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
     * @param data Turbine information.
     * @param template Name of template.
     * @exception Exception A generic exception.
     */
    public void doRedirect(PipelineData pipelineData, String template)
            throws Exception
    {
        doRedirect(pipelineData, template);
    }

    
}
