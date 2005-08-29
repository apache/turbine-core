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

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.om.security.User;
import org.apache.turbine.test.BaseTurbineHsqlTest;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.UnknownEntityException;

public class TestSecurityUserManager
        extends BaseTurbineHsqlTest
{
    public TestSecurityUserManager(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityUserManager.class);
    }

    private void checkUserList()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();
        assertEquals("User added to storage!", um.retrieveList(new org.apache.torque.util.Criteria()).size(), 2);
    }

    public void testUserManager1()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        assertTrue(um.accountExists("admin"));
        assertFalse(um.accountExists("does-not-exist"));

        User admin = um.retrieve("admin");
        assertTrue(um.accountExists(admin));

        User doesNotExist = TurbineSecurity.getUserInstance();
        assertFalse(um.accountExists(doesNotExist));

        checkUserList();
    }

    public void testUserManager2()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin");

        try
        {
            User doesNotExist = um.retrieve("does-not-exist");
            fail("Non existing Account was retrieved");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testUserManager3()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin", "admin");

        try
        {
            admin = um.retrieve("admin", "no such password");
            fail("User was authenticated with wrong password");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), PasswordMismatchException.class);
        }

        checkUserList();
    }

    public void testUserManager4()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin");
        um.store(admin);

        try
        {
            User newbie = TurbineSecurity.getUserInstance();
            newbie.setName("newbie");

            um.store(newbie);
            fail("Non Existing User could be stored!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testUserManager5()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin");

        um.authenticate(admin, "admin");

        try
        {
            User newbie = TurbineSecurity.getUserInstance();
            newbie.setName("newbie");

            um.authenticate(newbie, "somePw");
            fail("User was authenticated with wrong password");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testUserManager6()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User [] users = um.retrieve(new org.apache.torque.util.Criteria());
        assertNotNull(users);
        assertEquals("Wrong number of users retrieved!", users.length, 2);

        List userList = um.retrieveList(new org.apache.torque.util.Criteria());
        assertNotNull(userList);
        assertEquals("Wrong number of userList retrieved!", userList.size(), 2);

        assertEquals("Array and List have different sizes!", users.length, userList.size());

        checkUserList();
    }

    public void testUserManager7()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieveById(new Integer(1));

        try
        {
            User doesNotExist = um.retrieveById(new Integer(667));
            fail("Non existing Account was retrieved");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        checkUserList();
    }

    public void testAddUser()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User newbie = TurbineSecurity.getUserInstance();
        newbie.setName("newbie");

        newbie.setFirstName("John");
        newbie.setLastName("Doe");

        um.createAccount(newbie, "newbie");

        List users = um.retrieveList(new org.apache.torque.util.Criteria());
        assertEquals("User was not added", users.size(), 3);

        try
        {
            User admin = um.retrieve("admin");

            um.createAccount(admin, "admin");
            fail("Existing User could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), EntityExistsException.class);
        }

        try
        {
            User empty = TurbineSecurity.getUserInstance();

            um.createAccount(empty, "empty");
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
        UserManager um = ss.getUserManager();

        User newbie = um.retrieve("newbie");
        assertNotNull(newbie);

        um.removeAccount(newbie);

        try
        {
            User foo = TurbineSecurity.getUserInstance();
            foo.setName("foo");

            um.removeAccount(foo);
            fail("Non Existing User could be deleted!");
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
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin");
        assertNotNull(admin);

        um.changePassword(admin, admin.getPassword(), "foobar");

        User admin2 = um.retrieve("admin");
        assertEquals("Password was not changed!", "foobar", admin2.getPassword());

        try
        {
            admin = um.retrieve("admin");
            um.changePassword(admin, "admin", "foobar");
            fail("Password could be changed without old password!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), PasswordMismatchException.class);
        }

        admin2 = um.retrieve("admin");
        assertEquals("Password was changed!", "foobar", admin2.getPassword());

        checkUserList();
    }

    public void testForcePassword()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();
        UserManager um = ss.getUserManager();

        User admin = um.retrieve("admin");
        assertNotNull(admin);

        um.forcePassword(admin, "barbaz");

        User admin2 = um.retrieve("admin");
        assertEquals("Password was not changed!", "barbaz", admin2.getPassword());

        um.forcePassword(admin, "admin");

        admin2 = um.retrieve("admin");
        assertEquals("Password was not reset!", "admin", admin2.getPassword());


        checkUserList();
    }
}

