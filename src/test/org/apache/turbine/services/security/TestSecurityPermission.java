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

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.test.BaseTurbineHsqlTest;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.UnknownEntityException;

public class TestSecurityPermission
        extends BaseTurbineHsqlTest
{
    public TestSecurityPermission(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityPermission.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertTrue("Service failed to initialize", ss.getInit());
    }

    public void testPermissionByName()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Permission permission = ss.getPermissionByName("Login");
        assertNotNull(permission);
        assertEquals(permission.getName(), "Login");
    }

    public void testPermissionById()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Permission permission = ss.getPermissionById(2);
        assertNotNull(permission);
        assertEquals(permission.getName(), "Application");
    }

    public void testAllPermissions()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        PermissionSet gs = ss.getAllPermissions();

        assertEquals(3, gs.size());
    }

    public void testAddPermission()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Permission newbie = ss.getPermissionInstance();
        newbie.setName("newbie");

        ss.addPermission(newbie);

        assertEquals("Permission was not added", 4, ss.getAllPermissions().size());

        try
        {
            Permission application = ss.getPermissionByName("Application");

            ss.addPermission(application);
            fail("Existing Permission could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), EntityExistsException.class, e.getClass());
        }

        try
        {
            Permission empty = ss.getPermissionInstance();

            ss.addPermission(empty);
            fail("Permission with empty Permissionname could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), DataBackendException.class, e.getClass());
        }

        assertEquals("Permission was not added", 4, ss.getAllPermissions().size());
    }

    public void testRemovePermission()
    	throws Exception
    {
    	SecurityService ss = TurbineSecurity.getService();

    	assertEquals("Permission was not added", 4, ss.getAllPermissions().size());

        Permission newbie = ss.getPermissionByName("newbie");
        assertNotNull(newbie);

        ss.removePermission(newbie);

        try
        {
            Permission foo = ss.getPermissionInstance();
            foo.setName("foo");

            ss.removePermission(foo);
            fail("Non Existing Permission could be deleted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        assertEquals("Permission was not removed", 3, ss.getAllPermissions().size());
    }

    public void testGrantPermission()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role admin = ss.getRoleByName("Admin");
        assertNotNull(admin);

        Permission app = ss.getPermissionByName("Application");
        assertNotNull(app);

        PermissionSet ps = admin.getPermissions();
        assertFalse(ps.contains(app));

        ss.grant(admin, app);

        Role admin2 = ss.getRoleByName("Admin");
        assertNotNull(admin2);

        PermissionSet ps2 = admin2.getPermissions();
        assertTrue(ps2.contains(app));

        // Get existing PermissionSet modified?
        assertFalse(ps.contains(app));

        try
        {
            ss.grant(admin2, app);
            fail("Permission could be granted twice!");
        }
        catch (Exception e)
        {
            //
            // Ugh. DataBackendError? This means that our query actually hit the database and only the "unique key"
            // prevented us from a double entry. This seems to be a bug
            //
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), DataBackendException.class, e.getClass());
        }

        try
        {
            Permission unknown = ss.getPermissionInstance("unknown");

            ss.grant(admin, unknown);
            fail("Nonexisting Permission could be granted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
        }

    }

    public void testRevokePermission()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role admin = ss.getRoleByName("Admin");
        assertNotNull(admin);

        Permission app = ss.getPermissionByName("Application");
        assertNotNull(app);

        PermissionSet ps = admin.getPermissions();
        assertTrue(ps.contains(app));

        ss.revoke(admin, app);

        Role admin2 = ss.getRoleByName("Admin");
        assertNotNull(admin2);

        PermissionSet ps2 = admin2.getPermissions();
        assertFalse(ps2.contains(app));

        // Get existing PermissionSet modified?
        assertTrue(ps.contains(app));

         try
         {
             Permission unknown = ss.getPermissionInstance("unknown");
             ss.revoke(admin, unknown);
             fail("Nonexisting Permission could be revoked!");
         }
         catch (Exception e)
         {
             assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
         }

//
// One can revoke an existing permission in an existing group, even if this permission was
// never granted to an role. While this is not really a bug, this might be
// something that should be checked in the long run.
//
//         try
//         {
//             ss.revoke(admin2, app);
//             fail("Permission could be revoked twice!");
//         }
//         catch (Exception e)
//         {
//             assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
//         }

//         try
//         {
//             Permission login = ss.getPermissionByName("Login");
//             ss.revoke(admin, login);
//             fail("Permission could be revoked from wrong group!");
//         }
//         catch (Exception e)
//         {
//             assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
//         }

    }

    public void testRevokeAll()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role user = ss.getRoleByName("User");
        assertNotNull(user);

        PermissionSet ps = user.getPermissions();
        assertEquals(2, ps.size());

        ss.revokeAll(user);

        Role user2 = ss.getRoleByName("User");
        assertNotNull(user2);

        PermissionSet ps2 = user2.getPermissions();
        assertEquals(0, ps2.size());
    }

    public void testSavePermission()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Permission application = ss.getPermissionByName("Application");

        ss.savePermission(application);

        try
        {
            Permission fake = ss.getPermissionInstance("fake");

            ss.savePermission(fake);
            fail("Non Existing Permission could be saved!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }
    }

    public void testRenamePermission()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Permission newbie = ss.getPermissionInstance("newbie");
        ss.addPermission(newbie);

        Permission test = ss.getPermissionByName("newbie");
        assertNotNull(test);

        ss.renamePermission(test, "fake");

        Permission fake = ss.getPermissionByName("fake");
        assertNotNull(fake);

//
// Now this is a Turbine Bug...
//
//          try
//          {
//              PermissionSet gs = ss.getPermissions(new org.apache.torque.util.Criteria());
//              assertEquals(4, gs.size());

//              ss.renamePermission(fake, "Application");

//              PermissionSet gs2 = ss.getPermissions(new org.apache.torque.util.Criteria());
//              assertEquals("Two permissions with the same name exist!", 3, gs2.size());

//              fail("Permission could be renamed to existing Permission and got lost from the database!");
//          }
//          catch (Exception e)
//          {
//              assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), EntityExistsException.class);
//          }
     }
}
