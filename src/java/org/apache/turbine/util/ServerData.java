package org.apache.turbine.util;


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


import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private static final Logger log = LogManager.getLogger(ServerData.class);

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
            log.debug("Constructor({}, {}, {}, {}, {})", serverName,
                    Integer.valueOf(serverPort),
                    serverScheme,
                    scriptName,
                    contextPath);
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
        log.debug("Copy Constructor({})", serverData);

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
    @Override
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
        log.debug("setServerName({})", serverName);
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
        log.debug("setServerPort({})", Integer.valueOf(serverPort));
        this.serverPort = serverPort;
    }

    /**
     * Get the server scheme.
     *
     * @return the server scheme.
     */
    public String getServerScheme()
    {
        return StringUtils.defaultIfEmpty(serverScheme, "");
    }

    /**
     * Sets the cached serverScheme.
     *
     * @param serverScheme the server scheme.
     */
    public void setServerScheme(String serverScheme)
    {
        log.debug("setServerScheme({})", serverScheme);
        this.serverScheme = serverScheme;
    }

    /**
     * Get the script name
     *
     * @return the script name.
     */
    public String getScriptName()
    {
        return StringUtils.defaultIfEmpty(scriptName, "");
    }

    /**
     * Set the script name.
     *
     * @param scriptName the script name.
     */
    public void setScriptName(String scriptName)
    {
        log.debug("setScriptName({})", scriptName);
        this.scriptName = scriptName;
    }

    /**
     * Get the context path.
     *
     * @return the context path.
     */
    public String getContextPath()
    {
        return StringUtils.defaultIfEmpty(contextPath, "");
    }

    /**
     * Set the context path.
     *
     * @param contextPath A String.
     */
    public void setContextPath(String contextPath)
    {
        log.debug("setContextPath({})", contextPath);
        this.contextPath = contextPath;
    }

    /**
     * Appends the Host URL to the supplied StringBuilder.
     *
     * @param url A StringBuilder object
     */
    public void getHostUrl(StringBuilder url)
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
    @Override
    public String toString()
    {
        StringBuilder url = new StringBuilder();

        getHostUrl(url);

        url.append(getContextPath());
        url.append(getScriptName());
        return url.toString();
    }
}
