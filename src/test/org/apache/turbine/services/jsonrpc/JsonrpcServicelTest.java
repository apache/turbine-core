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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.test.BaseTurbineTest;

import com.metaparadigm.jsonrpc.JSONRPCBridge;


public class JsonrpcServicelTest
        extends BaseTurbineTest
{
    public JsonrpcServicelTest(String name)
            throws Exception
    {
        super(name, "conf/test/CompleteTurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(JsonrpcServicelTest.class);
    }

    public void testBridgeAccess()
    {
        JSONRPCBridge bridge = new JSONRPCBridge();
        assertNotNull(bridge);
    }

}
