package org.apache.turbine.modules.screens;

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

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.jsonrpc.TurbineJsonRpc;
import org.apache.turbine.util.RunData;

import com.metaparadigm.jsonrpc.JSONRPCBridge;

/**
 * A Screen class for dealing with JSON-RPC requests.  Typically you would
 * extend this class and override the doOutput() method to use TurbineJsonRpc
 * to register the POJOs that will provide the functions you are making
 * available via JSON-RPC.  Use JSONSecureScreen if you need the user to be
 * logged in prior to executing the functions you provide.
 *
 * <p>Here is an example from a superclass:
 * <code>
 * public void doOutput(RunData data) throws Exception
 * {
 *     User user = data.getUser();
 *
 *     MyJsonFunctions myFunctions = new MyJsonFunctions(user.getName());
 *
 *     // Session specific
 *     TurbineJsonRpc.registerObject(data.getSession(), "myFunctions", myFunctions);
 *
 *     // Global
 *     //TurbineJsonRpc.registerObjectGlobal("testGlobal", testObject);
 *
 *     super.doOutput(data);
 * }
 * </code>
 * 
 * <p>The class MyFunctions would be something like:
 * <code>
 * public class MyJsonFunctions
 * {
 *     private String getHello(String clientParameter)
 *     {
 *         return "Hello " + clientParameter;
 *     }
 * }
 * </code>
 *
 * <p>This code is derived from the com.metaparadigm.jsonrpc.JSONRPCServlet
 *
 * @author brad@folkens.com
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class JSONScreen extends RawScreen
{
    protected static final String JSONRPC_CONTENT_TYPE = "text/plain";

    protected final static int BUFFER_SIZE = 4096;

    /**
     * @see org.apache.turbine.modules.screens.RawScreen#getContentType(org.apache.turbine.util.RunData)
     * @deprecated Use PipelineData version instead.
     */
    protected String getContentType(RunData data)
    {
        return JSONRPC_CONTENT_TYPE;
    }

    /**
     * @see org.apache.turbine.modules.screens.RawScreen#getContentType(org.apache.turbine.pipeline.PipelineData)
     */
    protected String getContentType(PipelineData pipelineData)
    {
        return JSONRPC_CONTENT_TYPE;
    }
    
    /**
     * @see org.apache.turbine.modules.screens.RawScreen#doOutput(org.apache.turbine.util.RunData)
     */
    
    /**
     * Output the dynamic content.
     *
     * @param data The RunData object.
     * @deprecated Use PipelineData version instead.
     */
    protected void doOutput(RunData data) throws Exception
    {
        data.declareDirectResponse();
        HttpServletRequest request = data.getRequest();
        
        //String charset = request.getCharacterEncoding();
        //if(charset == null)
        //{
        //    charset = "UTF-8";
        //}
        //BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), charset));
        BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));

        // Read the request
        CharArrayWriter cdata = new CharArrayWriter();
        char buf[] = new char[BUFFER_SIZE];
        int ret;
        while ((ret = in.read(buf, 0, BUFFER_SIZE)) != -1)
        {
            cdata.write(buf, 0, ret);
        }

        // Find the JSONRPCBridge for this session or create one
        // if it doesn't exist
        JSONRPCBridge json_bridge = TurbineJsonRpc.getBridge(data.getSession());

        // Process the request
        Object json_res = TurbineJsonRpc.processCall(cdata, json_bridge, request);

        PrintWriter out = new PrintWriter(
                new OutputStreamWriter(data.getResponse().getOutputStream()));
        out.print(json_res.toString());
        out.flush();
        out.close();
    }
}
