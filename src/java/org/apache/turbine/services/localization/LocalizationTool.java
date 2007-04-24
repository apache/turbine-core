package org.apache.turbine.services.localization;

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

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.pull.ApplicationTool;
import org.apache.turbine.util.RunData;

/**
 * A pull tool which provides lookups for localized text by delegating
 * to the configured <code>LocalizationService</code>.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jon@collab.net">Jon Stevens</a>
 * @version $Id$
 */
public class LocalizationTool implements ApplicationTool
{
    /** Logging */
    private static Log log = LogFactory.getLog(LocalizationTool.class);

    /**
     * The language and country information parsed from the request's
     * <code>Accept-Language</code> header.  Reset on each request.
     */
    protected Locale locale;

    /**
     * The name of the bundle for this tool to use.
     */
    private String bundleName;

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
            return Localization.getString(getBundleName(null), getLocale(), key);
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
     * different bundle than specifed in your
     * <code>LocalizationService</code> configuration.
     *
     * @param data The inputs passed from {@link #init(Object)}.
     * (ignored by this implementation).
     */
    protected String getBundleName(Object data)
    {
        return bundleName;
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
        return Localization.format(getBundleName(null), getLocale(), key, arg1);
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
        return Localization.format(getBundleName(null), getLocale(), key, arg1, arg2);
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
        return Localization.format(getBundleName(null), getLocale(), key, args);
    }

    /**
     * Formats a localized value using the provided objects.  This variation
     * allows for a List so that the velocity ["arg1", "arg2", "arg3"] syntax
     * is supported.
     *
     * @param key The identifier for the localized text to retrieve,
     * @param args The objects to use as {0}, {1}, etc. when
     *             formatting the localized text.
     * @return Formatted localized text.
     */
    public String format(String key, List args)
    {
        return Localization.format(getBundleName(null), getLocale(), key, args.toArray());
    }

    // ApplicationTool implmentation

    /**
     * Sets the request to get the <code>Accept-Language</code> header
     * from (reset on each request).
     */
    public final void init(Object data)
    {
        if (data instanceof RunData)
        {
            // Pull necessary information out of RunData while we have
            // a reference to it.
            locale = Localization.getLocale(((RunData) data).getRequest());
            bundleName = Localization.getDefaultBundleName();
        }
    }

    /**
     * No-op.
     */
    public void refresh()
    {
        locale = null;
        bundleName = null;
    }
}
