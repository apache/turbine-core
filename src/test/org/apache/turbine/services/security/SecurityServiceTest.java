package org.apache.turbine.services.security;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.fulcrum.security.SecurityService;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.TurbineModelManager;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUserGroupRole;
import org.apache.fulcrum.security.model.turbine.entity.impl.TurbineUserImpl;
import org.apache.fulcrum.security.model.turbine.test.AbstractTurbineModelManagerTest;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.testcontainer.BaseUnit5Test;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test that the SecurityService works properly by comparing behaviour of Turbine and Fulcrum security services using memory user manager.
 *
 * Code adapted from SecurityServiceAdapter in Fulcrum Security Adapter
 *
 * @author gkallidis
 * @version $Id$
 */

public class SecurityServiceTest extends BaseUnit5Test
{

    SecurityService fulcrumSecurityService;
    org.apache.turbine.services.security.SecurityService securityService;
    static TurbineConfig tc;


    @BeforeAll
    public static void init() throws Exception
    {
    	tc = new TurbineConfig(".", "/conf/test/CompleteTurbineResources.properties");
        tc.initialize();

    }

    @BeforeEach
    public void setUpBefore() throws Exception
    {

        ServiceManager serviceManager = TurbineServices.getInstance();
        //
        fulcrumSecurityService = (SecurityService) serviceManager.getService(SecurityService.ROLE);

        securityService = (org.apache.turbine.services.security.SecurityService)
        		TurbineServices.getInstance().getService(org.apache.turbine.services.security.SecurityService.SERVICE_NAME);
    }

    @Test
    public void testAccountExists() throws Exception
    {

        User user = new org.apache.turbine.om.security.DefaultUserImpl(new TurbineUserImpl());
        user.setAccessCounter(5);

        assertFalse(securityService.accountExists(user));
        assertFalse(fulcrumSecurityService.getUserManager().checkExists(user));

    }
    @Test
	public void testCreateUser() throws Exception
	{

		User user = new org.apache.turbine.om.security.DefaultUserImpl(new TurbineUserImpl());
		user.setAccessCounter(5);
		user.setName("ringo");
		securityService.addUser(user,"fakepasswrod");
		assertTrue(securityService.accountExists(user));
		assertTrue(fulcrumSecurityService.getUserManager().checkExists(user));

	}

    /**
     * Tests Turbine and Fulcrum.
     *
     * Fulcrum part is similar/duplicated from {@link AbstractTurbineModelManagerTest#testGrantUserGroupRole()}
     *
     *
     * @throws Exception
     */
    @Test
    public void testGrantUserGroupRole() throws Exception
    {
        Group group = fulcrumSecurityService.getGroupManager().getGroupInstance();
        group.setName("TEST_GROUP");
        fulcrumSecurityService.getGroupManager().addGroup(group);
        Role role = fulcrumSecurityService.getRoleManager().getRoleInstance();
        role.setName("TEST_Role");
        fulcrumSecurityService.getRoleManager().addRole(role);

        //  Turbine security service returns a wrapped instance: org.apache.turbine.om.security.DefaultUserImpl
        // which implements org.apache.turbine.om.security.User and contains
        User user = securityService.getUserInstance("Clint");
		// memory
        securityService.addUser(user, "clint");
        securityService.grant(user, group, role);

		addUserAndCheck(group, role, user.getUserDelegate());

        // Fulcrum security service returns a raw org.apache.fulcrum.security.model.turbine.entity.impl.TurbineUserImpl,
		org.apache.fulcrum.security.UserManager  userManager = fulcrumSecurityService.getUserManager();
		TurbineUser fulcrumUser = userManager.getUserInstance("Clint2");
        userManager.addUser(fulcrumUser, "clint2");         // memory
        ((TurbineModelManager)fulcrumSecurityService.getModelManager()).grant(fulcrumUser, group, role);

        addUserAndCheck(group, role, fulcrumUser);

    }

    /**
     * Fulcrum contract check
     *
     * @param group Fulcrum interface
     * @param role Fulcrum interface
     * @param user Fulcrum interface
     * @throws EntityExistsException
     * @throws DataBackendException
     * @throws UnknownEntityException
     */
	private void addUserAndCheck(Group group, Role role, TurbineUser user)
			throws EntityExistsException, DataBackendException,
			UnknownEntityException
	{

        boolean ugrFound = false;
        TurbineUserGroupRole ugrTest = null;
        for (TurbineUserGroupRole ugr : user.getUserGroupRoleSet())
        {
            if (ugr.getUser().equals(user) && ugr.getGroup().equals(group) && ugr.getRole().equals(role))
            {
                ugrFound = true;
                ugrTest = ugr;
                break;
            }
        }
        assertTrue(ugrFound);
        assertTrue(ugrTest.getGroup().equals(group));
        assertTrue(ugrTest.getUser().equals(user));
	}


	@AfterAll
	public static void setupAfter()
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.shutdownService(org.apache.turbine.services.security.SecurityService.SERVICE_NAME);
        serviceManager.shutdownServices();
    }


}
