package org.apache.turbine.util;

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

/**
 * Holds basic server information under which Turbine is running.
 * This class is accessable via the RunData object within the Turbine
 * system.  You can also use it as a placeholder for this information
 * if you are only emulating a servlet system.
 *
 * @author <a href="mailto:burton@apache.org">Kevin A. Burton</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @version $Id$
 */
public class ServerData
{
    /** Cached serverName, */
    private String  serverName = null;

    /** Cached serverPort. */
    private int serverPort = 80;

    /** Cached serverScheme. */
    private String  serverScheme = null;

    /** Cached script name. */
    private String  scriptName = null;
    
    /** Cached context path. */
    private String  contextPath = null;

    /**
     * Constructor.
     *
     * @param serverName The server name.
     * @param serverPort The server port.
     * @param serverScheme The server scheme.
     * @param scriptName The script name.
     */
    public ServerData( String serverName,
                       int serverPort,
                       String serverScheme,
                       String scriptName,
		                 String contextPath )
    {
        this.serverName = serverName;
        this.serverPort = serverPort;
        this.serverScheme = serverScheme;
        this.scriptName = scriptName;
	     this.contextPath = contextPath;
    }

    /**
     * Get the name of the server.
     *
     * @return A String.
     */
    public String getServerName()
    {
        if ( this.serverName == null )
            return "";
        return serverName;
    }

    /**
     * Sets the cached serverName.
     *
     * @param sn A String.
     */
    public void setServerName(String sn)
    {
        this.serverName = sn;
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
    public void setServerPort(int port)
    {
        this.serverPort = port;
    }

    /**
     * Get the server scheme.
     *
     * @return A String.
     */
    public String getServerScheme()
    {
        if ( this.serverScheme == null )
            return "";
        return this.serverScheme;
    }

    /**
     * Sets the cached serverScheme.
     *
     * @param ss A String.
     */
    public void setServerScheme(String ss)
    {
        this.serverScheme = ss;
    }

    /**
     * Get the script name
     *
     * @return A String.
     */
    public String getScriptName()
    {
        if ( this.scriptName == null )
            return "";
        return this.scriptName;
    }

    /**
     * Set the script name.
     *
     * @param sname A String.
     */
    public void setScriptName(String sname)
    {
        this.scriptName = sname;
    }

   /**
     * Get the context path.
     *
     * @return A String.
     */
    public String getContextPath()
    {
        if ( this.contextPath == null )
            return "";
        return this.contextPath;
    }

    /**
     * Set the context path.
     *
     * @param sname A String.
     */
    public void setContextPath(String cpath)
    {
        this.contextPath = cpath;
    }
}
