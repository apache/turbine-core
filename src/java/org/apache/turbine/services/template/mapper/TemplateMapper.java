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

/**
 * To separate out the various map and search policies for class
 * names and template names, we use classes that implement this
 * interface.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface TemplateMapper
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
