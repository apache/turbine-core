package org.apache.turbine.services.cache;

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

import org.apache.fulcrum.cache.CachedObject;
import org.apache.fulcrum.cache.GlobalCacheService;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
/**
 * Unit test for Accessing the Fulcrum Cache component via the
 * CacheServiceFacade and the Component within Turbine.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class FulcrumCacheComponentTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public FulcrumCacheComponentTest(String name) throws Exception
    {
        super(name);
    }
    public void testComponentAndFacaded() throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
		GlobalCacheService cache = (GlobalCacheService)serviceManager.getService(GlobalCacheService.ROLE);
		CachedObject inputObject = new CachedObject(new Double(10.2));
		cache.addObject("testObj",inputObject);
    }

    public void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
    }
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
