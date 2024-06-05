package org.apache.turbine.util;

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
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.bag.HashBag;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Testing of the ObjectUtils class.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class ObjectUtilsTest extends BaseTestCase
{
    private TurbineConfig tc = null;

    @Before
    public void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();

        Turbine.getConfiguration().setProperty(TurbineConstants.SESSION_OBJECTINPUTFILTER,
            "!org.apache.commons.collections4.**");
    }

    @After
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    /**
     * Verify that we can filter classes to be deserialized
     *<p>
     * @throws Exception
     */
    @Test
    public void testDeserializationFilter()
        throws Exception
    {
	Map<String, Object> map = new HashMap<>();
	map.put("testKey1", new HashBag<String>()); // forbidden class
        map.put("testKey2", new Object()); // non-serializable
        map.put("testKey3", "actual Value");
        assertEquals(map.size(), 3);
	
        final byte[] serialized1 = ObjectUtils.serializeMap(map);
        assertNotNull(serialized1);

        Map<String, Object> result1 =  ObjectUtils.deserialize(serialized1);
        assertNull(result1); // Contains forbidden class
        
        map.remove("testKey1");
        assertEquals(map.size(), 2);

        final byte[] serialized2 = ObjectUtils.serializeMap(map);
        assertNotNull(serialized2);

        Map<String, Object> result2 =  ObjectUtils.deserialize(serialized2);
        assertNotNull(result2); // Does not contain forbidden class
        assertEquals(result2.size(), 1); // Non-serializable value skipped      
    }
}
