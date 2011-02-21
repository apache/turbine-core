package org.apache.turbine.services.template.mapper;


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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.template.TemplateEngineService;
import org.apache.turbine.services.template.TurbineTemplate;

/**
 * A base class for the various mappers which contains common
 * code.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class BaseMapper
{
    /** True if this mapper should cache template -> name mappings */
    private boolean useCache = false;

    /** Default cache size. Just a number out of thin air. Will be set at init time */
    private int cacheSize = 5;

    /** The internal template -> name mapping cache */
    private Map<String, String> templateCache = null;

    /** The name of the default property to pull from the Template Engine Service if the default is requested */
    protected String defaultProperty;

    /** The separator used to concatenate the result parts for this mapper. */
    protected char separator;

    // Note: You might _not_ use TurbineTemplate.<xxx> in the C'tor and the init method.
    // The service isn't configured yet and if you do, the Broker will try to reinit the
    // Service which leads to an endless loop and a deadlock.

    /**
     * Default C'tor. If you use this C'tor, you must use
     * the bean setter to set the various properties needed for
     * this mapper before first usage.
     */
    public BaseMapper()
    {
        // empty
    }

    /**
     * Get the CacheSize value.
     * @return the CacheSize value.
     */
    public int getCacheSize()
    {
        return cacheSize;
    }

    /**
     * Set the CacheSize value.
     * @param cacheSize The new CacheSize value.
     */
    public void setCacheSize(int cacheSize)
    {
        this.cacheSize = cacheSize;
    }

    /**
     * Get the UseCache value.
     * @return the UseCache value.
     */
    public boolean isUseCache()
    {
        return useCache;
    }

    /**
     * Set the UseCache value.
     * @param newUseCache The new UseCache value.
     */
    public void setUseCache(boolean useCache)
    {
        this.useCache = useCache;
    }

    /**
     * Get the DefaultProperty value.
     * @return the DefaultProperty value.
     */
    public String getDefaultProperty()
    {
        return defaultProperty;
    }

    /**
     * Set the DefaultProperty value.
     * @param defaultProperty The new DefaultProperty value.
     */
    public void setDefaultProperty(String defaultProperty)
    {
        this.defaultProperty = defaultProperty;
    }

    /**
     * Get the Separator value.
     * @return the Separator value.
     */
    public char getSeparator()
    {
        return separator;
    }

    /**
     * Set the Separator value.
     * @param separator The new Separator value.
     */
    public void setSeparator(char separator)
    {
        this.separator = separator;
    }

    /**
     * Initializes the Mapper. Must be called before the mapper might be used.
     */
    public void init()
    {
        if (useCache)
        {
            templateCache = new HashMap<String, String>(cacheSize);
        }
    }

    /**
     * Returns the default name for the passed Template.
     * If the passed template has no extension,
     * the default extension is assumed.
     * If the template is empty, the default template is
     * returned.
     *
     * @param template The template name.
     *
     * @return the mapped default name for the template.
     */

    public String getDefaultName(String template)
    {
        // We might get a Name without an extension passed. If yes, then we use
        // the Default extension

        TemplateEngineService tes
            = TurbineTemplate.getTemplateEngineService(template);

        if (StringUtils.isEmpty(template) || (tes == null))
        {
            return TurbineTemplate.getDefaultTemplate();
        }

        String defaultName = (String) tes.getTemplateEngineServiceConfiguration()
            .get(defaultProperty);

        return StringUtils.isEmpty(defaultName)
            ? TurbineTemplate.getDefaultTemplate()
            : defaultName;
    }

    /**
     * Return the first match name for the given template name.
     *
     * @param template The template name.
     *
     * @return The first matching class or template name.
     */
    public String getMappedName(String template)
    {
        if (StringUtils.isEmpty(template))
        {
            return null;
        }

        if (useCache && templateCache.containsKey(template))
        {
            return templateCache.get(template);
        }

        String res = doMapping(template);

        // Never cache "null" return values and empty Strings.
        if (useCache && StringUtils.isNotEmpty(res))
        {
            templateCache.put(template, res);
        }

        return res;
    }

    /**
     * The actual mapping implementation class. It
     * is guaranteed that never an empty or null
     * template name is passed to it. This might
     * return null.
     *
     * @param template The template name.
     * @return The mapped class or template name.
     */
    public abstract String doMapping(String template);
}
