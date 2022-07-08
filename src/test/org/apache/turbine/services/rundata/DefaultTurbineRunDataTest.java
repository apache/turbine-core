package org.apache.turbine.services.rundata;


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

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.LocaleUtils;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DefaultTurbineRunDataTest extends BaseTestCase
{
    private static TurbineConfig tc = null;


    @Test public void testGetDefaultCharSetWithMimeType()
    {
        Turbine.getConfiguration().setProperty(
            TurbineConstants.LOCALE_DEFAULT_CHARSET_KEY,
            "");
		Turbine.getConfiguration().setProperty(
			  TurbineConstants.LOCALE_DEFAULT_COUNTRY_KEY,
			  "UK");
        assertEquals("ISO-8859-1", LocaleUtils.getDefaultCharset().name());
    }

    @BeforeClass
    public static void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
    }

    @AfterClass
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

}
