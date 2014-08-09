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

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.metaparadigm.jsonrpc.JSONRPCBridge;

public class JsonrpcServicelTest
        extends BaseTestCase
{
    private static TurbineConfig turbineConfig = null;


    @BeforeClass
    public static void setUp() throws Exception {
//        serviceManager = TurbineServices.getInstance();
//        serviceManager.setApplicationRoot(".");

        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put(TurbineConfig.PROPERTIES_PATH_KEY, "conf/test/CompleteTurbineResources.properties"); // "conf/test/TurbineResources.properties"
        initParams.put(TurbineConstants.LOGGING_ROOT_KEY, "target/test-logs");

        turbineConfig = new TurbineConfig(".", initParams);
        turbineConfig.initialize();
    }

    @AfterClass
    public static void destroy() throws Exception {
        turbineConfig.dispose();
    }


    @Test public void testBridgeAccess()
    {
        JSONRPCBridge bridge = new JSONRPCBridge();
        assertNotNull(bridge);
    }

}
