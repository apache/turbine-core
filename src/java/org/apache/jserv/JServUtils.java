package org.apache.jserv;

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

// Java stuff.
import java.net.URLEncoder;
import java.util.Date;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Locale;
import java.util.TimeZone;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import javax.servlet.http.Cookie;

/**
 * Various utility methods used by the servlet engine.
 *
 * @author <a href="mailto:unknown">Francis J. Lacoste</a>
 * @author <a href="mailto:unknown">Ian Kluft</a>
 * @version $Id$
 */
public final class JServUtils
{
    private static SimpleDateFormat cookieDate =
        new SimpleDateFormat("EEE, dd-MMM-yyyy HH:mm:ss zz", Locale.US );

    static
    {
        cookieDate.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Encode a cookie as per the Netscape Cookies specification.  The
     * resulting string can be used in a Set-Cookie header.
     *
     * @param cookie The Cookie to encode.
     * @return A string following Netscape Cookies specification.
     */
    public static String encodeCookie(Cookie cookie)
    {
        StringBuffer buf = new StringBuffer( cookie.getName() );
        buf.append('=');
        buf.append(cookie.getValue());

        long age = cookie.getMaxAge();
        if (age > 0)
        {
            buf.append("; expires=");
            buf.append(cookieDate.format(new Date(System.currentTimeMillis() +
                                                  (long)age * 1000 )));
        }
        else if (age == 0)
        {
            buf.append("; expires=");

            // Set expiration to the epoch to delete the cookie.
            buf.append(cookieDate.format(new Date(0)));
        }

        if (cookie.getDomain() != null)
        {
            buf.append("; domain=");
            buf.append(cookie.getDomain());
        }

        if (cookie.getPath() != null)
        {
            buf.append("; path=");
            buf.append(cookie.getPath());
        }

        if (cookie.getSecure())
        {
            buf.append("; secure");
        }

        return buf.toString();
    }

    /**
     * Parse a content-type header for the character encoding.  If the
     * content-type is null or there is no explicit character
     * encoding, ISO-8859-1 is returned.
     *
     * @param contentType A content type header.
     * @return A String.
     */
    public static String parseCharacterEncoding(String contentType)
    {
        int start;
        int end;

        if ((contentType == null) ||
            ((start = contentType.indexOf("charset="))) == -1 )
        {
            return "ISO-8859-1";
        }

        String encoding = contentType.substring(start + 8);

        if ((end = encoding.indexOf(";")) > -1)
        {
            return encoding.substring(0, end);
        }
        else
        {
            return encoding;
        }
    }

    /**
     * Parse a cookie header into an array of cookies as per RFC2109 -
     * HTTP Cookies.
     *
     * @param cookieHdr The Cookie header value.
     * @return A Cookie[].
     */
    public static Cookie[] parseCookieHeader(String cookieHdr)
    {
        Vector cookieJar = new Vector();

        if(cookieHdr == null || cookieHdr.length() == 0)
            return new Cookie[0];

        StringTokenizer stok = new StringTokenizer(cookieHdr, "; ");
        while (stok.hasMoreTokens())
        {
            try
            {
                String tok = stok.nextToken();
                int equals_pos = tok.indexOf('=');
                if (equals_pos > 0)
                {
                    String name = URLDecode(tok.substring(0, equals_pos));
                    String value = URLDecode(tok.substring(equals_pos + 1));
                    cookieJar.addElement(new Cookie(name, value));
                }
                else if ( tok.length() > 0 && equals_pos == -1 )
                {
                    String name = URLDecode(tok);
                    cookieJar.addElement(new Cookie(name, ""));
                }
            }
            catch (IllegalArgumentException badcookie)
            {
            }
            catch (NoSuchElementException badcookie)
            {
            }
        }

        Cookie[] cookies = new Cookie[cookieJar.size()];
        cookieJar.copyInto(cookies);
        return cookies;
    }

    /**
     * This method decodes the given URL-encoded string.
     *
     * @param str The URL-encoded string.
     * @return The decoded string.
     * @exception IllegalArgumentException, if a '%' is not followed
     * by a valid 2-digit hex number.
     */
    public final static String URLDecode(String str)
        throws IllegalArgumentException
    {
        if (str == null)
            return  null;

        // Decoded string output.
        StringBuffer dec = new StringBuffer();

        int strPos = 0;
        int strLen = str.length();

        dec.ensureCapacity(str.length());
        while (strPos < strLen)
        {
            // Look ahead position.
            int laPos;

            // Look ahead to next URLencoded metacharacter, if any.
            for (laPos = strPos; laPos < strLen; laPos++)
            {
                char laChar = str.charAt(laPos);
                if ((laChar == '+') || (laChar == '%'))
                {
                    break;
                }
            }

            // If there were non-metacharacters, copy them all as a
            // block.
            if (laPos > strPos)
            {
                dec.append(str.substring(strPos,laPos));
                strPos = laPos;
            }

            // Shortcut out of here if we're at the end of the string.
            if (strPos >= strLen)
            {
                break;
            }

            // Process next metacharacter.
            char metaChar = str.charAt(strPos);
            if (metaChar == '+')
            {
                dec.append(' ');
                strPos++;
                continue;
            }
            else if (metaChar == '%')
            {
                try
                {
                    dec.append((char)
                               Integer.parseInt(str.substring(strPos + 1,
                                                              strPos + 3),
                                                16));
                }
                catch (NumberFormatException e)
                {
                    throw new IllegalArgumentException("invalid hexadecimal "
                    + str.substring(strPos + 1, strPos + 3)
                    + " in URLencoded string (illegal unescaped '%'?)" );
                }
                catch (StringIndexOutOfBoundsException e)
                {
                    throw new IllegalArgumentException("illegal unescaped '%' "
                    + " in URLencoded string" );
                }
                strPos += 3;
            }
        }

        return dec.toString();
    }

    /**
     * This method URL-encodes the given string.  This method is here
     * for symmetry and simplicity reasons and just calls
     * URLEncoder.encode().
     *
     * @param str The string.
     * @return The URL-encoded string.
     */
    public final static String URLEncode(String str)
    {
        if (str == null)
            return  null;
        return URLEncoder.encode(str);
    }
}
