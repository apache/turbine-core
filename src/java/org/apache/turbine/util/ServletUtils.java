package org.apache.turbine.util;

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

import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.Turbine;
import org.apache.turbine.util.uri.URIConstants;

/**
 * This is where common Servlet manipulation routines should go.
 *
 * @author <a href="mailto:gonzalo.diethelm@sonda.com">Gonzalo Diethelm</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class ServletUtils
{
    /**
     * The default HTTP port number.
     * @deprecated use URIConstants.HTTP_PORT
     */
    public static final int HTTP_PORT = URIConstants.HTTP_PORT;

    /**
     * The default HTTPS port number.
     * @deprecated use URIConstants.HTTPS_PORT
     */
    public static final int HTTPS_PORT = URIConstants.HTTPS_PORT;

    /**
     * The default FTP port number.
     * @deprecated use URIConstants.FTP_PORT
     */
    public static final int FTP_PORT = URIConstants.FTP_PORT;

    /**
     * The part of the URI which separates the protocol indicator (i.e. the
     * scheme) from the rest of the URI.
     * @deprecated use URIConstants.URI_SCHEME_SEPARATOR;
     */
    public static final String URI_SCHEME_SEPARATOR = URIConstants.URI_SCHEME_SEPARATOR;

    /**
     * Expands a string that points to a relative path or path list,
     * leaving it as an absolute path based on the servlet context.
     * It will return null if the text is empty or the config object
     * is null.
     *
     * @param config The ServletConfig.
     * @param text The String containing a path or path list.
     * @return A String with the expanded path or path list.
     */
    public static String expandRelative(ServletConfig config,
                                        String text)
    {
        if (StringUtils.isEmpty(text))
        {
            return text;
        }

        if (config == null)
        {
            return null;
        }

        // attempt to make it relative
        if (!text.startsWith("/") && !text.startsWith("./")
                && !text.startsWith("\\") && !text.startsWith(".\\"))
        {
            StringBuffer sb = new StringBuffer();
            sb.append("./");
            sb.append(text);
            text = sb.toString();
        }

        ServletContext context = config.getServletContext();
        String base = context.getRealPath("/");

        base = (StringUtils.isEmpty(base))
            ? config.getInitParameter(Turbine.BASEDIR_KEY)
            : base;

        if (StringUtils.isEmpty(base))
        {
            return text;
        }

        String separator = System.getProperty("path.separator");

        StringTokenizer tokenizer = new StringTokenizer(text,
                separator);
        StringBuffer buffer = new StringBuffer();
        while (tokenizer.hasMoreTokens())
        {
            buffer.append(base).append(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens())
            {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    /**
     * Defaults to the scheme used in the supplied request.
     *
     * @see #hostURL(HttpServletRequest req, String proto)
     * @deprecated Use ServerData(req).getHostUrl()
     */
    public static StringBuffer hostURL(HttpServletRequest req)
    {
        return hostURL(req, null);
    }

    /**
     * Returns a URL fragment derived from the provided HTTP request,
     * including the protocol used to address the server (if non-standard
     * for HTTP/HTTPS).  Returns the fragment as a buffer
     *
     * @param req The request to extract information from.
     * @param scheme The protocol indicator to prefix the host name with, or
     * the protocol used to address the server with if <code>null</code>.
     * @return The desired URL fragment.
     * @deprecated Use ServerData(req).getHostUrl()
     */
    public static StringBuffer hostURL(HttpServletRequest req, String scheme)
    {
        ServerData serverData = new ServerData(req);

        if (StringUtils.isNotEmpty(scheme))
        {
            serverData.setServerScheme(scheme);
        }

        StringBuffer sb = new StringBuffer();

        serverData.getHostUrl(sb);

        return sb;
    }
}
