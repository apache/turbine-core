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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.RunData;

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
 * @version $Id$
 */
public class TurbineLocalizationService
        extends TurbineBaseService
        implements LocalizationService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineLocalizationService.class);

    /**
     * The value to pass to <code>MessageFormat</code> if a
     * <code>null</code> reference is passed to <code>format()</code>.
     */
    private static final Object[] NO_ARGS = new Object[0];

    /**
     * Bundle name keys a Map of the ResourceBundles in this
     * service (which is in turn keyed by Locale).
     * Key=bundle name
     * Value=Hashtable containing ResourceBundles keyed by Locale.
     */
    private Map bundles = null;

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
        bundles = new HashMap();
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = Turbine.getConfiguration();

        initBundleNames(null);

        Locale jvmDefault = Locale.getDefault();

        defaultLanguage = conf.getString("locale.default.language",
                jvmDefault.getLanguage()).trim();
        defaultCountry = conf.getString("locale.default.country",
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
        Configuration conf = Turbine.getConfiguration();
        bundleNames = conf.getStringArray("locale.default.bundles");
        String name = conf.getString("locale.default.bundle");

        if (name != null && name.length() > 0)
        {
            // Using old-style single bundle name property.
            if (bundleNames == null || bundleNames.length <= 0)
            {
                bundleNames = new String[] {name};
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
     * Retrieves the default language (specified in the config file).
     */
    public String getDefaultLanguage()
    {
        return defaultLanguage;
    }

    /**
     * Retrieves the default country (specified in the config file).
     */
    public String getDefaultCountry()
    {
        return defaultCountry;
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
     * @see org.apache.turbine.services.localization.LocalizationService#getBundleNames()
     */
    public String[] getBundleNames()
    {
        return (String []) bundleNames.clone();
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
     * @param bundleName Name of bundle (or <code>null</code> for the
     * default bundle).
     * @param locale The locale (or <code>null</code> for the locale
     * indicated by the default language and country).
     * @return A localized ResourceBundle.
     */
    public ResourceBundle getBundle(String bundleName, Locale locale)
    {
        // Assure usable inputs.
        bundleName = (StringUtils.isEmpty(bundleName) ? getDefaultBundleName() : bundleName.trim());
        if (locale == null)
        {
            locale = getLocale((String) null);
        }

        // Find/retrieve/cache bundle.
        ResourceBundle rb = null;
        Map bundlesByLocale = (Map) bundles.get(bundleName);
        if (bundlesByLocale != null)
        {
            // Cache of bundles by locale for the named bundle exists.
            // Check the cache for a bundle corresponding to locale.
            rb = (ResourceBundle) bundlesByLocale.get(locale);

            if (rb == null)
            {
                // Not yet cached.
                rb = cacheBundle(bundleName, locale);
            }
        }
        else
        {
            rb = cacheBundle(bundleName, locale);
        }
        return rb;
    }

    /**
     * Caches the named bundle for fast lookups.  This operation is
     * relatively expesive in terms of memory use, but is optimized
     * for run-time speed in the usual case.
     *
     * @exception MissingResourceException Bundle not found.
     */
    private synchronized ResourceBundle cacheBundle(String bundleName,
                                                    Locale locale)
        throws MissingResourceException
    {
        Map bundlesByLocale = (HashMap) bundles.get(bundleName);
        ResourceBundle rb = (bundlesByLocale == null ? null :
                             (ResourceBundle) bundlesByLocale.get(locale));

        if (rb == null)
        {
            bundlesByLocale = (bundlesByLocale == null ? new HashMap(3) :
                               new HashMap(bundlesByLocale));
            try
            {
                rb = ResourceBundle.getBundle(bundleName, locale);
            }
            catch (MissingResourceException e)
            {
                rb = findBundleByLocale(bundleName, locale, bundlesByLocale);
                if (rb == null)
                {
                    throw (MissingResourceException) e.fillInStackTrace();
                }
            }

            if (rb != null)
            {
                // Cache bundle.
                bundlesByLocale.put(rb.getLocale(), rb);

                Map bundlesByName = new HashMap(bundles);
                bundlesByName.put(bundleName, bundlesByLocale);
                this.bundles = bundlesByName;
            }
        }
        return rb;
    }

    /**
     * <p>Retrieves the bundle most closely matching first against the
     * supplied inputs, then against the defaults.</p>
     *
     * <p>Use case: some clients send a HTTP Accept-Language header
     * with a value of only the language to use
     * (i.e. "Accept-Language: en"), and neglect to include a country.
     * When there is no bundle for the requested language, this method
     * can be called to try the default country (checking internally
     * to assure the requested criteria matches the default to avoid
     * disconnects between language and country).</p>
     *
     * <p>Since we're really just guessing at possible bundles to use,
     * we don't ever throw <code>MissingResourceException</code>.</p>
     */
    private ResourceBundle findBundleByLocale(String bundleName, Locale locale,
                                              Map bundlesByLocale)
    {
        ResourceBundle rb = null;
        if ( !StringUtils.isNotEmpty(locale.getCountry()) &&
             defaultLanguage.equals(locale.getLanguage()) )
        {
            /*
              log.debug("Requested language '" + locale.getLanguage() +
              "' matches default: Attempting to guess bundle " +
              "using default country '" + defaultCountry + '\'');
            */
            Locale withDefaultCountry = new Locale(locale.getLanguage(),
                                                   defaultCountry);
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultCountry);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultCountry);
            }
        }
        else if ( !StringUtils.isNotEmpty(locale.getLanguage()) &&
                  defaultCountry.equals(locale.getCountry()) )
        {
            Locale withDefaultLanguage = new Locale(defaultLanguage,
                                                    locale.getCountry());
            rb = (ResourceBundle) bundlesByLocale.get(withDefaultLanguage);
            if (rb == null)
            {
                rb = getBundleIgnoreException(bundleName, withDefaultLanguage);
            }
        }

        if (rb == null && !defaultLocale.equals(locale))
        {
            rb = getBundleIgnoreException(bundleName, defaultLocale);
        }

        return rb;
    }

    /**
     * Retrieves the bundle using the
     * <code>ResourceBundle.getBundle(String, Locale)</code> method,
     * returning <code>null</code> instead of throwing
     * <code>MissingResourceException</code>.
     */
    private final ResourceBundle getBundleIgnoreException(String bundleName,
                                                          Locale locale)
    {
        try
        {
            return ResourceBundle.getBundle(bundleName, locale);
        }
        catch (MissingResourceException ignored)
        {
            return null;
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
                    bundleNames = new String[] {defaultBundle};
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
            String mesg = LocalizationService.SERVICE_NAME +
                " noticed missing resource: " +
                "bundleName=" + bundleName + ", locale=" + loc +
                ", key=" + key;
            log.debug(mesg);
            // Text not found in requested or default bundles.
            throw new MissingResourceException(mesg, bundleName, key);
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
