
package org.apache.turbine.services.localization;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * @deprecated Use the Fulcrum Localization component instead.
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public class Localization
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
