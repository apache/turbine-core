package org.apache.turbine.services.jsp.tags;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
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
