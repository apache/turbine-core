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

import javax.servlet.http.HttpServletRequest;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.ParameterParser;
import org.apache.turbine.util.TurbineException;

/**
 * <p> This is a facade class for {@link UploadService}.
 *
 * <p> This class provides static methods that retrieve the configured
 * (in TurbineResource.properties) implementation of {@link
 * UploadService} and perform certain operations on it.  It uses
 * constants defined in {@link UploadService} interface for accessing
 * the service's properties and default values for them.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class TurbineUpload
{
    /**
     * <p> Retrieves an instance of system's configured implementation
     * of <code>UploadService</code>
     *
     * @return An instance of UploadService
     */
    public static UploadService getService()
    {
        return (UploadService)TurbineServices.getInstance().
            getService(UploadService.SERVICE_NAME);
    }

    /**
     * <p> Retrieves the value of 'automatic' property of {@link
     * UploadService}.
     *
     * @return The value of 'automatic' property of {@link
     * UploadService}.
     */
    public static boolean getAutomatic()
    {
        UploadService upload = null;
        try
        {
            upload = getService();
        }
        catch(org.apache.turbine.services.InstantiationException ie)
        {
            // If the service couldn't be instantiated, it obviously
            // can't be used for automatic uploading.
            return false;
        }
        String auto = upload.getProperties()
            .getProperty(UploadService.AUTOMATIC_KEY,
                         UploadService.AUTOMATIC_DEFAULT.toString())
            .toLowerCase();
        if(auto.equals("true") || auto.equals("yes") || auto.equals("1"))
        {
            return true;
        }
        if(auto.equals("false") || auto.equals("no") || auto.equals("0"))
        {
            return false;
        }
        return UploadService.AUTOMATIC_DEFAULT.booleanValue();
    }

    /**
     * <p> Retrieves the value of 'size.max' property of {@link
     * UploadService}.
     *
     * @return The value of 'size.max' property of {@link
     * UploadService}.
     */
    public static int getSizeMax()
    {
        return getService().getSizeMax();
    }

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    public static int getSizeThreshold()
    {
        return getService().getSizeThreshold();
    }

    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The repository.
     */
    public static String getRepository()
    {
        return getService().getRepository();
    }

    /**
     * <p> Performs parsing the request and storing files and form
     * fields.  Default file repository is used.  This method is
     * called by the {@link ParameterParser} if automatic upload is
     * enabled.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @exception TurbineException If there are problems reading/parsing
     * the request or storing files.
     */
    public static void parseRequest( HttpServletRequest req,
                                     ParameterParser params )
        throws TurbineException
    {
        UploadService upload = getService();
        upload.parseRequest(req, params, upload.getRepository());
    }

    /**
     * <p> Performs parsing the request and storing files and form
     * fields.  Custom file repository may be specified.  You can call
     * this method in your file upload {@link
     * org.apache.turbine.modules.Action} to if you need to specify a
     * custom directory for storing files.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception TurbineException If there are problems reading/parsing
     * the request or storing files.
     */
    public static void parseRequest( HttpServletRequest req,
                                     ParameterParser params,
                                     String path )
        throws TurbineException
    {
        UploadService upload = getService();
        upload.parseRequest(req, params, path);
    }
}
