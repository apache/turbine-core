package org.apache.turbine.services.mimetype;

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

import java.io.File;
import java.util.Locale;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.mimetype.util.MimeType;

/**
 * This is a static accessor to MIME types and charsets.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public abstract class TurbineMimeTypes
{
    /**
     * Gets the MIME content type for a file as a string.
     *
     * @param file the file.
     * @return the MIME type string.
     */
    public static String getContentType(File file)
    {
        return getService().getContentType(file);
    }

    /**
     * Gets the MIME content type for a named file as a string.
     *
     * @param name the name of the file.
     * @return the MIME type string.
     */
    public static String getContentType(String name)
    {
        return getService().getContentType(name);
    }

    /**
     * Gets the MIME content type for a file name extension as a string.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type string.
     */
    public static String getContentType(String ext,
                                        String def)
    {
        return getService().getContentType(ext,def);
    }

    /**
     * Gets the MIME content type for a file.
     *
     * @param file the file.
     * @return the MIME type.
     */
    public static MimeType getMimeContentType(File file)
    {
        return getService().getMimeContentType(file);
    }

    /**
     * Gets the MIME content type for a named file.
     *
     * @param name the name of the file.
     * @return the MIME type.
     */
    public static MimeType getMimeContentType(String name)
    {
        return getService().getMimeContentType(name);
    }

    /**
     * Gets the MIME content type for a file name extension.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type.
     */
    public static MimeType getMimeContentType(String ext,
                                              String def)
    {
        return getService().getMimeContentType(ext,def);
    }

    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param mime the MIME type.
     * @return the file name extension or null.
     */
    public static String getDefaultExtension(MimeType mime)
    {
        return getService().getDefaultExtension(mime);
    }

    /**
     * Gets the charset for a locale. First a locale specific charset
     * is searched for, then a country specific one and lastly a language
     * specific one. If none is found, the default charset is returned.
     *
     * @param locale the locale.
     * @return the charset.
     */
    public static String getCharSet(Locale locale)
    {
        return getService().getCharSet(locale);
    }

    /**
     * Gets the charset for a locale with a variant. The search
     * is performed in the following order:
     * "lang"_"country"_"variant"="charset",
     * _"counry"_"variant"="charset",
     * "lang"__"variant"="charset",
     * __"variant"="charset",
     * "lang"_"country"="charset",
     * _"country"="charset",
     * "lang"="charset".
     * If nothing of the above is found, the default charset is returned.
     *
     * @param locale the locale.
     * @param variant a variant field.
     * @return the charset.
     */
    public static String getCharSet(Locale locale,
                                    String variant)
    {
        return getService().getCharSet(locale,variant);
    }

    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @return the found charset or the default one.
     */
    public static String getCharSet(String key)
    {
        return getService().getCharSet(key);
    }

    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @param def the default charset if none is found.
     * @return the found charset or the given default.
     */
    public static String getCharSet(String key,
                                    String def)
    {
        return getService().getCharSet(key,def);
    }

    /**
     * Gets the MIME type service implementation.
     *
     * @return the MIME type service implementation.
     */
    protected static MimeTypeService getService()
    {
        return (MimeTypeService) TurbineServices.
            getInstance().getService(MimeTypeService.SERVICE_NAME);
    }
}
