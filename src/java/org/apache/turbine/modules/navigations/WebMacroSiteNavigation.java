package org.apache.turbine.modules.navigations;

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

// Turbine/Village/ECS Imports
import org.apache.ecs.ConcreteElement;
import org.apache.ecs.StringElement;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.webmacro.TurbineWebMacro;
import org.apache.turbine.services.webmacro.WebMacroService;

// WebMacro Stuff
import org.webmacro.servlet.WebContext;


/**
 * WebMacroSiteNavigation.  This screen relies on the WebMacroSitePage
 * being set as the default page.  This screen can be called directly
 * to generate a WebMacro Screen.  The doBuildTemplate() assumes the
 * user has put the template filename in the RunData parameter and set
 * it to the value of the template file to execute.  Specialized
 * Navigations screens should extend this class and overide the
 * doBuildTemplate( data , context) method.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated you should use velocity
 */
public class WebMacroSiteNavigation extends TemplateNavigation
{
    /**
     * WebMacro Navigations extending this class should overide this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate( RunData data,
                                    WebContext context )
        throws Exception
    {
    }

    /**
     * Needs to be implemented to make TemplateNavigation like us.
     * The actual method that you should override is the one with the
     * context in the parameter list.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate( RunData data )
        throws Exception
    {
        doBuildTemplate( data, getContext(data) );
    }

    /**
     * This Builds the WebMacro template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate( RunData data )
        throws Exception
    {
        WebContext context = getContext( data );

        String templateName = data.getTemplateInfo().getNavigationTemplate();

        StringElement output = new StringElement();
        output.setFilterState(false);
        output.addElement(
            buildWMTemplate(context,"navigations/" + templateName));
        return output;
    }

    /**
     * Return the WebContext needed by WebMacro.
     *
     *
     * @param data Turbine information.
     * @return A WebContext.
     */
    protected WebContext getContext(RunData data)
    {
        // Attempt to get it from the TemplateInfo first.  If it
        // doesn't exist, create it and then stuff it into the
        // TemplateInfo.
        WebContext wc = (WebContext)data.getTemplateInfo().getTemplateContext(
            WebMacroService.WEBMACRO_CONTEXT);
        if (wc == null)
        {
            wc = TurbineWebMacro.getContext(data);
            data.getTemplateInfo().setTemplateContext(
                WebMacroService.WEBMACRO_CONTEXT, wc);
        }
        return wc;
    }

    /**
     * Build WM template.
     *
     * @param context A WebContext.
     * @param templateFile A String, the name of the template file.
     * @return A String with the processed template.
     * @exception Exception, a generic exception.
     */
    protected String buildWMTemplate(WebContext context,
                                     String templateFile)
        throws Exception
    {
        return TurbineWebMacro.handleRequest(context, templateFile);
    }
}
