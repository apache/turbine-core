package org.apache.turbine.modules.layouts;

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
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.webmacro.TurbineWebMacro;
import org.apache.turbine.services.webmacro.WebMacroService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateNavigation;

// WebMacro Stuff
import org.webmacro.servlet.WebContext;

/**
 * This Layout module allows WebMacro templates to be used as layouts.
 * Since dynamic content is supposed to be primarily located in
 * screens and navigations there should be relatively few reasons to
 * subclass this Layout.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 * @deprecated you should use velocity
 */
public class WebMacroSiteLayout extends Layout
{
    /**
     * Build the layout.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    public void doBuild( RunData data )
        throws Exception
    {
        /* Get the context needed by WebMacro. */
        WebContext context = getContext( data );
        /* Screen results. */
        String returnValue = "";

        /*
         * First, generate the screen and put it in the context so we
         * can grab it the layout template.
         */
        ConcreteElement results = ScreenLoader.getInstance()
            .eval(data, data.getScreen());
        if (results != null)
            returnValue = results.toString();

        /* Variable for the screen in the layout template. */
        context.put("screen_placeholder", returnValue);

        /*
         * Variable to reference the navigation screen in the layout
         * template.
         */
        context.put("navigation", new TemplateNavigation( data ));

        /*
         * Grab the layout template set in the WebMacroSitePage.  If
         * null, then use the default layout template ( done by the
         * TemplateInfo object ).
         */
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        /*
         * Finally, generate the layout template and add it to the
         * body of the Document in the RunData.
         */
        data.getPage().getBody()
            .addElement(TurbineWebMacro.handleRequest(context, "layouts"
            + templateName));
    }

    /**
     * Return the WebContext needed by WebMacro.
     *
     * @param RunData data
     * @return WebContext
     */
    protected WebContext getContext(RunData data)
    {
        // attempt to get it from the session first.
        // if it doesn't exist, create it and then stuff it into the session
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

}
