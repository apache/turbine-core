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
import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.services.Service;

/**
 * Provides localization functionality using the interface provided by
 * <code>ResourceBundle</code>.
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
public interface LocalizationService
    extends Service
{
    /**
     * The name of this service.
     */
    static final String SERVICE_NAME = "LocalizationService";

    /**
     * A constant for the HTTP <code>Accept-Language</code> header.
     */
    static final String ACCEPT_LANGUAGE = "Accept-Language";

    /**
     * Retrieves the name of the default bundle (as specified in the
     * config file), or the first in the list if there are more than
     * one.
     */
    String getDefaultBundleName();

    /**
     * Convenience method to get a default ResourceBundle.
     *
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle();

    /**
     * Convenience method to get a ResourceBundle based on name.
     *
     * @param bundleName Name of bundle.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header.
     *
     * @param bundleName Name of bundle.
     * @param languageHeader A String with the language header.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, String languageHeader);

    /**
     * Convenience method to get a ResourceBundle based on HTTP
     * Accept-Language header in HttpServletRequest.
     *
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle (HttpServletRequest req);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * HTTP Accept-Language header in HttpServletRequest.
     *
     * @param bundleName Name of bundle.
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, HttpServletRequest req);

    /**
     * Convenience method to get a ResourceBundle based on name and
     * Locale.
     *
     * @param bundleName Name of bundle.
     * @param locale A Locale.
     * @return A localized ResourceBundle.
     */
    ResourceBundle getBundle(String bundleName, Locale locale);

    /**
     * Attempts to pull the <code>Accept-Language</code> header out of
     * the <code>HttpServletRequest</code> object and then parse it.
     * If the header is not present, it will return a
     * <code>null</code> <code>Locale</code>.
     *
     * @param req The HTTP request to parse the
     * <code>Accept-Language</code> of.
     * @return The parsed locale.
     */
    Locale getLocale(HttpServletRequest req);

    /**
     * This method parses the <code>Accept-Language</code> header and
     * attempts to create a <code>Locale</code> out of it.
     *
     * @param languageHeader The <code>Accept-Language</code> HTTP
     * header.
     * @return The parsed locale.
     */
    Locale getLocale(String languageHeader);

    /**
     * This method sets the name of the defaultBundle.
     *
     * @param defaultBundle Name of default bundle.
     */
    void setBundle(String defaultBundle);


    /**
     * Tries very hard to return a value, looking first in the
     * specified bundle, then searching list of default bundles
     * (giving precedence to earlier bundles over later bundles).
     *
     * @param bundleName Name of the bundle to look in first.
     * @param locale Locale to get text for.
     * @param key Name of the text to retrieve.
     * @return Localized text.
     */
    String getString(String bundleName, Locale locale, String key);

}
