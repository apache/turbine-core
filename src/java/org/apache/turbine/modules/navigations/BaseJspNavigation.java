package org.apache.turbine.modules.navigations;

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

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.TurbineConstants;

import org.apache.turbine.services.jsp.TurbineJsp;

import org.apache.turbine.util.RunData;

/**
 * Base JSP navigation that should be subclassed by Navigation that want to
 * use JSP.  Subclasses should override the doBuildTemplate() method.
 *
 * @version $Id$
 */
public class BaseJspNavigation
        extends TemplateNavigation
{
    /** The prefix for lookup up navigation pages */
    private String prefix = TurbineConstants.NAVIGATION_PREFIX + "/";

    /**
     * Method to be overidden by subclasses to include data in beans, etc.
     *
     * @param data the Rundata object
     * @throws Exception a generic exception.
     */
    protected void doBuildTemplate(RunData data)
        throws Exception
    {
    }

    /**
     * Method that sets up beans and forward the request to the JSP.
     *
     * @param data the Rundata object
     * @return null - the JSP sends the information
     * @throws Exception a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data)
        throws Exception
    {
        // get the name of the JSP we want to use
        String templateName = data.getTemplateInfo().getNavigationTemplate();

        // navigations are used by a layout
        TurbineJsp.handleRequest(data, prefix + templateName);
        return null;
    }
}
