
package org.apache.turbine.services.localization;

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

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;

/**
 * Wrapper around the TurbineLocalization Service that makes it easy
 * to grab something from the service and make the code cleaner.
 *
 * <p>
 *
 * Instead of typing:
 *
 * <br>
 *
 * ((LocalizationService)TurbineServices.getInstance()<br>
 *           .getService(LocalizationService.SERVICE_NAME))<br>
 *     .getBundle(data)<br>
 *     .getString(str)<br>
 *
 * Now you only need to type:
 *
 * <br>
 *
 * Localization.getString(str)
 *
 * @version $Id$
 */
public abstract class Localization
{
    /**
     * Fetches the localized text from the specified bundle, ignoring
     * any default bundles.
     *
     * @see LocalizationService#getString(String, Locale, String)
     */
    public static String getString(String bundleName, Locale locale,
                                   String key)
    {
        return getService().getString(bundleName, locale, key);
    }

    /**
     * Pulls a string out of the LocalizationService with the default
     * locale values of what is defined in the
     * TurbineResources.properties file for the
     * locale.default.language and locale.default.country property
     * values.  If those cannot be found, then the JVM defaults are
     * used.
     *
     * @param key Name of string.
     * @return A localized String.
     */
    public static String getString(String key)
    {
        return getService().getString(null, null, key);
    }

    /**
     * @param key Name of the text to retrieve.
     * @param locale Locale to get text for.
     * @return Localized text.
     */
    public static String getString(String key, Locale locale)
    {
        return getService().getString(null, locale, key);
    }

    /**
     * Pulls a string out of the LocalizationService and attempts to
     * determine the Locale by the Accept-Language header.  If that
     * header is not present, it will fall back to using the locale
     * values of what is defined in the TurbineResources.properties
     * file for the locale.default.language and locale.default.country
     * property values.  If those cannot be found, then the JVM
     * defaults are used.
     *
     * @param req HttpServletRequest information.
     * @param key Name of string.
     * @return A localized String.
     */
    public static String getString(String key, HttpServletRequest req)
    {
        return getService().getString(null, getLocale(req), key);
    }

    /**
     * Convenience method that pulls a localized string off the
     * LocalizationService using the default ResourceBundle name
     * defined in the TurbineResources.properties file and the
     * specified language name in ISO format.
     *
     * @param key Name of string.
     * @param lang Desired language for the localized string.
     * @return A localized string.
     */
    public static String getString(String key, String lang)
    {
        return getString(getDefaultBundleName(), new Locale(lang, ""), key);
    }

    /**
     * Convenience method to get a ResourceBundle based on name.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName)
    {
        return getService().getBundle(bundleName);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName,
                                           String languageHeader)
    {
        return getService().getBundle(bundleName, languageHeader);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header in HttpServletRequest.
     *
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(HttpServletRequest req)
    {
        return getService().getBundle(req);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header in HttpServletRequest.
     *
     * @param bundleName Name of bundle.
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName,
                                           HttpServletRequest req)
    {
        return getService().getBundle(bundleName, req);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName, Locale locale)
    {
        return getService().getBundle(bundleName, locale);
    }

    /**
     * This method sets the name of the default bundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    public static void setBundle(String defaultBundle)
    {
        getService().setBundle(defaultBundle);
    }

    /**
     * Attempts to pull the <code>Accept-Language</code> header out of
     * the HttpServletRequest object and then parse it.  If the header
     * is not present, it will return a null Locale.
     *
     * @param req HttpServletRequest.
     * @return A Locale.
     */
    public static Locale getLocale(HttpServletRequest req)
    {
        return getService().getLocale(req);
    }

    /**
     * This method parses the <code>Accept-Language</code> header and
     * attempts to create a Locale out of it.
     *
     * @param languageHeader A String with the language header.
     * @return A Locale.
     */
    public static Locale getLocale(String languageHeader)
    {
        return getService().getLocale(languageHeader);
    }

    /**
     * @see org.apache.turbine.services.localization.LocalizationService#getDefaultBundle()
     */
    public static String getDefaultBundleName()
    {
        return getService().getDefaultBundleName();
    }

    /**
     * Gets the <code>LocalizationService</code> implementation.
     *
     * @return the LocalizationService implementation.
     */
    protected static final LocalizationService getService()
    {
        return (LocalizationService) TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME);
    }

    /**
     * @deprecated Call getString(data.getRequest()) instead.
     */
    public static String getString(RunData data, String key)
    {
        return getBundle(data.getRequest()).getString(key);
    }

    /**
     * @deprecated Call getBundle(bundleName, data.getRequest()) instead.
     */
    public static ResourceBundle getBundle(String bundleName, RunData data)
    {
        return getBundle(bundleName, data.getRequest());
    }
}
