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

import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.uri.URIConstants;

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
 * @author <a href="mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author Jon S. Stevens <a href="mailto:jon@latchkey.com">jon@latchkey.com</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * deprecated Use {@org.apache.turbine.services.pull.tools.TemplateLink} instead.
 */
public class JspLink
    extends DynamicURI
{
    /**
     * Constructor
     *
     * @param data A Rundata Object
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
        // JSP template
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
        return (JspLink) addPathInfo(URIConstants.CGI_TEMPLATE_PARAM, t);
    }
}
