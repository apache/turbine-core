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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Parses the HTTP <code>Accept-Language</code> header as per section
 * 14.4 of RFC 2068 (HTTP 1.1 header field definitions).
 * 
 * @deprecated Use the Fulcrum Localization component instead.
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public class LocaleTokenizer
        implements Iterator
{
    /**
     * Separates elements of the <code>Accept-Language</code> HTTP
     * header.
     */
    private static final String LOCALE_SEPARATOR = ",";

    /**
     * Separates locale from quality within elements.
     */
    private static final char QUALITY_SEPARATOR = ';';

    /**
     * The default quality value for an <code>AcceptLanguage</code>
     * object.
     */
    private static final Float DEFAULT_QUALITY = new Float(1.0f);

    /**
     * The parsed locales.
     */
    private ArrayList locales = new ArrayList(3);

    /**
     * Parses the <code>Accept-Language</code> header.
     *
     * @param header The <code>Accept-Language</code> header
     * (i.e. <code>en, es;q=0.8, zh-TW;q=0.1</code>).
     */
    public LocaleTokenizer(String header)
    {
        StringTokenizer tok = new StringTokenizer(header, LOCALE_SEPARATOR);
        while (tok.hasMoreTokens())
        {
            AcceptLanguage acceptLang = new AcceptLanguage();
            String element = tok.nextToken().trim();
            int index;

            // Record and cut off any quality value that comes after a
            // semi-colon.
            if ((index = element.indexOf(QUALITY_SEPARATOR)) != -1)
            {
                String q = element.substring(index);
                element = element.substring(0, index);
                if ((index = q.indexOf('=')) != -1)
                {
                    try
                    {
                        acceptLang.quality =
                                Float.valueOf(q.substring(index + 1));
                    }
                    catch (NumberFormatException useDefault)
                    {
                    }
                }
            }

            element = element.trim();

            // Create a Locale from the language.  A dash may separate the
            // language from the country.
            if ((index = element.indexOf('-')) == -1)
            {
                // No dash means no country.
                acceptLang.locale = new Locale(element, "");
            }
            else
            {
                acceptLang.locale = new Locale(element.substring(0, index),
                        element.substring(index + 1));
            }

            locales.add(acceptLang);
        }

        // Sort by quality in descending order.
        Collections.sort(locales, Collections.reverseOrder());
    }

    /**
     * @return Whether there are more locales.
     */
    public boolean hasNext()
    {
        return !locales.isEmpty();
    }

    /**
     * Creates a <code>Locale</code> from the next element of the
     * <code>Accept-Language</code> header.
     *
     * @return The next highest-rated <code>Locale</code>.
     * @throws NoSuchElementException No more locales.
     */
    public Object next()
    {
        if (locales.isEmpty())
        {
            throw new NoSuchElementException();
        }
        return ((AcceptLanguage) locales.remove(0)).locale;
    }

    /**
     * Not implemented.
     */
    public final void remove()
    {
        throw new UnsupportedOperationException(getClass().getName() +
                " does not support remove()");
    }

    /**
     * Struct representing an element of the HTTP
     * <code>Accept-Language</code> header.
     */
    private class AcceptLanguage implements Comparable
    {
        /**
         * The language and country.
         */
        Locale locale;

        /**
         * The quality of our locale (as values approach
         * <code>1.0</code>, they indicate increased user preference).
         */
        Float quality = DEFAULT_QUALITY;

        public final int compareTo(Object acceptLang)
        {
            return quality.compareTo(((AcceptLanguage) acceptLang).quality);
        }
    }
}
