package org.apache.turbine.util.template;

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

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.modules.NavigationLoader;

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.template.TurbineTemplate;

import org.apache.turbine.util.RunData;

/**
 * Returns output of a Navigation module.  An instance of this is
 * placed in the WebMacro context by the WebMacroSiteLayout.  This
 * allows template authors to set the navigation template they'd like
 * to place in their templates.  Here's how it's used in a
 * template:
 *
 * <p><code>
 * $navigation.setTemplate("admin_navigation.wm")
 * </code>
 *
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class TemplateNavigation
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplateNavigation.class);

    /* The RunData object. */
    private RunData data;

    /* The name of the navigation template. */
    private String template;

    /**
     * Constructor
     *
     * @param data A Turbine RunData object.
     */
    public TemplateNavigation(RunData data)
    {
        this.data = data;
    }

    /**
     * Set the template.
     *
     * @param template A String with the name of the navigation
     * template.
     * @return A TemplateNavigation (self).
     */
    public TemplateNavigation setTemplate(String template)
    {
        this.template = template;
        return this;
    }

    /**
     * Builds the output of the navigation template.
     *
     * @return A String.
     */
    public String toString()
    {
        log.debug("toString: " + this.template);
        data.getTemplateInfo().setNavigationTemplate(this.template);
        String module = null;
        String returnValue = null;
        try
        {
            module = TurbineTemplate.getNavigationName(template);

            ConcreteElement results = NavigationLoader.getInstance()
                    .eval(data, module);
            returnValue = results.toString();
        }
        catch (Exception e)
        {
            String message = ("Error processing navigation template:"
                    + this.template + " using module: " + module);
            log.error(message, e);
            returnValue = message;
        }
        return returnValue;
    }
}
