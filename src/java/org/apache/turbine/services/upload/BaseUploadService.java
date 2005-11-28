package org.apache.turbine.services.upload;


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


import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.servlet.TurbineServlet;
import org.apache.turbine.util.ServletUtils;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.parser.ParameterParser;

/**
 * <p> This class is a base implementation of
 * {@link org.apache.turbine.services.upload.UploadService}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 * @deprecated Please use org.apache.fulcrum.upload.UploadService
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
    public long getSizeMax()
    {
        return getConfiguration().getLong(
                UploadService.SIZE_MAX_KEY,
                UploadService.SIZE_MAX_DEFAULT);
    }

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    public int getSizeThreshold()
    {
        return getConfiguration().getInt(
                UploadService.SIZE_THRESHOLD_KEY,
                UploadService.SIZE_THRESHOLD_DEFAULT);
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
        String tmpPath = getConfiguration().getString(
                UploadService.REPOSITORY_KEY,
                UploadService.REPOSITORY_DEFAULT);

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
        return getConfiguration().getBoolean(
                UploadService.AUTOMATIC_KEY,
                UploadService.AUTOMATIC_DEFAULT);
    }
}
