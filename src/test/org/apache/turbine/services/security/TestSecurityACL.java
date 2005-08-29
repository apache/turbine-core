package org.apache.turbine.services.security;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTurbineHsqlTest;
import org.apache.turbine.util.security.AccessControlList;

public class TestSecurityACL
        extends BaseTurbineHsqlTest
{
    public TestSecurityACL(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityACL.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertTrue("Service failed to initialize", ss.getInit());
    }

    public void testAcl1()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        AccessControlList acl = ss.getACL(admin);
        assertNotNull(acl);

        assertFalse(acl.hasRole("Admin", "global"));
        assertTrue(acl.hasRole("Admin", "Turbine"));
        assertFalse(acl.hasRole("User", "global"));
        assertFalse(acl.hasRole("User", "Turbine"));

        assertFalse(acl.hasPermission("Admin", "global"));
        assertTrue(acl.hasPermission("Admin", "Turbine"));
        assertFalse(acl.hasPermission("Login", "global"));
        assertFalse(acl.hasPermission("Login", "Turbine"));
        assertFalse(acl.hasPermission("Application", "global"));
        assertFalse(acl.hasPermission("Application", "Turbine"));
    }

    public void testAcl2()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("user");
        assertNotNull(admin);

        AccessControlList acl = ss.getACL(admin);
        assertNotNull(acl);

        assertFalse(acl.hasRole("Admin", "global"));
        assertFalse(acl.hasRole("Admin", "Turbine"));
        assertFalse(acl.hasRole("User", "global"));
        assertTrue(acl.hasRole("User", "Turbine"));

        assertFalse(acl.hasPermission("Admin", "global"));
        assertFalse(acl.hasPermission("Admin", "Turbine"));
        assertFalse(acl.hasPermission("Login", "global"));
        assertTrue(acl.hasPermission("Login", "Turbine"));
        assertFalse(acl.hasPermission("Application", "global"));
        assertTrue(acl.hasPermission("Application", "Turbine"));
    }

    public void testAcl3()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User user = ss.getUser("user");
        assertNotNull(user);

        AccessControlList acl = ss.getACL(user);
        assertNotNull(acl);

        Group turbine = ss.getGroupByName("Turbine");
        assertNotNull(turbine);

        assertEquals(0, acl.getRoles().size());
        assertEquals(1, acl.getRoles(turbine).size());
        assertEquals(0, acl.getPermissions().size());
        assertEquals(2, acl.getPermissions(turbine).size());
    }

}

