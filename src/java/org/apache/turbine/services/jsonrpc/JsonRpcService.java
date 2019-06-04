package org.apache.turbine.services.jsonrpc;

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

import java.io.CharArrayWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.turbine.services.Service;
import org.jabsorb.JSONRPCBridge;


/**
 * The interface an JsonRpcService implements.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public interface JsonRpcService
        extends Service
{
    /** TurbineJsonRpcService. */
    String SERVICE_NAME = "JsonRpcService";

    /**
     * Process a JSON RPC call
     * @param cdata the JSON data
     * @param json_bridge the {@link JSONRPCBridge} object
     * @param request the request
     * @return the return object of the JSON RPC call
     */
    Object processCall(CharArrayWriter cdata,
            JSONRPCBridge json_bridge, HttpServletRequest request);

    /**
     * Register an object with the {@link JSONRPCBridge} in a given session
     *
     * @param session the session
     * @param key the name of the object in the session
     * @param value the object to register (not null)
     */
    void registerObject(HttpSession session, String key, Object value);

    /**
     * Register an object with the {@link JSONRPCBridge} globally
     *
     * @param key the name of the object in the session
     * @param value the object to register (not null)
     */
    void registerObjectGlobal(String key, Object value);

    /**
     * Get the {@link JSONRPCBridge} from the session
     *
     * @param session the session
     * @return the {@link JSONRPCBridge} instance
     */
    JSONRPCBridge getBridge(HttpSession session);

    /**
     * Remove the {@link JSONRPCBridge} from the session
     *
     * @param session the session
     */
    void clearBridge(HttpSession session);
}
