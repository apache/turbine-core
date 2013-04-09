package org.apache.turbine.services.security.torque;

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

import java.util.Iterator;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.turbine.test.HsqlDB;

public class TestTorqueSecurity
        extends BaseTurbineTest
{
    public static final String DATA_SOURCE="turbine";

    private HsqlDB hsqlDB = null;

    public TestTorqueSecurity(String name)
            throws Exception
    {
        super(name, "conf/test/CompleteTurbineResources.properties");
        hsqlDB = new HsqlDB("jdbc:hsqldb:.", Turbine.getRealPath("conf/test/create-db.sql"));
    }

    public static Test suite()
    {
        return new TestSuite(TestTorqueSecurity.class);
    }

    public void testInit()
    {
        SecurityService ss = TurbineSecurity.getService();
        assertEquals("No Torque Security Service", TorqueSecurityService.class, ss.getClass());
        assertEquals("No Torque User Manager", TorqueUserManager.class, ss.getUserManager().getClass());
        assertTrue("Service failed to initialize", ss.getInit());
    }

    public void testAcccountExists() throws Throwable 
    {
        List users = null;
        assertTrue(TurbineSecurity.accountExists("user"));
        Criteria criteria = new Criteria();
        criteria.add(UserPeerManager.getNameColumn(), "user");
        String query = BasePeer.createQueryString(criteria);
        assertTrue(query.contains("FROM TURBINE_USER WHERE TURBINE_USER.LOGIN_NAME='user'"));
        for (Iterator keys = criteria.keySet().iterator(); keys.hasNext();) {
            String key = (String) keys.next();
            assertEquals("TURBINE_USER.LOGIN_NAME", key);
            assertEquals("TURBINE_USER", UserPeerManager.getTableName());
            Criteria.Criterion[] criterion = criteria.getCriterion(key)
                    .getAttachedCriterion();
            assertTrue("one single condition  exptected", criterion.length == 1);
            users = UserPeerManager.doSelect(criteria);
            assertTrue("should be unique result", users.size() == 1);
            for (Iterator userx = users.iterator(); userx.hasNext();) {
                User usr = (User) userx.next();
                assertTrue(usr != null);
                assertTrue(usr instanceof TorqueObject);
                assertTrue(usr.getName().equals("user"));
                assertTrue(usr.getPassword().equals("user"));
            }
        }
    }
}
