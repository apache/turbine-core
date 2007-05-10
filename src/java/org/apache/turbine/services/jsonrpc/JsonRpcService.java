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

import com.metaparadigm.jsonrpc.JSONRPCBridge;

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
    public static final String SERVICE_NAME = "JsonRpcService";

    public Object processCall(CharArrayWriter cdata,
            JSONRPCBridge json_bridge, HttpServletRequest request);

    public void registerObject(HttpSession session, String key, Object value);

    public void registerObjectGlobal(String key, Object value);

    public JSONRPCBridge getBridge(HttpSession session);

    public void clearBridge(HttpSession session);
}
