package org.apache.turbine.util;

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

import java.lang.reflect.Method;

import org.apache.turbine.services.pull.ApplicationTool;

/**
 * Utility class to allow the easy inclusion of
 * images in templates: &lt;img src="$content.getURI("image.jpg")">
 *
 * @author <a href="mailto:criley@ekmail.com">Cameron Riley</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 * @deprecated Use {@link org.apache.turbine.services.pull.tools.ContentTool} for tool usage
 * and {@link org.apache.turbine.util.uri.DataURI} for code usage instead.
 */
public class ContentURI
        extends DynamicURI
        implements ApplicationTool
{
    /** stores the context path for servlet 2.1+ compliant containers */
    private String contextPath;

    /**
     * Constructor
     *
     * @param data a RunData instance
     */
    public ContentURI(RunData data)
    {
        super(data);
        init(data);
    }

    /**
     * Default constructor
     */
    public ContentURI()
    {
    }

    /**
     * Initialize this object using the data given (ApplicationTool
     * method).
     *
     * @param data assumed to be a RunData instance
     */
    public void init(Object data)
    {
        // we blithely cast to RunData as the runtime error thrown
        // if data is null or another type is appropriate.
        init((RunData) data);
    }

    /**
     * Refresh method - does nothing
     */
    public void refresh()
    {
        // empty
    }

    /**
     * Initialize this object using the given RunData object
     *
     * @param data a RunData instance
     */
    public void init(RunData data)
    {
        super.init(data);
        try
        {
            Class runDataClass = RunData.class;
            Method meth = runDataClass.getDeclaredMethod("getContextPath", null);
            contextPath = (String) meth.invoke(data, null);
        }
        catch (Exception e)
        {
            /*
             * Ignore a NoSuchMethodException because it means we are
             * using Servlet API 2.0.  Make sure scriptName is not
             * null.
             */
            contextPath = "";
        }
    }

    /**
     * Returns a URI pointing to the given content (where content is a
     * path relative to the webapp root.
     *
     * @param pathToContent a path relative to the webapp root
     */
    public String getURI(String pathToContent)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getServerScheme()); //http
        sb.append("://");
        sb.append(getServerName()); //www.foo.com
        sb.append(":");
        sb.append(getServerPort()); //port webserver running on (8080 for TDK)
        //the context for tomcat adds a / so no need to add another
        sb.append(contextPath); //the tomcat context
        sb.append("/");
        sb.append(pathToContent);
        return (sb.toString());
    }
}
