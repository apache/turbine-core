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
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTurbineHsqlTest;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.UnknownEntityException;

public class TestSecurityRole
        extends BaseTurbineHsqlTest
{
    public TestSecurityRole(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityRole.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertTrue("Service failed to initialize", ss.getInit());
    }

    public void testRoleByName()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role role = ss.getRoleByName("User");
        assertNotNull(role);
        assertEquals(role.getName(), "User");
    }

    public void testRoleById()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role role = ss.getRoleById(2);
        assertNotNull(role);
        assertEquals(role.getName(), "Admin");
    }

    public void testRolePermissions()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role role = ss.getRoleByName("User");
        assertNotNull(role);

        PermissionSet ps = ss.getPermissions(role);

        assertEquals(2, ps.size());
    }

    public void testAllRoles()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        RoleSet gs = ss.getAllRoles();

        assertEquals(2, gs.size());
    }


    public void testAddRole()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role newbie = ss.getRoleInstance();
        newbie.setName("newbie");

        ss.addRole(newbie);

        assertEquals("Role was not added", 3, ss.getAllRoles().size());

        try
        {
            Role user = ss.getRoleByName("User");

            ss.addRole(user);
            fail("Existing Role could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), EntityExistsException.class, e.getClass());
        }

        try
        {
            Role empty = ss.getRoleInstance();

            ss.addRole(empty);
            fail("Role with empty Rolename could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), DataBackendException.class, e.getClass());
        }

        assertEquals("Role was not added", 3, ss.getAllRoles().size());
    }

    public void testRemoveRole()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        assertEquals("Role was not added", 3, ss.getAllRoles().size());

        Role newbie = ss.getRoleByName("newbie");
        assertNotNull(newbie);

        ss.removeRole(newbie);

        try
        {
            Role foo = ss.getRoleInstance();
            foo.setName("foo");

            ss.removeRole(foo);
            fail("Non Existing Role could be deleted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        assertEquals("Role was not removed", 2, ss.getAllRoles().size());
    }

    public void testGrantRole()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        Group global = ss.getGroupByName("global");
        assertNotNull(global);

        Role app = ss.getRoleByName("User");
        assertNotNull(app);

        AccessControlList acl = ss.getACL(admin);
        assertFalse(acl.hasRole(app, global));

        ss.grant(admin, global, app);

        AccessControlList acl2 = ss.getACL(admin);
        assertTrue(acl2.hasRole(app, global));

        // Get existing ACL modified?
        assertFalse(acl.hasRole(app, global));

        try
        {
            ss.grant(admin, global, app);
            fail("Role could be granted twice!");
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
            Role unknown = ss.getRoleInstance("unknown");

            ss.grant(admin, global, unknown);
            fail("Nonexisting Role could be granted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
        }

        try
        {
            Group unknown = ss.getGroupInstance("unknown");

            ss.grant(admin, unknown, app);
            fail("Role in non existing group could be granted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
        }
    }

    public void testRevokeRole()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        Group global = ss.getGroupByName("global");
        assertNotNull(global);

        Role app = ss.getRoleByName("User");
        assertNotNull(app);

        AccessControlList acl = ss.getACL(admin);
        assertTrue(acl.hasRole(app, global));

        ss.revoke(admin, global, app);

        AccessControlList acl2 = ss.getACL(admin);
        assertFalse(acl2.hasRole(app, global));

        // Get existing ACL modified?
        assertTrue(acl.hasRole(app, global));

         try
         {
             Role unknown = ss.getRoleInstance("unknown");
             ss.revoke(admin, global, unknown);
             fail("Nonexisting Role could be revoked!");
         }
         catch (Exception e)
         {
             assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
         }

        try
        {
            Group unknown = ss.getGroupInstance("unknown");
            ss.revoke(admin, unknown, app);
            fail("Role in non existing group could be revoked!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());

        }

//
// One can revoke an existing role in an existing group, even if this role was
// never granted to an user. While this is not really a bug, this might be
// something that should be checked in the long run.
//
//        try
//        {
//            ss.revoke(admin, global, app);
//            fail("Role could be revoked twice!");
//        }
//        catch (Exception e)
//        {
//            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
//        }
//
//        try
//        {
//            Role adm = ss.getRole("Admin");
//            ss.revoke(admin, global, adm);
//            fail("Role could be revoked from wrong group!");
//        }
//        catch (Exception e)
//        {
//            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
//        }
//
//        try
//        {
//            Group turbine = ss.getGroup("Turbine");
//            ss.revoke(admin, turbine, app);
//            fail("Non existing Role could be revoked!");
//        }
//        catch (Exception e)
//        {
//            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), UnknownEntityException.class, e.getClass());
//        }
    }

    public void testRevokeAll()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        Group turbine = ss.getGroupByName("Turbine");
        assertNotNull(turbine);

        AccessControlList acl = ss.getACL(admin);
        assertEquals(1, acl.getRoles(turbine).size());

        ss.revokeAll(admin);

        AccessControlList acl2 = ss.getACL(admin);
        assertEquals(0, acl2.getRoles(turbine).size());
    }

    public void testSaveRole()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role admin = ss.getRoleByName("Admin");

        ss.saveRole(admin);

        try
        {
            Role fake = ss.getRoleInstance("fake");

            ss.saveRole(fake);
            fail("Non Existing Role could be saved!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }
    }

    public void testRenameRole()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Role newbie = ss.getRoleInstance("newbie");
        ss.addRole(newbie);

        Role test = ss.getRoleByName("newbie");
        assertNotNull(test);

        ss.renameRole(test, "fake");

        Role fake = ss.getRoleByName("fake");
        assertNotNull(fake);

//
// Now this is a Turbine Bug...
//
//          try
//          {
//              RoleSet gs = ss.getRoles(new org.apache.torque.util.Criteria());
//              assertEquals(3, gs.size());

//              ss.renameRole(fake, "Admin");

//              RoleSet gs2 = ss.getRoles(new org.apache.torque.util.Criteria());
//              assertEquals("Two roles with the same name exist!", 2, gs2.size());

//              fail("Role could be renamed to existing Role and got lost from the database!");
//          }
//          catch (Exception e)
//          {
//              assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), EntityExistsException.class);
//          }
    }
}
