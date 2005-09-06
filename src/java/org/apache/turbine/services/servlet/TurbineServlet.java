package org.apache.turbine.services.servlet;

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

import java.io.InputStream;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.turbine.services.TurbineServices;

/**
 * Simple static accessor to the EngineContextService
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:ekkerbj@netscape.net">Jeff Brekke</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public class TurbineServlet
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a ServletService implementation instance
     */
    protected static ServletService getService()
    {
        return (ServletService) TurbineServices
                .getInstance().getService(ServletService.SERVICE_NAME);
    }

    /**
     * Returns an URL object for a given URI string.
     * This URI is considered relative to the context.
     *
     * @param uri the URI to resolve as an URL
     * @return an URL object or null is the uri is malformed or can't be resolved
     */
    public static URL getResource(String uri)
    {
        return getService().getResource(uri);
    }

    /**
     * Same as getResource except that it returns an InputStream
     *
     * @see javax.servlet.ServletContext#getResourceAsStream
     * @param uri the URI to resolve
     * @return an InputStream on the URI content or null
     */
    public static InputStream getResourceAsStream(String uri)
    {
        return getService().getResourceAsStream(uri);
    }

    /**
     * Returns the complete filesystem path for a
     * given URI
     *
     * @see javax.servlet.ServletContext#getRealPath
     * @param uri the URI to resolve
     * @return the full system path of this URI
     */
    public static String getRealPath(String path)
    {
        return getService().getRealPath(path);
    }

    /**
     * Returns the servlet config used by this
     * Turbine web application.
     *
     * @return turbine servlet config
     */
    public static ServletConfig getServletConfig()
    {
        return getService().getServletConfig();
    }

    /**
     * Returns the servlet context used by this
     * Turbine web application.
     *
     * @return turbine servlet context
     */
    public static ServletContext getServletContext()
    {
        return getService().getServletContext();
    }

    /**
     * Returns the server scheme for this
     * Turbine application. This will either
     * be http or https.
     *
     * @return String
     */
    public static String getServerScheme()
    {
        return getService().getServerScheme();
    }

    /**
     * Returns the server name that this
     * Turbine application is running
     * on.
     *
     * @return String
     */
    public static String getServerName()
    {
        return getService().getServerName();
    }

    /**
     * Returns the port that this Turbine
     * application is running through
     * on the server.
     *
     * @return String
     */
    public static String getServerPort()
    {
        return getService().getServerPort();
    }

    /**
     * Returns the context path for this
     * Turbine application.
     *
     * @return String
     */
    public static String getContextPath()
    {
        return getService().getContextPath();
    }
}
