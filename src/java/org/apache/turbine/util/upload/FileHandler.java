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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.RunData;

/**
 * This class is used to upload files from a client to the server (any
 * types and any number of fields is supported) and download files
 * from the server to the client.
 *
 * @author <a href="mailto:oleg@one.lv">Oleg M. Podolsky</a>
 * @author <a href="mailto:neeme@one.lv">Neeme Praks</a>
 *
 * @deprecated Use TurbineUploadService counterpart FileItem.
 */
public class FileHandler
{
    public static String PARAMETER_NAME_FILENAME = "_fileuploader_filename";
    public static String PARAMETER_NAME_FILESIZE = "_fileuploader_filesize";
    public static String PARAMETER_NAME_FILENAME_SAVED = "_fileuploader_filename_saved";
    public static String PARAMETER_NAME_ENCODING = "_fileuploader_encoding";
    public static String PARAMETER_NAME_CONTENT_TYPE = "_fileuploader_content-type";

    /** Carriage return - line feed two times. */
    private static final byte[] CRLF2 = { 0xD, 0xA, 0xD, 0xA };

    /**
     *  Buffer for reading/writing data and buffer for storing all
     *  data.
     */
    private static int readbuffersize = 4096;
    private byte[] readbuffer = new byte[readbuffersize];
    private int storebuffersize;
    private byte[] storebuffer = null;

    /** Buffer for sending data back to browser. */
    private static int writebuffersize = 4096;
    private byte[] writebuffer = new byte[writebuffersize];

    /** Maximum number of bytes accepted, if zero then no limit. */
    private long maxdata = 0;

    /** Place for boundary that separates field data. */
    private int boundarysize;
    private byte[] boundary = null;

    private String fieldname, filename, contenttype;

    private HttpServletRequest req;
    private HttpServletResponse res;
    private ParameterParser pp;
    private String root;

    /**
     * If the form data is non-multipart (simple), it returns true,
     * otherwise returns false.
     *
     * @param req An HttpServletRequest.
     * @return True if the form data is non-multipart (simple).
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public static boolean isSimpleForm(HttpServletRequest req)
    {
        String header = req.getHeader("Content-Type");
        if (header != null && header.indexOf("multipart/form-data") >= 0)
        {
            return false;
        }
        return true;
    }

    /**
     * Copies stream ins to stream outs.
     *
     * @param ins An InputStream.
     * @param outs An OutputStream.
     * @exception IOException.
     */
    private void copyStream(InputStream ins,
                            OutputStream outs)
        throws IOException
    {
        BufferedInputStream bis = new BufferedInputStream(ins, writebuffersize);
        BufferedOutputStream bos
                = new BufferedOutputStream(outs, writebuffersize);
        int bufread;

        while ((bufread = bis.read(writebuffer)) != -1)
        {
            bos.write(writebuffer, 0, bufread);
        }
        bos.flush(); bos.close();
        bis.close();
    }

    /**
     * Writes HTTP headers and the file data to the response.
     *
     * @param savedname A String.
     * @param filename A String.
     * @param contenttype A String.
     * @exception Exception, a generic exception.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public void writeFileToResponse(String savedname,
                                    String filename,
                                    String contenttype)
        throws Exception
    {
        String fullPath = root + "/" + savedname;
        File f = new File(fullPath);

        // The following try block replaces:
        // res.setBufferSize(writebuffersize);
        try
        {
            Class[] sig = new Class[1];
            sig[0] = Integer.TYPE;
            Object[] param = new Object[1];
            param[0] = new Integer(writebuffersize);
            Method method = res.getClass()
                .getDeclaredMethod("setBufferSize", sig);
            method.invoke(res, param);
        }
        catch (Exception e)
        {
            // Ignore it.
        }
        res.setContentLength((int)f.length());
        res.setContentType(contenttype);
        res.setHeader("Content-Disposition",
                      "attachment; filename=\"" + filename + "\"");
        res.setDateHeader("Date", new Date().getTime());
        res.setDateHeader("Expires", new Date().getTime());
        res.setIntHeader("Age",0);
        res.setIntHeader("Retry-After",60);
        res.setHeader("Pragma","no-cache");
        res.setHeader("Connection","Keep-Alive");

        copyStream(new FileInputStream(fullPath), res.getOutputStream());
    }

    /**
     * Finds random name in the ROOT directory.  For example, if
     * ROOT="c:/temp", name="c:/temp/upload23967879053.dat".  ROOT
     * cannot finish with '/' or '\'.
     *
     * @return A String with a random name in the given directory.
     */
    private String getRandomName()
    {
        // TODO: if user is logged in, then put the file in user's
        // folder.
        String name = null;
        String fullpath = null;
        File f = null;

        do
        {
            name = "upload" + System.currentTimeMillis() + "_";
            fullpath = root + "/" + name;
            f = new File(fullpath);
        }
        while(f.exists());

        return name;
    }

    /**
     * Cuts short name of a file from its fully qualified name.  For
     * example, "c:\a\b\info.txt" -> "info.txt".
     *
     * @param fullname Full name of a file.
     * @return A String with the short name.
     */
    private String getShortName(String fullname)
    {
        int lastslash = fullname.lastIndexOf("/");
        int lastbackslash = fullname.lastIndexOf("\\");
        int pos;

        if (lastslash == lastbackslash)
        {
            pos = -1;
        }
        else if (lastslash > lastbackslash)
        {
            pos = lastslash + 1;
        }
        else
        {
            pos = lastbackslash + 1;
        }

        return fullname.substring(pos);
    }

    /**
     * Finds the first position of B in StoreBuffer beginning with
     * position number Pos, or -1 if not found.
     *
     * @param b Byte to find.
     * @param pos Position to start from.
     * @return The first occurence of B from Pos.
     */
    private int findByte(byte b, int pos)
    {
        if (pos < 0)
        {
            pos = 0;
        }
        else if (pos >= storebuffersize)
        {
            return -1;
        }

        for (int i = pos; i < storebuffersize ; i++)
        {
            if (storebuffer[i] == b)
            {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds the first position of SubArray in StoreBuffer beginning
     * with position number Pos, or -1 if not found.
     *
     * @param subarray Subarray to find.
     * @param pos Position to start from.
     * @return The first occurence of Subarray from Pos.
     */
    private int findSubArray(byte[] subarray, int pos)
    {
        int sublen = subarray.length;
        int maxpos, first, sp=0;

        maxpos = storebuffersize - sublen;

        for(first = pos ; (sp != sublen) && (first <= maxpos); first++)
        {
            first=findByte(subarray[0],first);
            if ((first < 0) || (first > maxpos))
            {
                return - 1;
            }

            for (sp = 1 ; sp < sublen ; sp++)
            {
                if (storebuffer[first+sp] != subarray[sp])
                {
                    sp = sublen;
                }
            }
        }

        if (sublen == 0)
        {
            return 0;
        }
        if (sp == sublen)
        {
            return (first - 1);
        }
        return -1;
    }

    /**
     * Reads the data from the given inputstream to StoreBuffer.
     *
     * @param instream InputStream to use.
     * @exception IOException.
     */
    private void readToStoreBuffer(InputStream instream)
        throws IOException
    {
        int bufread;
        int pos = 0;
        BufferedInputStream bis =
            new BufferedInputStream(instream, readbuffersize);

        while ((bufread=bis.read(readbuffer)) != -1 &&
               (maxdata >= pos || maxdata==0) )
        {
            System.arraycopy(readbuffer, 0, storebuffer, pos, bufread);
            pos += bufread;
        }
        bis.close();
    }

    /**
     * Finds values of Fieldname and Filename variables from the given
     * chunk of StoreBuffer.
     *
     * @param start Starting position in StoreBuffer.
     * @param end Ending position in StoreBuffer.
     */
    private void getHeaderInfo(int start, int end)
    {
        String temp = new String(storebuffer, start, end - start + 1);
        int namestart, nameend;
        int filenamestart, filenameend;
        int conttypestart, conttypeend;

        contenttype = null;
        filename = null;

        conttypestart = temp.indexOf("Content-Type: ");
        if(conttypestart >= 0)
        {
            conttypestart += 14;
            conttypeend = temp.length() - 1;
            contenttype = temp.substring(conttypestart, conttypeend);
        }

        namestart = temp.indexOf("; name=\"") + 8;
        nameend = temp.indexOf("\"", namestart);
        fieldname = temp.substring(namestart, nameend);

        filenamestart = temp.indexOf("; filename=\"", nameend + 1);
        if (filenamestart >= 0)
        {
            filenamestart += 12;
            filenameend = temp.indexOf("\"", filenamestart);
            filename = temp.substring(filenamestart, filenameend);
        }
    }

    /**
     * Returns the given chunk of StoreBuffer as a String.
     *
     * @param start Starting position in StoreBuffer.
     * @param end Ending position in StoreBuffer.
     * @return Given chunk's representation as a String.
     */
    private String getChunkAsText(int start, int end)
    {
        String text = new String(storebuffer, start, end - start + 1);
        return text;
    }

    /**
     * Writes the given chunk of StoreBuffer to given OutputStream.
     *
     * @param start Starting position in StoreBuffer.
     * @param end Ending position in StoreBuffer.
     * @param outstream OutputStream to write to.
     * @exception IOException.
     */
    private void storeChunk(int start,
                            int end,
                            OutputStream outstream)
        throws IOException
    {
        BufferedOutputStream bos =
            new BufferedOutputStream(outstream, readbuffersize);

        bos.write(storebuffer, start, end - start + 1);
        bos.flush();
        bos.close();
    }

    /**
     * Divides StoreBuffer into simple fields and binary fields
     * (files) and writes them to files (simple fields - to a text
     * file, files - to files with random names, providing name
     * mappings in the file for simple fields).
     *
     * @exception IOException.
     */
    private void writeChunks()
        throws IOException
    {
        int a;
        int b;
        int c = 0;
        String savename = null;
        String param = null;

        b = findSubArray(boundary, 0);
        do
        {
            if ((a = findSubArray(CRLF2, b + boundarysize)) >= 0)
            {
                if ((b = findSubArray(boundary, a + 4)) >= 0);
                {
                    getHeaderInfo(c, a);
                    c = b;

                    if (filename == null || filename.length() == 0)
                    {
                        param = getChunkAsText(a + 4, b - 5);
                        org.apache.turbine.util.Log.info("FileUploader: received field: " + fieldname + "=" + param);
                        pp.add(fieldname, param);
                    }
                    else
                    {
                        // TODO: implement also retrieving enconding
                        // and other header info.

                        // TODO: strip the path information from the
                        // original filename.

                        String shortname = getShortName(filename);
                        savename = getRandomName() + shortname + ".dat";
                        String fullPath = root + "/" + savename;
                        org.apache.turbine.util.Log.info ("FileUploader: received file: field=" + fieldname + ", fullpath=" + filename + ", filename=" + shortname + ", saved to file: " + fullPath);
                        storeChunk(a + 4, b - 5,
                                   new FileOutputStream(fullPath));
                        File f = new File(fullPath);
                        pp.add(this.PARAMETER_NAME_FILESIZE, f.length());
                        pp.add(this.PARAMETER_NAME_FILENAME, shortname);
                        pp.add(this.PARAMETER_NAME_FILENAME_SAVED, savename);
                        pp.add(this.PARAMETER_NAME_CONTENT_TYPE, contenttype);
                    }
                }
            }
        }
        while (a >= 0 && b >= 0);
    }

    /**
     * Stores all data.
     *
     * @exception ServletException.
     * @exception IOException.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public void saveStream()
        throws ServletException,
               IOException
    {
        readToStoreBuffer(req.getInputStream());
        writeChunks();
    }

    /**
     * Performs initialization.
     *
     * @param req An HttpServletRequest.
     * @param res An HttpServletResponse.
     * @param pp A ParameterParser.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public FileHandler(HttpServletRequest req,
                       HttpServletResponse res,
                       ParameterParser pp)
    {
        this.req = req;
        this.res = res;
        this.pp = pp;
        this.root = "c:";

        String conttype = req.getHeader("Content-Type");
        if (conttype != null)
        {
            boundary = conttype.substring(conttype.indexOf("boundary") + 9).getBytes();
            boundarysize = boundary.length;
            storebuffersize = Integer.parseInt(req.getHeader("Content-Length"));
            storebuffer = new byte[storebuffersize];
        }
    }

    /**
     * Performs initialization.
     *
     * @param req An HttpServletRequest.
     * @param res An HttpServletResponse.
     * @param pp A ParameterParser.
     * @param root A String.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public FileHandler( HttpServletRequest req,
                        HttpServletResponse res,
                        ParameterParser pp,
                        String root )
    {
        this( req, res, pp );
        this.setFileRepository(root);
    }

    /**
     * Performs initialization.
     *
     * @param req An HttpServletRequest.
     * @param res An HttpServletResponse.
     * @param root A String.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public FileHandler( HttpServletRequest req,
                        ParameterParser pp,
                        String root )
    {
        this( req, null, pp );
        this.setFileRepository(root);
    }

    /**
     * Performs initialization.
     *
     * @param data A Turbine RunData object.
     * @param root A String.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public FileHandler( RunData data,
                        String root )
    {
        this( data.getRequest(),
              data.getResponse(),
              data.getParameters(),
              root );
    }

    /**
     * Performs initialization.
     *
     * @param data A Turbine RunData object.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public FileHandler( RunData data )
    {
        this( data.getRequest(),
              data.getResponse(),
              data.getParameters() );
    }

    /**
     * Sets the root file repository.
     *
     * @param root A String.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public void setFileRepository(String root)
    {
        this.root = root;
    }

    /**
     * Gets the root file repository.
     *
     * @return A String.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public String getFileRepository()
    {
        return this.root;
    }

    /**
     * Sets the maximum size.
     *
     * @param size A long.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public void setMaxSize(long size)
    {
        this.maxdata = size;
    }

    /**
     * Deletes the file.
     *
     * @param filename A String.
     * @return True if file was deleted.
     * @deprecated Use TurbineUploadService counterpart FileItem.
     */
    public boolean deleteFile(String filename)
    {
        File f = new File(root + "/" + filename);
        return f.delete();
    }
}
