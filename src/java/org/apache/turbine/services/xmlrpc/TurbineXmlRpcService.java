package org.apache.turbine.services.xmlrpc;

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

import org.apache.xmlrpc.WebServer;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcServer;
import org.apache.xmlrpc.secure.SecureWebServer;
import org.apache.xmlrpc.secure.SecurityTool;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.xmlrpc.util.FileTransfer;
import org.apache.turbine.util.Log;
import org.apache.turbine.util.TurbineException;


/**
 * This is a service which will make an xml-rpc call to a remote
 * server.
 *
 * Here's an example of how it would be done:
 * <blockquote><code><pre>
 * XmlRpcService xs =
 *   (XmlRpcService)TurbineServices.getInstance()
 *   .getService(XmlRpcService.XMLRPC_SERVICE_NAME);
 * Vector vec = new Vector();
 * vec.addElement(new Integer(5));
 * URL url = new URL("http://betty.userland.com/RPC2");
 * String name = (String)xs.executeRpc(url, "examples.getStateName", vec);
 * </pre></code></blockquote>
 *
 * @author <a href="mailto:josh@stonecottage.com">Josh Lucas</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineXmlRpcService
    extends TurbineBaseService
    implements XmlRpcService
{
    /**
     * Whether a version of Apache's XML-RPC library greater than 1.1
     * is available.
     */
    protected boolean isModernVersion = false;

    /** The standalone xmlrpc server. */
    protected WebServer webserver = null;

    /** The encapsulated xmlrpc server. */
    protected XmlRpcServer server = null;

    /**
     * The address to listen on.  The default of <code>null</code>
     * indicates all network interfaces on a multi-homed host.
     */
    private InetAddress address = null;

    /** The xmlrpc client. */
    private XmlRpcClient client = null;

    /** The port to listen on. */
    protected int port = 0;

    /**
     * This function initializes the XmlRpcService.
     */
    public void init(ServletConfig config) throws InitializationException
    {
        try
        {
            server = new XmlRpcServer();

            // setup JSSE System properties from secure.server.options
            Configuration secureServerOptions =
                getConfiguration().subset("secure.server.option");
            setSystemPropertiesFromConfiguration(secureServerOptions);

            // Host and port information for the WebServer
            String addr = getConfiguration().getString("address", "0.0.0.0");
            port = getConfiguration().getInt("port", 0);

            if(port != 0)
            {
                if (addr != null && addr.length() > 0)
                {
                    try
                    {
                        address = InetAddress.getByName(addr);
                    }
                    catch (UnknownHostException useDefault)
                    {
                        address = null;
                    }
                }

                if (getConfiguration().getBoolean("secure.server", false))
                {
                    webserver = new SecureWebServer(port, address);
                }
                else
                {
                    webserver = new WebServer(port, address);
                }
            }

            // Set the XML driver to the correct SAX parser class
            String saxParserClass = getConfiguration().getString("parser",
                "org.apache.xerces.parsers.SAXParser");

            XmlRpc.setDriver ( saxParserClass );

            // Check if there are any handlers to register at startup
            Iterator keys = getConfiguration().getKeys("handler");
            while ( keys.hasNext() )
            {
                String handler = (String)keys.next();
                String handlerName = handler.substring(handler.indexOf(".")+1);
                String handlerClass = getConfiguration().getString(handler);
                registerHandler(handlerName, handlerClass);
            }

            /*
             * Turn on paranoia for the webserver if requested.
             */
            boolean stateOfParanoia = getConfiguration().getBoolean("paranoid", false);

            if (stateOfParanoia)
            {
                webserver.setParanoid(stateOfParanoia);
                Log.info(XmlRpcService.SERVICE_NAME +
                         ": Operating in a state of paranoia");

                /*
                 * Only set the accept/deny client lists if we
                 * are in a state of paranoia as they will just
                 * be ignored so there's no point in setting them.
                 */

                /*
                 * Set the list of clients that can connect
                 * to the xmlrpc server. The accepted client list
                 * will only be consulted if we are paranoid.
                 */
                Vector acceptedClients = getConfiguration().getVector("acceptClient");

                for (int i = 0; i < acceptedClients.size(); i++)
                {
                    String acceptClient = (String) acceptedClients.get(i);

                    if (acceptClient != null && ! acceptClient.equals(""))
                    {
                        webserver.acceptClient(acceptClient);
                        Log.info(XmlRpcService.SERVICE_NAME +
                                 ": Accepting client -> " + acceptClient);
                    }
                }

                /*
                 * Set the list of clients that can connect
                 * to the xmlrpc server. The denied client list
                 * will only be consulted if we are paranoid.
                 */
                Vector deniedClients = getConfiguration().getVector("denyClient");

                for (int i = 0; i < deniedClients.size(); i++)
                {
                    String denyClient = (String) deniedClients.get(i);

                    if (denyClient != null && ! denyClient.equals(""))
                    {
                        webserver.denyClient(denyClient);
                        Log.info(XmlRpcService.SERVICE_NAME +
                                 ": Denying client -> " + denyClient);
                    }
                }
            }
            // If we have a XML-RPC JAR whose version is greater than the
            // 1.1 series, the WebServer must be explicitly start()'d.
            try
            {
                Class.forName("org.apache.xmlrpc.XmlRpcRequest");
                isModernVersion = true;
                webserver.start();
            }
            catch (ClassNotFoundException ignored)
            {
                // XmlRpcRequest does not exist in versions 1.1 and lower.
                // Assume that our WebServer was already started.
            }
            Log.debug(XmlRpcService.SERVICE_NAME + ": Using " +
                      "Apache XML-RPC version " +
                      (isModernVersion ?
                       "greater than 1.1" : "1.1 or lower"));
        }
        catch (Exception e)
        {
            throw new InitializationException
                ("XMLRPCService failed to initialize", e);
        }

        setInit(true);
    }

    /**
     * Create System properties using the key-value pairs in a given
     * Configuration.  This is used to set system properties and the
     * URL https connection handler needed by JSSE to enable SSL
     * between XMLRPC client and server.
     *
     * @param configuration the Configuration defining the System
     * properties to be set
     */
    void setSystemPropertiesFromConfiguration(Configuration configuration)
    {
        for( Iterator i = configuration.getKeys();i.hasNext();)
        {
            String key = (String) i.next();
            String value = configuration.getString(key);

            Log.debug("JSSE option: " + key + " => " + value);

            System.setProperty(key, value);
        }
    }

    /**
     * Register an Object as a default handler for the service.
     *
     * @param handler The handler to use.
     * @exception XmlRpcException.
     * @exception IOException.
     */
    public void registerHandler(Object handler)
        throws XmlRpcException,
        IOException
    {
        registerHandler("$default", handler);
    }

    /**
     * Register an Object as a handler for the service.
     *
     * @param handlerName The name the handler is registered under.
     * @param handler The handler to use.
     * @exception XmlRpcException.
     * @exception IOException.
     */
    public void registerHandler(String handlerName,
                                Object handler)
        throws XmlRpcException,
        IOException
    {
        if(webserver != null)
        {
            webserver.addHandler(handlerName, handler);
        }

        server.addHandler(handlerName, handler);
    }

    /**
     * A helper method that tries to initialize a handler and register it.
     * The purpose is to check for all the exceptions that may occur in
     * dynamic class loading and throw an InitializationException on
     * error.
     *
     * @param handlerName The name the handler is registered under.
     * @param handlerClass The name of the class to use as a handler.
     * @exception TurbineException Couldn't instantiate handler.
     */
    public void registerHandler(String handlerName, String handlerClass)
        throws TurbineException
    {
        try
        {
            Object handler = Class.forName(handlerClass).newInstance();

            if(webserver != null)
            {
                webserver.addHandler(handlerName,handler);
            }

            server.addHandler(handlerName,handler);
        }
        // those two errors must be passed to the VM
        catch( ThreadDeath t )
        {
            throw t;
        }
        catch( OutOfMemoryError t )
        {
            throw t;
        }

        catch( Throwable t )
        {
            throw new TurbineException
                ("Failed to instantiate " + handlerClass, t);
        }
    }

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public void unregisterHandler(String handlerName)
    {
        if(webserver != null)
        {
            webserver.removeHandler(handlerName);
        }

        server.removeHandler(handlerName);
    }

    /**
     * Handle an XML-RPC request using the encapsulated server.
     *
     * You can use this method to handle a request from within
     * a Turbine screen.
     *
     * @param is the stream to read request data from.
     * @return the response body that needs to be sent to the client.
     */
    public byte[] handleRequest(InputStream is)
    {
        return server.execute(is);
    }

    /**
     * Handle an XML-RPC request using the encapsulated server with user
     * authentication.
     *
     * You can use this method to handle a request from within
     * a Turbine screen.
     *
     * <p> Note that the handlers need to implement AuthenticatedXmlRpcHandler
     * interface to access the authentication infomration.
     *
     * @param is the stream to read request data from.
     * @param user the user that is making the request.
     * @param password the password given by user.
     * @return the response body that needs to be sent to the client.
     */
    public byte[] handleRequest(InputStream is, String user, String password)
    {
        return server.execute(is, user, password);
    }

    /**
     * Client's interface to XML-RPC.
     *
     * The return type is Object which you'll need to cast to
     * whatever you are expecting.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception XmlRpcException.
     * @exception IOException.
     */
    public Object executeRpc(URL url,
                             String methodName,
                             Vector params)
        throws TurbineException
    {
        try
        {
            XmlRpcClient client = new XmlRpcClient ( url );
            return client.execute(methodName, params);
        }
        catch (Exception e)
        {
            throw new TurbineException("XML-RPC call failed", e);
        }
    }

    /**
     * Client's Authenticated interface to XML-RPC.
     *
     * The return type is Object which you'll need to cast to
     * whatever you are expecting.
     *
     * @param url A URL.
     * @param username The username to try and authenticate with
     * @param password The password to try and authenticate with
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception XmlRpcException.
     * @exception IOException.
     */
    public Object executeAuthenticatedRpc(URL url,
                             String username,
                             String password,
                             String methodName,
                             Vector params)
        throws TurbineException
    {
        try
        {
            XmlRpcClient client = new XmlRpcClient ( url );
            client.setBasicAuthentication(username, password);
            return client.execute(methodName, params);
        }
        catch (Exception e)
        {
            throw new TurbineException("XML-RPC call failed", e);
        }
    }

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public void send(String serverURL,
                     String sourceLocationProperty,
                     String sourceFileName,
                     String destinationLocationProperty,
                     String destinationFileName)
                     throws Exception
    {
        FileTransfer.send(serverURL,
                          sourceLocationProperty,
                          sourceFileName,
                          destinationLocationProperty,
                          destinationFileName);
    }

    /**
     * Method to allow a client to send a file to a server that
     * requires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public void send(String serverURL,
                     String username,
                     String password,
                     String sourceLocationProperty,
                     String sourceFileName,
                     String destinationLocationProperty,
                     String destinationFileName)
                     throws Exception
    {
        FileTransfer.send(serverURL,
                          username,
                          password,
                          sourceLocationProperty,
                          sourceFileName,
                          destinationLocationProperty,
                          destinationFileName);
    }

    /**
     * Method to allow a client to get a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public void get(String serverURL,
                    String sourceLocationProperty,
                    String sourceFileName,
                    String destinationLocationProperty,
                    String destinationFileName)
                    throws Exception
    {
        FileTransfer.get(serverURL,
                         sourceLocationProperty,
                         sourceFileName,
                         destinationLocationProperty,
                         destinationFileName);
    }

    /**
     * Method to allow a client to get a file from a server that
     * requires authentication.
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     */
    public void get(String serverURL,
                    String username,
                    String password,
                    String sourceLocationProperty,
                    String sourceFileName,
                    String destinationLocationProperty,
                    String destinationFileName)
                    throws Exception
    {
        FileTransfer.get(serverURL,
                         username,
                         password,
                         sourceLocationProperty,
                         sourceFileName,
                         destinationLocationProperty,
                         destinationFileName);
    }

    /**
     * Method to allow a client to remove a file from
     * the server
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     */
    public void remove(String serverURL,
                       String sourceLocationProperty,
                       String sourceFileName)
                       throws Exception
    {
        FileTransfer.remove(serverURL,
                            sourceLocationProperty,
                            sourceFileName);
    }

    /**
     * Method to allow a client to remove a file from
     * a server that requires authentication.
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     */
    public void remove(String serverURL,
                       String username,
                       String password,
                       String sourceLocationProperty,
                       String sourceFileName)
                       throws Exception
    {
        FileTransfer.remove(serverURL,
                            username,
                            password,
                            sourceLocationProperty,
                            sourceFileName);
    }

    /**
     * Switch client filtering on/off.
     *
     * @param state Whether to filter clients.
     *
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public void setParanoid(boolean state)
    {
        webserver.setParanoid(state);
    }

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void acceptClient(String address)
    {
        webserver.acceptClient(address);
    }

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @param address The address to add to the list.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void denyClient(String address)
    {
        webserver.denyClient(address);
    }

    /**
     * Shuts down this service, stopping running threads.
     */
    public void shutdown()
    {
        // Stop the XML RPC server.
        webserver.shutdown();

        if (!isModernVersion)
        {
            // org.apache.xmlrpc.WebServer used to block in a call to
            // ServerSocket.accept() until a socket connection was made.
            try
            {
                Socket interrupt = new Socket(address, port);
                interrupt.close();
            }
            catch (Exception notShutdown)
            {
                // It's remotely possible we're leaving an open listener
                // socket around.
                Log.warn(XmlRpcService.SERVICE_NAME +
                         "It's possible the xmlrpc server was not " +
                         "shutdown: " + notShutdown.getMessage());
            }
        }

        setInit(false);
    }
}
