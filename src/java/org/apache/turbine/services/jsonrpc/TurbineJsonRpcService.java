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

import org.apache.turbine.services.TurbineBaseService;
import org.jabsorb.JSONRPCBridge;


/**
 * This is a service that will respond to JSON-RPC calls.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class TurbineJsonRpcService
        extends TurbineBaseService
        implements JsonRpcService
{
    /** The key used to store the bridge in the session. */
    public static final String JSON_BRIDGE_KEY = "JSONRPCBridge";

    @Override
    public Object processCall(CharArrayWriter cdata,
            JSONRPCBridge json_bridge, HttpServletRequest request)
    {
        return JSONProcessor.processCall(cdata, json_bridge, request);
    }

    @Override
    public void registerObjectGlobal(String key, Object value)
    {
        JSONRPCBridge.getGlobalBridge().registerObject(key, value);
    }

    @Override
    public void registerObject(HttpSession session, String key, Object value)
    {
        JSONRPCBridge json_bridge = getBridge(session);
        json_bridge.registerObject(key, value);
    }

    @Override
    public JSONRPCBridge getBridge(HttpSession session)
    {
        JSONRPCBridge json_bridge = (JSONRPCBridge) session.getAttribute(JSON_BRIDGE_KEY);
        if (json_bridge == null)
        {
            json_bridge = new JSONRPCBridge();
            session.setAttribute(JSON_BRIDGE_KEY, json_bridge);
        }
        return json_bridge;
    }

    @Override
    public void clearBridge(HttpSession session)
    {
        session.removeAttribute(JSON_BRIDGE_KEY);
    }

// The following is modeled on XmlRpcSercice.
//    /**
//     * Initialize the JsonRpcService.
//     *
//     * @throws InitializationException Something went wrong in the init stage.
//     */
//    public void init() throws InitializationException
//    {
//        //Configuration conf = getConfiguration();
//        setInit(true);
//    }
//
//    /**
//     * Shuts down this service, stopping running threads.
//     */
//    public void shutdown()
//    {
//        setInit(false);
//    }

}
