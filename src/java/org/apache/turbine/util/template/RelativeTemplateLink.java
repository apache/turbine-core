package org.apache.turbine.util.template;

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

import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RelativeDynamicURI;
import org.apache.turbine.util.RunData;

/**
 * A customized version of the RelativeDynamicURI to be used in Templates.
 * Here's an example of its Velocity/WebMacro use:
 *
 * <p><code>
 * $link.setPage("index.wm").addPathInfo("hello","world")
 * This would return: /myapp/servlet/myapp/template/index.wm/hello/world
 * </code>
 *
 * @author <a href="jmcnally@collab.net">John D. McNally</a>
 * @author see the authors of TemplateLink
 * @version $Id$
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
     * Default constructor
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
