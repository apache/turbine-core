package org.apache.turbine.util.mail;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.activation.DataSource;

/**
 * This class implements a typed DataSource from:<br>
 *
 * - an InputStream<br>
 * - a byte array<br>
 * - a String<br>
 *
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class ByteArrayDataSource
    implements DataSource
{
    /** Data. */
    private byte[] data;

    /** Content-type. */
    private String type;

    private ByteArrayOutputStream baos;


    /**
     * Create a datasource from a byte array.
     *
     * @param data A byte[].
     * @param type A String.
     */
    public ByteArrayDataSource(byte[] data,
                               String type)
    {
        this.data = data;
        this.type = type;
    }

    /**
     * Create a datasource from an input stream.
     *
     * @param is An InputStream.
     * @param type A String.
     */
    public ByteArrayDataSource(InputStream is,
                               String type)
    {
        this.type = type;
        try
        {
            int ch;

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BufferedInputStream isReader = new BufferedInputStream( is );
            BufferedOutputStream osWriter = new BufferedOutputStream( os );

            while ((ch = isReader.read()) != -1)
            {
                osWriter.write(ch);
            }
            data = os.toByteArray();
        }
        catch (IOException ioex)
        {
            // Do something!
        }
    }

    /**
     * Create a datasource from a String.
     *
     * @param data A String.
     * @param type A String.
     */
    public ByteArrayDataSource(String data,
                               String type)
    {
        this.type = type;
        try
        {
            // Assumption that the string contains only ASCII
            // characters!  Else just pass in a charset into this
            // constructor and use it in getBytes().
            this.data = data.getBytes("iso-8859-1");
        }
        catch (UnsupportedEncodingException uex)
        {
            // Do something!
        }
    }

    /**
     * Get the content type.
     *
     * @return A String.
     */
    public String getContentType()
    {
        if ( type == null )
            return "application/octet-stream";
        else
            return type;
    }

    /**
     * Get the input stream.
     *
     * @return An InputStream.
     * @exception IOException.
     */
    public InputStream getInputStream()
        throws IOException
    {
        if (data == null)
            throw new IOException("no data");
        return new ByteArrayInputStream(data);
    }

    /**
     * Get the name.
     *
     * @return A String.
     */
    public String getName()
    {
        return "ByteArrayDataSource";
    }

    /**
     * Get the output stream.
     *
     * @return An OutputStream.
     * @exception IOException.
     */
    public OutputStream getOutputStream()
        throws IOException
    {
        baos = new ByteArrayOutputStream();
        return baos;
    }
}
