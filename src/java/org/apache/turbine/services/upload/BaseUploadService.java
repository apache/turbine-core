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

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.ServletUtils;
import org.apache.turbine.util.TurbineException;

/**
 * <p> This class is a base implementation of
 * {@link org.apache.turbine.services.upload.UploadService}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class BaseUploadService
        extends TurbineBaseService
        implements UploadService
{
    /**
     * A maximum lenght of a single header line that will be
     * parsed. (1024 bytes).
     */
    public static final int MAX_HEADER_SIZE = 1024;

    /**
     * Initializes the service.
     *
     * This method processes the repository path, to make it relative to the
     * web application root, if neccessary
     */
    public void init()
    {
        String path = getProperties()
                .getProperty(UploadService.REPOSITORY_KEY,
                        UploadService.REPOSITORY_DEFAULT.toString());
        if (!path.startsWith("/"))
        {
            String realPath = TurbineServlet.getRealPath(path);
            if (realPath != null)
            {
                path = realPath;
            }
        }
        getProperties().setProperty(UploadService.REPOSITORY_KEY, path);
        setInit(true);
    }

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
    public abstract void parseRequest(HttpServletRequest req,
                                      ParameterParser params,
                                      String path)
            throws TurbineException;

    /**
     * <p> Retrieves the value of <code>size.max</code> property of the
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The maximum upload size.
     */
    public int getSizeMax()
    {
        String sizeMax = getProperties()
                .getProperty(UploadService.SIZE_MAX_KEY,
                        UploadService.SIZE_MAX_DEFAULT.toString());
        try
        {
            return Integer.parseInt(sizeMax);
        }
        catch (NumberFormatException e)
        {
            return UploadService.SIZE_MAX_DEFAULT.intValue();
        }
    }

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    public int getSizeThreshold()
    {
        String sizeThreshold = getProperties()
                .getProperty(UploadService.SIZE_THRESHOLD_KEY,
                        UploadService.SIZE_THRESHOLD_DEFAULT.toString());
        try
        {
            return Integer.parseInt(sizeThreshold);
        }
        catch (NumberFormatException e)
        {
            return UploadService.SIZE_THRESHOLD_DEFAULT.intValue();
        }
    }

    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The repository.
     */
    public String getRepository()
    {
        // get the reposity value from TR.props
        String tmpPath = getProperties()
                .getProperty(UploadService.REPOSITORY_KEY,
                        UploadService.REPOSITORY_DEFAULT.toString());

        // return the expanded path name
        ServletConfig config = Turbine.getTurbineServletConfig();
        return ServletUtils.expandRelative(config, tmpPath);

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
        String auto = 
            getConfiguration().getString(
                UploadService.AUTOMATIC_KEY,
                UploadService.AUTOMATIC_DEFAULT).toLowerCase();

        // True, yes, 1 is "true", everything else is "false".
        return auto.equals("true") 
            || auto.equals("yes") 
            || auto.equals("1");
    }
}
