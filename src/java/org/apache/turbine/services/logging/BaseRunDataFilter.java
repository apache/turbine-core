package org.apache.turbine.services.logging;

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpUtils;
import org.apache.turbine.util.RunData;

/**
 * This class extracts data from RunData object. It is configured
 * by a string describing format of output.
 *
 * <P>
 * Format description<BR>
 * Each token should be seperate by space. In output known token
 * will be replaced by coresponding data from RunData, unkonwn will be
 * returned unchanged on output.<BR>
 *
 * Conversion token:
 * <UL>
 *      <LI>   %t - current Time
 * </LI><LI>   %U - URL Requested
 * </LI><LI>   %h - Remote Host
 * </LI><LI>   %a - Remote Address
 * </LI><LI>   %l - Remote User
 * </LI><LI>   %p - Server Port
 * </LI><LI>   %v - Server Name
 * </LI><LI>   %m - Method
 * </LI><LI>   %q - Query String
 * </LI><LI>   %cp - Context Path
 * </LI><LI>   %sid - Session Id
 * </LI><LI>   %au - Authentication Type
 * </LI><LI>   %ct - Content Type
 * </LI><LI>   %enc - Character Encoding
 * </LI><LI>   %pro - Protocol
 * </LI><LI>   %sce - Scheme
 * </LI><LI>   %cln - Content Length
 * </LI><LI>   %ua - User Agent
 * </LI><LI>   %ban - Banner Info
 * </LI><LI>   %usr - User
 * </LI><LI>   %cook - Cookies
 * </LI>
 * </UL>
 *
 * @author <a href="mailto:Tomasz.Zielinski@e-point.pl">Tomasz Zielinski</a>
 * @author <a href="mailto:Michal.Majdan@e-point.pl">Michal Majdan</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class BaseRunDataFilter implements RunDataFilter
{
    /** table matching conversion tokens to methods names */
    protected static Map methodNamesMap = null;

    /** applied format */
    protected String format = null;

    /** parsed format */
    protected List pattern = null;

    protected final String DELIM = " ";

    /** initialization of the conversion table */
    static
    {
        if (methodNamesMap == null)
        {
            methodNamesMap= new HashMap();
            methodNamesMap.put("%t", "getTime");
            methodNamesMap.put("%U", "getURLRequested");
            methodNamesMap.put("%h", "getRemoteHost");
            methodNamesMap.put("%a", "getRemoteAddr");
            methodNamesMap.put("%l", "getRemoteUser");
            methodNamesMap.put("%p", "getServerPort");
            methodNamesMap.put("%v", "getServerName");
            methodNamesMap.put("%m", "getMethod");
            methodNamesMap.put("%q", "getQueryString");
            methodNamesMap.put("%cp", "getContextPath");
            methodNamesMap.put("%sid", "getSessionId");
            methodNamesMap.put("%au", "getAuthType");
            methodNamesMap.put("%ct", "getContentType");
            methodNamesMap.put("%enc", "getCharacterEncoding");
            methodNamesMap.put("%pro", "getProtocol");
            methodNamesMap.put("%sce", "getScheme");
            methodNamesMap.put("%cln", "getContentLength");
            methodNamesMap.put("%ua", "getUserAgent");
            methodNamesMap.put("%ban", "getBannerInfo");
            methodNamesMap.put("%usr", "getUser");
            methodNamesMap.put("%cook", "getCookies");
        }
    }

    /** parses format string */
    public void setFormat(String format)
    {
        if (format != null && !format.trim().equals(""))
        {
            pattern = new ArrayList();
            StringTokenizer st = new StringTokenizer(format);
            while (st.hasMoreTokens())
            {
                pattern.add(st.nextToken());
            }
            this.format = format;
        }
    }

    /**
     * For each field in tha pattern looking for method extracting data from
     * RunData, invokes the method, and adds return value to return value.
     * If there is no method for the token, adds token to return value
     *
     * @param data - RunDate from which data will be extracted
     */
    public String getString(RunData data)
    {
        Method method = null;
        StringBuffer answer = new StringBuffer();
        if (format == null)
        {
            return "";
        }

        for(int i = 0; i < pattern.size(); i++)
        {
            try
            {
                //when we want ordinary information we do
                String methodName = (String)methodNamesMap.get(pattern.get(i));
                if(methodName != null)
                {
                    method = getClass().getDeclaredMethod(methodName,
                        new Class[]{RunData.class});
                    answer.append((String)method.invoke(null,
                        new Object[] {data}));
                    answer.append(DELIM);
                }
                else
                {
                    //when we need the content of a header line
                    if(((String)pattern.get(i)).endsWith("i"))
                    {
                        method=getClass().getDeclaredMethod("getHeader",
                            new Class[]{RunData.class, String.class});
                        answer.append((String)method.invoke(null,
                            new Object[] {data, (String)pattern.get(i)}));
                        answer.append(DELIM);
                    }
                    else
                    {
                        answer.append((String)pattern.get(i));
                        answer.append(DELIM);
                    }
                }
            }
            catch (Exception e)
            {
            }
        }
        return answer.toString();
    }

    /**
     * Retrives current system time.
     *
     * @param   data  RunData object associated with this request
     * @return  date and time
     */
    private static String getTime(RunData data) {
        long time = System.currentTimeMillis();
        Date d = new Date(time);
        return d.toString();
    }

    /**
     * Retrives the url requested by the client
     *
     * @param   data RunData object associated with this request
     * @return  url requested by the client
     */
    private static String getURLRequested(RunData data)
    {
        return HttpUtils.getRequestURL(data.getRequest()).toString();
    }

    /**
     * Retrives remote host name.
     *
     * @param   data  RunData object associated with this request
     * @return  name of the host that sent the request
     */
    private static String getRemoteHost(RunData data)
    {
        return data.getRemoteHost();
    }

    /**
     * Retrives remote host address.
     *
     * @param   data  RunData object associated with this request
     * @return  address of the host that sent the request
     */
    private static String getRemoteAddr(RunData data)
    {
        return data.getRemoteAddr();
    }

    /**
     * Retrives the login of the user making this request, if the user
     * has been authenticated, or null if the user has not been authenticated.
     *
     * @param   data  RunData object associated with this request
     * @return  remote user login  name if he/she has been authenticated
     */
    private static String getRemoteUser(RunData data)
    {
        return data.getRequest().getRemoteUser();
    }

    /**
     * Retrives the cached serverPort that is stored in the ServerData object
     *
     * @param   data  RunData object associated with this request
     * @return  port that this request was recived on
     */
    private static String getServerPort(RunData data)
    {
        return String.valueOf(data.getServerPort());
    }

    /**
     * Retrives the cached serverName that is stored in the ServerData object
     *
     * @param   data  RunData object associated with this request
     * @return  name of the server that served the request
     */
    private static String getServerName(RunData data)
    {
        return data.getServerName();
    }

    /**
     * Retrives the cached method that is stored in the ServerData object
     *
     * @param   data  RunData object associated with this request
     * @return  method used by the request
     */
    private static String getMethod(RunData data)
    {
        return data.getRequest().getMethod();
    }

    /**
     * Retrives the  value of the specified request header.
     *
     * @param   data RunData object associated with this request
     * @param   symbol pattern element containig header line name between {}
     *          brackets
     * @return  header line contents
     */
    private static String getHeader(RunData data, String symbol)
    {
        Enumeration names = data.getRequest().getHeaderNames();
        StringTokenizer st = new StringTokenizer(symbol, "%{}");
        return data.getRequest().getHeader(st.nextToken());
    }

    /**
     * Retrives the query string that is contained in the request URL after the
     * path.
     *
     * @param   data  RunData object associated with this request
     * @return  query string
     */
    private static String getQueryString(RunData data)
    {
        return data.getRequest().getQueryString();
    }

    /**
     * Retrives the portion of the request URI that indicates the context of the
     * request.
     *
     * @param   data  RunData object associated with this request
     * @return  the portion of the request URI that indicates the context of the
     *          request.
     */
    private static String getContextPath(RunData data)
    {
        return data.getRequest().getContextPath();
    }

    /**
     * Retrives a string containing the unique identifier assigned to this
     * session
     *
     * @param   data  RunData object associated with this request
     * @return  Session Id
     */
    private static String getSessionId(RunData data)
    {
        return data.getRequest().getSession().getId();
    }

    /**
     * Retrives  the name of the authentication scheme used to protect the
     * servlet, for example, "BASIC" or "SSL," or null if the servlet was not
     * protected.
     *
     * @param   data  RunData object associated with this request
     * @return  authentication scheme  used
     */
    private static String getAuthType(RunData data)
    {
        return data.getRequest().getAuthType();
    }

    /**
     * Retrives the MIME type of the body of the request, or null if the type is
     * not known.
     *
     * @param   data  RunData object associated with this request
     * @return  content type of the request
     */
    private static String getContentType(RunData data)
    {
        return data.getRequest().getContentType();
    }

    /**
     * Retrives the name of the character encoding used in the body of this
     * request.
     *
     * @param   data  RunData object associated with this request
     * @return  character encoding
     */
    private static String getCharacterEncoding(RunData data)
    {
        return data.getRequest().getCharacterEncoding();
    }

    /**
     * Retrives the name and version of the protocol the request uses in the
     * form protocol/majorVersion.minorVersion
     *
     * @param   data  RunData object associated with this request
     * @return  protocol
     */
    private static String getProtocol(RunData data)
    {
        return data.getRequest().getProtocol();
    }

    /**
     * Retrives the name of the scheme used to make this request, for example,
     * http, https, or ftp.
     *
     * @param   data  RunData object associated with this request
     * @return  scheme
     */
    private static String getScheme(RunData data)
    {
        return data.getRequest().getScheme();
    }

    /**
     * Retrives  the length, in bytes, of the request body and made available
     * by the input stream, or -1 if the length is not known.
     *
     * @param   data  RunData object associated with this request
     * @return  content length or -1 if not yet known
     */
    private static String getContentLength(RunData data)
    {
        return String.valueOf(data.getRequest().getContentLength());
    }

    /**
     * Retrives the user agent name.
     *
     * @param   data  RunData object associated with this request
     * @return  user agent string
     */
    private static String getUserAgent(RunData data)
    {
        return data.getUserAgent();
    }

    /**
     * Retrives the banner info asociated with the user making the request.
     *
     * @param   data  RunData object associated with this request
     * @return  banner info or empty stirng
     */
    private static String getBannerInfo(RunData data)
    {
        return (String) data.getUser().getTemp("bannerInfo", "");
    }

    /**
     * Retrives  first and last name of the user making the request.
     *
     * @param   data  RunData object associated with this request
     * @return  user
     */
    private static String getUser(RunData data)
    {
        return data.getUser().getFirstName() + " " +
            data.getUser().getLastName() ;
    }

    /**
     * Retrives cookies.
     *
     * @param   data  RunData object associated with this request
     * @return  cookies
     */
    private static String getCookies(RunData data) {
        Cookie cookies[] = data.getRequest().getCookies();
        StringBuffer answer = new StringBuffer();
        answer.append("Cookies: [ ");
        for (int i = 0; i < cookies.length; i++ )
        {
            answer.append(cookies[i].getName());
            answer.append(" = ");
            answer.append(cookies[i].getValue());
            answer.append(";\t");
        }
        answer.append("]");
        return answer.toString();
    }
}
