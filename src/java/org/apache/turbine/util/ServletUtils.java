package org.apache.turbine.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import java.util.StringTokenizer;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.Turbine;

/**
 * This is where common Servlet manipulation routines should go.
 *
 * @author <a href="mailto:gonzalo.diethelm@sonda.com">Gonzalo Diethelm</a>
 * @version $Id$
 */
public class ServletUtils
{
    /**
     * The default HTTP port number.
     */
    public static final int HTTP_PORT = 80;

    /**
     * The default HTTPS port number.
     */
    public static final int HTTPS_PORT = 443;

    /**
     * The default FTP port number.
     */
    public static final int FTP_PORT = 20;

    /**
     * The part of the URI which separates the protocol indicator (i.e. the
     * scheme) from the rest of the URI.
     */
    public static final String URI_SCHEME_SEPARATOR = "://";

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
    public static String expandRelative( ServletConfig config,
                                         String text )
    {
        if (text == null || text.length() <= 0)
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
            sb.append ("./");
            sb.append (text);
            text = sb.toString();
        }

        ServletContext context = config.getServletContext();
        String base = context.getRealPath("/");
        if(base == null)
        {
            base = config.getInitParameter(Turbine.BASEDIR_KEY);
        }
        if(base == null)
        {
            return text;
        }

        String separator = System.getProperty("path.separator");
        StringTokenizer tokenizer = new StringTokenizer(text,
                                                        separator);
        StringBuffer buffer = new StringBuffer();
        while(tokenizer.hasMoreTokens())
        {
            buffer.append(base).append(tokenizer.nextToken());
            if(tokenizer.hasMoreTokens())
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
     */
    public static StringBuffer hostURL(HttpServletRequest req, String scheme)
    {
        if (scheme == null)
        {
            scheme = req.getScheme();
        }
        StringBuffer url = new StringBuffer()
            .append(scheme)
            .append(URI_SCHEME_SEPARATOR)
            .append(req.getServerName());
        int port = req.getServerPort();
        if (! (("http".equalsIgnoreCase(scheme) && port == HTTP_PORT)  ||
               ("https".equalsIgnoreCase(scheme) && port == HTTPS_PORT) ||
               ("ftp".equalsIgnoreCase(scheme) && port == FTP_PORT)) )
        {
            url.append(':').append(port);
        }
        return url;
    }
}
