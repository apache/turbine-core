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

import org.apache.xmlrpc.XmlRpcException;
import java.util.Vector;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
    public static final String SERVICE_NAME = "XmlRpcService";

    /**
     * Execute a remote procedure call.
     *
     * @param url A URL.
     * @param methodName A String with the method name.
     * @param params A Vector with the parameters.
     * @return An Object.
     * @exception TurbineException
     */
    public Object executeRpc(URL url,
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
    public Object executeAuthenticatedRpc(URL url,
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
     * @exception XmlRpcException
     * @exception IOException
     */
    public void registerHandler(String handlerName, Object handler)
        throws XmlRpcException,
               IOException;

    /**
     * Register an object as a the default handler for
     * the XmlRpc Server part.
     *
     * @param handler The handler object
     * @exception XmlRpcException
     * @exception IOException
     */
    public void registerHandler(Object handler)
        throws XmlRpcException,
               IOException;

    /**
     * Unregister a handler.
     *
     * @param handlerName The name of the handler to unregister.
     */
    public void unregisterHandler(String handlerName);

    /**
     * Handle an XML-RPC request using the encapsulated server.
     *
     * You can use this method to handle a request from within
     * a Turbine screen.
     *
     * @param is the stream to read request data from.
     * @return the response body that needs to be sent to the client.
     */
    public byte[] handleRequest(InputStream is);

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
    public byte[] handleRequest(InputStream is, String user, String password);

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws Exception
     */
    public void send(String serverURL,
                     String sourceLocationProperty,
                     String sourceFileName,
                     String destinationLocationProperty,
                     String destinationFileName)
                     throws Exception;
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
     * @throws Exception
     */
    public void send(String serverURL,
                     String username,
                     String password,
                     String sourceLocationProperty,
                     String sourceFileName,
                     String destinationLocationProperty,
                     String destinationFileName)
                     throws Exception;

    /**
     * Method to allow a client to send a file to a server.
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws Exception
     */
    public void get(String serverURL,
                    String sourceLocationProperty,
                    String sourceFileName,
                    String destinationLocationProperty,
                    String destinationFileName)
                    throws Exception;

    /**
     * Method to allow a client to send a file to a server that
     * rewuires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @param destinationLocationProperty
     * @param destinationFileName
     * @throws Exception
     */
    public void get(String serverURL,
                    String username,
                    String password,
                    String sourceLocationProperty,
                    String sourceFileName,
                    String destinationLocationProperty,
                    String destinationFileName)
                    throws Exception;

    /**
     * Method to allow a client to remove a file from
     * the server
     *
     * @param serverURL
     * @param sourceLocationProperty
     * @param sourceFileName
     * @throws Exception
     */
    public void remove(String serverURL,
                       String sourceLocationProperty,
                       String sourceFileName)
                       throws Exception;

    /**
     * Method to allow a client to remove a file from
     * a server that requires authentication
     *
     * @param serverURL
     * @param username
     * @param password
     * @param sourceLocationProperty
     * @param sourceFileName
     * @throws Exception
     */
    public void remove(String serverURL,
                       String username,
                       String password,
                       String sourceLocationProperty,
                       String sourceFileName)
                       throws Exception;

    /**
     * Switch client filtering on/off.
     *
     * @param state
     * @see #acceptClient(java.lang.String)
     * @see #denyClient(java.lang.String)
     */
    public void setParanoid(boolean state);

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
    public void acceptClient(String address);

    /**
     * Add an IP address to the list of denied clients. The parameter can
     * contain '*' as wildcard character, e.g. "192.168.*.*". You must call
     * setParanoid(true) in order for this to have any effect.
     *
     * @param address
     * @see #acceptClient(java.lang.String)
     * @see #setParanoid(boolean)
     */
    public void denyClient(String address);

}
