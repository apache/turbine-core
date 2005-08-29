package org.apache.turbine.services.security;

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

import java.util.Hashtable;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTurbineHsqlTest;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.UnknownEntityException;

public class TestSecurityUser
        extends BaseTurbineHsqlTest
{
    public TestSecurityUser(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityUser.class);
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

        List users = ss.getUserList(new org.apache.torque.util.Criteria());

        assertEquals("User DB Wrong!", users.size(), 2);
    }

    public void testUsers()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User u = um.retrieve("admin");
        assertNotNull("No Admin found!", u);
        assertEquals("Admin Id wrong!", u.getId(), 1);

        // Check Logged in
        assertFalse(u.hasLoggedIn());
        u.setHasLoggedIn(Boolean.TRUE);
        assertTrue(u.hasLoggedIn());
        u.setHasLoggedIn(Boolean.FALSE);
        assertFalse(u.hasLoggedIn());

        // Check perm and temp storage
        assertEquals(u.getPermStorage().getClass(), Hashtable.class);
        assertEquals(u.getTempStorage().getClass(), Hashtable.class);

        Hashtable permStorage = u.getPermStorage();

        int access = u.getAccessCounter();
        u.incrementAccessCounter();

        um.store(u);

        u = null;

        User u2 = um.retrieve("admin");


        // Hashtable has changed
        assertNotSame(permStorage, u2.getPermStorage());

        // But the Count should be the same
        assertEquals(access + 1 , u2.getAccessCounter());

        checkUserList();
    }

    public void testAddUser()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User newbie = TurbineSecurity.getUserInstance();
        newbie.setName("newbie");

        newbie.setFirstName("John");
        newbie.setLastName("Doe");

        ss.addUser(newbie, "newbie");

        List users = ss.getUserList(new org.apache.torque.util.Criteria());
        assertEquals("User was not added", users.size(), 3);

        try
        {
            User admin = ss.getUser("admin");

            ss.addUser(admin, "admin");
            fail("Existing User could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), EntityExistsException.class);
        }

        try
        {
            User empty = TurbineSecurity.getUserInstance();

            ss.addUser(empty, "empty");
            fail("User with empty Username could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), DataBackendException.class);
        }

        assertEquals("User was not added", users.size(), 3);
    }

    public void testRemoveUser()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        User newbie = ss.getUser("newbie");
        assertNotNull(newbie);

        ss.removeUser(newbie);

        try
        {
            User foo = TurbineSecurity.getUserInstance();
            foo.setName("foo");

            ss.removeUser(foo);
            fail("Non Existing User could be deleted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testRetrieve()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User u = um.retrieve("admin");
        assertNotNull("No Admin found!", u);
        assertEquals("Admin Id wrong!", u.getId(), 1);

        User u2 = um.retrieveById(new Integer(1));
        assertNotNull("No Admin found!", u2);
        assertEquals("Admin Name wrong!", u.getName(), "admin");

        assertEquals("Two different User objects retrieved!", u, u2);
    }
}

