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
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
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
