package org.apache.turbine.modules.layouts;

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

import org.apache.turbine.modules.Layout;
import org.apache.turbine.util.RunData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.jsp.JspService;
import org.apache.turbine.services.jsp.util.JspNavigation;
import org.apache.turbine.services.jsp.util.JspScreenPlaceholder;


/**
 * This Layout module allows JSP templates to be used as layouts. Since
 * dynamic content is supposed to be primarily located in screens and
 * navigations there should be relatively few reasons to subclass this Layout.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 */
public class JspLayout extends Layout
{
    /**
     * Method called by LayoutLoader.
     *
     * @param data RunData
     * @throws Exception generic exception
     */
    public void doBuild(RunData data) throws Exception
    {
        data.getResponse().setContentType("text/html");
        data.declareDirectResponse();
        // variable to reference the screen in the layout template
        data.getRequest().setAttribute("screen_placeholder",
                new JspScreenPlaceholder(data));
        // variable to reference the navigations in the layout template
        data.getRequest().setAttribute("navigation", new JspNavigation(data));

        // Grab the layout template set in the TemplatePage.
        String templateName = data.getTemplateInfo().getLayoutTemplate();
        // Finally, generate the layout template and output to the response
        JspService jsp = (JspService) TurbineServices.getInstance()
            .getService(JspService.SERVICE_NAME);
        jsp.handleRequest(data, "/layouts" + templateName, true);
    }
}
