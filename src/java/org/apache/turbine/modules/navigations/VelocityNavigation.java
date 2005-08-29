package org.apache.turbine.modules.navigations;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
import org.apache.ecs.StringElement;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * VelocityNavigation.  This screen relies on the VelocityPage
 * being set as the default page.  The doBuildTemplate() assumes the
 * user has put the template filename in the RunData parameter and set
 * it to the value of the template file to execute.  Specialized
 * Navigations screens should extend this class and overide the
 * doBuildTemplate( data , context) method.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class VelocityNavigation
        extends TemplateNavigation
{
    /** Logging */
    private static Log log = LogFactory.getLog(VelocityNavigation.class);

    /** The prefix for lookup up navigation pages */
    private String prefix = TurbineConstants.NAVIGATION_PREFIX + "/";

    /**
     * Velocity Navigations extending this class should overide this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(RunData data,
                                   Context context)
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
    protected void doBuildTemplate(RunData data)
            throws Exception
    {
        doBuildTemplate(data, TurbineVelocity.getContext(data));
    }

    /**
     * This Builds the Velocity template.
     *
     * @param data Turbine information.
     * @return A ConcreteElement.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data)
            throws Exception
    {
        Context context = TurbineVelocity.getContext(data);

        String navigationTemplate = data.getTemplateInfo().getNavigationTemplate();
        String templateName
                = TurbineTemplate.getNavigationTemplateName(navigationTemplate);

        StringElement output = new StringElement();
        output.setFilterState(false);
        output.addElement(TurbineVelocity
                .handleRequest(context, prefix + templateName));
        return output;
    }

}
