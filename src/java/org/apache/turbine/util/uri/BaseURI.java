package org.apache.turbine.util.uri;

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

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.ServerData;

/**
 * This is the base class for all dynamic URIs in the Turbine System.
 *
 * All of the classes used for generating URIs are derived from this.
 *
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */

public abstract class BaseURI
        implements URI,
                   URIConstants
{
    /** Logging */
    private static Log log = LogFactory.getLog(BaseURI.class);

    /** ServerData Object for scheme, name, port etc. */
    private ServerData serverData =
            new ServerData(null, HTTP_PORT, HTTP, null, null);

    /** Whether we want to redirect or not. */
    private boolean redirect = false;

    /** Servlet response interface. */
    private HttpServletResponse response = null;

    /** Reference Anchor (#ref) */
    private String reference = null;

    /*
     * ========================================================================
     *
     * Constructors
     *
     * ========================================================================
     *
     */

    /**
     * Empty C'tor. Uses Turbine.getDefaultServerData().
     *
     */
    public BaseURI()
    {
        init(Turbine.getDefaultServerData());
        setResponse(null);
    }

    /**
     * Constructor with a RunData object
     *
     * @param runData A RunData object
     */
    public BaseURI(RunData runData)
    {
        init(runData.getServerData());
        setResponse(runData.getResponse());
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param runData A RunData object
     * @param redirect True if redirection allowed.
     */
    public BaseURI(RunData runData, boolean redirect)
    {
        init(runData.getServerData());
        setResponse(runData.getResponse());
        setRedirect(redirect);
    }

    /**
     * Constructor with a ServerData object
     *
     * @param serverData A ServerData object
     */
    public BaseURI(ServerData serverData)
    {
        init(serverData);
        setResponse(null);
    }

    /**
     * Constructor, set explicit redirection
     *
     * @param serverData A ServerData object
     * @param redirect True if redirection allowed.
     */
    public BaseURI(ServerData serverData, boolean redirect)
    {
        init(serverData);
        setResponse(null);
        setRedirect(redirect);
    }

    /*
     * ========================================================================
     *
     * Init
     *
     * ========================================================================
     *
     */

    /**
     * Init with a ServerData object
     *
     * @param serverData A ServerData object
     *
     */
    private void init(ServerData serverData)
    {
        log.debug("init(" + serverData + ")");

        if(serverData != null)
        {
            // We must clone this, because if BaseURI is used in a pull tool,
            // then the fields might be changed. If we don't clone, this might pull
            // through to the ServerData object saved at firstRequest() in the
            // Turbine object.
            this.serverData = (ServerData) serverData.clone();
        }
        else
        {
            log.error("Passed null ServerData object!");
        }
        reference = null;
    }

    /*
     * ========================================================================
     *
     * Getter / Setter
     *
     * ========================================================================
     *
     */

    /**
     * Set the redirect Flag
     *
     * @param redirect The new value of the redirect flag.
     */
    public void setRedirect(boolean redirect)
    {
        this.redirect = redirect;
    }

    /**
     * Returns the current value of the Redirect flag
     *
     * @return True if Redirect is allowed
     *
     */
    public boolean isRedirect()
    {
        return redirect;
    }

    /**
     * Gets the script name (/servlets/Turbine).
     *
     * @return A String with the script name.
     */
    public String getScriptName()
    {
        return serverData.getScriptName();
    }

    /**
     * Sets the script name (/servlets/Turbine).
     *
     * @param scriptName A String with the script name.
     */
    public void setScriptName(String scriptName)
    {
        serverData.setScriptName(scriptName);
    }

    /**
     * Gets the context path.
     *
     * @return A String with the context path.
     */
    public String getContextPath()
    {
        return serverData.getContextPath();
    }

    /**
     * Sets the context path.
     *
     * @param contextPath A String with the context path
     */
    public void setContextPath(String contextPath)
    {
        serverData.setContextPath(contextPath);
    }

    /**
     * Gets the server name.
     *
     * @return A String with the server name.
     */
    public String getServerName()
    {
        return serverData.getServerName();
    }

    /**
     * Sets the server name.
     *
     * @param serverName A String with the server name.
     */
    public void setServerName(String serverName)
    {
        serverData.setServerName(serverName);
    }

    /**
     * Gets the server port.
     *
     * @return A String with the server port.
     */
    public int getServerPort()
    {
        int serverPort = serverData.getServerPort();

        if (serverPort == 0)
        {
            if(getServerScheme().equals(HTTPS))
            {
                serverPort = HTTPS_PORT;
            }
            else
            {
                serverPort = HTTP_PORT;
            }
        }
        return serverPort;
    }

    /**
     * Sets the server port.
     *
     * @param serverPort An int with the port.
     */
    public void setServerPort(int serverPort)
    {
        serverData.setServerPort(serverPort);
    }

    /**
     * Method to specify that a URI should use SSL. The default port
     * is used.
     */
    public void setSecure()
    {
        setSecure(HTTPS_PORT);
    }

    /**
     * Method to specify that a URI should use SSL.
     * Whether or not it does is determined from Turbine.properties.
     * If use.ssl in the Turbine.properties is set to false, then
     * http is used in any case. (Default of use.ssl is true).
     *
     * @param port An int with the port number.
     */
    public void setSecure(int port)
    {
        boolean useSSL =
                Turbine.getConfiguration()
                .getBoolean(TurbineConstants.USE_SSL_KEY,
                        TurbineConstants.USE_SSL_DEFAULT);

        setServerScheme(useSSL ? HTTPS : HTTP);
        setServerPort(port);
    }

    /**
     * Sets the scheme (HTTP or HTTPS).
     *
     * @param serverScheme A String with the scheme.
     */
    public void setServerScheme(String serverScheme)
    {
        serverData.setServerScheme(StringUtils.isNotEmpty(serverScheme)
                ? serverScheme : "");
    }

    /**
     * Returns the current Server Scheme
     *
     * @return The current Server scheme
     *
     */
    public String getServerScheme()
    {
        String serverScheme = serverData.getServerScheme();

        return StringUtils.isNotEmpty(serverScheme) ? serverScheme : HTTP;
    }

    /**
     * Sets a reference anchor (#ref).
     *
     * @param reference A String containing the reference.
     */
    public void setReference(String reference)
    {
        this.reference = reference;
    }

    /**
     * Returns the current reference anchor.
     *
     * @return A String containing the reference.
     */
    public String getReference()
    {
        return hasReference() ? reference : "";
    }

    /**
     * Does this URI contain an anchor? (#ref)
     *
     * @return True if this URI contains an anchor.
     */
    public boolean hasReference()
    {
        return StringUtils.isNotEmpty(reference);
    }

    /*
     * ========================================================================
     *
     * Protected / Private Methods
     *
     * ========================================================================
     *
     */

    /**
     * Set a Response Object to use when creating the
     * response string.
     *
     */
    protected void setResponse(HttpServletResponse response)
    {
        this.response = response;
    }

    /**
     * Returns the Response Object from the Servlet Container.
     *
     * @return The Servlet Response object or null
     *
     */
    protected HttpServletResponse getResponse()
    {
        return response;
    }

    /**
     * Append the Context Path and Script Name to the passed
     * String Buffer.
     *
     * <p>
     * This is a convenience method to be
     * used in the Link output routines of derived classes to
     * easily append the correct path.
     *
     * @param sb The StringBuffer to store context path and script name.
     */
    protected void getContextAndScript(StringBuffer sb)
    {
        String context = getContextPath();

        if(StringUtils.isNotEmpty(context))
        {
            if(context.charAt(0) != '/')
            {
                sb.append('/');
            }
            sb.append (context);
        }

        // /servlet/turbine
        String script = getScriptName();

        if(StringUtils.isNotEmpty(script))
        {
            if(script.charAt(0) != '/')
            {
                sb.append('/');
            }
            sb.append (script);
        }
    }

    /**
     * Appends Scheme, Server and optionally the port to the
     * supplied String Buffer.
     *
     * <p>
     * This is a convenience method to be
     * used in the Link output routines of derived classes to
     * easily append the correct server scheme.
     *
     * @param sb The StringBuffer to store the scheme and port information.
     */
    protected void getSchemeAndPort(StringBuffer sb)
    {
        // http(s)://<servername>
        sb.append(getServerScheme());
        sb.append(URIConstants.URI_SCHEME_SEPARATOR);
        sb.append(getServerName());

        // (:<port>)
        if ((getServerScheme().equals(HTTP)
                    && getServerPort() != HTTP_PORT)
                || (getServerScheme().equals(HTTPS)
                        && getServerPort() != HTTPS_PORT))
        {
            sb.append(':');
            sb.append(getServerPort());
        }
    }

    /**
     * Encodes a Response Uri according to the Servlet Container.
     * This might add a Java session identifier or do redirection.
     * The resulting String can be used in a page or template.
     *
     * @param uri The Uri to encode
     *
     * @return An Uri encoded by the container.
     */
    protected String encodeResponse(String uri)
    {
        String res = uri;

        HttpServletResponse response = getResponse();

        if(response == null)
        {
            log.debug("No Response Object!");
        }
        else
        {
            try
            {
                if(isRedirect())
                {
                    log.debug("Should Redirect");
                    res = response.encodeRedirectURL(uri);
                }
                else
                {
                    res = response.encodeURL(uri);
                }
            }
            catch(Exception e)
            {
                log.error("response" + response + ", uri: " + uri);
                log.error("While trying to encode the URI: ", e);
            }
        }

        log.debug("encodeResponse():  " + res);
        return res;
    }
}
