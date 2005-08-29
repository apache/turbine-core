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
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.ServletUtils;

/**
 * <p>This class provides a context service when the application
 * is run in a ServletContainer. It is mainly a wrapper around
 * the ServletContext API.</p>
 * <p>This class requires Servlet API 2.1 or better.</p>
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:ekkerbj@netscape.net">Jeff Brekke</a>
 * @author <a href="mailto:sgala@hisitech.com">Santiago Gala</a>
 * @author <a href="mailto:jvanzyl@periapt.com.com">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class TurbineServletService
        extends TurbineBaseService implements ServletService
{
    /** Logging */
    private static Log log = LogFactory.getLog(TurbineServletService.class);

    /** The servlet context for this servlet */
    private ServletContext servletContext = null;

    /** The servlet configuration for this servlet */
    private ServletConfig servletConfig = null;

    /**
     * Load all configured components and initialize them. This is
     * a zero parameter variant which queries the Turbine Servlet
     * for its config.
     */
    public void init()
    {
        this.servletConfig = Turbine.getTurbineServletConfig();
        try
        {
            this.servletContext = servletConfig.getServletContext();

            log.debug("Initializing with ServletConfig");
        }
        catch (Exception e)
        {
            log.error("Cannot initialize TurbineServletService.", e);
        }
        setInit(true);
    }

    /**
     * Called during Turbine.init()
     *
     * @param servletConfig A ServletConfig.
     *
     * @deprecated use init() instead.
     */
    public void init(ServletConfig servletConfig)
    {
        init();
    }

    /**
     * Returns an URL object for a given URI string.
     * This URI is considered relative to the context.
     *
     * @see javax.servlet.ServletContext#getResource
     * @param uri the URI to resolve as an URL
     * @return an URL object or null is the uri is malformed or
     * can't be resolved
     */
    public URL getResource(String uri)
    {
        if (servletContext == null)
        {
            return null;
        }

        URL url = null;

        try
        {
            url = getServletContext().getResource(uri);
            // work-around for Websphere 3.52
            if (url != null && url.toString().startsWith("classloader:"))
            {
                url = new URL("file:" + url.toString().substring(12));
            }
            else if (url == null)
            {
                url = new URL("file:" + getServletContext().getRealPath(uri));
            }
        }
        catch (MalformedURLException e)
        {
            //if the URL is wrong, return null
        }

        return url;
    }

    /**
     * Same as getResource except that it returns an InputStream
     *
     * @see javax.servlet.ServletContext#getResourceAsStream
     * @param uri the URI to resolve
     * @return an InputStream on the URI content or null
     */
    public InputStream getResourceAsStream(String uri)
    {
        if (servletContext == null)
        {
            return null;
        }

        InputStream is = null;
        is = servletContext.getResourceAsStream(uri);
        return is;
    }

    /**
     * Returns the complete filesystem path for a
     * given URI
     *
     * @see javax.servlet.ServletContext#getRealPath
     * @param uri the URI to resolve
     * @return the full system path of this URI
     */
    public String getRealPath(String uri)
    {
        if (getServletContext() == null || uri == null)
        {
            return null;
        }
        else
        {
            return getServletContext().getRealPath(uri);
        }
    }

    /**
     * Returns the servlet config used by this
     * Turbine web application.
     *
     * @return turbine servlet config
     */
    public ServletConfig getServletConfig()
    {
        return servletConfig;
    }

    /**
     * Returns the servlet context used by this
     * Turbine web application.
     *
     * @return turbine servlet context
     */
    public ServletContext getServletContext()
    {
        return servletContext;
    }

    /**
     * Returns the server scheme for this
     * Turbine application. This will either
     * be http or https.
     *
     * @return String
     */
    public String getServerScheme()
    {
        return Turbine.getServerScheme();
    }

    /**
     * Returns the server name that this
     * Turbine application is running
     * on.
     *
     * @return String
     */
    public String getServerName()
    {
        return Turbine.getServerName();
    }

    /**
     * Returns the port that this Turbine
     * application is running through
     * on the server.
     *
     * @return String
     */
    public String getServerPort()
    {
        return Turbine.getServerPort();
    }

    /**
     * Returns the context path for this
     * Turbine application.
     *
     * @return String
     */
    public String getContextPath()
    {
        return Turbine.getContextPath();
    }

    /**
     * Expands a string that points to a relative path or path list,
     * leaving it as an absolute path based on the servlet context.
     * It will return null if the text is empty or the config object
     * is null.
     *
     * @param path The String containing a path or path list.
     * @return A String with the expanded path or path list.
     */
    public String expandRelative(String path)
    {
        return ServletUtils.expandRelative(getServletConfig(), path);
    }
}
