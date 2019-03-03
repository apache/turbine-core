package org.apache.turbine.util.template;


import org.apache.logging.log4j.LogManager;

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


import org.apache.logging.log4j.Logger;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TemplateService;
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
    private static final Logger log = LogManager.getLogger(TemplateNavigation.class);

    /* The RunData object. */
    private RunData data;

    /* The name of the navigation template. */
    private String template = null;

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
        log.debug("setTemplate({})", template);
        this.template = template;
        return this;
    }

    /**
     * Builds the output of the navigation template.
     *
     * @return A String.
     */
    @Override
    public String toString()
    {
        String module = null;
        String returnValue = null;

        try
        {
            if (template == null)
            {
                returnValue = "Navigation Template is null (Might be unset)";
                throw new Exception(returnValue);
            }

            data.getTemplateInfo().setNavigationTemplate(template);
            TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
            module = templateService.getNavigationName(template);

            if (module == null)
            {
                returnValue = "Template Service returned null for Navigation Template " + template;
                throw new Exception(returnValue);
            }

            returnValue = NavigationLoader.getInstance().eval(data, module);
        }
        catch (Exception e)
        {
            if (returnValue == null)
            {
                returnValue = "Error processing navigation template: "
                        + template + ", using module: " + module;
            }
            log.error(returnValue, e);
        }

        return returnValue;
    }
}
