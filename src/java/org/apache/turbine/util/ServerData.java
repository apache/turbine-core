package org.apache.turbine.util;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

        log.debug("Constructor( " + serverName + ", " + serverPort + ", "
            + serverScheme + ", " + scriptName + ", " + contextPath + ")");

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
     *
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
     *
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
     * @param sn A String.
     */
    public void setServerName(String serverName)
    {
        log.debug("setServerName(" + serverName + ")");
        this.serverName = serverName;
    }

    /**
     * Get the server port.
     *
     * @return An int.
     */
    public int getServerPort()
    {
        return this.serverPort;
    }

    /**
     * Sets the cached serverPort.
     *
     * @param port An int.
     */
    public void setServerPort(int serverPort)
    {
        log.debug("setServerPort(" + serverPort + ")");
        this.serverPort = serverPort;
    }

    /**
     * Get the server scheme.
     *
     * @return A String.
     */
    public String getServerScheme()
    {
        return StringUtils.isEmpty(serverScheme) ? "" : serverScheme;
    }

    /**
     * Sets the cached serverScheme.
     *
     * @param ss A String.
     */
    public void setServerScheme(String serverScheme)
    {
        log.debug("setServerScheme(" + serverScheme + ")");
        this.serverScheme = serverScheme;
    }

    /**
     * Get the script name
     *
     * @return A String.
     */
    public String getScriptName()
    {
        return StringUtils.isEmpty(scriptName) ? "" : scriptName;
    }

    /**
     * Set the script name.
     *
     * @param sname A String.
     */
    public void setScriptName(String scriptName)
    {
        log.debug("setScriptName(" + scriptName + ")");
        this.scriptName = scriptName;
    }

    /**
     * Get the context path.
     *
     * @return A String.
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
     * Returns this element as an URL
     *
     * @return The contents of this element as a String
     */
    public String toString()
    {
        StringBuffer url = new StringBuffer();
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

        url.append(getScriptName());
        url.append(getContextPath());
        return url.toString();
    }
}
