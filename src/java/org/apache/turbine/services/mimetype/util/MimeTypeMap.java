package org.apache.turbine.services.mimetype.util;

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
import java.io.IOException;
import java.io.InputStream;

/**
 * This class maintains a set of mappers defining mappings
 * between MIME types and the corresponding file name extensions.
 * The mappings are defined as lines formed by a MIME type name
 * followed by a list of extensions separated by a whitespace.
 * The definitions can be listed in MIME type files located in user's
 * home directory, Java home directory or the current class jar.
 * In addition, this class maintains static default mappings
 * and constructors support application specific mappings.
 *
 * @version $Id$
 */
public class MimeTypeMap
{
    /**
     * The default MIME type when nothing else is applicable.
     */
    public static final MimeType DEFAULT_MIMETYPE =
            MimeType.APPLICATION_OCTET_STREAM;

    /**
     * The default MIME type as a string.
     */
    public static final String DEFAULT_TYPE = DEFAULT_MIMETYPE.toString();

    /**
     * The name for MIME type mapper resources.
     */
    public static final String MIMETYPE_RESOURCE = "mime.types";

    /**
     * Common MIME type extensions.
     */
    public static final String EXT_HTML = "html";
    public static final String EXT_HTM = "htm";
    public static final String EXT_WML = "wml";
    public static final String EXT_HDML = "hdml";
    public static final String EXT_HDM = "hdm";
    public static final String EXT_CHTML = "chtml";
    public static final String EXT_TEXT = "txt";
    public static final String EXT_GIF = "gif";
    public static final String EXT_JPEG = "jpeg";
    public static final String EXT_JPG = "jpg";
    public static final String EXT_WBMP = "wbmp";

    /**
     * Priorities of available mappers.
     */
    private static final int MAP_PROG = 0;
    private static final int MAP_HOME = 1;
    private static final int MAP_SYS = 2;
    private static final int MAP_JAR = 3;
    private static final int MAP_COM = 4;

    /**
     * A common MIME type mapper.
     */
    private static MimeTypeMapper commonMapper = new MimeTypeMapper();

    static
    {
        commonMapper.setContentType(
                MimeType.TEXT_HTML.toString() + " " + EXT_HTML + " " + EXT_HTM);
        commonMapper.setContentType(
                MimeType.TEXT_WML.toString() + " " + EXT_WML);
        commonMapper.setContentType(
                MimeType.TEXT_HDML.toString() + " " + EXT_HDML + " " + EXT_HDM);
        commonMapper.setContentType(
                MimeType.TEXT_CHTML.toString() + " " + EXT_CHTML);
        commonMapper.setContentType(
                MimeType.TEXT_PLAIN.toString() + " " + EXT_TEXT);
        commonMapper.setContentType(
                MimeType.IMAGE_GIF.toString() + " " + EXT_GIF);
        commonMapper.setContentType(
                MimeType.IMAGE_JPEG.toString() + " " + EXT_JPEG + " " + EXT_JPG);
        commonMapper.setContentType(
                MimeType.IMAGE_WBMP.toString() + " " + EXT_WBMP);
    }

    /**
     * An array of available MIME type mappers.
     */
    private MimeTypeMapper mappers[] = new MimeTypeMapper[5];

    /**
     * Loads mappings from a file path.
     *
     * @param path a file path.
     * @return the mappings.
     * @throws IOException for an incorrect file.
     */
    protected static MimeTypeMapper loadPath(String path)
            throws IOException
    {
        return new MimeTypeMapper(path);
    }

    /**
     * Loads mappings from a resource.
     *
     * @param name a resource name.
     * @return the mappings.
     */
    protected static MimeTypeMapper loadResource(String name)
    {
        InputStream input = MimeTypeMap.class.getResourceAsStream(name);
        if (input != null)
        {
            try
            {
                return new MimeTypeMapper(input);
            }
            catch (IOException x)
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * Constructs a new MIME type map with default mappers.
     */
    public MimeTypeMap()
    {
        String path;
        try
        {
            // Check whether the user directory contains mappings.
            path = System.getProperty("user.home");
            if (path != null)
            {
                path = path + File.separator + MIMETYPE_RESOURCE;
                mappers[MAP_HOME] = loadPath(path);
            }
        }
        catch (Exception x)
        {
        }

        try
        {
            // Check whether the system directory contains mappings.
            path = System.getProperty("java.home") +
                    File.separator + "lib" + File.separator + MIMETYPE_RESOURCE;
            mappers[MAP_SYS] = loadPath(path);
        }
        catch (Exception x)
        {
        }

        // Check whether the current class jar contains mappings.
        mappers[MAP_JAR] = loadResource("/META-INF/" + MIMETYPE_RESOURCE);

        // Set the common mapper to have the lowest priority.
        mappers[MAP_COM] = commonMapper;
    }

    /**
     * Contructs a MIME type map read from a stream.
     *
     * @param input an input stream.
     * @throws IOException for an incorrect stream.
     */
    public MimeTypeMap(InputStream input)
            throws IOException
    {
        this();
        mappers[MAP_PROG] = new MimeTypeMapper(input);
    }

    /**
     * Contructs a MIME type map read from a file.
     *
     * @param path an input file.
     * @throws IOException for an incorrect input file.
     */
    public MimeTypeMap(File file)
            throws IOException
    {
        this();
        mappers[MAP_PROG] = new MimeTypeMapper(file);
    }

    /**
     * Contructs a MIME type map read from a file path.
     *
     * @param path an input file path.
     * @throws IOException for an incorrect input file.
     */
    public MimeTypeMap(String path)
            throws IOException
    {
        this();
        mappers[MAP_PROG] = new MimeTypeMapper(path);
    }

    /**
     * Sets a MIME content type mapping to extensions.
     *
     * @param spec a MIME type extension specification to set.
     */
    public synchronized void setContentType(String spec)
    {
        if (mappers[MAP_PROG] == null)
        {
            mappers[MAP_PROG] = new MimeTypeMapper();
        }
        mappers[MAP_PROG].setContentType(spec);
    }

    /**
     * Gets the MIME content type for a file as a string.
     *
     * @param file the file.
     * @return the MIME type string.
     */
    public String getContentType(File file)
    {
        return getContentType(file.getName());
    }

    /**
     * Gets the MIME content type for a named file as a string.
     *
     * @param name the name of the file.
     * @return the MIME type string.
     */
    public String getContentType(String name)
    {
        int i = name.lastIndexOf('.');
        if (i >= 0)
        {
            String ext = name.substring(i + 1);
            return ext.length() > 0 ?
                    getContentType(ext, DEFAULT_TYPE) : DEFAULT_TYPE;
        }
        else
        {
            return DEFAULT_TYPE;
        }
    }

    /**
     * Gets the MIME content type for a file name extension as a string.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type string.
     */
    public String getContentType(String ext,
                                 String def)
    {
        int i = ext.lastIndexOf('.');
        if (i >= 0)
        {
            ext = ext.substring(i + 1);
        }

        String mime;
        MimeTypeMapper mapper;
        for (i = 0; i < mappers.length; i++)
        {
            mapper = mappers[i];
            if (mapper != null)
            {
                mime = mapper.getContentType(ext);
                if (mime != null)
                {
                    return mime;
                }
            }
        }
        return def;
    }

    /**
     * Gets the MIME content type for a file.
     *
     * @param file the file.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(File file)
    {
        try
        {
            return new MimeType(getContentType(file));
        }
        catch (Exception x)
        {
            return DEFAULT_MIMETYPE;
        }
    }

    /**
     * Gets the MIME content type for a named file.
     *
     * @param name the name of the file.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(String name)
    {
        try
        {
            return new MimeType(getContentType(name));
        }
        catch (Exception x)
        {
            return DEFAULT_MIMETYPE;
        }
    }

    /**
     * Gets the MIME content type for a file name extension.
     *
     * @param ext the file name extension.
     * @param def the default type if none is found.
     * @return the MIME type.
     */
    public MimeType getMimeContentType(String ext,
                                       String def)
    {
        try
        {
            return new MimeType(getContentType(ext, def));
        }
        catch (Exception x)
        {
            return DEFAULT_MIMETYPE;
        }
    }

    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param type the MIME type as a string.
     * @return the file name extension or null.
     */
    public String getDefaultExtension(String type)
    {
        String ext;
        MimeTypeMapper mapper;
        int i = type.indexOf(';');
        if (i >= 0)
        {
            type = type.substring(0, i);
        }
        type = type.trim();
        for (i = mappers.length - 1; i >= 0; i--)
        {
            mapper = mappers[i];
            if (mapper != null)
            {
                ext = mapper.getExtension(type);
                if (ext != null)
                {
                    return ext;
                }
            }
        }
        return null;
    }

    /**
     * Gets the default file name extension for a MIME type.
     * Note that the mappers are called in the reverse order.
     *
     * @param mime the MIME type.
     * @return the file name extension or null.
     */
    public String getDefaultExtension(MimeType mime)
    {
        return getDefaultExtension(mime.getTypes());
    }

    /**
     * Sets a common MIME content type mapping to extensions.
     *
     * @param spec a MIME type extension specification to set.
     */
    protected synchronized void setCommonContentType(String spec)
    {
        mappers[MAP_COM].setContentType(spec);
    }
}
