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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class defines mappings between MIME types and the corresponding
 * file name extensions. The mappings are defined as lines formed
 * by a MIME type name followed by a list of extensions separated
 * by a whitespace.
 *
 * @version $Id$
 */
public class MimeTypeMapper
{
    /**
     * Mappings between MIME types and file name extensions.
     */
    private HashMap mimeTypeExtensions = new HashMap();
    protected HashMap extensionMimeTypes = new HashMap();

    /**
     * Constructs an empty MIME type mapper.
     */
    public MimeTypeMapper()
    {
    }

    /**
     * Constructs a mapper reading from a stream.
     *
     * @param input an input stream.
     * @throws IOException for an incorrect stream.
     */
    public MimeTypeMapper(InputStream input)
            throws IOException
    {
        parse(new BufferedReader(
                new InputStreamReader(input, CharSetMap.DEFAULT_CHARSET)));
    }

    /**
     * Constructs a mapper reading from a file.
     *
     * @param file an input file.
     * @throws IOException for an incorrect file.
     */
    public MimeTypeMapper(File file)
            throws IOException
    {
        FileReader freader = new FileReader(file);
        try
        {
            parse(new BufferedReader(freader));
        }
        finally
        {
            try
            {
                freader.close();
            }
            catch (IOException x)
            {
            }
        }
    }

    /**
     * Constructs a mapper reading from a file path.
     *
     * @param path an input file path.
     * @throws IOException for an incorrect file.
     */
    public MimeTypeMapper(String path)
            throws IOException
    {
        this(new File(path));
    }

    /**
     * Sets a MIME content type mapping to extensions.
     *
     * @param spec a MIME type extension specification to parse.
     */
    public void setContentType(String spec)
    {
        try
        {
            parse(new BufferedReader(new StringReader(spec)));
        }
        catch (IOException x)
        {
        }
    }

    /**
     * Gets a MIME content type corresponding to a specified file name extension.
     *
     * @param ext a file name extension.
     * @return the corresponding MIME type as a string or null.
     */
    public String getContentType(String ext)
    {
        return (String) mimeTypeExtensions.get(ext);
    }

    /**
     * Gets a file name extension corresponding to a specified MIME content type.
     *
     * @param mime a MIME type as a string.
     * @return the corresponding file name extension or null.
     */
    public String getExtension(String type)
    {
        return (String) extensionMimeTypes.get(type);
    }

    /**
     * Parses MIME type extensions.
     *
     * @param reader a reader to parse.
     * @throws IOException for an incorrect reader.
     */
    protected synchronized void parse(BufferedReader reader)
            throws IOException
    {
        int l,count = 0;
        String next;
        String str = null;
        HashMap mimeTypes = (HashMap) extensionMimeTypes.clone();
        HashMap extensions = (HashMap) mimeTypeExtensions.clone();
        while ((next = reader.readLine()) != null)
        {
            str = str == null ? next : str + next;
            if ((l = str.length()) == 0)
            {
                str = null;
                continue;
            }
            // Check for continuation line.
            if (str.charAt(l - 1) != '\\')
            {
                count += parseMimeTypeExtension(str, mimeTypes, extensions);
                str = null;
            }
            else
            {
                str = str.substring(0, l - 1);
            }
        }
        if (str != null)
        {
            count += parseMimeTypeExtension(str, mimeTypes, extensions);
        }
        if (count > 0)
        {
            extensionMimeTypes = mimeTypes;
            mimeTypeExtensions = extensions;
        }
    }

    /**
     * Parses a MIME type extension.
     *
     * @param spec an extension specification to parse.
     * @param mimeTypes a map of MIME types.
     * @param extensions a map of extensions.
     * @return the number of file name extensions parsed.
     */
    protected int parseMimeTypeExtension(String spec,
                                         Map mimeTypes,
                                         Map extensions)
    {
        int count = 0;
        spec = spec.trim();
        if ((spec.length() > 0) &&
                (spec.charAt(0) != '#'))
        {
            StringTokenizer tokens = new StringTokenizer(spec);
            String type = tokens.nextToken();
            String ext;
            while (tokens.hasMoreTokens())
            {
                ext = tokens.nextToken();
                if (ext.length() == 0)
                {
                    continue;
                }
                extensions.put(ext, type);
                if (count++ == 0)
                {
                    mimeTypes.put(type, ext);
                }
            }
        }
        return count;
    }
}
