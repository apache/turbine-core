package org.apache.turbine.modules.layouts;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Layout;
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateNavigation;

import org.apache.velocity.context.Context;

/**
 * This Layout module allows Velocity templates to be used as layouts.
 * Since dynamic content is supposed to be primarily located in
 * screens and navigations there should be relatively few reasons to
 * subclass this Layout.
 *
 * This class uses ECS to render the layout.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated you should use VelocityOnlyLayout
 */
public class VelocityECSLayout
    extends Layout
{
    /** Logging */
    private static Log log = LogFactory.getLog(VelocityECSLayout.class);

    /** The prefix for lookup up layout pages */
    private String prefix = TurbineConstants.LAYOUT_PREFIX + "/";

    /** Warn only once */
    private static boolean hasWarned = false;

    /**
     * C'tor warns once about deprecation.
     */
    public VelocityECSLayout()
    {
        if (!hasWarned)
        {
            log.warn("The VelocityECSLayout is deprecated. "
                     + "Please switch to VelocityOnlyLayout.");
            hasWarned = true;
        }
    }

    /**
     * Build the layout.  Also sets the ContentType and Locale headers
     * of the HttpServletResponse object.
     *
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    public void doBuild(RunData data)
        throws Exception
    {
        // Get the context needed by Velocity.
        Context context = TurbineVelocity.getContext(data);

        String screenName = data.getScreen();

        log.debug("Loading Screen " + screenName);

        // First, generate the screen and put it in the context so
        // we can grab it the layout template.
        ConcreteElement results =
            ScreenLoader.getInstance().eval(data, screenName);

        String returnValue = (results == null) ? "" : results.toString();

        // variable for the screen in the layout template
        context.put(TurbineConstants.SCREEN_PLACEHOLDER, returnValue);

        // variable to reference the navigation screen in the layout template
        context.put(TurbineConstants.NAVIGATION_PLACEHOLDER, 
                    new TemplateNavigation(data));

        // Grab the layout template set in the VelocityPage.
        // If null, then use the default layout template
        // (done by the TemplateInfo object)
        String templateName = data.getTemplateInfo().getLayoutTemplate();

        log.debug("Now trying to render layout " + templateName);

        // Finally, generate the layout template and add it to the body of the
        // Document in the RunData.
        data.getPage().getBody().addElement(TurbineVelocity
                .handleRequest(context, prefix +  templateName));
    }
}
