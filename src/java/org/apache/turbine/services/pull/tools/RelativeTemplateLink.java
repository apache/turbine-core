package org.apache.turbine.services.pull.tools;

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

import org.apache.turbine.services.pull.ApplicationTool;

/**
 * This is exactly the same thing as TemplateLink but it returns a 
 * relative link on toString(). Everything else is identical. This class is
 * here for legacy purposes if you used the old org.apache.turbine.util.template.RelativeLink
 * class and have lots of templates which you don't want to rewrite. 
 *
 * <p>
 * For new Code please use TemplateLink and get a relative Link with $link.RelativeLink and
 * the URI without resetting the query_data and path_info with $link.RelativeURI
 *
 * <p>
 *
 * This is an application pull tool for the template system. You should <b>not</b>
 * use it in a normal application!
 *
 * @deprecated Use {@link org.apache.turbine.services.pull.tools.TemplateLink} with the
 * {@link org.apache.turbine.services.pull.tools.TemplateLink#getRelativeLink} method.
 *
 * @version $Id$
 */
public class RelativeTemplateLink
    extends TemplateLink
    implements ApplicationTool

{
    /**
     * Default constructor
     * <p>
     * The init method must be called before use.
     */
    public RelativeTemplateLink()
    {
        super();
    }
    
    /**
     * Returns the URI. After rendering the URI, it clears the 
     * pathInfo and QueryString portions of the TemplateURI. Equivalent
     * to the getRelativeLink() method of this class.
     *
     * @return A String with the URI in the form
     * /Turbine/template/index.wm/hello/world
     */
    public String toString()
    {
        return getRelativeLink();
    }
}
