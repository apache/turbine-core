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
 * @version $Id$
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
