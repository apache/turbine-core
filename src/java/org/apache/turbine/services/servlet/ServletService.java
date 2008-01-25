package org.apache.turbine.services.servlet;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.turbine.services.Service;

/**
 * <p>This interface exposes methods of the runner context in order
 * resolve or get access to external resources</p>
 *
 * @author <a href="mailto:ekkerbj@netscape.net">Jeff Brekke</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface ServletService extends Service
{
    /**
     * The service identifier
     */
    String SERVICE_NAME = "ServletService";

    /**
     * Returns an URL object for a given URI string.
     *
     * @param uri the URI to resolve as an URL
     * @return an URL object or null is the uri is malformed or
     * can't be resolved
     */
    URL getResource(String uri);

    /**
     * Same as getResource except that it returns an InputStream
     *
     * @param uri the URI to resolve
     * @return an InputStream on the URI content or null
     */
    InputStream getResourceAsStream(String uri);

    /**
     * Returns the complete filesystem path for a
     * given URI
     *
     * @param uri the URI to resolve
     * @return the full system path of this URI
     */
    String getRealPath(String uri);

    /**
     * Returns the servlet config used by this
     * Turbine web application.
     *
     * @return turbine servlet config
     */
    ServletConfig getServletConfig();

    /**
     * Returns the servlet context used by this
     * Turbine web application.
     *
     * @return turbine servlet context
     */
    ServletContext getServletContext();

    /**
     * Returns the server scheme for this
     * Turbine application. This will either
     * be http or https.
     *
     * @return String
     */
    String getServerScheme();

    /**
     * Returns the server name that this
     * Turbine application is running
     * on.
     *
     * @return String
     */
    String getServerName();

    /**
     * Returns the port that this Turbine
     * application is running through
     * on the server.
     *
     * @return String
     */
    String getServerPort();

    /**
     * Returns the context path for this
     * Turbine application.
     *
     * @return String
     */
    String getContextPath();
}
