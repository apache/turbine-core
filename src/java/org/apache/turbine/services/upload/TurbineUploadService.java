package org.apache.turbine.services.upload;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
import java.io.UnsupportedEncodingException;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.parser.ParameterParser;

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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineUploadService
        extends TurbineBaseService
        implements UploadService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineUploadService.class);

    /** A File Upload object for the actual uploading */
    protected DiskFileUpload fileUpload = null;

    /** Auto Upload yes? */
    private boolean automatic;

    /**
     * Initializes the service.
     *
     * This method processes the repository path, to make it relative to the
     * web application root, if neccessary
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = getConfiguration();

        String repoPath = conf.getString(
                UploadService.REPOSITORY_KEY,
                UploadService.REPOSITORY_DEFAULT);

        if (!repoPath.startsWith("/"))
        {
            // If our temporary directory is in the application
            // space, try to create it. If this fails, throw
            // an exception.
            String testPath = Turbine.getRealPath(repoPath);
            File testDir = new File(testPath);
            if (!testDir.exists())
            {
                if (!testDir.mkdirs())
                {
                    throw new InitializationException(
                            "Could not create target directory!");
                }
            }
            repoPath = testPath;
            conf.setProperty(UploadService.REPOSITORY_KEY, repoPath);
        }

        log.debug("Upload Path is now " + repoPath);

        long sizeMax = conf.getLong(
                UploadService.SIZE_MAX_KEY,
                UploadService.SIZE_MAX_DEFAULT);

        log.debug("Max Size " + sizeMax);

        int sizeThreshold = conf.getInt(
                UploadService.SIZE_THRESHOLD_KEY,
                UploadService.SIZE_THRESHOLD_DEFAULT);

        log.debug("Threshold Size " + sizeThreshold);

        automatic = conf.getBoolean(
                UploadService.AUTOMATIC_KEY,
                UploadService.AUTOMATIC_DEFAULT);

        log.debug("Auto Upload " + automatic);

        fileUpload = new DiskFileUpload();
        fileUpload.setSizeMax(sizeMax);
        fileUpload.setSizeThreshold(sizeThreshold);
        fileUpload.setRepositoryPath(repoPath);

        setInit(true);
    }

    /**
     * <p> Retrieves the value of <code>size.max</code> property of the
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The maximum upload size.
     */
    public long getSizeMax()
    {
        return fileUpload.getSizeMax();
    }

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    public int getSizeThreshold()
    {
        return fileUpload.getSizeThreshold();
    }

    /**
     * Retrieves the value of the 'automatic' property of {@link
     * UploadService}. This reports whether the Parameter parser
     * should allow "automatic" uploads if it is submitted to
     * Turbine.
     *
     * @return The value of 'automatic' property of {@link
     * UploadService}.
     */
    public boolean getAutomatic()
    {
        return automatic;
    }

    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The repository.
     */
    public String getRepository()
    {
        return fileUpload.getRepositoryPath();
    }

    /**
     * <p> Processes an <a href="http://rf.cx/rfc1867.html">RFC
     * 1867</a> compliant <code>multipart/form-data</code> stream.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception TurbineException Problems reading/parsing the
     * request or storing the uploaded file(s).
     */
    public void parseRequest(HttpServletRequest req,
                             ParameterParser params,
                             String path)
            throws TurbineException
    {
        String contentType = req.getHeader(CONTENT_TYPE);
        if (!contentType.startsWith(MULTIPART_FORM_DATA))
        {
            throw new TurbineException("the request doesn't contain a " +
                    MULTIPART_FORM_DATA + " stream");
        }
        int requestSize = req.getContentLength();
        if (requestSize == -1)
        {
            throw new TurbineException("the request was rejected because " +
                    "it's size is unknown");
        }
        if (requestSize > getSizeMax())
        {
            throw new TurbineException("the request was rejected because " +
                    "it's size exceeds allowed range");
        }

        try
        {
            List fileList = fileUpload
                    .parseRequest(req, 
                            getSizeThreshold(),
                            getSizeMax(),
                            path);

            if (fileList != null)
            {
                for (Iterator it = fileList.iterator(); it.hasNext();)
                {
                    FileItem fi = (FileItem) it.next();
                    if (fi.isFormField())
                    {
                        log.debug("Found an simple form field: " + fi.getFieldName() +", adding value " + fi.getString());
                        
                        String value = null;
                        try
                        {
                            value = fi.getString(params.getCharacterEncoding());
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            log.error(params.getCharacterEncoding()
                                    + " encoding is not supported."
                                    + "Used the default when reading form data.");
                            value = fi.getString();
                        }
                        params.append(fi.getFieldName(), value);
                    }
                    else
                    {
                        log.debug("Found an uploaded file: " + fi.getFieldName());
                        log.debug("It has " + fi.getSize() + " Bytes and is " + (fi.isInMemory() ? "" : "not ") + "in Memory");
                        log.debug("Adding FileItem as " + fi.getFieldName() + " to the params");
                        params.append(fi.getFieldName(), fi);
                    }
                }
            }
        }
        catch (FileUploadException e)
        {
            throw new TurbineException(e);
        }
    }
}
