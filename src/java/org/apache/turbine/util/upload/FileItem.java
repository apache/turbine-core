package org.apache.turbine.util.upload;

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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.apache.turbine.services.uniqueid.TurbineUniqueId;
import org.apache.turbine.services.upload.TurbineUpload;

/**
 * <p> This class represents a file that was received by Turbine using
 * <code>multipart/form-data</code> POST request.
 *
 * <p> After retrieving an instance of this class from the {@link
 * org.apache.turbine.util.ParameterParser ParameterParser} (see
 * {@link org.apache.turbine.util.ParameterParser#getFileItem(String)
 * ParameterParser.getFileItem(String)} and {@link
 * org.apache.turbine.util.ParameterParser#getFileItems(String)
 * ParameterParser.getFileItems(String)}) you can use it to acces the
 * data that was sent by the browser.  You may either request all
 * contents of file at once using {@link #get()} or request an {@link
 * java.io.InputStream InputStream} with {@link #getStream()} and
 * process the file without attempting to load it into memory, which
 * may come handy with large files.
 *
 * Implements the javax.activation.DataSource interface (which allows
 * for example the adding of a FileItem as an attachment to a multipart
 * email).
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @version $Id$
 */
public class FileItem implements DataSource
{
    /**
     * The maximal size of request that will have it's elements stored
     * in memory.
     */
    public static final int DEFAULT_UPLOAD_SIZE_THRESHOLD = 10240;

    /** The original filename in the user's filesystem. */
    protected String fileName;

    /**
     * The content type passed by the browser or <code>null</code> if
     * not defined.
     */
    protected String contentType;

    /** Cached contents of the file. */
    protected byte[] content;

    /** Temporary storage location. */
    protected File storeLocation;

    /** Temporary storage for in-memory files. */
    protected ByteArrayOutputStream byteStream;


    /**
     * Constructs a new <code>FileItem</code>.
     *
     * <p>Use {@link #newInstance(String,String,String,int)} to
     * instantiate <code>FileItems</code>.
     *
     * @param fileName The original filename in the user's filesystem.
     * @param contentType The content type passed by the browser or
     * <code>null</code> if not defined.
     */
    protected FileItem( String fileName,
                        String contentType )
    {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    /**
     * Returns the original filename in the user's filesystem.
     * (implements DataSource method)
     *
     * @return The original filename in the user's filesystem.
     */
    public String getName()
    {
        return getFileName();
    }

    /**
     * Returns the original filename in the user's filesystem.
     *
     * @return The original filename in the user's filesystem.
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
    * Returns the content type passed by the browser or
    * <code>null</code> if not defined. (implements
    * DataSource method).
    *
    * @return The content type passed by the browser or
    * <code>null</code> if not defined.
    */
    public String getContentType()
    {
        return contentType;
    }

    /**
     * Provides a hint if the file contents will be read from memory.
     *
     * @return <code>True</code> if the file contents will be read
     * from memory.
     */
    public boolean inMemory()
    {
        return (content != null || byteStream != null);
    }

    /**
     * Returns the size of the file.
     *
     * @return The size of the file.
     */
    public long getSize()
    {
        if(storeLocation != null)
        {
            return storeLocation.length();
        }
        else if(byteStream != null)
        {
            return byteStream.size();
        }
        else
        {
            return content.length;
        }
    }

    /**
     * Returns the contents of the file as an array of bytes.  If the
     * contents of the file were not yet cached int the memory, they
     * will be loaded from the disk storage and chached.
     *
     * @return The contents of the file as an array of bytes.
     */
    public byte[] get()
    {
        if(content == null)
        {
            if(storeLocation != null)
            {
                content = new byte[(int)getSize()];
                try
                {
                    FileInputStream fis = new FileInputStream(storeLocation);
                    fis.read(content);
                }
                catch (Exception e)
                {
                    content = null;
                }
            }
            else
            {
                content = byteStream.toByteArray();
                byteStream = null;
            }
        }
        return content;
    }

    /**
     * Returns the contents of the file as a String, using default
     * encoding.  This method uses {@link #get()} to retrieve the
     * contents of the file.
     *
     * @return The contents of the file.
     */
    public String getString()
    {
        return new String(get());
    }

    /**
     * Returns the contents of the file as a String, using specified
     * encoding.  This method uses {@link #get()} to retireve the
     * contents of the file.<br>
     *
     * @param encoding The encoding to use.
     * @return The contents of the file.
     * @exception UnsupportedEncodingException.
     */
    public String getString( String encoding )
        throws UnsupportedEncodingException
    {
        return new String(get(), encoding);
    }

    /**
     * Returns an {@link java.io.InputStream InputStream} that can be
     * used to retrieve the contents of the file. (implements DataSource
     * method)
     *
     * @return An {@link java.io.InputStream InputStream} that can be
     * used to retrieve the contents of the file.
     * @exception Exception, a generic exception.
     */
    public InputStream getInputStream()
        throws IOException
    {
        return getStream();
    }
    
    /**
     * Returns an {@link java.io.InputStream InputStream} that can be
     * used to retrieve the contents of the file.
     *
     * @return An {@link java.io.InputStream InputStream} that can be
     * used to retrieve the contents of the file.
     * @exception Exception, a generic exception.
     */
    public InputStream getStream()
        throws IOException
    {
        if(content == null)
        {
            if(storeLocation != null)
            {
                return new FileInputStream(storeLocation);
            }
            else
            {
                content = byteStream.toByteArray();
                byteStream = null;
            }
        }
        return new ByteArrayInputStream(content);
    }

    /**
     * Returns the {@link java.io.File} objects for the FileItems's
     * data temporary location on the disk.  Note that for
     * <code>FileItems</code> that have their data stored in memory
     * this method will return <code>null</code>.  When handling large
     * files, you can use {@link java.io.File#renameTo(File)} to
     * move the file to new location without copying the data, if the
     * source and destination locations reside within the same logical
     * volume.
     *
     * @return A File.
     */
    public File getStoreLocation()
    {
        return storeLocation;
    }

    /**
     * Removes the file contents from the temporary storage.
     */
    protected void finalize()
    {
        if(storeLocation != null && storeLocation.exists())
        {
            storeLocation.delete();
        }
    }

    /**
     * Returns an {@link java.io.OutputStream OutputStream} that can
     * be used for storing the contents of the file.
     * (implements DataSource method)
     *
     * @return an {@link java.io.OutputStream OutputStream} that can be
     * used for storing the contensts of the file.
     * @exception IOException.
     */
    public OutputStream getOutputStream()
        throws IOException
    {
        if(storeLocation == null)
        {
            return byteStream;
        }
        else
        {
            return new FileOutputStream(storeLocation);
        }
    }

    /**
     * Instantiates a FileItem.  It uses <code>requestSize</code> to
     * decide what temporary storage approach the new item should
     * take.  The largest request that will have its items cached in
     * memory can be configured in
     * <code>TurbineResources.properties</code> in the entry named
     * <code>file.upload.size.threshold</code>
     *
     * @param path A String.
     * @param name The original filename in the user's filesystem.
     * @param contentType The content type passed by the browser or
     * <code>null</code> if not defined.
     * @param requestSize The total size of the POST request this item
     * belongs to.
     * @return A FileItem.
     */
    public static FileItem newInstance(String path,
                                       String name,
                                       String contentType,
                                       int requestSize)
    {
        FileItem item = new FileItem(name, contentType);
        if(requestSize > TurbineUpload.getSizeThreshold())
        {
            String instanceName = TurbineUniqueId.getInstanceId();
            String fileName = TurbineUniqueId.getUniqueId();
            fileName = instanceName + "_upload_" + fileName + ".tmp";
            fileName = path + "/" + fileName;
            item.storeLocation = new File(fileName);
            item.storeLocation.deleteOnExit();
        }
        else
        {
            item.byteStream = new ByteArrayOutputStream();
        }
        return item;
    }

    /**
     * A convenience method to write an uploaded
     * file to disk. The client code is not concerned
     * whether or not the file is stored in memory,
     * or on disk in a temporary location. They just
     * want to write the uploaded file to disk.
     *
     * @param String full path to location where uploaded
     *               should be stored.
     */
    public void write(String file) throws Exception
    {
        if (inMemory())
        {
            FileOutputStream fout = null;
            try
            {
                fout = new FileOutputStream(file);
                fout.write(get());
            }
            finally
            {
                if (fout != null)
                {
                    fout.close();
                }
            }
        }
        else if (storeLocation != null)
        {
            /*
             * The uploaded file is being stored on disk
             * in a temporary location so move it to the
             * desired file.
             */
            if (storeLocation.renameTo(new File(file)) == false)
            {
                BufferedInputStream in = null;
                BufferedOutputStream out = null;
                try
                {
                    in = new BufferedInputStream
                        ( new FileInputStream(storeLocation));
                    out = new BufferedOutputStream(new FileOutputStream(file));
                    byte[] bytes = new byte[2048];
                    int s = 0;
                    while ( (s = in.read(bytes)) != -1 )
                    {
                        out.write(bytes,0,s);
                    }
                }
                finally
                {
                    try
                    {
                        in.close();
                    }
                    catch (Exception e)
                    {
                                // ignore
                    }
                    try
                    {
                        out.close();
                    }
                    catch (Exception e)
                    {
                                // ignore
                    }
                }
            }
        }
        else
        {
            /*
             * For whatever reason we cannot write the
             * file to disk.
             */
            throw new Exception("Cannot write uploaded file to disk!");
        }
    }
}
