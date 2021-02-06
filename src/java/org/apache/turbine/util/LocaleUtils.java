package org.apache.turbine.util;

import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

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

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
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
 * This class provides utilities for handling locales and charsets
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class LocaleUtils
{
    /** Logging */
    private static final Logger log = LogManager.getLogger(LocaleUtils.class);

    /** The default locale. */
    private static Locale defaultLocale = null;

    /** The default charset. */
    private static Charset defaultCharSet = null;

    /**
     * Returns the default input encoding for the servlet.
     *
     * @return the default input encoding.
     */
    public static String getDefaultInputEncoding()
    {
        // Get the default input defaultEncoding
        String inputEncoding = Turbine.getConfiguration()
                .getString(TurbineConstants.PARAMETER_ENCODING_KEY,
                        TurbineConstants.PARAMETER_ENCODING_DEFAULT);

        log.debug("Input Encoding has been set to {}", inputEncoding);

        return inputEncoding;
    }

    /**
     * Gets the default locale defined by properties named "locale.default.lang"
     * and "locale.default.country".
     *
     * This changed from earlier Turbine versions that you can rely on
     * getDefaultLocale() to never return null.
     *
     * @return A Locale object.
     */
    public static Locale getDefaultLocale()
    {
        if (defaultLocale == null)
        {
            /* Get the default locale and cache it in a static variable. */
            String lang = Turbine.getConfiguration()
                    .getString(TurbineConstants.LOCALE_DEFAULT_LANGUAGE_KEY,
                            TurbineConstants.LOCALE_DEFAULT_LANGUAGE_DEFAULT);

            String country = Turbine.getConfiguration()
                    .getString(TurbineConstants.LOCALE_DEFAULT_COUNTRY_KEY,
                            TurbineConstants.LOCALE_DEFAULT_COUNTRY_DEFAULT);

            // We ensure that lang and country is never null
            defaultLocale = new Locale(lang, country);
        }

        return defaultLocale;
    }

    /**
     * Gets the default charset defined by a property named
     * "locale.default.charset"
     *
     * @return the name of the default charset or null.
     */
    @Deprecated
    public static String getDefaultCharSet()
    {
        return getDefaultCharset().name();
    }

    /**
     * Gets the default charset defined by a property named
     * "locale.default.charset"
     *
     * @return the default charset, never null.
     */
    public static Charset getDefaultCharset()
    {
        if (defaultCharSet == null)
        {
            /* Get the default charset and cache it in a static variable. */
            String charSet = Turbine.getConfiguration()
                    .getString(TurbineConstants.LOCALE_DEFAULT_CHARSET_KEY,
                            TurbineConstants.LOCALE_DEFAULT_CHARSET_DEFAULT);

            if (StringUtils.isNotEmpty(charSet))
            {
                defaultCharSet = charSetForName(charSet);
                log.debug("defaultCharSet = {} (From Properties)", defaultCharSet);
            }
        }

        Charset charset = defaultCharSet;

        if (charset == null) // can happen if set explicitly in the configuration
        {
            log.debug("Default charset is empty!");
            /* Default charset isn't specified, get the locale specific one. */
            Locale locale = getDefaultLocale();
            log.debug("Locale is {}", locale);

            if (!locale.equals(Locale.US))
            {
                log.debug("We don't have US Locale!");
                ServiceManager serviceManager = TurbineServices.getInstance();
                if (serviceManager.isRegistered(MimeTypeService.ROLE))
                {
                    try
                    {
                        MimeTypeService mimeTypeService = (MimeTypeService) serviceManager.getService(MimeTypeService.ROLE);
                        charset = charSetForName(mimeTypeService.getCharSet(locale));
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }

                    log.debug("Charset now {}", charset);
                }
            }

            // The fallback to end all fallbacks
            if (charset == null)
            {
                charset = StandardCharsets.ISO_8859_1;
            }
        }

        log.debug("Returning default Charset of {}", charset);
        return charset;
    }

    /**
     * Gets the charset defined by a property named "locale.override.charset"
     * This property has no default. If it exists, the output charset is always
     * set to its value
     *
     * @return the name of the override charset or null.
     */
    @Deprecated
    public static String getOverrideCharSet()
    {
        return Turbine.getConfiguration()
                .getString(TurbineConstants.LOCALE_OVERRIDE_CHARSET_KEY);
    }

    /**
     * Gets the charset defined by a property named "locale.override.charset"
     * This property has no default. If it exists, the output charset is always
     * set to its value
     *
     * @return the override charset or null.
     */
    public static Charset getOverrideCharset()
    {
        String charset = Turbine.getConfiguration()
                .getString(TurbineConstants.LOCALE_OVERRIDE_CHARSET_KEY);

        if (StringUtils.isEmpty(charset))
        {
            return null;
        }

        return charSetForName(charset);
    }

    /**
     * Get a Charset object for a given name
     * This method does not throw exceptions on illegal input but returns null.
     *
     * @param charSet the charset name
     *
     * @return the Charset or null if it does not exist
     */
    private static Charset charSetForName(String charSet)
    {
        try
        {
            return Charset.forName(charSet);
        }
        catch (IllegalCharsetNameException | UnsupportedCharsetException e)
        {
            log.error("Illegal default charset {}", charSet);
        }

        return null;
    }

}
