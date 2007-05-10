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

import org.apache.turbine.services.TurbineServices;

import com.metaparadigm.jsonrpc.JSONRPCBridge;

/**
 * This is a static accessor class for {@link JsonRpcService}.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public abstract class TurbineJsonRpc
{
    /**
     * Returns system's configured implementation of {@link JsonRpcService}.
     *
     * @return an implementation of <code>JsonRpcService</code>
     */
    public static JsonRpcService getService()
    {
        return (JsonRpcService) TurbineServices.getInstance()
                .getService(JsonRpcService.SERVICE_NAME);
    }

    public static Object processCall(CharArrayWriter cdata, 
            JSONRPCBridge json_bridge, HttpServletRequest request)
    {
        return getService().processCall(cdata, json_bridge, request);
    }

    public static void registerObject(HttpSession session, String key, Object value)
    {
        getService().registerObject(session, key, value);
    }

    public static void registerObjectGlobal(String key, Object value)
    {
        getService().registerObjectGlobal(key, value);
    }

    public static JSONRPCBridge getBridge(HttpSession session)
    {
        return getService().getBridge(session);
    }

    public static void clearBridge(HttpSession session)
    {
        getService().clearBridge(session);
    }
}
