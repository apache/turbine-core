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
import java.util.MissingResourceException;
import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.util.RunData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.StringUtils;

/**
 * <p>This class is the single point of access to all localization
 * resources.  It caches different ResourceBundles for different
 * Locales.</p>
 *
 * <p>Usage example:</p>
 *
 * <blockquote><code><pre>
 * LocalizationService ls = (LocalizationService) TurbineServices
 *     .getInstance().getService(LocalizationService.SERVICE_NAME);
 * </pre></code></blockquote>
 *
 * <p>Then call one of four methods to retrieve a ResourceBundle:
 *
 * <ul>
 * <li>getBundle("MyBundleName")</li>
 * <li>getBundle("MyBundleName", httpAcceptLanguageHeader)</li>
 * <li>etBundle("MyBundleName", HttpServletRequest)</li>
 * <li>getBundle("MyBundleName", Locale)</li>
 * <li>etc.</li>
 * </ul></p>
 *
 * @author <a href="mailto:jm@mediaphil.de">Jonas Maurus</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:novalidemail@foo.com">Frank Y. Kim</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 */
public class TurbineLocalizationService
    extends TurbineBaseService
    implements LocalizationService
{
    /** Logging */
    private static Log log = LogFactory.getLog(LocalizationTool.class);

    /**
     * The ResourceBundles in this service.
     * Key=bundle name
     * Value=Hashtable containing ResourceBundles keyed by Locale.
     */
    private Hashtable bundles = null;

    /**
     * The list of default bundles to search.
     */
    private String[] bundleNames = null;

    /**
     * The name of the default locale to use (includes language and
     * country).
     */
    private Locale defaultLocale = null;

    /** The name of the default language to use. */
    private String defaultLanguage = null;

    /** The name of the default country to use. */
    private String defaultCountry = null;

    /**
     * Constructor.
     */
    public TurbineLocalizationService()
    {
        bundles = new Hashtable();
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        initBundleNames(null);

        Locale jvmDefault = Locale.getDefault();
        defaultLanguage = TurbineResources
            .getString("locale.default.language",
                       jvmDefault.getLanguage()).trim();
        defaultCountry = TurbineResources
            .getString("locale.default.country",
                       jvmDefault.getCountry()).trim();
        defaultLocale = new Locale(defaultLanguage, defaultCountry);
        setInit(true);
    }


    /**
     * Initialize list of default bundle names.
     *
     * @param ignored Ignored.
     */
    protected void initBundleNames(String[] ignored)
    {
        bundleNames =
            TurbineResources.getStringArray("locale.default.bundles");
        String name = TurbineResources.getString("locale.default.bundle");

        if (name != null && name.length() > 0)
        {
            // Using old-style single bundle name property.
            if (bundleNames == null || bundleNames.length <= 0)
            {
                bundleNames = new String[] { name };
            }
            else
            {
                // Prepend "default" bundle name.
                String[] array = new String[bundleNames.length + 1];
                array[0] = name;
                System.arraycopy(bundleNames, 0, array, 1, bundleNames.length);
                bundleNames = array;
            }
        }
        if (bundleNames == null)
        {
            bundleNames = new String[0];
        }
    }

    /**
     * Retrieves the name of the default bundle (as specified in the
     * config file).
     * @see org.apache.turbine.services.localization.LocalizationService#getDefaultBundleName()
     */
    public String getDefaultBundleName()
    {
        return (bundleNames.length > 0 ? bundleNames[0] : "");
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
        return getBundle(getDefaultBundleName(), (Locale) null);
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
        return getBundle(bundleName, (Locale) null);
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
    public ResourceBundle getBundle(String bundleName, String languageHeader)
    {
        return getBundle(bundleName, getLocale(languageHeader));
    }

    /**
     * This method returns a ResourceBundle given the Locale
     * information supplied in the HTTP "Accept-Language" header which
     * is stored in HttpServletRequest.
     *
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(HttpServletRequest req)
    {
        return getBundle(getDefaultBundleName(), getLocale(req));
    }

    /**
     * This method returns a ResourceBundle given the bundle name and
     * the Locale information supplied in the HTTP "Accept-Language"
     * header which is stored in HttpServletRequest.
     *
     * @param bundleName Name of the bundle to use if the request's
     * locale cannot be resolved.
     * @param req HttpServletRequest.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, HttpServletRequest req)
    {
        return getBundle(bundleName, getLocale(req));
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
        return getBundle(getDefaultBundleName(), getLocale(data.getRequest()));
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
    public ResourceBundle getBundle(String bundleName, RunData data)
    {
        return getBundle(bundleName, getLocale(data.getRequest()));
    }

    /**
     * This method returns a ResourceBundle for the given bundle name
     * and the given Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, Locale locale)
    {
        // Assure usable inputs.
        bundleName = (bundleName == null ? getDefaultBundleName() : bundleName.trim());
        if (locale == null)
        {
            locale = getLocale((String) null);
        }

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
     * This method sets the name of the first bundle in the search
     * list (the "default" bundle).
     *
     * @param defaultBundle Name of default bundle.
     */
    public void setBundle(String defaultBundle)
    {
        if (bundleNames.length > 0)
        {
            bundleNames[0] = defaultBundle;
        }
        else
        {
            synchronized (this)
            {
                if (bundleNames.length <= 0)
                {
                    bundleNames = new String[] { defaultBundle };
                }
            }
        }
    }

    /**
     * @see org.apache.turbine.services.localization.LocalizationService#getLocale(HttpServletRequest)
     */
    public final Locale getLocale(HttpServletRequest req)
    {
        return getLocale(req.getHeader(ACCEPT_LANGUAGE));
    }

    /**
     * @see org.apache.turbine.services.localization.LocalizationService#getLocale(String)
     */
    public Locale getLocale(String header)
    {
        if (!StringUtils.isEmpty(header))
        {
            LocaleTokenizer tok = new LocaleTokenizer(header);
            if (tok.hasNext())
            {
                return (Locale) tok.next();
            }
        }

        // Couldn't parse locale.
        return defaultLocale;
    }


    /**
     * @exception MissingResourceException Specified key cannot be matched.
     * @see org.apache.turbine.services.localization.LocalizationService#getString(String, Locale, String)
     */
    public String getString(String bundleName, Locale locale, String key)
    {
        String value = null;

        if (locale == null)
        {
            locale = getLocale((String) null);
        }

        // Look for text in requested bundle.
        ResourceBundle rb = getBundle(bundleName, locale);
        value = getStringOrNull(rb, key);

        // Look for text in list of default bundles.
        if (value == null && bundleNames.length > 0)
        {
            String name;
            for (int i = 0; i < bundleNames.length; i++)
            {
                name = bundleNames[i];
                //System.out.println("getString(): name=" + name +
                //                   ", locale=" + locale + ", i=" + i);
                if (!name.equals(bundleName))
                {
                    rb = getBundle(name, locale);
                    value = getStringOrNull(rb, key);
                    if (value != null)
                    {
                        locale = rb.getLocale();
                        break;
                    }
                }
            }
        }

        if (value == null)
        {
            String loc = locale.toString();
            log.debug(LocalizationService.SERVICE_NAME +
                           " noticed missing resource: " +
                           "bundleName=" + bundleName + ", locale=" + loc +
                           ", key=" + key);
            // Text not found in requested or default bundles.
            throw new MissingResourceException(bundleName, loc, key);
        }

        return value;
    }

    /**
     * Gets localized text from a bundle if it's there.  Otherwise,
     * returns <code>null</code> (ignoring a possible
     * <code>MissingResourceException</code>).
     */
    protected final String getStringOrNull(ResourceBundle rb, String key)
    {
        if (rb != null)
        {
            try
            {
                return rb.getString(key);
            }
            catch (MissingResourceException ignored)
            {
            }
        }
        return null;
    }

}
