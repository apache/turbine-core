package org.apache.turbine.services.xmlrpc.util;

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
 * @version $Id$
 * @deprecated This is not scope of the Service itself but of an
 *             application which uses the service. This class shouldn't
 *             be part of Turbine but of an addon application.
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
                obj = Boolean.valueOf(this.send((String) params.elementAt(0),
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
                AuthenticatedFileHandler.remove((String) params.elementAt(0),
                        (String) params.elementAt(1));
                obj = Boolean.TRUE;
            }
        }
        else
        {
            obj = Boolean.FALSE;
        }

        return (Object) obj;
    }
}
