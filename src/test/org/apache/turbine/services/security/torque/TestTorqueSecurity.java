package org.apache.turbine.services.security.torque;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.Turbine;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.services.security.TurbineSecurity;

import org.apache.turbine.test.HsqlDB;

import org.apache.turbine.test.BaseTurbineTest;

public class TestTorqueSecurity
        extends BaseTurbineTest
{
    public static final String DATA_SOURCE="turbine";

    private HsqlDB hsqlDB = null;

    public TestTorqueSecurity(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
        hsqlDB = new HsqlDB("jdbc:hsqldb:.", Turbine.getRealPath("conf/test/create-db.sql"));
    }

    public static Test suite()
    {
        return new TestSuite(TestTorqueSecurity.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();

        assertEquals("No Torque Security Service", ss.getClass(), TorqueSecurityService.class);
        assertEquals("No Torque User Manager", ss.getUserManager().getClass(), TorqueUserManager.class);
        assertTrue("Service failed to initialize", ss.getInit());
    }
}
