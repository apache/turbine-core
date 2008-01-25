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

import org.apache.turbine.test.BaseTestCase;

/**
 * Testing of the BrowserDetector class.
 *
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class BrowserDetectorTest extends BaseTestCase
{
    public BrowserDetectorTest(String name) throws Exception
    {
        super(name);
    }

    public void testFirefox()
    {
        String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8) Gecko/20051111 Firefox/1.5";
        BrowserDetector bd = new BrowserDetector(userAgent);
        assertEquals(BrowserDetector.MOZILLA, bd.getBrowserName());
        // Should this really be 5?
        assertEquals(5f, bd.getBrowserVersion(), 0.0f);
        assertEquals(BrowserDetector.WINDOWS, bd.getBrowserPlatform());
    }

    public void testOpera()
    {
        String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 8.02";
        BrowserDetector bd = new BrowserDetector(userAgent);
        assertEquals(BrowserDetector.OPERA, bd.getBrowserName());
        assertEquals(8.02f, bd.getBrowserVersion(), 0.0f);
        assertEquals(BrowserDetector.WINDOWS, bd.getBrowserPlatform());

        userAgent = "Opera/7.51 (Windows NT 5.1; U) [en]";
        bd = new BrowserDetector(userAgent);
        assertEquals(BrowserDetector.OPERA, bd.getBrowserName());
        assertEquals(7.51f, bd.getBrowserVersion(), 0.0f);
        assertEquals(BrowserDetector.WINDOWS, bd.getBrowserPlatform());
    }

    public void testIE()
    {
        String userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";
        BrowserDetector bd = new BrowserDetector(userAgent);
        assertEquals(BrowserDetector.MSIE, bd.getBrowserName());
        assertEquals(6.0f, bd.getBrowserVersion(), 0.0f);
        assertEquals(BrowserDetector.WINDOWS, bd.getBrowserPlatform());

        userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)";
        bd = new BrowserDetector(userAgent);
        assertEquals(BrowserDetector.MSIE, bd.getBrowserName());
        assertEquals(6.0f, bd.getBrowserVersion(), 0.0f);
        assertEquals(BrowserDetector.WINDOWS, bd.getBrowserPlatform());
    }
}
