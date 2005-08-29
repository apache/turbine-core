package org.apache.turbine.services.xmlrpc;

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

import java.util.Vector;

import org.apache.turbine.services.Service;
import org.apache.turbine.util.TurbineException;

/**
 * The interface an XmlRpcService implements.
 *
 * @author <a href="mailto:josh@stonecottage.com">Josh Lucas</a>
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public interface XmlRpcService
        extends Service
{
    /** TurbineXmlRpcService. */
    String SERVICE_NAME = "XmlRpcService";

    /**
     * Execute a remote procedure call.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception TurbineException
     */
    Object executeRpc(URL url,
            String methodName,
            Vector params)
            throws TurbineException;

    /**
     * Execute a remote procedure call taht requires
     * authentication.
     *
     * @param url A URL.
     * @param username The username to authenticate with
     * @param password The password to authenticate with
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception TurbineException
     */
    Object executeAuthenticatedRpc(URL url,
            String username,
            String password,
            String methodName,
            Vector params)
            throws TurbineException;

    /**
     * Register an object as a handler for the XmlRpc Server part.
     *
     * @param handlerName The name under which we want
     * to register the service
     * @param handler The handler object
     */
    void registerHandler(String handlerName, Object handler);

    /**
     * Register an object as a the default handler for
     * the XmlRpc Server part.
     *
     * @param handler The handler object
     */
    void registerHandler(Object handler);

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    void unregisterHandler(String handlerName);

    /**
     * Handle an XML-RPC request using the encapsulated server.
     *
     * You can use this method to handle a request from within
     * a Turbine screen.
     *
     * @param is the stream to read request data from.
     * @return the response body that needs to be sent to the client.
     */
    byte[] handleRequest(InputStream is);

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
    byte[] handleRequest(InputStream is, String user, String password);

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void send(String serverURL,
            String sourceLocationProperty,
            String sourceFileName,
            String destinationLocationProperty,
            String destinationFileName)
            throws TurbineException;

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
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void send(String serverURL,
            String username,
            String password,
            String sourceLocationProperty,
            String sourceFileName,
            String destinationLocationProperty,
            String destinationFileName)
            throws TurbineException;

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void get(String serverURL,
            String sourceLocationProperty,
            String sourceFileName,
            String destinationLocationProperty,
            String destinationFileName)
            throws TurbineException;

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
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void get(String serverURL,
            String username,
            String password,
            String sourceLocationProperty,
            String sourceFileName,
            String destinationLocationProperty,
            String destinationFileName)
            throws TurbineException;

    /**
     * Method to allow a client to remove a file from
     * the server
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void remove(String serverURL,
            String sourceLocationProperty,
            String sourceFileName)
            throws TurbineException;

    /**
     * Method to allow a client to remove a file from
     * a server that requires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @throws TurbineException
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    void remove(String serverURL,
            String username,
            String password,
            String sourceLocationProperty,
            String sourceFileName)
            throws TurbineException;

    /**
     * Switch client filtering on/off.
     *
     * @param state
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    void setParanoid(boolean state);

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @param address
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    void acceptClient(String address);

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @param address
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    void denyClient(String address);

}
