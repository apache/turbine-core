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
 * Supporting class for the body tag.
 * Tags that surround the screen content that will replace the 
 * &lt;x:screen section="body" /&gt; tag in a layout template.
 * An optional attributes parameter can be used to set the value
 * of the &lt;x:bodyAttributes /&gt;, which can also be in the
 * layout.
 * Example usage:
 * &lt;x:body attributes='onLoad="jsfunc()"'&gt;
 * some html content
 * &lt;/x:body&gt;
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 */
public class BodyTag extends TagSupport 
{
    static final String ATTRIBUTES = "_body_tag_attributes_";
    
    /**
     * attributes parameter can be used to add screen specific attribute values
     * to the &lt;body&gt; tag.
     */
    private String attributes;

    /** 
     * The setter for body tag attributes relevant to the screen containing
     * this tag. 
     */
    public void setAttributes(String attributes) 
    {
        pageContext.setAttribute(ATTRIBUTES, attributes, 
            PageContext.REQUEST_SCOPE);
        this.attributes = attributes;
    }
    
    /**
     * Method called when the tag is encountered.  If the layout is in
     * the body section, the contents between the body tags are executed.
     * Otherwise they are skipped.
     *
     * @return EVAL_BODY_INCLUDE, if the section has been set to body
     * or SKIP_BODY, if this section is not active.
     */
    public int doStartTag() throws JspException 
    {
        boolean isActive = ScreenTag.BODY.equals(
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
