package org.apache.turbine.services.jsp.tags;


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


import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.turbine.modules.NavigationLoader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * Supporting class for the bodyAttributes tag.
 * Sends the contents of the a screen's body tag's attributes
 * parameter to the output stream.  If the screen did not set
 * the attributes parameter, a default may be used if specified
 * in this tag.  Example usage:
 * &lt;body &lt;x:bodyAttributes default='onLoad="jsfunc()"' /&gt;&gt;
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 */
public class NavigationTag extends TagSupport 
{
    /**
     * template parameter defines the template whose contents will replace
     * this tag in the layout.
     */
    private String template;

    /** 
     * The setter for template parameter
     */
    public void setTemplate(String template) 
    {
        this.template = template;
    }

    /**
     * Method called when the tag is encountered to send the navigation
     * template's contents to the output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext
                .getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        data.getTemplateInfo().setNavigationTemplate(template);
        String module = null;
        try
        {  
            pageContext.getOut().flush();
            module = ((TemplateService)TurbineServices.getInstance().getService(
            TemplateService.SERVICE_NAME)).getNavigationName(template);
            NavigationLoader.getInstance().exec(data, module);
        }
        catch (Exception e)
        {
            String message = "Error processing navigation template:" +
                template + " using module: " + module;
            Log.error(message, e);
            try
            {
                data.getOut().print("Error processing navigation template: " 
                    + template + " using module: " + module);
            }
            catch(java.io.IOException ioe) {}    
        }
        return SKIP_BODY;
    }
}
