package org.apache.turbine.modules.screens;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.lang.StringUtils;

import org.apache.ecs.ConcreteElement;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.jsp.TurbineJsp;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * Base JSP Screen that should be subclassed by screens that want to
 * use JSP.  Subclasses should override the doBuildTemplate() method.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author Frank Y. Kim
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class BaseJspScreen
        extends TemplateScreen
{
    /** The prefix for lookup up screen pages */
    private String prefix = TurbineConstants.SCREEN_PREFIX + "/";

    /**
     * Method that sets up beans and forward the request to the JSP.
     *
     * @param data Turbine information.
     * @return null - the JSP sends the information.
     * @exception Exception, a generic exception.
     */
    public ConcreteElement buildTemplate(RunData data)
            throws Exception
    {
        String screenTemplate = data.getTemplateInfo().getScreenTemplate();
        // get the name of the JSP we want to use
        String templateName
            = TurbineTemplate.getScreenTemplateName(screenTemplate);

        // The Template Service could not find the Screen
        if (StringUtils.isEmpty(templateName))
        {
            log.error("Screen " + screenTemplate + " not found!");
            throw new Exception("Could not find screen for " + screenTemplate);
        }

        // let service know whether we are using a layout
        TurbineJsp.handleRequest(data, prefix + templateName,
                                 getLayout(data) == null);

        return null;
    }

    /**
     * Method to be overidden by subclasses to include data in beans, etc.
     *
     * @param data, the Rundata object
     * @exception Exception, a generic exception.
     */
    protected void doBuildTemplate(RunData data)
        throws Exception
    {
    }
}
