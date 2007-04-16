package org.apache.turbine.services.upload;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.services.InstantiationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.parser.ParameterParser;

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
        return (UploadService) TurbineServices.getInstance().
                getService(UploadService.SERVICE_NAME);
    }

    /**
     * Checks whether an Upload Service is configured.
     * This method is safe to call even with no Upload
     * service installed.
     *
     * @return True if an upload Service is configured
     */
    public static boolean isAvailable()
    {
        try
        {
            getService();
        }
        catch (InstantiationException ie)
        {
            // If the service couldn't be instantiated, it obviously
            // isn't configured.
            return false;
        }
        return true;
    }

    /**
     * Retrieves the value of the 'automatic' property of {@link
     * UploadService}. This reports whether the Upload Service
     * is available and (if yes), the Parameter parser should
     * allow "automatic" uploads if it is submitted to Turbine.
     *
     * This method is safe to call even with no Upload Service
     * configured.
     *
     * @return The value of 'automatic' property of {@link
     * UploadService}.
     */
    public static boolean getAutomatic()
    {
        // Short circuit evaluation of the && operator!
        return isAvailable() && getService().getAutomatic();
    }

    /**
     * <p> Retrieves the value of 'size.max' property of {@link
     * UploadService}.
     *
     * @return The value of 'size.max' property of {@link
     * UploadService}.
     */
    public static long getSizeMax()
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
    public static void parseRequest(HttpServletRequest req,
                                    ParameterParser params)
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
    public static void parseRequest(HttpServletRequest req,
                                    ParameterParser params,
                                    String path)
            throws TurbineException
    {
        getService().parseRequest(req, params, path);
    }
}
