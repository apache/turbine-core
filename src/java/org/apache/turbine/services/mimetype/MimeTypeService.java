package org.apache.turbine.services.mimetype;

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

import java.io.File;
import java.util.Locale;

import org.apache.turbine.services.Service;
import org.apache.turbine.services.mimetype.util.MimeType;

/**
 * The MimeType Service maintains mappings between MIME types and
 * the corresponding file name extensions, and between locales and
 * character encodings. The mappings are typically defined in
 * properties or files located in user's home directory, Java home
 * directory or the current class jar depending on the implementation.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface MimeTypeService extends Service
{
    /**
     * The name of the service.
     */
    String SERVICE_NAME = "MimeTypeService";

    /**
     * Sets a MIME content type mapping to extensions to the map.
     * The extension is specified by a MIME type name followed
     * by a list of file name extensions separated by a whitespace.
     *
     * @param spec a MIME type extension specification to add.
     */
    void setContentType(String spec);

    /**
     * Gets the MIME content type for a file as a string.
     *
     * @param file the file.
     * @return the MIME type string.
     */
    String getContentType(File file);

    /**
     * Gets the MIME content type for a named file as a string.
     *
     * @param name the name of the file.
     * @return the MIME type string.
     */
    String getContentType(String name);

    /**
     * Gets the MIME content type for a file name extension as a string.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type string.
     */
    String getContentType(String ext,
                          String def);

    /**
     * Gets the MIME content type for a file.
     *
     * @param file the file.
     * @return the MIME type.
     */
    MimeType getMimeContentType(File file);

    /**
     * Gets the MIME content type for a named file.
     *
     * @param name the name of the file.
     * @return the MIME type.
     */
    MimeType getMimeContentType(String name);

    /**
     * Gets the MIME content type for a file name extension.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type.
     */
    MimeType getMimeContentType(String ext,
                                String def);

    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param type the MIME type as a string.
     * @return the file name extension or null.
     */
    String getDefaultExtension(String type);

    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param mime the MIME type.
     * @return the file name extension or null.
     */
    String getDefaultExtension(MimeType mime);

    /**
     * Sets a locale-charset mapping.
     *
     * @param key the key for the charset.
     * @param charset the corresponding charset.
     */
    void setCharSet(String key,
                    String charset);

    /**
     * Gets the charset for a locale. First a locale specific charset
     * is searched for, then a country specific one and lastly a language
     * specific one. If none is found, the default charset is returned.
     *
     * @param locale the locale.
     * @return the charset.
     */
    String getCharSet(Locale locale);

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
    String getCharSet(Locale locale,
                      String variant);

    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @return the found charset or the default one.
     */
    String getCharSet(String key);

    /**
     * Gets the charset for a specified key.
     *
     * @param key the key for the charset.
     * @param def the default charset if none is found.
     * @return the found charset or the given default.
     */
    String getCharSet(String key,
                      String def);
}
