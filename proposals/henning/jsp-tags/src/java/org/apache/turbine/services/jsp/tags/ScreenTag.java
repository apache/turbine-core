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
import org.apache.turbine.modules.ScreenLoader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.template.TemplateInfo;

/**
 * Supporting class for the screen tag.
 * Sends the content between the screen's body or head tags to the 
 * output stream.  The decision to output the head or body contents 
 * is decided by the section parameter.  Example usage:
 * &lt;x:screen section="head" /&gt;
 *
 * @version $Id$
 */
public class ScreenTag extends TagSupport 
{
    /**
     * section attribute. Valid values are head or body.
     */
    private String section;
    /**
     * contextRelevant attribute. Meant to allow the Screen
     * java code to be skipped in the event the content is static.
     * Not working and have not decided whether to make it work or
     * remove the functionality.
     */
    private boolean staticContent;    
    /**
     * Key used to prevent the templates Screen module from executing
     * multiple times while evaluating the layout.
     */
    private static final String FLAG = "_screen_executed_";

    /**
     * Key used to access the section parameter in the request.
     */
    static final String SECTION_KEY = "_layout_section_";
    /**
     * One of the valid values for the section parameter
     */
    static final String BODY = "body";
    /**
     * One of the valid values for the section parameter
     */
    static final String HEAD = "head";
    
    /**
     * setter for the section parameter
     */
    public void setSection(String section) 
    {
        pageContext.setAttribute(SECTION_KEY, section, 
            PageContext.REQUEST_SCOPE);
        this.section = section;
    }
    
    /**
     * setter for the staticContent parameter
     */
    public void setStaticContent(boolean staticContent) 
    {
        this.staticContent = staticContent;
    }

    /**
     * Method called when the tag is encountered to send attributes to the
     * output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException 
    {
        RunData data = (RunData)pageContext
                .getAttribute(JspService.RUNDATA, PageContext.REQUEST_SCOPE);    
        
        String module = null;
        String template = null;
        Boolean flag = (Boolean)pageContext
                .getAttribute(FLAG, PageContext.REQUEST_SCOPE);
        JspService jsp = (JspService) TurbineServices.getInstance()
            .getService(JspService.SERVICE_NAME);
        try
        {  
            pageContext.getOut().flush();
            template = data.getTemplateInfo().getScreenTemplate();
            
            if ( staticContent || (flag != null && flag.booleanValue()) ) 
            {
                jsp.handleRequest(data, "/screens/" + template, false);
            }
            else
            {
                module = ((TemplateService)TurbineServices.getInstance().getService(
                TemplateService.SERVICE_NAME)).getScreenName(template);
                ScreenLoader.getInstance().exec(data, module);
                pageContext.setAttribute(FLAG, 
                    Boolean.TRUE, PageContext.REQUEST_SCOPE);
            }
        }
        catch (Exception e)
        {
            String message = "Error processing screen template:" +
                template + " using module: " + module;
            Log.error(message, e);
            try
            {
                data.getOut().print("Error processing screen template: " 
                    + template + " using module: " + module);
            }
            catch(java.io.IOException ioe) {}    
        }
        return SKIP_BODY;
    }
}
