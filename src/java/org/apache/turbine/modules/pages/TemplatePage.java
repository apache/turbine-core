package org.apache.turbine.modules.pages;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.template.TurbineTemplate;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * When building sites using templates, Screens need only be defined
 * for templates which require dynamic (database or object) data.
 *
 * <p>
 *
 * This page can be used on sites where the number of Screens can be
 * much less than the number of templates.  The templates can be
 * grouped in directories with common layouts.  Screen modules are
 * then expected to be placed in packages corresponding with the
 * templates' directories and follow a specific naming scheme.
 *
 * <p>
 *
 * The template parameter is parsed and and a Screen whose package
 * matches the templates path and shares the same name minus any
 * extension and beginning with a capital letter is searched for.  If
 * not found, a Screen in a package matching the template's path with
 * name Default is searched for.  If still not found, a Screen with
 * name Default is looked for in packages corresponding to parent
 * directories in the template's path until a match is found.
 *
 * <p>
 *
 * For example if data.getParameters().getString("template") returns
 * /about_us/directions/driving.wm, the search follows
 * about_us.directions.Driving, about_us.directions.Default,
 * about_us.Default, Default, WebMacroSiteScreen (i.e. the default
 * screen set in TurbineResources).
 *
 * <p>
 *
 * Only one Layout module is used, since it is expected that any
 * dynamic content will be placed in navigations and screens.  The
 * layout template to be used is found in a similar way to the Screen.
 * For example the following paths will be searched in the layouts
 * subdirectory: /about_us/directions/driving.wm,
 * /about_us/directions/default.wm, /about_us/default.wm, /default.wm,
 * where wm is the value of the template.default.extension property.
 *
 * <p>
 *
 * This approach allows a site with largely static content to be
 * updated and added to regularly by those with little Java
 * experience.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TemplatePage
    extends DefaultPage
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplatePage.class);

    /**
     * Works with TemplateService to set up default templates and
     * corresponding class modules.
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildAfterAction(RunData data)
        throws Exception
    {
        // The Template Service at this point must fetch the Screen class
        // to match a given template. If the Screen class has already been
        // set by an action, skip this, because the user has the already
        // specified the Screen class he wants to use.
        if (!data.hasScreen())
        {
            // This is effectively getting the "template" parameter
            // from the parameter parser in rundata. This is coming
            // from the request for a template.
            String template = data.getTemplateInfo().getScreenTemplate();
            
            // Get the layout template and the correct Screen.
            String layoutTemplate =
                    TurbineTemplate.getLayoutTemplateName(template);
            data.getTemplateInfo().setLayoutTemplate(layoutTemplate);

            String screen = TurbineTemplate.getScreenName(template);

            if (screen == null)
            {
                String errMsg = "Couldn't map Template " 
                    + template + " to any Screen class!";
                log.error(errMsg);
                throw new TurbineException(errMsg);
            }
            data.setScreen(screen);
        }
    }
}
