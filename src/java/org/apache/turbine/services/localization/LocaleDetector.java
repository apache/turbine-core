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
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.Turbine;
import org.apache.turbine.util.RunData;

/**
 * This class returns a Locale object based on the HTTP
 * Accept-Language header.
 *
 * This class is based on examples from Jason Hunter's book <i>Java
 * Servlet Programming</i>.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * @deprecated Use LocaleTokenizer instead.
 */
public class LocaleDetector
{
    /**
     * Attempts to pull the "Accept-Language" header out of the
     * HttpServletRequest object and then parse it.  If the header is
     * not present, it will return a null Locale.
     *
     * @param data Turbine information.
     * @return A Locale.
     */
    public static Locale getLocale(RunData data)
    {
        String header = data.getRequest().getHeader("Accept-Language");
        if (header == null || header.length() == 0)
            return null;
        return getLocale(header);
    }

    /**
     * This method parses the Accept-Language header and attempts to
     * create a Locale out of it.
     *
     * @param languageHeader A String with the language header.
     * @return A Locale.
     */
    public static Locale getLocale(String languageHeader)
    {
        Configuration conf = Turbine.getConfiguration();

        Locale locale = null;

        // return a "default" locale
        if (languageHeader == null ||
                languageHeader.trim().equals(""))
        {
            return new Locale(
                    conf.getString("locale.default.language", "en"),
                    conf.getString("locale.default.country", "US"));
        }

        // The HTTP Accept-Header is something like
        //
        // "en, es;q=0.8, zh-TW;q=0.1"
        StringTokenizer tokenizer = new StringTokenizer(languageHeader, ",");

        // while ( tokenizer.hasMoreTokens() )
        // {
        String language = tokenizer.nextToken();
        // This should never be true but just in case
        // if ( !language.trim().equals("") )
        return getLocaleForLanguage(language.trim());
        // }
    }

    /**
     * This method creates a Locale from the language.
     *
     * @param language A String with the language.
     * @return A Locale.
     */
    private static Locale getLocaleForLanguage(String language)
    {
        Locale locale;
        int semi, dash;

        // Cut off any q-value that comes after a semicolon.
        if ((semi = language.indexOf(';')) != -1)
        {
            language = language.substring(0, semi);
        }

        language = language.trim();

        // Create a Locale from the language.  A dash may separate the
        // language from the country.
        if ((dash = language.indexOf('-')) == -1)
        {
            // No dash means no country.
            locale = new Locale(language, "");
        }
        else
        {
            locale = new Locale(language.substring(0, dash),
                    language.substring(dash + 1));
        }

        return locale;
    }
}
