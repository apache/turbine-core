package org.apache.turbine.util;

/*
 * Copyright (c) 1997-2000 The Java Apache Project.  All rights reserved.
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
 * 3. All advertising materials mentioning features or use of this
 *    software must display the following acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * 4. The names "Apache JServ", "Apache JServ Servlet Engine", "Turbine",
 *    "Apache Turbine", "Turbine Project", "Apache Turbine Project" and
 *    "Java Apache Project" must not be used to endorse or promote products
 *    derived from this software without prior written permission.
 *
 * 5. Products derived from this software may not be called "Apache JServ"
 *    nor may "Apache" nor "Apache JServ" appear in their names without
 *    prior written permission of the Java Apache Project.
 *
 * 6. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the Java Apache
 *    Project for use in the Apache JServ servlet engine project
 *    <http://java.apache.org/>."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JAVA APACHE PROJECT "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JAVA APACHE PROJECT OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Java Apache Group. For more information
 * on the Java Apache Project and the Apache JServ Servlet Engine project,
 * please see <http://java.apache.org/>.
 *
 */

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.turbine.util.RunData;

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
                "EEE, dd MMM yyyyy HH:mm:ss z", Locale.US  );
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
        if ( expiry == 0 )
        {
            data.getResponse().setHeader("Pragma", "no-cache");
            data.getResponse().setHeader("Cache-Control", "no-cache");
            data.getResponse().setHeader(
                    "Expires", formatHttpDate(new Date()));
        }
        else
        {
            Date expiryDate = new Date( System.currentTimeMillis() + expiry );
            data.getResponse().setHeader(
                    "Expires", formatHttpDate(expiryDate));
        }
    }
}
