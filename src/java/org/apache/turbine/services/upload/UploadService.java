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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.parser.ParameterParser;

/**
 * <p> This service handles parsing <code>multipart/form-data</code>
 * POST requests and turing them into form fields and uploaded files.
 * This can be either performed automatically by the {@link
 * org.apache.turbine.util.parser.ParameterParser} or manually by an user
 * definded {@link org.apache.turbine.modules.Action}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public interface UploadService
        extends Service
{
    /**
     * HTTP header.
     */
    String CONTENT_TYPE = "Content-type";

    /**
     * HTTP header.
     */
    String CONTENT_DISPOSITION = "Content-disposition";

    /**
     * HTTP header base type.
     */
    String MULTIPART = "multipart";

    /**
     * HTTP header base type modifier.
     */
    String FORM_DATA = "form-data";

    /**
     * HTTP header base type modifier.
     */
    String MIXED = "mixed";

    /**
     * HTTP header.
     */
    String MULTIPART_FORM_DATA =
            MULTIPART + '/' + FORM_DATA;

    /**
     * HTTP header.
     */
    String MULTIPART_MIXED = MULTIPART + '/' + MIXED;

    /**
     * The key in the TurbineResources.properties that references this
     * service.
     */
    String SERVICE_NAME = "UploadService";

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'automatic' property.
     */
    String AUTOMATIC_KEY = "automatic";

    /**
     * <p> The default value of 'automatic' property
     * (<code>false</code>).  If set to <code>true</code>, parsing the
     * multipart request will be performed automaticaly by {@link
     * org.apache.turbine.util.parser.ParameterParser}.  Otherwise, an {@link
     * org.apache.turbine.modules.Action} may decide to to parse the
     * request by calling {@link #parseRequest(HttpServletRequest,
     * ParameterParser, String) parseRequest} manually.
     */
    boolean AUTOMATIC_DEFAULT = false;

    /**
     * The request parameter name for overriding 'repository' property
     * (path).
     */
    String REPOSITORY_PARAMETER = "path";

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'repository' property.
     */
    String REPOSITORY_KEY = "repository";

    /**
     * <p> The default value of 'repository' property (.).  This is
     * the directory where uploaded fiels will get stored temporarily.
     * Note that "."  is whatever the servlet container chooses to be
     * it's 'current directory'.
     */
    String REPOSITORY_DEFAULT = ".";

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'size.max' property.
     */
    String SIZE_MAX_KEY = "size.max";

    /**
     * <p> The default value of 'size.max' property (1 megabyte =
     * 1048576 bytes).  This is the maximum size of POST request that
     * will be parsed by the uploader.  If you need to set specific
     * limits for your users, set this property to the largest limit
     * value, and use an action + no auto upload to enforce limits.
     *
     */
    long SIZE_MAX_DEFAULT = 1048576;

    /**
     * The key in UploadService properties in
     * TurbineResources.properties 'size.threshold' property.
     */
    String SIZE_THRESHOLD_KEY = "size.threshold";

    /**
     * <p> The default value of 'size.threshold' property (10
     * kilobytes = 10240 bytes).  This is the maximum size of a POST
     * request that will have it's components stored temporarily in
     * memory, instead of disk.
     */
    int SIZE_THRESHOLD_DEFAULT = 10240;

    /**
     * <p> This method performs parsing the request, and storing the
     * acquired information in apropriate places.
     *
     * @param req The servlet request to be parsed.
     * @param params The ParameterParser instance to insert form
     * fields into.
     * @param path The location where the files should be stored.
     * @exception TurbineException Problems reading/parsing the
     * request or storing the uploaded file(s).
     */
    void parseRequest(HttpServletRequest req,
                      ParameterParser params,
                      String path)
            throws TurbineException;

    /**
     * <p> Retrieves the value of <code>size.max</code> property of the
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The maximum upload size.
     */
    long getSizeMax();

    /**
     * <p> Retrieves the value of <code>size.threshold</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The threshold beyond which files are written directly to disk.
     */
    int getSizeThreshold();

    /**
     * <p> Retrieves the value of the <code>repository</code> property of
     * {@link org.apache.turbine.services.upload.UploadService}.
     *
     * @return The repository.
     */
    String getRepository();

    /**
     * <p> Retrieves the value of 'automatic' property of {@link
     * UploadService}.
     *
     * @return The value of 'automatic' property of {@link
     * UploadService}.
     */
    boolean getAutomatic();
}
