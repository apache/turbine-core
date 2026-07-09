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

import java.util.Objects;

import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

/**
 * This class parses the user agent string and provides getters for
 * its parts. It uses (<a href="https://yauaa.basjes.nl/">YAUAA</a>).
 *
 * The initialization step for a full UserAgentAnalyzer
 * (i.e. all fields) usually takes something in the range of 2-5 seconds.
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

    /** The user agent parser */
    private static UserAgentAnalyzer uaa = UserAgentAnalyzer
            .newBuilder()
            .withFields(UserAgent.AGENT_NAME,
                    UserAgent.AGENT_VERSION,
                    UserAgent.OPERATING_SYSTEM_NAME)
            .hideMatcherLoadStats()
            .withCache(10000)
            .build();

    /**
     * Constructor used to initialize this class.
     *
     * @param userAgentString A String with the user agent field.
     */
    public BrowserDetector(String userAgentString)
    {
        this.userAgentString = userAgentString;
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
        // Get the browser name.
        return Objects.requireNonNullElse(
                getUserAgent().getValue(UserAgent.AGENT_NAME),
                "");
    }

    /**
     * The browser platform specified in the user agent string.
     *
     * @return A String with the browser platform.
     */
    public String getBrowserPlatform()
    {
        // Try to figure out what platform.
        return Objects.requireNonNullElse(
                getUserAgent().getValue(UserAgent.OPERATING_SYSTEM_NAME),
                "unknown");
    }

    /**
     * The browser version specified in the user agent string.
     *
     * @return A String with the browser version.
     */
    public float getBrowserVersion()
    {
        // The browser version specified in the user agent string.  If we
        // can't parse the version just assume an old browser.
        String version = Objects.requireNonNullElse(
                getUserAgent().getValue(UserAgent.AGENT_VERSION),
                "1.0");
        return Float.parseFloat(version);
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
    public UserAgent getUserAgent()
    {
        return uaa.parse(userAgentString);
    }
}
