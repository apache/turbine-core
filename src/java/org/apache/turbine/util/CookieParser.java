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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CookieParser is an interface to a utility to to get and set values
 * of Cookies on the Client Browser. You can use CookieParser to convert
 * Cookie values to various types or to set Bean values with setParameters().
 * Servlet Spec for more information on Cookies.
 * <p>
 * Use set() or unset() to Create or Destroy Cookies.
 * <p>
 * NOTE: The name= portion of a name=value pair may be converted
 * to lowercase or uppercase when the object is initialized and when
 * new data is added.  This behaviour is determined by the url.case.folding
 * property in TurbineResources.properties.  Adding a name/value pair may
 * overwrite existing name=value pairs if the names match:
 *
 * <pre>
 * CookieParser cp = data.getCookies();
 * cp.add("ERROR",1);
 * cp.add("eRrOr",2);
 * int result = cp.getInt("ERROR");
 * </pre>
 *
 * In the above example, result is 2.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @version $Id$
 */
public interface CookieParser
        extends ValueParser
{
    static final int AGE_SESSION = -1;
    static final int AGE_DELETE = 0;

    /**
     * Gets the parsed RunData.
     *
     * @return the parsed RunData object or null.
     * @deprecated. Don't use the Run Data object. use getRequest().
     */
    RunData getRunData();

    /**
     * Gets the Request Object for this parser.
     *
     * @return the HttpServletRequest or null.
     */
    HttpServletRequest getRequest();

    /**
     * Sets the RunData to be parsed.
     * All previous cookies will be cleared.
     *
     * @param data the RunData object.
     * @deprecated. Use setData (HttpServletRequest request, HttpServletResponse response) instead
     */
    void setRunData(RunData data);

    /**
     * Sets Request and Response to be parsed.
     *
     * All previous cookies will be cleared.
     *
     * @param request The http request from the servlet
     * @param response The http reponse from the servlet
     */
    void setData(HttpServletRequest request,
                 HttpServletResponse response);

    /**
     * Get the Path where cookies will be stored
     */
    DynamicURI getCookiePath();

    /**
     * Set the path for cookie storage
     */
    void setCookiePath(DynamicURI path);

    /**
     * Set a cookie that will be stored on the client for
     * the duration of the session.
     */
    void set(String name, String value);

    /**
     * Set a persisten cookie on the client that will expire
     * after a maximum age (given in seconds).
     */
    void set(String name, String value, int seconds_age);

    /**
     * Remove a previously set cookie from the client machine.
     */
    void unset(String name);
}
