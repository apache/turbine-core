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

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;

/**
 * This class provides utilities for handling locales and charsets
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class LocaleUtils
{
    /** Logging */
    private static Log log = LogFactory.getLog(LocaleUtils.class);

    /** The default locale. */
    private static Locale defaultLocale = null;

    /** The default charset. */
    private static String defaultCharSet = null;

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

        if (log.isDebugEnabled())
        {
            log.debug("Input Encoding has been set to " + inputEncoding);
        }

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
    public static String getDefaultCharSet()
    {
        if (defaultCharSet == null)
        {
            /* Get the default charset and cache it in a static variable. */
            defaultCharSet = Turbine.getConfiguration()
                    .getString(TurbineConstants.LOCALE_DEFAULT_CHARSET_KEY,
                            TurbineConstants.LOCALE_DEFAULT_CHARSET_DEFAULT);
            log.debug("defaultCharSet = " + defaultCharSet + " (From Properties)");
        }

        String charset = defaultCharSet;

        if (StringUtils.isEmpty(charset)) // can happen if set explicitly in the configuration
        {
            log.debug("Default charset is empty!");
            /* Default charset isn't specified, get the locale specific one. */
            Locale locale = getDefaultLocale();
            log.debug("Locale is " + locale);

            if (!locale.equals(Locale.US))
            {
                log.debug("We don't have US Locale!");
                ServiceManager serviceManager = TurbineServices.getInstance();
                MimeTypeService mimeTypeService = null;
                try
                {
                    mimeTypeService = (MimeTypeService) serviceManager.getService(MimeTypeService.ROLE);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                charset = mimeTypeService.getCharSet(locale);

                log.debug("Charset now " + charset);
            }
        }

        log.debug("Returning default Charset of " + charset);
        return charset;
    }

    /**
     * Gets the charset defined by a property named "locale.override.charset"
     * This property has no default. If it exists, the output charset is always
     * set to its value
     *
     * @return the name of the override charset or null.
     */
    public static String getOverrideCharSet()
    {
        return Turbine.getConfiguration()
                .getString(TurbineConstants.LOCALE_OVERRIDE_CHARSET_KEY);
    }
}
