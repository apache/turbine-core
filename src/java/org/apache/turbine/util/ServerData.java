package org.apache.turbine.util;

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

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.util.uri.URIConstants;

/**
 * Holds basic server information under which Turbine is running.
 * This class is accessable via the RunData object within the Turbine
 * system.  You can also use it as a placeholder for this information
 * if you are only emulating a servlet system.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class ServerData
{
    /** Cached serverName, */
    private String serverName = null;

    /** Cached serverPort. */
    private int serverPort = 0;

    /** Cached serverScheme. */
    private String serverScheme = null;

    /** Cached script name. */
    private String  scriptName = null;

    /** Cached context path. */
    private String  contextPath = null;

    /** Logging */
    private static Log log = LogFactory.getLog(ServerData.class);

    /**
     * Constructor.
     *
     * @param serverName The server name.
     * @param serverPort The server port.
     * @param serverScheme The server scheme.
     * @param scriptName The script name.
     * @param contextPath The context Path
     */
    public ServerData(String serverName,
        int serverPort,
        String serverScheme,
        String scriptName,
        String contextPath)
    {
        if (log.isDebugEnabled())
        {
            StringBuffer sb = new StringBuffer();
            sb.append("Constructor(");
            sb.append(serverName);
            sb.append(", ");
            sb.append(serverPort);
            sb.append(", ");
            sb.append(serverScheme);
            sb.append(", ");
            sb.append(scriptName);
            sb.append(", ");
            sb.append(contextPath);
            sb.append(")");
            log.debug(sb.toString());
        }

        setServerName(serverName);
        setServerPort(serverPort);
        setServerScheme(serverScheme);
        setScriptName(scriptName);
        setContextPath(contextPath);
    }

    /**
     * Copy-Constructor
     *
     * @param serverData A ServerData Object
     */
    public ServerData(ServerData serverData)
    {
        log.debug("Copy Constructor(" + serverData + ")");

        setServerName(serverData.getServerName());
        setServerPort(serverData.getServerPort());
        setServerScheme(serverData.getServerScheme());
        setScriptName(serverData.getScriptName());
        setContextPath(serverData.getContextPath());
    }

    /**
     * A C'tor that takes a HTTP Request object and
     * builds the server data from its contents
     *
     * @param req The HTTP Request
     */
    public ServerData(HttpServletRequest req)
    {
        setServerName(req.getServerName());
        setServerPort(req.getServerPort());
        setServerScheme(req.getScheme());
        setScriptName(req.getServletPath());
        setContextPath(req.getContextPath());
    }

    /**
     * generates a new Object with the same values as this one.
     *
     * @return A cloned object.
     */
    public Object clone()
    {
        log.debug("clone()");
        return new ServerData(this);
    }

    /**
     * Get the name of the server.
     *
     * @return A String.
     */
    public String getServerName()
    {
        return StringUtils.isEmpty(serverName) ? "" : serverName;
    }

    /**
     * Sets the cached serverName.
     *
     * @param serverName the server name.
     */
    public void setServerName(String serverName)
    {
        log.debug("setServerName(" + serverName + ")");
        this.serverName = serverName;
    }

    /**
     * Get the server port.
     *
     * @return the server port.
     */
    public int getServerPort()
    {
        return this.serverPort;
    }

    /**
     * Sets the cached serverPort.
     *
     * @param serverPort the server port.
     */
    public void setServerPort(int serverPort)
    {
        log.debug("setServerPort(" + serverPort + ")");
        this.serverPort = serverPort;
    }

    /**
     * Get the server scheme.
     *
     * @return the server scheme.
     */
    public String getServerScheme()
    {
        return StringUtils.isEmpty(serverScheme) ? "" : serverScheme;
    }

    /**
     * Sets the cached serverScheme.
     *
     * @param serverScheme the server scheme.
     */
    public void setServerScheme(String serverScheme)
    {
        log.debug("setServerScheme(" + serverScheme + ")");
        this.serverScheme = serverScheme;
    }

    /**
     * Get the script name
     *
     * @return the script name.
     */
    public String getScriptName()
    {
        return StringUtils.isEmpty(scriptName) ? "" : scriptName;
    }

    /**
     * Set the script name.
     *
     * @param scriptName the script name.
     */
    public void setScriptName(String scriptName)
    {
        log.debug("setScriptName(" + scriptName + ")");
        this.scriptName = scriptName;
    }

    /**
     * Get the context path.
     *
     * @return the context path.
     */
    public String getContextPath()
    {
        return StringUtils.isEmpty(contextPath) ? "" : contextPath;
    }

    /**
     * Set the context path.
     *
     * @param contextPath A String.
     */
    public void setContextPath(String contextPath)
    {
        log.debug("setContextPath(" + contextPath + ")");
        this.contextPath = contextPath;
    }

    /**
     * Appends the Host URL to the supplied StringBuffer.
     *
     * @param url A StringBuffer object
     */
    public void getHostUrl(StringBuffer url)
    {
        url.append(getServerScheme());
        url.append("://");
        url.append(getServerName());
        if ((getServerScheme().equals(URIConstants.HTTP)
                && getServerPort() != URIConstants.HTTP_PORT)
            ||
            (getServerScheme().equals(URIConstants.HTTPS)
                && getServerPort() != URIConstants.HTTPS_PORT)
            )
        {
            url.append(":");
            url.append(getServerPort());
        }
    }

    /**
     * Returns this object as an URL.
     *
     * @return The contents of this object as a String
     */
    public String toString()
    {
        StringBuffer url = new StringBuffer();

        getHostUrl(url);

        url.append(getContextPath());
        url.append(getScriptName());
        return url.toString();
    }
}
