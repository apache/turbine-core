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

/**
 * This class parses the user agent string and sets javasciptOK and
 * cssOK following the rules described below.  If you want to check
 * for specific browsers/versions then use this class to parse the
 * user agent string and use the accessor methods in this class.
 *
 * JavaScriptOK means that the browser understands JavaScript on the
 * same level the Navigator 3 does.  Specifically, it can use named
 * images.  This allows easier rollovers.  If a browser doesn't do
 * this (Nav 2 or MSIE 3), then we just assume it can't do any
 * JavaScript.  Referencing images by load order is too hard to
 * maintain.
 *
 * CSSOK is kind of sketchy in that Nav 4 and MSIE work differently,
 * but they do seem to have most of the functionality.  MSIE 4 for the
 * Mac has buggy CSS support, so we let it do JavaScript, but no CSS.
 *
 * Ported from Leon's PHP code at
 * http://www.working-dogs.com/freetrade by Frank.
 *
 * @version $Id$
 * @deprecated This class will be removed after the 2.3 release. Please
 *             use BrowserDetector from <a href="http://jakarta.apache.org/commons/">commons-http</a>.
 */
public class BrowserDetector
{
    /** The user agent string. */
    private String userAgentString = "";

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

    /** Whether or not javascript works in this browser. */
    private boolean javascriptOK = false;

    /** Whether or not CSS works in this browser. */
    private boolean cssOK = false;

    /** Whether or not file upload works in this browser. */
    private boolean fileUploadOK = false;

    /** Constants used by this class. */
    public static final String MSIE = "MSIE";
    public static final String OPERA = "Opera";
    public static final String MOZILLA = "Mozilla";
    public static final String WINDOWS = "Windows";
    public static final String UNIX = "Unix";
    public static final String MACINTOSH = "Macintosh";

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
        this.userAgentString =
                data.getRequest().getHeader("User-Agent");
        parse();
    }

    /**
     * Whether or not CSS works in this browser.
     *
     * @return True if CSS works in this browser.
     */
    public boolean isCssOK()
    {
        return cssOK;
    }

    /**
     * Whether or not file upload works in this browser.
     *
     * @return True if file upload works in this browser.
     */
    public boolean isFileUploadOK()
    {
        return fileUploadOK;
    }

    /**
     * Whether or not Javascript works in this browser.
     *
     * @return True if Javascript works in this browser.
     */
    public boolean isJavascriptOK()
    {
        return javascriptOK;
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
     * Helper method to initialize this class.
     */
    private void parse()
    {
        int versionStartIndex = userAgentString.indexOf("/");
        int versionEndIndex = userAgentString.indexOf(" ");

        // Get the browser name and version.
        browserName = userAgentString.substring(0, versionStartIndex);
        try
        {
            // Not all user agents will have a space in the reported
            // string.
            String agentSubstring = null;
            if (versionEndIndex < 0)
                agentSubstring = userAgentString.substring(versionStartIndex + 1);
            else
                agentSubstring = userAgentString.substring(versionStartIndex + 1, versionEndIndex);
            browserVersion = toFloat(agentSubstring);
        }
        catch (NumberFormatException e)
        {
            // Just use the default value.
        }

        // MSIE lies about its name.  Of course...
        if (userAgentString.indexOf(MSIE) != -1)
        {
            // Ex: Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)
            versionStartIndex = (userAgentString.indexOf(MSIE) +
                    MSIE.length() + 1);
            versionEndIndex = userAgentString.indexOf(";",
                    versionStartIndex);

            browserName = MSIE;
            try
            {
                browserVersion = toFloat(userAgentString.substring(versionStartIndex, versionEndIndex));
            }
            catch (NumberFormatException e)
            {
                // Just use the default value.
            }

            // PHP code
            // $Browser_Name = "MSIE";
            // $Browser_Version = strtok("MSIE");
            // $Browser_Version = strtok(" ");
            // $Browser_Version = strtok(";");
        }

        // Opera isn't completely honest, either...
        // Modificaton by Chris Mospaw <mospaw@polk-county.com>
        if (userAgentString.indexOf(OPERA) != -1)
        {
            //Ex: Mozilla/4.0 (Windows NT 4.0;US) Opera 3.61  [en]
            versionStartIndex = (userAgentString.indexOf(OPERA) +
                    OPERA.length() + 1);
            versionEndIndex = userAgentString.indexOf(" ",
                    versionStartIndex);

            browserName = OPERA;
            try
            {
                browserVersion = toFloat(userAgentString.substring(versionStartIndex, versionEndIndex));
            }
            catch (NumberFormatException e)
            {
                // Just use the default value.
            }

            // PHP code
            // $Browser_Name = "Opera";
            // $Browser_Version = strtok("Opera");
            // $Browser_Version = strtok("/");
            // $Browser_Version = strtok(";");
        }


        // Try to figure out what platform.
        if ((userAgentString.indexOf("Windows") != -1) ||
                (userAgentString.indexOf("WinNT") != -1) ||
                (userAgentString.indexOf("Win98") != -1) ||
                (userAgentString.indexOf("Win95") != -1))
        {
            browserPlatform = WINDOWS;
        }

        if (userAgentString.indexOf("Mac") != -1)
        {
            browserPlatform = MACINTOSH;
        }

        if (userAgentString.indexOf("X11") != -1)
        {
            browserPlatform = UNIX;
        }

        if (browserPlatform == WINDOWS)
        {
            if (browserName.equals(MOZILLA))
            {
                if (browserVersion >= 3.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                }
                if (browserVersion >= 4.0)
                {
                    cssOK = true;
                }
            }
            else if (browserName == MSIE)
            {
                if (browserVersion >= 4.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                    cssOK = true;
                }
            }
            else if (browserName == OPERA)
            {
                if (browserVersion >= 3.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                    cssOK = true;
                }
            }
        }
        else if (browserPlatform == MACINTOSH)
        {
            if (browserName.equals(MOZILLA))
            {
                if (browserVersion >= 3.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                }
                if (browserVersion >= 4.0)
                {
                    cssOK = true;
                }
            }
            else if (browserName == MSIE)
            {
                if (browserVersion >= 4.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                }
                if (browserVersion > 4.0)
                {
                    cssOK = true;
                }
            }
        }
        else if (browserPlatform == UNIX)
        {
            if (browserName.equals(MOZILLA))
            {
                if (browserVersion >= 3.0)
                {
                    javascriptOK = true;
                    fileUploadOK = true;
                }
                if (browserVersion >= 4.0)
                {
                    cssOK = true;
                }
            }
        }
    }

    /**
     * Helper method to conver String to a float.
     *
     * @param s A String.
     * @return The String converted to float.
     */
    private float toFloat(String s)
    {
        return Float.valueOf(s).floatValue();
    }
}
