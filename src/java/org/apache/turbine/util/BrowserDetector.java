package org.apache.turbine.util;

import java.util.HashMap;
import java.util.Map;

import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.VersionNumber;
import net.sf.uadetector.service.UADetectorServiceFactory;

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

/**
 * This class parses the user agent string and provides getters for
 * its parts. It uses UADetector (http://uadetector.sourceforge.net/)
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:leon@clearink.com">Leon Atkisnon</a>
 * @author <a href="mailto:mospaw@polk-county.com">Chris Mospaw</a>
 * @author <a href="mailto:bgriffin@cddb.com">Benjamin Elijah Griffin</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class BrowserDetector
{
    /** The user agent string. */
    private String userAgentString = "";

    /** The user agent cache. */
    private static volatile Map<String, ReadableUserAgent> userAgentCache =
            new HashMap<String, ReadableUserAgent>();

    /** The user agent parser */
    private static UserAgentStringParser parser =
            UADetectorServiceFactory.getCachingAndUpdatingParser();

    /** The browser name specified in the user agent string. */
    private String browserName = "";

    /**
     * The browser version specified in the user agent string.  If we
     * can't parse the version just assume an old browser.
     */
    private float browserVersion = (float) 1.0;

    /**
     * The browser platform specified in the user agent string.
     */
    private String browserPlatform = "unknown";

    /**
     * Constructor used to initialize this class.
     *
     * @param userAgentString A String with the user agent field.
     */
    public BrowserDetector(String userAgentString)
    {
        this.userAgentString = userAgentString;
        parse();
    }

    /**
     * Constructor used to initialize this class.
     *
     * @param data The Turbine RunData object.
     */
    public BrowserDetector(RunData data)
    {
        this(data.getUserAgent());
    }

    /**
     * The browser name specified in the user agent string.
     *
     * @return A String with the browser name.
     */
    public String getBrowserName()
    {
        return browserName;
    }

    /**
     * The browser platform specified in the user agent string.
     *
     * @return A String with the browser platform.
     */
    public String getBrowserPlatform()
    {
        return browserPlatform;
    }

    /**
     * The browser version specified in the user agent string.
     *
     * @return A String with the browser version.
     */
    public float getBrowserVersion()
    {
        return browserVersion;
    }

    /**
     * The user agent string for this class.
     *
     * @return A String with the user agent.
     */
    public String getUserAgentString()
    {
        return userAgentString;
    }

    /**
     * The user agent for this class.
     *
     * @return A user agent.
     */
    public ReadableUserAgent getUserAgent()
    {
        return userAgentCache.get(userAgentString);
    }

    /**
     * Helper method to initialize this class.
     */
    private void parse()
    {
        ReadableUserAgent userAgent = userAgentCache.get(userAgentString);

        if (userAgent == null)
        {
            userAgent = parser.parse(userAgentString);
            userAgentCache.put(userAgentString, userAgent);
        }

        // Get the browser name and version.
        browserName = userAgent.getName();
        VersionNumber version = userAgent.getVersionNumber();
        browserVersion = toFloat(version.toVersionString());

        // Try to figure out what platform.
        OperatingSystem os = userAgent.getOperatingSystem();
        browserPlatform = os.getFamilyName();
    }

    /**
     * Helper method to convert String to a float.
     *
     * @param s A String.
     * @return The String converted to float.
     */
    private static final float toFloat(String s)
    {
        return Float.parseFloat(s);
    }

}
