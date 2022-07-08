package org.apache.turbine.om;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * This class tests the {@link OMTool} functionality.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @deprecated OMTool is deprecated
 */
@Deprecated
public class OMToolTest
{
    private static TurbineConfig tc = null;
    private OMTool om;

    @BeforeClass
    public static void init() {
        tc = new TurbineConfig(
                            ".",
                            "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();
    }

    @Before
    public void setUpBefore() throws Exception
    {
        om = new OMTool();
    }

    @Test
    public void testGetString() throws Exception
    {
        assertNotNull("RetrieverFactory should not be null", om.omFactory);
        OMTool.PullHelper ph1 = om.get("test1");
        assertNotNull("PullHelper should not be null", ph1);
        OMTool.PullHelper ph2 = om.get("test2");
        assertNotNull("PullHelper should not be null", ph2);
        assertNotSame("Should not be same instance", ph1, ph2);
        OMTool.PullHelper ph3 = om.get("test2");
        assertNotNull("PullHelper should not be null", ph3);
        assertSame("Should be same instance", ph3, ph2);
    }

    @Test
    public void testGetStringString() throws Exception
    {
        Object testString1 = om.get("test1", "testString");
        assertNotNull("Object should not be null", testString1);
        assertTrue("Object should be a string", testString1 instanceof String);
        assertEquals("Object should be a string", "testString", testString1);
        Object testString2 = om.get("test1", "testString");
        assertNotNull("Object should not be null", testString2);
        assertSame("Should be same instance", testString1, testString2);
    }

    @AfterClass
    public static void destroy()
    {
        tc.dispose();
    }
}
