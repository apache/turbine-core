package org.apache.turbine.util.parser;

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

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.TurbineServices;

/**
 * Static helpers for folding fields to upper or lower case
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class ParserUtils
{
    /** Property for setting the URL folding value */
    public static final String URL_CASE_FOLDING_KEY = "url.case.folding";

    /** No folding */
    public static final String URL_CASE_FOLDING_NONE_VALUE  = "none";

    /** Fold to lower case */
    public static final String URL_CASE_FOLDING_LOWER_VALUE = "lower";

    /** Fold to upper case */
    public static final String URL_CASE_FOLDING_UPPER_VALUE = "upper";

    /** No folding set */
    private static final int URL_CASE_FOLDING_UNSET = 0;

    /** Folding set to "no folding" */
    public static final int URL_CASE_FOLDING_NONE  = 1;

    /** Folding set to "lowercase" */
    public static final int URL_CASE_FOLDING_LOWER = 2;

    /** Folding set to "uppercase" */
    public static final int URL_CASE_FOLDING_UPPER = 3;

    /** Logging */
    private static Log log = LogFactory.getLog(ParserUtils.class);

    /** The folding from the properties */
    private static int folding = URL_CASE_FOLDING_UNSET;

    /**
     * Convert a String value according to the url.case.folding property.
     *
     * @param value the String to convert
     *
     * @return a new String. 
     *
     */
    public static String convertAndTrim(String value)
    {
        return convertAndTrim(value, getUrlFolding());
    }        

    /**
     * A static version of the convert method, which
     * trims the string data and applies the conversion specified in
     * the property given by URL_CASE_FOLDING.  It returns a new
     * string so that it does not destroy the value data.
     *
     * @param value A String to be processed.
     * @return A new String converted to lowercase and trimmed.
     */
    public static String convertAndTrim(String value, int fold)
    {
        String tmp = value.trim();

        switch (fold)
        {
        case URL_CASE_FOLDING_NONE:
            {
                break;
            }
        case URL_CASE_FOLDING_LOWER:
            {
                tmp = tmp.toLowerCase();
                break;
            }
        case URL_CASE_FOLDING_UPPER:
            {
                tmp = tmp.toUpperCase();
                break;
            }
        default:
            {
                log.error("Passed " + fold + " as fold rule, which is illegal!");
                break;
            }
        }
        return tmp;
    }

    /** 
     * Gets the folding value from the properties
     *
     * @return The current Folding Value
     */
    public static int getUrlFolding()
    {
        if (folding == URL_CASE_FOLDING_UNSET)
        {
            Configuration conf = TurbineServices.getInstance().getConfiguration();
            String foldString = conf.getString(URL_CASE_FOLDING_KEY,
                                               URL_CASE_FOLDING_NONE_VALUE).toLowerCase();

            folding = URL_CASE_FOLDING_NONE;

            log.debug("Setting folding from " + foldString);
            if (StringUtils.isNotEmpty(foldString))
            {
                if (foldString.equals(URL_CASE_FOLDING_NONE_VALUE))
                {
                    folding = URL_CASE_FOLDING_NONE;
                }
                else if (foldString.equals(URL_CASE_FOLDING_LOWER_VALUE))
                {
                    folding = URL_CASE_FOLDING_LOWER;
                }
                else if (foldString.equals(URL_CASE_FOLDING_UPPER_VALUE))
                {
                    folding = URL_CASE_FOLDING_UPPER;
                }
                else
                {
                    log.error("Got " + foldString + " from " + URL_CASE_FOLDING_KEY + " property, which is illegal!");
                }
            }
        }
        return folding;
    }
}
