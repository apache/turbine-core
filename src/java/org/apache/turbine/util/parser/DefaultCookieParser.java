package org.apache.turbine.util.parser;

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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.pool.Recyclable;
import org.apache.turbine.util.uri.DataURI;
import org.apache.turbine.util.uri.URI;

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
    private URI cookiePath = null;

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
     * @deprecated Don't use the Run Data object. use getRequest().
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
        this.data = data;
        setData(data.getRequest(), data.getResponse());
    }

    /**
     * Sets Request and Response to be parsed.
     * <p>
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

        cookiePath = new DataURI(data);

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
     *
     * @return path for cookie storage
     */
    public URI getCookiePath()
    {
        return cookiePath;
    }

    /**
     * Set the path for cookie storage
     *
     * @param cookiePath path for cookie storage
     */
    public void setCookiePath(URI cookiePath)
    {
        this.cookiePath = cookiePath;
    }

    /**
     * Set a cookie that will be stored on the client for
     * the duration of the session.
     *
     * @param name name of the cookie
     * @param value value of the cookie
     */
    public void set(String name, String value)
    {
        set(name, value, AGE_SESSION);
    }

    /**
     * Set a persisten cookie on the client that will expire
     * after a maximum age (given in seconds).
     *
     * @param name name of the cookie
     * @param value value of the cookie
     * @param seconds_age max age of the cookie in seconds
     */
    public void set(String name, String value, int seconds_age)
    {
        if (response == null)
        {
            throw new IllegalStateException("Servlet response not available");
        }

        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(seconds_age);
        cookie.setPath(cookiePath.getContextPath()+cookiePath.getScriptName());
        response.addCookie (cookie);
    }

    /**
     * Remove a previously set cookie from the client machine.
     *
     * @param name name of the cookie
     */
    public void unset(String name)
    {
        set(name, " ", AGE_DELETE);
    }
}
