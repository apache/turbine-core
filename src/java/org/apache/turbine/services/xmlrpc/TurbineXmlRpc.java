package org.apache.turbine.services.xmlrpc;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;

/**
 * This is a static accesor class for {@link XmlRpcService}.
 *
 * @author <a href="mailto:magnus@handtolvur.is">Magnús Þór Torfason</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 */
public abstract class TurbineXmlRpc
{
    /**
     * Returns system's configured implementation of {@link XmlRpcService}.
     *
     * @return an implementaion of <code>XmlRpcService</code>
     */
    public static XmlRpcService getService()
    {
        return (XmlRpcService) TurbineServices.getInstance()
                .getService(XmlRpcService.SERVICE_NAME);
    }

    /**
     * Execute a remote procedure call.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception TurbineException
     */
    public static Object executeRpc(URL url, String methodName, Vector params)
            throws TurbineException
    {
        return getService().executeRpc(url, methodName, params);
    }

    /**
     * Execute a remote procedure call taht requires authentication
     *
     * @param url A URL.
     * @param username The username to try and authenticate with
     * @param password The password to try and authenticate with
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception TurbineException
     */
    public static Object executeAuthenticatedRpc(URL url, String username,
            String password, String methodName, Vector params)
            throws TurbineException
    {
        return getService().executeAuthenticatedRpc(url, username, password,
                methodName, params);
    }

    /**
     * Register an object as a handler for the XmlRpc Server part.
     *
     * @param handlerName The name under which we want
     * to register the service
     * @param handler The handler object
     */
    public static void registerHandler(String handlerName, Object handler)
    {
        getService().registerHandler(handlerName, handler);
    }

    /**
     * Register an object as a the default handler for
     * the XmlRpc Server part.
     *
     * @param handler The handler object
     */
    public static void registerHandler(Object handler)
    {
        getService().registerHandler(handler);
    }

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public static void unregisterHandler(String handlerName)
    {
        getService().unregisterHandler(handlerName);
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
    public static byte[] handleRequest(InputStream is)
    {
        return getService().handleRequest(is);
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
    public static byte[] handleRequest(InputStream is, String user, String password)
    {
        return getService().handleRequest(is, user, password);
    }

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void send(String serverURL,
                            String sourceLocationProperty,
                            String sourceFileName,
                            String destinationLocationProperty,
                            String destinationFileName)
            throws TurbineException
    {
        getService().send(serverURL,
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
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void send(String serverURL,
                            String username,
                            String password,
                            String sourceLocationProperty,
                            String sourceFileName,
                            String destinationLocationProperty,
                            String destinationFileName)
            throws TurbineException
    {
        getService().send(serverURL,
                username,
                password,
                sourceLocationProperty,
                sourceFileName,
                destinationLocationProperty,
                destinationFileName);
    }

    /**
     * Method to allow a client to get a file from a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void get(String serverURL,
                           String sourceLocationProperty,
                           String sourceFileName,
                           String destinationLocationProperty,
                           String destinationFileName)
            throws TurbineException
    {
        getService().get(serverURL,
                sourceLocationProperty,
                sourceFileName,
                destinationLocationProperty,
                destinationFileName);
    }

    /**
     * Method to allow a client to get a file to a server that
     * requires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void get(String serverURL,
                           String username,
                           String password,
                           String sourceLocationProperty,
                           String sourceFileName,
                           String destinationLocationProperty,
                           String destinationFileName)
            throws TurbineException
    {
        getService().get(serverURL,
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
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void remove(String serverURL,
                              String sourceLocationProperty,
                              String sourceFileName)
            throws TurbineException
    {
        getService().remove(serverURL,
                sourceLocationProperty,
                sourceFileName);
    }

    /**
     * Method to allow a client to remove a file from
     * a server that requires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @deprecated This is not scope of the Service itself but of an
     *             application which uses the service.
     */
    public static void remove(String serverURL,
                              String username,
                              String password,
                              String sourceLocationProperty,
                              String sourceFileName)
            throws TurbineException
    {
        getService().remove(serverURL,
                username,
                password,
                sourceLocationProperty,
                sourceFileName);
    }

    /**
     * Switch client filtering on/off.
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public static void setParanoid(boolean state)
    {
        getService().setParanoid(state);
    }

    /**
     * Add an IP address to the list of accepted clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must
     * call setParanoid(true) in order for this to have
     * any effect.
     *
     * @see #denyClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public static void acceptClient(String address)
    {
        getService().acceptClient(address);
    }

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public static void denyClient(String address)
    {
        getService().denyClient(address);
    }
}
