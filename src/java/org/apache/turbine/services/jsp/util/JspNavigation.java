package org.apache.turbine.services.jsp.util;

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

import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * Returns output of a Navigation module. An instance of this is placed in the
 * request by the JspLayout. This allows template authors to
 * set the navigation template they'd like to place in their templates.<br>
 * Here's how it's used in a JSP template:<br>
 * <code>
 * <%useBean id="navigation" class="JspNavigation" scope="request"/%>
 * ...
 * <%= navigation.setTemplate("admin_navigation.jsp") %>
 * </code>
 * @author <a href="john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class JspNavigation
{
    /** Logging */
    private static Log log = LogFactory.getLog(JspNavigation.class);

    /* The RunData object */
    private RunData data;

    /**
     * Constructor
     *
     * @param data
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
            module = TurbineTemplate.getNavigationName(template);
            NavigationLoader.getInstance().exec(data, module);
        }
        catch (Exception e)
        {
            String message = "Error processing navigation template:" +
                    template + " using module: " + module;
            log.error(message, e);
            try
            {
                data.getOut().print("Error processing navigation template: "
                        + template + " using module: " + module);
            }
            catch (java.io.IOException ioe)
            {
            }
        }
    }
}
