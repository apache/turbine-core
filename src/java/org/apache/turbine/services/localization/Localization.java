package org.apache.turbine.services.localization;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import org.apache.turbine.services.Service;
import org.apache.turbine.services.ServiceBroker;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.resources.TurbineResources;
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
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public abstract class Localization
{
    /**
     * Pulls a string out of the LocalizationService with the default
     * locale values of what is defined in the
     * TurbineResources.properties file for the
     * locale.default.language and locale.default.country property
     * values.  If those cannot be found, then en/US is used.
     *
     * @param str Name of string.
     * @return A localized String.
     */
    public static String getString ( String str )
    {
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle()
            .getString ( str );
    }

    /**
     * Pulls a string out of the LocalizationService and attempts to
     * determine the Locale by the Accept-Language header.  If that
     * header is not present, it will fall back to using the locale
     * values of what is defined in the TurbineResources.properties
     * file for the locale.default.language and locale.default.country
     * property values.  If those cannot be found, then en/US is used.
     *
     * @param data Turbine information.
     * @param str Name of string.
     * @return A localized String.
     */
    public static String getString ( RunData data,
                                     String str )
    {
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle(data)
            .getString ( str );
    }

    /**
     * Convenience method that pulls a localized string off the LocalizationService
     * using the default ResourceBundle name defined in the TurbineResources.properties
     * file and the specified language name in ISO format. 
     *
     * @param str Name of string.
     * @param lang Desired language for the localized string.
     * @return A localized string.
     */
       
    public static String getString (String str, String lang) 
    {
	String bundleName = TurbineResources.getString("locale.default.bundle", "");
	return Localization.getBundle (bundleName, new Locale (lang, "")).getString (str);
    }

    /**
     * Convenience method to get a ResourceBundle based on name.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName)
    {
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle(bundleName);
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
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle(bundleName, languageHeader);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header in RunData.
     *
     * @param bundleName Name of bundle.
     * @param data Turbine information.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName,
                                           RunData data)
    {
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle(bundleName, data);
    }

    /**
     * Convenience method to get a ResourceBundle based on name and
     * Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public static ResourceBundle getBundle(String bundleName,
                                           Locale locale)
    {
        return ((LocalizationService)TurbineServices.getInstance()
                .getService(LocalizationService.SERVICE_NAME))
            .getBundle(bundleName, locale);
    }

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    public static void setBundle(String defaultBundle)
    {
        ((LocalizationService)TurbineServices.getInstance()
         .getService(LocalizationService.SERVICE_NAME))
            .setBundle(defaultBundle);
    }
}
