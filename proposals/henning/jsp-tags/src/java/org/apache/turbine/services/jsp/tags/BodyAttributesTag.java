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

/**
 * Supporting class for the bodyAttributes tag.
 * Sends the contents of the a screen's body tag's attributes
 * parameter to the output stream.  If the screen did not set
 * the attributes parameter, a default may be used if specified
 * in this tag.  Example usage:
 * &lt;body &lt;x:bodyAttributes default='onLoad="jsfunc()"' /&gt;&gt;
 *
 * @version $Id$
 */
public class BodyAttributesTag extends TagSupport 
{
    /** 
     * The default body tag attributes, if none is specified in the screen. 
     */
    private String def;
    
    /** 
     * The setter for the default body tag attributes. 
     */
    public void setDefault(String def)
    {
        this.def = def;
    }
    
    /**
     * Method called when the tag is encountered to send attributes to the
     * output stream
     *
     * @return SKIP_BODY, as it is intended to be a single tag.
     */
    public int doStartTag() throws JspException 
    {
        String bodyAttributes = (String)pageContext
            .getAttribute(BodyTag.ATTRIBUTES, PageContext.REQUEST_SCOPE);
        try 
        {
            if (bodyAttributes != null)
            {
                pageContext.getOut().print(bodyAttributes);
            }
            else
            {
                pageContext.getOut().print(def);
            }
        }
        catch(java.io.IOException ex) 
        {
            throw new JspException(ex.getMessage());
        }
        return SKIP_BODY;
    }
}
