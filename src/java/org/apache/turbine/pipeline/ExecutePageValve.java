package org.apache.turbine.pipeline;

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

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.PageLoader;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the Page Generation portion of the "Turbine classic"
 * processing pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
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
            executePage((RunData)pipelineData.get(RunData.class));
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
    protected void executePage(RunData data)
        throws Exception
    {      
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

        PageLoader.getInstance().exec(data, defaultPage);

    }
}
