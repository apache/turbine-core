package org.apache.turbine.services.xmlrpc.util;

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

import java.util.Vector;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.TurbineException;
import org.apache.xmlrpc.AuthenticatedXmlRpcHandler;

/**
 * An authenticated Handler for use with the XML-RPC service that will deal
 * with clients sending file to the server (Turbine application)
 * and clients getting files from the server (Turbine application).
 *
 * usage in TurbineResources.properties is:
 * services.XmlRpcService.handler.file = org.apache.turbine.services.xmlrpc.util.AuthenticatedFileHandler
 *
 * See the FileHandler class for further documentation.
 *
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 */
public class AuthenticatedFileHandler
        extends FileHandler
        implements AuthenticatedXmlRpcHandler
{
    /**
     * Default Constructor
     */
    public AuthenticatedFileHandler()
    {
    }

    /**
     * Handles all requests for an Authenticated file transfer.
     */
    public Object execute(String method, Vector params, String username, String password)
            throws TurbineException
    {
        Object obj = null;

        // Authenticate the user and get the object.
        User user = null;
        user = TurbineSecurity.getAuthenticatedUser(username, password);

        if (user != null)
        {
            if (method.equals("send"))
            {
                obj = new Boolean(this.send((String) params.elementAt(0),
                        (String) params.elementAt(1),
                        (String) params.elementAt(2)));
            }

            if (method.equals("get"))
            {
                obj = this.get((String) params.elementAt(0),
                        (String) params.elementAt(1));
            }

            if (method.equals("remove"))
            {
                this.remove((String) params.elementAt(0),
                        (String) params.elementAt(1));
                obj = new Boolean("true");
            }
        }
        else
        {
            obj = new Boolean("false");
        }

        return (Object) obj;
    }
}
