package org.apache.turbine.services.jsp.util;


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
 * Returns output of a Navigation module. An instance of this is placed in the
 * request by the JspLayout. This allows template authors to
 * set the navigation template they'd like to place in their templates.<br>
 * Here's how it's used in a JSP template:<br>
 * <code>
 * &lt;%useBean id="navigation" class="JspNavigation" scope="request" %&gt;
 * ...
 * &lt;%= navigation.setTemplate("admin_navigation.jsp") %&gt;
 * </code>
 * @author <a href="john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class JspNavigation
{
    /** Logging */
    private static Logger log = LogManager.getLogger(JspNavigation.class);

    /* The RunData object */
    private final RunData data;

    /**
     * Constructor
     *
     * @param data Turbine request data
     */
    public JspNavigation(RunData data)
    {
        this.data = data;
    }

    /**
     * builds the output of the navigation template
     * @param template the name of the navigation template
     */
    public void setTemplate(String template)
    {
        data.getTemplateInfo().setNavigationTemplate(template);
        String module = null;
        try
        {
            TemplateService templateService = (TemplateService)TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
            module = templateService.getNavigationName(template);
            NavigationLoader.getInstance().exec(data, module);
        }
        catch (Exception e)
        {
            String message = "Error processing navigation template:" +
                    template + " using module: " + module;
            log.error(message, e);
            try
            {
                data.getResponse().getWriter().print(message);
            }
            catch (java.io.IOException ioe)
            {
                // ignore
            }
        }
    }
}
