package org.apache.turbine.util.parser;

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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.CookieParser;
import org.apache.turbine.util.DynamicURI;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.pool.Recyclable;

/**
 * CookieParser is used to get and set values of Cookies on the Client
 * Browser.  You can use CookieParser to convert Cookie values to
 * various types or to set Bean values with setParameters(). See the
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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DefaultCookieParser
    extends BaseValueParser
    implements CookieParser, Recyclable
{
    /** Logging */
    private static Log log = LogFactory.getLog(DefaultCookieParser.class);

    /** Internal Run Data object containing the parameters to parse */
    private RunData data = null;

    /** Just like Fulcrum, we actually use the Request and response objects */
    private HttpServletRequest request;

    /** Just like Fulcrum, we actually use the Request and response objects */
    private HttpServletResponse response;

    /** The cookie path. */
    private DynamicURI cookiePath = null;

    /**
     * Constructs a new CookieParser.
     */
    public DefaultCookieParser()
    {
        super();
    }

    /**
     * Disposes the parser.
     */
    public void dispose()
    {
        this.data = null;
        this.cookiePath = null;
        this.request = null;
        this.response = null;
        super.dispose();
    }

    /**
     * Gets the parsed RunData.
     *
     * @return the parsed RunData object or null.
     * @deprecated. Don't use the Run Data object. use getRequest().
     */

    public RunData getRunData()
    {
        return data;
    }

    /**
     * Gets the Request Object for this parser.
     *
     * @return the HttpServletRequest or null.
     */
    public HttpServletRequest getRequest()
    {
        return request;
    }

    /**
     * Sets the RunData to be parsed. This is a convenience method to
     * set the request and response from the RunData object. It is
     * equivalent to
     *
     * <pre>
     *  setData(data.getRequest(), data.getResponse());
     * </pre>
     *
     * All previous cookies will be cleared.
     *
     * @param data the RunData object.
     */

    public void setRunData(RunData data)
    {
        setData(data.getRequest(), data.getResponse());
        this.data = data;
    }

    /**
     * Sets Request and Response to be parsed.
     *
     * All previous cookies will be cleared.
     *
     * @param request The http request from the servlet
     * @param response The http reponse from the servlet
     */
    public void setData (HttpServletRequest request,
                         HttpServletResponse response)
    {
        clear();

        String enc = request.getCharacterEncoding();
        setCharacterEncoding(enc != null ? enc : "US-ASCII");

        cookiePath = new DynamicURI(data);

        Cookie[] cookies = request.getCookies();

        int cookiesCount = (cookies != null) ? cookies.length : 0;

        log.debug ("Number of Cookies: " + cookiesCount);

        for (int i = 0; i < cookiesCount; i++)
        {
            String name = convert (cookies[i].getName());
            String value = cookies[i].getValue();
            log.debug("Adding " + name + "=" + value);
            add(name, value);
        }

        this.request = request;
        this.response = response;
    }

    /**
     * Get the Path where cookies will be stored
     */
    public DynamicURI getCookiePath()
    {
        return cookiePath;
    }

    /**
     * Set the path for cookie storage
     */
    public void setCookiePath(DynamicURI path)
    {
        cookiePath = path;
    }

    /**
     * Set a cookie that will be stored on the client for
     * the duration of the session.
     */
    public void set(String name, String value)
    {
        set(name, value, AGE_SESSION);
    }

    /**
     * Set a persisten cookie on the client that will expire
     * after a maximum age (given in seconds).
     */
    public void set(String name, String value, int seconds_age)
    {
        if (response == null)
        {
            throw new IllegalStateException("Servlet response not available");
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(seconds_age);
        cookie.setPath(cookiePath.getScriptName());
        response.addCookie (cookie);
    }

    /**
     * Remove a previously set cookie from the client machine.
     */
    public void unset(String name)
    {
        set(name, " ", AGE_DELETE);
    }
}
