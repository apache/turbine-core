package org.apache.turbine.services.template.mapper;

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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.template.TurbineTemplate;
import org.apache.turbine.services.template.TemplateEngineService;

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
    private Map templateCache = null;

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
            templateCache = new HashMap(cacheSize);
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
            return (String) templateCache.get(template);
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
