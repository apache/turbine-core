package org.apache.turbine.services.mimetype.util;

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
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
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
