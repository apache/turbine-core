package org.apache.turbine.util.template;

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
import org.apache.turbine.util.RelativeDynamicURI;
import org.apache.turbine.util.RunData;

/**
 * A customized version of the RelativeDynamicURI to be used in Templates.
 * Here's an example of its Velocity/WebMacro use:
 *
 * <p><code>
 * $link.setPage("index.wm").addPathInfo("hello","world")
 * </code><br />This would return: <code>/myapp/servlet/myapp/template/index.wm/hello/world
 * </code>
 *
 * @author <a href="jmcnally@collab.net">John D. McNally</a>
 * @author see the authors of TemplateLink
 * @version $Id$
 * @deprecated Use {@link org.apache.turbine.services.pull.tools.RelativeTemplateLink} instead.
 */
public class RelativeTemplateLink
        extends RelativeDynamicURI
        implements ApplicationTool
{
    /** the pathinfo key stored in the DynamicURI */
    private static final String TEMPLATE_KEY = "template";

    /** cache of the template name for getPage() */
    private String template = null;

    /**
     * Default constructor.
     * <p>
     * The init method must be called before use.
     */
    public RelativeTemplateLink()
    {
    }

    /**
     * Constructor.
     *
     * @param data a Turbine RunData object.
     */
    public RelativeTemplateLink(RunData data)
    {
        super(data);
    }

    /**
     * This will initialise a TemplateLink object that was
     * constructed with the default constructor (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData object
     */
    public void init(Object data)
    {
        // we just blithely cast to RunData as if another object
        // or null is passed in we'll throw an appropriate runtime
        // exception.
        super.init((RunData) data);
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * This will turn off the execution of res.encodeURL()
     * by making res == null. This is a hack for cases
     * where you don't want to see the session information
     *
     * @return instance of RelativeTemplateLink (this)
     */
    public RelativeTemplateLink setEncodeURLOff()
    {
        this.res = null;
        return this;
    }

    /**
     * Sets the template variable used by the Template Service.
     *
     * @param template A String with the template name.
     * @return instance of RelativeTemplateLink (this)
     */
    public RelativeTemplateLink setPage(String template)
    {
        this.template = template;
        addPathInfo(TEMPLATE_KEY, template);
        return this;
    }

    /**
     * Gets the template variable used by the Template Service.
     * It is only available after setPage() has been called.
     *
     * @return The template name.
     */
    public String getPage()
    {
        return template;
    }

    /**
     * Returns the URI. After rendering the URI, it clears the
     * pathInfo and QueryString portions of the DynamicURI.
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String toString()
    {
        String output = super.toString();

        // This was added to allow multilple $link variables in one
        // template.
        removePathInfo();
        removeQueryData();

        return output;
    }

    /**
     * Returns the URI leaving the source intact. Wraps directly to the
     * <code>DynamicURI.toString</code> method of the superclass
     * (avoiding the local toString implementation).
     *
     * @return A String with the URI in the form
     * http://foo.com/Turbine/template/index.wm/hello/world
     */
    public String getURI()
    {
        return super.toString();
    }
}
