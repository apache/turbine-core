package org.apache.turbine.services.mimetype;

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
        return getService().getContentType(ext, def);
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
        return getService().getMimeContentType(ext, def);
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
        return getService().getCharSet(locale, variant);
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
        return getService().getCharSet(key, def);
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
