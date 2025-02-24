package org.apache.turbine.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;

import jakarta.servlet.http.HttpServletResponse;

/**
 * This class provides utilities for handling some semi-trivial HTTP stuff that
 * would otherwise be handled elsewhere.
 *
 * @author <a href="mailto:magnus@handpoint.com">Magnús Þór Torfason</a>
 * @version $Id$
 */
public class HttpUtils
{
    /**
     * Characters not allowed in external keys (name), that is not alphanumeric, underscore, hyphen, slash and dot.
     * Validates only external key (name), as internal key may also contain colon and space.
     */
    private static final String CHARACTERS_NOT_ALLOWED_IN_KEY = "[^\\w_/\\.-]";
    
    private static final Pattern CNAIK_PATTERN = Pattern.compile(CHARACTERS_NOT_ALLOWED_IN_KEY);
    /**
     * The date format to use for HTTP Dates.
     */
    private static FastDateFormat httpDateFormat = FastDateFormat.getInstance(
                "EEE, dd MMM yyyy HH:mm:ss z",
                TimeZone.getTimeZone("GMT"),
                Locale.US);

    /**
     * Formats a java Date according to rfc 1123, the rfc standard for dates in
     * http.
     *
     * @param date The Date to format
     * @return A String representation of the date
     */
    public static String formatHttpDate(Date date)
    {
        return httpDateFormat.format(date);
    }

    /**
     * This method sets the required expiration headers in the response for a
     * given {@link PipelineData} object.  This method attempts to set all relevant headers,
     * both for HTTP 1.0 and HTTP 1.1.
     *
     * @param pipelineData The {@link PipelineData} object we are setting cache information for.
     * @param expiry The number of milliseconds until the document should expire,
     * <code>0</code> indicating immediate expiration (i.e. no caching).
     */
    public static void setCacheHeaders(PipelineData pipelineData, int expiry)
    {
        HttpServletResponse response = pipelineData.get(Turbine.class, HttpServletResponse.class);

        if (0 == expiry)
        {
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", System.currentTimeMillis());
        }
        else
        {
            response.setDateHeader("Expires", System.currentTimeMillis() + expiry);
        }
    }
    
    /**
     * Check, if there is any not allowed {@value #CHARACTERS_NOT_ALLOWED_IN_KEY}
     * in parameters, eg. Turbine keys like actions, screens, layouts.
     * 
     * @param parameter or key to be checked
     * @return True, if it contains any non allowed characters
     */
    public static boolean keyRequiresClean(String parameter) {
        Matcher testMatcher = CNAIK_PATTERN.matcher(parameter);
        return testMatcher.find();
    }
    
    /**
     * Cleans parameter/key from disallowed characters defined in {@link #CHARACTERS_NOT_ALLOWED_IN_KEY}.
     * 
     * @param parameter to be cleaned
     * @return the cleaned parameter
     */
    public static String getCleanedKey(String parameter) {
        return parameter.replaceAll(CHARACTERS_NOT_ALLOWED_IN_KEY,"");
    }
    
}
