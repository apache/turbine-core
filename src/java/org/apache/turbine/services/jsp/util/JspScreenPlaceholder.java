package org.apache.turbine.services.jsp.util;

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

import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.util.RunData;

/**
 * Returns output of a Screen module. An instance of this is placed in the
 * request by the JspLayout. This allows template authors to
 * place the screen template within the layout.<br>
 * Here's how it's used in a JSP template:<br>
 * <code>
 * <%useBean id="screen_placeholder" class="JspScreenPlaceholder" scope="request"/%>
 * ...
 * <%= screen_placeholder %>
 *</code>
 *
 * @version $Id$
 */
public class JspScreenPlaceholder
{
    /** Logging */
    private static Log log = LogFactory.getLog(JspNavigation.class);

    /* The RunData object */
    private RunData data;

    /**
     * Constructor
     *
     * @param data A Rundata Object
     */
    public JspScreenPlaceholder(RunData data)
    {
        this.data = data;
    }

    /**
     * builds the output of the navigation template
     */
    public void exec()
    {
        String template = null;
        String module = null;
        try
        {
            template = data.getTemplateInfo().getScreenTemplate();
            module = TurbineTemplate.getScreenName(template);
            ScreenLoader.getInstance().exec(data, module);
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
