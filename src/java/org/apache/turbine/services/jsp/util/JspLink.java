package org.apache.turbine.services.jsp.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;

/**
 * A customized version of the DynamicURI to be used in JSP templates.
 * This is automatically inserted into the request so page authors
 * can create links in templates.
 * Here's an example of its use:<br>
 * <code>
 * <jsp:useBean id="link" class="JspLink" scope="request"/%>
 * <%= link.setPage("index.jsp").setPathInfo("key", "value") %>
 * This would return:
 *     http://foo.com/myapp/servlet/Turbine/key/value/template/index.jsp
 * </code>
 *
 * @author <a href="john.mcnally@clearink.com">John McNally</a>
 * @author Dave Bryson<a href="mbryson@mont.mindspring.com">mbryson@mont.mindspring.com</a>
 * @author Jon S. Stevens <a href="mailto:jon@latchkey.com">jon@latchkey.com</a>
 */
public class JspLink extends DynamicURI
{
    /**
     Constructor
     */
    public JspLink(RunData data)
    {
        super(data);
    }

    /**
     * Returns the URI
     * @return String the uri http://foo.com/...
     */
    public String toString()
    {
        String output = super.toString();

        // This was added to allow multilple $link variables in one
        // WebMacro template
        removePathInfo();
        removeQueryData();

        return output;
    }

    /**
     * Sets the template variable used by the WebMacroSite Service
     * @param String the template name
     * @return JspLink
     */
    public JspLink setPage(String t)
    {
        return (JspLink) addPathInfo("template", t);
    }
}
