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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Supporting class for the head tag.
 * Tags that surround the screen content that will replace the 
 * &lt;x:screen section="head" /&gt; tag in a layout template.
 * Example usage:
 * &lt;x:head&gt;&lt;title&gt;Login&lt;/title&gt;&lt;/x:head&gt;
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 */
public class HeadTag extends TagSupport 
{
    /**
     * Method called when the start tag is encountered.  If the layout is 
     * in the head section, the contents between the head tags are executed.
     * Otherwise they are skipped.
     *
     * @return EVAL_BODY_INCLUDE, if the section has been set to body
     * or SKIP_BODY, if this section is not active.
     */
	public int doStartTag() throws JspException 
    {
        boolean isActive = ScreenTag.HEAD.equals(
            pageContext.getAttribute(ScreenTag.SECTION_KEY, 
                PageContext.REQUEST_SCOPE) );
        if (isActive)
        {
		     return EVAL_BODY_INCLUDE;
        }
        return SKIP_BODY;
	}
    
    /**
     * Method called when the end tag is encountered; it does nothing.  
     *
     * @return EVAL_PAGE
     */
	public int doEndTag() throws JspException 
    {
		return EVAL_PAGE;
	}
}
