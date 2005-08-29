package org.apache.turbine.modules.layouts;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
