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

import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.UnknownEntityException;

import org.apache.turbine.test.BaseTurbineTest;
import org.apache.turbine.test.HsqlDB;

public class TestSecurityGroup
        extends BaseTurbineTest
{
    private HsqlDB hsqlDB = null;

    public TestSecurityGroup(String name)
            throws Exception
    {
        super(name);
        hsqlDB = new HsqlDB("jdbc:hsqldb:.", Turbine.getRealPath("conf/test/create-db.sql"));
    }

    public static Test suite()
    {
        return new TestSuite(TestSecurityGroup.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertTrue("Service failed to initialize", ss.getInit());
    }

    public void testGroupByName()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Group role = ss.getGroupByName("Turbine");
        assertNotNull(role);
        assertEquals("Turbine", role.getName());
    }

    public void testGroupById()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Group role = ss.getGroupById(2);
        assertNotNull(role);
        assertEquals("Turbine", role.getName());
    }

    public void testAllGroups()
            throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        GroupSet gs = ss.getAllGroups();

        assertEquals(2, gs.size());
    }

    public void testAddGroup()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Group newbie = ss.getGroupInstance();
        newbie.setName("newbie");

        ss.addGroup(newbie);

        assertEquals("Group was not added", 3, ss.getAllGroups().size());

        try
        {
            Group turbine = ss.getGroupByName("Turbine");

            ss.addGroup(turbine);
            fail("Existing Group could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), EntityExistsException.class, e.getClass());
        }

        try
        {
            Group empty = ss.getGroupInstance();

            ss.addGroup(empty);
            fail("Group with empty Groupname could be added!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), DataBackendException.class, e.getClass());
        }

        assertEquals("Group was not added", 3, ss.getAllGroups().size());
    }

    public void testRemoveGroup()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        assertEquals("Group was not added", 3, ss.getAllGroups().size());

        Group newbie = ss.getGroupByName("newbie");
        assertNotNull(newbie);

        ss.removeGroup(newbie);

        try
        {
            Group foo = ss.getGroupInstance();
            foo.setName("foo");

            ss.removeGroup(foo);
            fail("Non Existing Group could be deleted!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }

        assertEquals("Group was not removed", 2, ss.getAllGroups().size());
    }

    public void testSaveGroup()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Group turbine = ss.getGroupByName("Turbine");

        ss.saveGroup(turbine);

        try
        {
            Group fake = ss.getGroupInstance("fake");

            ss.saveGroup(fake);
            fail("Non Existing Group could be saved!");
        }
        catch (Exception e)
        {
            assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), UnknownEntityException.class);
        }
    }

    public void testRenameGroup()
    	throws Exception
    {
        SecurityService ss = TurbineSecurity.getService();

        Group newbie = ss.getGroupInstance("newbie");
        ss.addGroup(newbie);

        Group test = ss.getGroupByName("newbie");
        assertNotNull(test);

        ss.renameGroup(test, "fake");

        Group fake = ss.getGroupByName("fake");
        assertNotNull(fake);

//
// Now this is a Turbine Bug...
//
//         try
//         {
//             GroupSet gs = ss.getGroups(new org.apache.torque.util.Criteria());
//             assertEquals(3, gs.size());

//             ss.renameGroup(fake, "Turbine");

//             GroupSet gs2 = ss.getGroups(new org.apache.torque.util.Criteria());
//             assertEquals("Two groups with the same name exist!", 2, gs2.size());

//             fail("Group could be renamed to existing Group and got lost from the database!");
//         }
//         catch (Exception e)
//         {
//             assertEquals("Wrong Exception thrown: " + e.getClass().getName(), e.getClass(), EntityExistsException.class);
//         }
    }
}
