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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jabsorb.JSONRPCBridge;
import org.jabsorb.JSONRPCResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Process a JSON RPC call
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 */
public class JSONProcessor
{
    /** Log. */
    private static Logger log = LogManager.getLogger(JSONProcessor.class);

    /**
     * Process a JSON RPC call
     * @param cdata the JSON data
     * @param json_bridge the {@link JSONRPCBridge} object
     * @param request the request
     * @return the return object of the JSON RPC call
     */
    public static Object processCall(CharArrayWriter cdata, JSONRPCBridge json_bridge, HttpServletRequest request)
    {
        // Process the request
        JSONObject json_req = null;
        Object json_res = null;
        try
        {
            json_req = new JSONObject(cdata.toString());
            if (log.isDebugEnabled())
            {
                String methodName = json_req.getString("method");
                JSONArray arguments = json_req.getJSONArray("params");

                // If this a CallableReference it will have a non-zero objectID
                int object_id = json_req.optInt("objectID");
                StringBuilder sb = new StringBuilder(".doprocessCall(): call ");
                if (object_id != 0)
                {
                    sb.append("objectID=").append(object_id).append(" ");
                }
                sb.append(methodName).append("(").append(arguments).append(")");
                log.debug(sb.toString());
            }
            //json_res = json_bridge.call(new Object[] {request}, object_id, methodName, arguments);
            json_res = json_bridge.call(new Object[] {request}, json_req);
        }
        catch (JSONException e)
        {
            log.error(".processCall(): can't parse call: {}", cdata, e);
            json_res = JSONRPCResult.MSG_ERR_PARSE;
        }
        // Write the response
        log.debug(".processCall():  returns ", json_res::toString);
        return json_res;
    }

}
