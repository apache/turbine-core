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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineAccessControlList;
import org.apache.turbine.util.security.UnknownEntityException;

import org.apache.turbine.services.security.torque.TorqueGroup;
import org.apache.turbine.services.security.torque.TorquePermission;
import org.apache.turbine.services.security.torque.TorqueRole;
import org.apache.turbine.services.security.torque.TorqueUser;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.turbine.test.HsqlDB;

public class TestSecurity
        extends BaseTurbineTest
{
    private HsqlDB hsqlDB = null;

    public TestSecurity(String name)
            throws Exception
    {
        super(name);
        hsqlDB = new HsqlDB("jdbc:hsqldb:.", Turbine.getRealPath("conf/test/create-db.sql"));
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurity.class);
    }

    private void checkUserList()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        assertEquals("User added to storage!", ss.getUserList(new org.apache.torque.util.Criteria()).size(), 2);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertTrue("Service failed to initialize", ss.getInit());
    }

    // Make sure that our database contains what we need
    public void testDatabase()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        GroupSet gs = ss.getAllGroups();
        RoleSet rs = ss.getAllRoles();
        PermissionSet ps = ss.getAllPermissions();

        List users = ss.getUserManager().retrieveList(new org.apache.torque.util.Criteria());

        assertEquals("Group DB Wrong!", gs.size(), 2);
        assertEquals("Role DB Wrong!", rs.size(), 2);
        assertEquals("Permission DB Wrong!", ps.size(), 3);
    }

    public void testClasses()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        assertEquals("Class for User Objects is wrong!",       ss.getUserClass(),       TorqueUser.class);
        assertEquals("Class for Group Objects is wrong!",      ss.getGroupClass(),      TorqueGroup.class);
        assertEquals("Class for Role Objects is wrong!",       ss.getRoleClass(),       TorqueRole.class);
        assertEquals("Class for Permission Objects is wrong!", ss.getPermissionClass(), TorquePermission.class);

        assertEquals("Class for ACLs is wrong!",               ss.getAclClass(),        TurbineAccessControlList.class);
    }

    public void testInstance()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        assertEquals("Instance for User Objects is wrong!",       ss.getUserInstance().getClass(),       TorqueUser.class);
        assertEquals("Instance for Group Objects is wrong!",      ss.getGroupInstance().getClass(),      TorqueGroup.class);
        assertEquals("Instance for Role Objects is wrong!",       ss.getRoleInstance().getClass(),       TorqueRole.class);
        assertEquals("Instance for Permission Objects is wrong!", ss.getPermissionInstance().getClass(), TorquePermission.class);
    }

    public void testUserExists()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        assertTrue(ss.accountExists("admin"));
        assertFalse(ss.accountExists("does-not-exist"));

        checkUserList();
    }

    public void testAuthenticateUser()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getAuthenticatedUser("admin", "admin");

        try
        {
            admin = ss.getAuthenticatedUser("admin", "no such password");
            fail("User was authenticated with wrong password");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), PasswordMismatchException.class);
        }

        checkUserList();
    }

    public void testGetUser()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");

        try
        {
            User newbie = ss.getUser("newbie");
            fail("Non Existing User could be loaded!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testUserLists()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User [] users = ss.getUsers(new org.apache.torque.util.Criteria());
        assertNotNull(users);
        assertEquals("Wrong number of users retrieved!", users.length, 2);


        List userList = ss.getUserList(new org.apache.torque.util.Criteria());
        assertNotNull(userList);
        assertEquals("Wrong number of users retrieved!", userList.size(), 2);

        assertEquals("Array and List have different sizes!", users.length, userList.size());

        checkUserList();
    }

    public void testAnonymousUser()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User user = ss.getAnonymousUser();

        assertNotNull(user);

        assertTrue(ss.isAnonymousUser(user));

        user = ss.getUser("admin");
        assertNotNull(user);

        assertFalse(ss.isAnonymousUser(user));
    }

    public void testSaveUser()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");

        ss.saveUser(admin);

        try
        {
            User newbie = TurbineSecurity.getUserInstance();
            newbie.setName("newbie");

            ss.saveUser(newbie);
            fail("Non Existing User could be stored!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testChangePassword()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        ss.changePassword(admin, admin.getPassword(), "foobar");

        User admin2 = ss.getUser("admin");
        assertEquals("Password was not changed!", "foobar", admin2.getPassword());

        try
        {
            admin = ss.getUser("admin");
            ss.changePassword(admin, "admin", "foobar");
            fail("Password could be changed without old password!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), PasswordMismatchException.class);
        }

        admin2 = ss.getUser("admin");
        assertEquals("Password was changed!", "foobar", admin2.getPassword());

        checkUserList();
    }

    public void testForcePassword()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User admin = ss.getUser("admin");
        assertNotNull(admin);

        ss.forcePassword(admin, "barbaz");

        User admin2 = ss.getUser("admin");
        assertEquals("Password was not changed!", "barbaz", admin2.getPassword());

        ss.forcePassword(admin, "admin");

        admin2 = ss.getUser("admin");
        assertEquals("Password was not reset!", "admin", admin2.getPassword());


        checkUserList();
    }
}

