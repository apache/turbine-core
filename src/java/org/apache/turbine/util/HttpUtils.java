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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class provides utilities for handling some semi-trivial
 * HTTP stuff that would othterwize be handled elsewhere.
 *
 * @author <a href="mailto:magnus@handpoint.com">Magnús Þór Torfason</a>
 */
public class HttpUtils
{
    /**
     * The date format to use for HTTP Dates.
     */
    private static SimpleDateFormat httpDateFormat;

    static
    {
        httpDateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyyy HH:mm:ss z", Locale.US);
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Formats a java Date according to rfc 1123, the rfc
     * standard for dates in http.
     *
     * @param date The Date to format
     * @return A String represeentation of the date
     */
    public static String formatHttpDate(Date date)
    {
        synchronized (httpDateFormat)
        {
            return httpDateFormat.format(date);
        }
    }

    /**
     * This method sets the required expiration headers in the response
     * for a given RunData object.  This method attempts to set all
     * relevant headers, both for HTTP 1.0 and HTTP 1.1.
     *
     * @param data The RunData object we are setting cache information for.
     * @param expiry The number of seconds untill the document should expire,
     * <code>0</code> indicating immediate expiration (i.e. no caching).
     */
    public static void setCacheHeaders(RunData data, int expiry)
    {
        if (expiry == 0)
        {
            data.getResponse().setHeader("Pragma", "no-cache");
            data.getResponse().setHeader("Cache-Control", "no-cache");
            data.getResponse().setHeader(
                    "Expires", formatHttpDate(new Date()));
        }
        else
        {
            Date expiryDate = new Date(System.currentTimeMillis() + expiry);
            data.getResponse().setHeader(
                    "Expires", formatHttpDate(expiryDate));
        }
    }
}
