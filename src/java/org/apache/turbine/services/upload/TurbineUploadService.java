package org.apache.turbine.services.upload;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.apache.turbine.services.BaseService;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.ValueParser;
import org.apache.turbine.util.upload.FileItem;
import org.apache.turbine.util.upload.MultipartStream;

/**
 * <p> This class is an implementation of {@link UploadService}.
 *
 * <p> Files will be stored in temporary disk storage on in memory,
 * depending on request size, and will be available from the {@link
 * org.apache.turbine.util.ParameterParser} as {@link
 * org.apache.turbine.util.upload.FileItem}s.
 *
 * <p>This implementation of {@link UploadService} handles multiple
 * files per single html widget, sent using multipar/mixed encoding
 * type, as specified by RFC 1867.  Use {@link
 * org.apache.turbine.util.ParameterParser#getFileItems(String)} to
 * acquire an array of {@link
 * org.apache.turbine.util.upload.FileItem}s associated with given
 * html widget.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public class TurbineUploadService
    extends BaseUploadService
{
    /**
     * <p> Processes an <a href="http://rf.cx/rfc1867.html">RFC
     * 1867</a> compliant <code>multipart/form-data</code> stream.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception TurbineException If there are problems reading/parsing
     * the request or storing files.
     */
    public void parseRequest( HttpServletRequest req,
                              ParameterParser params,
                              String path )
        throws TurbineException
    {
        String contentType = req.getHeader(CONTENT_TYPE);
        if(!contentType.startsWith(MULTIPART_FORM_DATA))
        {
            throw new TurbineException("the request doesn't contain a " + 
                MULTIPART_FORM_DATA + " stream");
        }
        int requestSize = req.getContentLength();
        if(requestSize == -1)
        {
            throw new TurbineException("the request was rejected because " + 
                "it's size is unknown");
        }
        if(requestSize > TurbineUpload.getSizeMax())
        {
            throw new TurbineException("the request was rejected because " + 
                "it's size exceeds allowed range");
        }

        try 
        {
            byte[] boundary = contentType.substring(
                                contentType.indexOf("boundary=")+9).getBytes();
            InputStream input = (InputStream)req.getInputStream();
            
            MultipartStream multi = new MultipartStream(input, boundary);
            boolean nextPart = multi.skipPreamble();
            while(nextPart)
            {
                Map headers = parseHeaders(multi.readHeaders());
                String fieldName = getFieldName(headers);
                if (fieldName != null)
                {
                    String subContentType = getHeader(headers, CONTENT_TYPE);
                    if (subContentType != null && subContentType
                                                .startsWith(MULTIPART_MIXED))
                    {
                        // Multiple files.
                        byte[] subBoundary = 
                            subContentType.substring(
                                subContentType
                                .indexOf("boundary=")+9).getBytes();
                        multi.setBoundary(subBoundary);
                        boolean nextSubPart = multi.skipPreamble();
                        while (nextSubPart)
                        {
                            headers = parseHeaders(multi.readHeaders());
                            if (getFileName(headers) != null)
                            {
                                FileItem item = createItem(path, headers,
                                                           requestSize);
                                OutputStream os = item.getOutputStream();
                                try
                                {
                                    multi.readBodyData(os);
                                }
                                finally
                                {
                                    if (os != null)
                                    {
                                        os.close();
                                    }
                                }
                                params.append(getFieldName(headers), item);
                            }
                            else
                            {
                                // Ignore anything but files inside
                                // multipart/mixed.
                                multi.discardBodyData();
                            }
                            nextSubPart = multi.readBoundary();
                        }
                        multi.setBoundary(boundary);
                    }
                    else
                    {
                        if (getFileName(headers) != null)
                        {
                            // A single file.
                            FileItem item = createItem(path, headers,
                                                       requestSize);
                            OutputStream os = item.getOutputStream();
                            try
                            {
                                multi.readBodyData(os);
                            }
                            finally
                            {
                                os.close();
                            }
                            params.append(getFieldName(headers), item);
                        }
                        else
                        {
                            // A form field.
                            FileItem item = createItem(path, headers,
                                                       requestSize);
                            OutputStream os = item.getOutputStream();
                            try
                            {
                                multi.readBodyData(os);
                            }
                            finally
                            {
                                os.close();
                            }
                            params.append(getFieldName(headers),
                                          new String(item.get()));
                        }
                    }
                }
                else
                {
                    // Skip this part.
                    multi.discardBodyData();
                }
                nextPart = multi.readBoundary();
            }
        }
        catch(IOException e)
        {
            throw new TurbineException("Processing of " + MULTIPART_FORM_DATA
                                       + " request failed", e);
        }
    }

    /**
     * <p> Retrieves file name from <code>Content-disposition</code> header.
     *
     * @param headers The HTTP request headers.
     * @return A the file name for the current <code>encapsulation</code>.
     */
    protected String getFileName(Map headers)
    {
        String fileName = null;
        String cd = getHeader(headers, CONTENT_DISPOSITION);
        if(cd.startsWith(FORM_DATA) || cd.startsWith("attachment"))
        {
            int start = cd.indexOf("filename=\"");
            int end = cd.indexOf('"', start + 10);
            if(start != -1 && end != -1 && (start + 10) != end)
            {
                String str = cd.substring(start + 10, end).trim();
                if (str.length() > 0)
                {
                    fileName = str;
                }
            }
        }
        return fileName;
    }

    /**
     * <p> Retrieves field name from <code>Content-disposition</code> header.
     *
     * @param headers The HTTP request headers.
     * @return The field name for the current <code>encapsulation</code>.
     */
    protected String getFieldName(Map headers)
    {
        String fieldName = null;
        String cd = getHeader(headers, CONTENT_DISPOSITION);
        if(cd != null && cd.startsWith(FORM_DATA))
        {
            int start = cd.indexOf("name=\"");
            int end = cd.indexOf('"', start + 6);
            if(start != -1 && end != -1)
            {
                fieldName = cd.substring(start + 6, end);
            }
        }
        return fieldName;
    }

    /**
     * <p> Creates a new instance of {@link
     * org.apache.turbine.util.upload.FileItem}.
     *
     * @param path The path for the FileItem.
     * @param headers The HTTP request headers.
     * @param requestSize The size of the request.
     * @return A newly created <code>FileItem</code>.
     */
    protected FileItem createItem( String path,
                                   Map headers,
                                   int requestSize )
    {
        return FileItem.newInstance(path,
                                    getFileName(headers),
                                    getHeader(headers, CONTENT_TYPE),
                                    requestSize);
    }

    /**
     * <p> Parses the <code>header-part</code> and returns as key/value
     * pairs.
     *
     * <p> If there are multiple headers of the same names, the name
     * will map to a comma-separated list containing the values.
     *
     * @param headerPart The <code>header-part</code> of the current
     * <code>encapsulation</code>.
     * @return The parsed HTTP request headers.
     */
    protected Map parseHeaders( String headerPart )
    {
        Map headers = new HashMap();
        char buffer[] = new char[MAX_HEADER_SIZE];
        boolean done = false;
        int j = 0;
        int i;
        String header, headerName, headerValue;
        try
        {
            while (!done)
            {
                i=0;
                // Copy a single line of characters into the buffer,
                // omitting trailing CRLF.
                while (i<2 || buffer[i-2] != '\r' || buffer[i-1] != '\n')
                {
                    buffer[i++] = headerPart.charAt(j++);
                }
                header = new String(buffer, 0, i-2);
                if (header.equals(""))
                {
                    done = true;
                }
                else
                {
                    if (header.indexOf(':') == -1)
                    {
                        // This header line is malformed, skip it.
                        continue;
                    }
                    headerName = header.substring(0, header.indexOf(':'))
                        .trim().toLowerCase();
                    headerValue = 
                        header.substring(header.indexOf(':') + 1).trim();
                    if (getHeader(headers, headerName) != null)
                    {
                        // More that one heder of that name exists,
                        // append to the list.
                        headers.put(headerName,
                                    getHeader(headers, headerName) + ',' +
                                    headerValue);
                    }
                    else
                    {
                        headers.put(headerName, headerValue);
                    }
                }
            }
        }
        catch(IndexOutOfBoundsException e)
        {
            // Headers were malformed. continue with all that was
            // parsed.
        }
        return headers;
    }

    /**
     * <p> Returns a header with specified name.
     *
     * @param headers The HTTP request headers.
     * @param name The name of the header to fetch.
     * @return The value of specified header, or a comma-separated
     * list if there were multiple headers of that name.
     */
    protected final String getHeader( Map headers, String name )
    {
        return (String)headers.get(name.toLowerCase());
    }
}
