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

/**
 * To separate out the various map and search policies for class
 * names and template names, we use classes that implement this
 * interface.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface Mapper
{
    /**
     * Mapper initialization.
     */
    void init();

    /**
     * Get the CacheSize value.
     * @return the CacheSize value.
     */
    int getCacheSize();

    /**
     * Set the CacheSize value.
     * @param cacheSize The new CacheSize value.
     */
    void setCacheSize(int cacheSize);

    /**
     * Get the UseCache value.
     * @return the UseCache value.
     */
    boolean isUseCache();

    /**
     * Set the UseCache value.
     * @param newUseCache The new UseCache value.
     */
    void setUseCache(boolean useCache);

    /**
     * Get the DefaultProperty value.
     * @return the DefaultProperty value.
     */
    String getDefaultProperty();

    /**
     * Set the DefaultProperty value.
     * @param defaultProperty The new DefaultProperty value.
     */
    void setDefaultProperty(String defaultProperty);

    /**
     * Get the Separator value.
     * @return the Separator value.
     */
    char getSeparator();

    /**
     * Set the Separator value.
     * @param separator The new Separator value.
     */
    void setSeparator(char separator);


    /**
     * Returns the default name for the supplied template
     * name. Must never return null.
     *
     * @param template The template name.
     *
     * @return The default name for this template.
     */
    String getDefaultName(String template);

    /**
     * Return the first match name for the given template name.
     * This method might return null if no possible match can
     * be found.
     *
     * @param template The template name.
     *
     * @return The first matching class or template name.
     */
    String getMappedName(String template);
}
