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
