package org.apache.turbine.services.localization;

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
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.localization.LocalizationService;
import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;
/**
 * A pull tool which provides lookups for localized text by delegating
 * to the configured Fulcrum <code>LocalizationService</code>.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jon@collab.net">Jon Stevens</a>
 */
public class LocalizationTool implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(LocalizationTool.class);
    /** Fulcrum Localization component */
    private LocalizationService localizationService;
    /**
     * The language and country information parsed from the request's
     * <code>Accept-Language</code> header.  Reset on each request.
     */
    protected Locale locale;

    /**
     * Lazy load the LocalizationService.
     * @return a fulcrum LocalizationService
     */
    public LocalizationService getLocalizationService()
    {
        if (localizationService == null)
        {
            try
            {
                localizationService = (LocalizationService)TurbineServices.getInstance()
                    .getService(LocalizationService.ROLE);
            }
            catch (Exception e)
            {
                throw new InstantiationException("Problem looking up Localization Service:"+e.getMessage());
            }
        }
        return localizationService;
    }

    /**
     * Creates a new instance.  Used by <code>PullService</code>.
     */
    public LocalizationTool()
    {
        refresh();
    }

    /**
     * <p>Performs text lookups for localization.</p>
     *
     * <p>Assuming there is a instance of this class with a HTTP
     * request set in your template's context named <code>l10n</code>,
     * the VTL <code>$l10n.HELLO</code> would render to
     * <code>hello</code> for English requests and <code>hola</code>
     * in Spanish (depending on the value of the HTTP request's
     * <code>Accept-Language</code> header).</p>
     *
     * @param key The identifier for the localized text to retrieve.
     * @return The localized text.
     */
    public String get(String key)
    {
        try
        {
            return getLocalizationService().getString(getBundleName(null), getLocale(), key);
        }
        catch (MissingResourceException noKey)
        {
            log.error(noKey);
            return null;
        }
    }

    /**
     * Gets the current locale.
     *
     * @return The locale currently in use.
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * The return value of this method is used to set the name of the
     * bundle used by this tool.  Useful as a hook for using a
     * different bundle than specified in your
     * <code>LocalizationService</code> configuration.
     *
     * @param data The inputs passed from {@link #init(Object)}.
     * (ignored by this implementation).
     */
    protected String getBundleName(Object data)
    {
        return getLocalizationService().getDefaultBundleName();
    }

    /**
     * Formats a localized value using the provided object.
     *
     * @param key The identifier for the localized text to retrieve,
     * @param arg1 The object to use as {0} when formatting the localized text.
     * @return Formatted localized text.
     * @see #format(String, Locale, String, Object[])
     */
    public String format(String key, Object arg1)
    {
        return getLocalizationService()
                .format(getBundleName(null), getLocale(), key, arg1);
    }

    /**
     * Formats a localized value using the provided objects.
     *
     * @param key The identifier for the localized text to retrieve,
     * @param arg1 The object to use as {0} when formatting the localized text.
     * @param arg2 The object to use as {1} when formatting the localized text.
     * @return Formatted localized text.
     * @see #format(String, Locale, String, Object[])
     */
    public String format(String key, Object arg1, Object arg2)
    {
        return getLocalizationService()
                .format(getBundleName(null), getLocale(), key, arg1, arg2);
    }

    /**
     * Formats a localized value using the provided objects.
     *
     * @param key The identifier for the localized text to retrieve,
     * @param args The objects to use as {0}, {1}, etc. when
     *             formatting the localized text.
     * @return Formatted localized text.
     */
    public String format(String key, Object[] args)
    {
        return getLocalizationService()
                .format(getBundleName(null), getLocale(), key, args);
    }

    // ApplicationTool implementation

    /**
     * Sets the request to get the <code>Accept-Language</code> header
     * from (reset on each request).
     */
    public void init(Object data)
    {
        if (data instanceof RunData)
        {
            // Pull necessary information out of RunData while we have
            // a reference to it.
            locale = getLocalizationService().getLocale(((RunData) data).getRequest());
        }
    }

    /**
     * No-op.
     */
    public void refresh()
    {
        locale = null;
    }
}
