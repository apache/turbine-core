package org.apache.turbine.util;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * This class provides utilities for handling some semi-trivial HTTP stuff that
 * would othterwise be handled elsewhere.
 *
 * @author <a href="mailto:magnus@handpoint.com">Magnús Þór Torfason</a>
 * @version $Id$
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
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        httpDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * Formats a java Date according to rfc 1123, the rfc standard for dates in
     * http.
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
     * This method sets the required expiration headers in the response for a
     * given RunData object.  This method attempts to set all relevant headers,
     * both for HTTP 1.0 and HTTP 1.1.
     *
     * @param data The RunData object we are setting cache information for.
     * @param expiry The number of seconds untill the document should expire,
     * <code>0</code> indicating immediate expiration (i.e. no caching).
     */
    public static void setCacheHeaders(RunData data, int expiry)
    {
        if (0 == expiry)
        {
            data.getResponse().setHeader("Pragma", "no-cache");
            data.getResponse().setHeader("Cache-Control", "no-cache");
            data.getResponse().setHeader("Expires",
                    formatHttpDate(new Date()));
        }
        else
        {
            Date expiryDate = new Date(System.currentTimeMillis() + expiry);
            data.getResponse().setHeader("Expires",
                    formatHttpDate(expiryDate));
        }
    }

}
