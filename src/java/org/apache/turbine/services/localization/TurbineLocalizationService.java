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

import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;

/**
 * This class has been added 17.01.2000 by [jm],mediaphil digital
 * media.  Please contact me if you have any questions, which ARE NOT
 * ANSWERED by the Java Localization/Internationalization
 * Documentation.
 *
 * This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.
 *
 * <p>
 *
 * Usage example:<br>
 * LocalizationService ls =<br>
 *     (LocalizationService)TurbineServices<br>
 *         .getInstance()<br>
 *         .getService(LocalizationService.SERVICE_NAME);<br>
 *
 * <p>
 *
 * Then call one of four methods to retrieve a ResourceBundle:
 *
 * <br>
 * - getBundle("MyBundleName")<br>
 * - getBundle("MyBundleName", httpAcceptLanguageHeader)<br>
 * - getBundle("MyBundleName", RunData)<br>
 * - getBundle("MyBundleName", Locale)<br>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:novalidemail@foo.com">Frank Y. Kim</a>
 * @version $Id$
 */
public class TurbineLocalizationService
    extends TurbineBaseService
    implements LocalizationService
{
    /**
     * The ResourceBundles in this service.
     * Key=bundle name
     * Value=Hashtable containing ResourceBundles keyed by Locale.
     */
    private static Hashtable bundles = null;

    /** The name of the default bundle to use. */
    private static String defaultBundle = null;

    /**
     * Constructor.
     */
    public TurbineLocalizationService()
    {
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        bundles = new Hashtable();
        defaultBundle = TurbineResources.getString("locale.default.bundle");
        setInit(true);
    }

    /**
     * This method returns a ResourceBundle given the bundle name
     * "DEFAULT" and the default Locale information supplied in
     * TurbineProperties.
     *
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle()
    {
        return getBundle( defaultBundle );
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the default Locale information supplied in TurbineProperties.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName)
    {
        String language =
            TurbineResources.getString("locale.default.language", "en").trim();
        String country =
            TurbineResources.getString("locale.default.country", "US").trim();

        return getBundle( bundleName,
                          new Locale(language, country) );
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    String languageHeader)
    {
        return getBundle( bundleName,
                          LocaleDetector.getLocale(languageHeader) );
    }

    /**
     * This method returns a ResourceBundle given the Locale
     * information supplied in the HTTP "Accept-Language" header which
     * is stored in RunData.
     *
     * @param data Turbine information.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(RunData data)
    {
        Locale locale = LocaleDetector.getLocale(data);
        if (locale == null)
            return getBundle();
        return getBundle( defaultBundle, locale );
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header which is stored in RunData.
     *
     * @param bundleName Name of bundle.
     * @param data Turbine information.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    RunData data)
    {
        Locale locale = LocaleDetector.getLocale(data);
        if (locale == null)
            return getBundle(bundleName);
        return getBundle( bundleName, locale );
    }

    /**
     * This method returns a ResourceBundle for the given bundle name
     * and the given Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName,
                                    Locale locale)
    {
        bundleName = bundleName.trim();

        if ( bundles.containsKey(bundleName) )
        {
            Hashtable locales = (Hashtable)bundles.get(bundleName);

            if ( locales.containsKey(locale) )
            {
                return (ResourceBundle)locales.get(locale);
            }
            else
            {
                // Try to create a ResourceBundle for this Locale.
                ResourceBundle rb = ResourceBundle.getBundle(bundleName,
                                                             locale);

                // Cache the ResourceBundle in memory.
                locales.put( rb.getLocale(), rb );

                return rb;
            }
        }
        else
        {
            // Try to create a ResourceBundle for this Locale.
            ResourceBundle rb = ResourceBundle.getBundle(bundleName,
                                                         locale);

            // Cache the ResourceBundle in memory.
            Hashtable ht = new Hashtable();
            ht.put( locale, rb );

            // Can't call getLocale(), because that is jdk2.  This
            // needs to be changed back, since the above approach
            // caches extra Locale and Bundle objects.
            // ht.put( rb.getLocale(), rb );

            bundles.put( bundleName, ht );

            return rb;
        }
    }

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    public void setBundle(String defaultBundle)
    {
        this.defaultBundle = defaultBundle;
    }
}
