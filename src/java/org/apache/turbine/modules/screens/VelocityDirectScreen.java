package org.apache.turbine.modules.screens;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.services.template.TurbineTemplate;

import org.apache.turbine.services.velocity.TurbineVelocity;

import org.apache.turbine.util.RunData;
import org.apache.commons.lang.exception.ExceptionUtils;

import org.apache.velocity.context.Context;

/**
 * Base Velocity Screen.  The buildTemplate() assumes the template
 * parameter has been set in the RunData object.  This provides the
 * ability to execute several templates from one Screen.
 *
 * <p>
 *
 * If you need more specific behavior in your application, extend this
 * class and override the doBuildTemplate() method.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class VelocityDirectScreen extends VelocityScreen
{

    /**
     * This builds the Velocity template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data) throws Exception
    {
        StringElement output = new StringElement();
        String screenData = null;
        Context context = TurbineVelocity.getContext(data);

        // This will already be properly set and will not be null
        // because of TemplateSessionValidator.
        String templateName = TurbineTemplate.getScreenTemplateName(
            data.getTemplateInfo().getScreenTemplate());

        // Template service adds the leading slash, but make it sure.
        if ((templateName.length() > 0) &&
            (templateName.charAt(0) != '/'))
        {
            templateName = '/' + templateName;
        }

        try
        {
            // if a layout has been defined return the results, otherwise
            // send the results directly to the output stream.
            if (getLayout(data) == null)
            {
                TurbineVelocity.handleRequest(context,
                    "screens" + templateName,
                    data.getResponse().getOutputStream());
            }
            else
            {
                TurbineVelocity.handleRequest(context,
                    "screens" + templateName,
                    data.getOut());
            }
        }
        catch (Exception e)
        {
            // If there is an error, build a $processingException and
            // attempt to call the error.vm template in the screens
            // directory.
            context.put ("processingException", e.toString());
            context.put ("stackTrace", ExceptionUtils.getStackTrace(e));
            templateName = TurbineResources.getString(
                "template.error", "/error.vm");
            if ((templateName.length() > 0) &&
                (templateName.charAt(0) != '/'))
            {
                templateName = '/' + templateName;
            }
            TurbineVelocity.handleRequest(context,
                "screens" + templateName,
                data.getOut());
        }

        // package the response in an ECS element
        if (screenData != null)
        {
            output.setFilterState(false);
            output.addElement(screenData);
        }
        return output;
    }
}
