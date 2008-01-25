package org.apache.turbine.util;


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


import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * Utility for doing security checks in Screens and Actions.
 *
 * Sample usage:<br>
 *
 * <pre><code>
 * SecurityCheck mycheck =
 *   new SecurityCheck(data, "Unauthorized to do this!", "WrongPermission");
 * if (!mycheck.hasPermission("add_user");
 *   return;
 *</code></pre>
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="jh@byteaction.de">J&#252;rgen Hoffmann</a>
 * @version $Id$
 */
public class SecurityCheck
{
    private String message;

    private String failScreen;

    private RunData data = null;

    /**
     * Holds information if a missing Permission or Role should be created and granted on-the-fly.
     * This is good behaviour, if these change a lot.
     */
    private boolean initialize;

    /**
     * Constructor.
     *
     * @param data A Turbine RunData object.
     * @param message The message to display upon failure.
     * @param failedScreen The screen to redirect to upon failure.
     */
    public SecurityCheck(RunData data,
                         String message,
                         String failedScreen)
    {
        this(data, message, failedScreen, false);
    }

    /**
     * Constructor.
     *
     * @param data
     *            A Turbine RunData object.
     * @param message
     *            The message to display upon failure.
     * @param failedScreen
     *            The screen to redirect to upon failure.
     * @param initialize
     *            if a non-existing Permission or Role should be created.
     */
    public SecurityCheck(RunData data, String message, String failedScreen, boolean initialize)
    {
        this.data = data;
        this.message = message;
        this.failScreen = failedScreen;
        this.initialize = initialize;
    }

    /**
     * Does the user have this role?
     *
     * @param role A Role.
     * @return True if the user has this role.
     * @exception Exception, a generic exception.
     */
    public boolean hasRole(Role role)
            throws Exception
    {
        boolean value = false;
        if (data.getACL() == null ||
                !data.getACL().hasRole(role))
        {
            data.setScreen(failScreen);
            data.setMessage(message);
        }
        else
        {
            value = true;
        }
        return value;
    }

    /**
     * Does the user have this role?
     *
     * @param role
     *            A String.
     * @return True if the user has this role.
     * @exception Exception,
     *                a generic exception.
     */
    public boolean hasRole(String role) throws Exception
    {
        Role roleObject = null;
        try
        {
            roleObject = TurbineSecurity.getRoleByName(role);
        }
        catch (UnknownEntityException e)
        {
            if(initialize)
            {
                roleObject = TurbineSecurity.createRole(role);
                TurbineSecurity.grant(data.getUser(), TurbineSecurity.getGlobalGroup(), roleObject);
            }
            else
            {
                throw(e);
            }
        }
        return hasRole(TurbineSecurity.getRoleByName(role));
    }

    /**
     * Does the user have this permission?
     *
     * @param permission A Permission.
     * @return True if the user has this permission.
     * @exception Exception, a generic exception.
     */
    public boolean hasPermission(Permission permission)
            throws Exception
    {
        boolean value = false;
        if (data.getACL() == null ||
                !data.getACL().hasPermission(permission))
        {
            data.setScreen(failScreen);
            data.setMessage(message);
        }
        else
        {
            value = true;
        }
        return value;
    }

    /**
     * Does the user have this permission? If initialze is set to <code>true</code>
     * The permission will be created and granted to the first available Role of
     * the user, that the SecurityCheck is running against.
     *
     * If the User has no Roles, the first Role via TurbineSecurity is granted the
     * permission.
     *
     * @param permission
     *            A String.
     * @return True if the user has this permission.
     * @exception Exception,
     *                a generic exception.
     */
    public boolean hasPermission(String permission)
            throws Exception
    {
        Permission permissionObject = null;
        try
        {
            permissionObject = TurbineSecurity.getPermissionByName(permission);
        }
        catch (UnknownEntityException e)
        {
            if(initialize)
            {
                permissionObject = TurbineSecurity.createPermission(permission);

                Role role = null;
                RoleSet roles = data.getACL().getRoles();
                if(roles.size() > 0) role = roles.getRolesArray()[0];

                if(role == null)
                {
                    /*
                     * The User within data has no roles yet, let us grant the permission
                     * to the first role available through TurbineSecurity.
                     */
                    roles = TurbineSecurity.getAllRoles();
                    if(roles.size() > 0) role = roles.getRolesArray()[0];
                }

                if(role != null)
                {
                    /*
                     * If we have no role, there is nothing we can do about it. So only grant it,
                     * if we have a role to grant it to.
                     */
                    TurbineSecurity.grant(data.getACL().getRoles().getRolesArray()[0], permissionObject);
                }
            }
            else
            {
                throw(e);
            }
        }
        return hasPermission(permissionObject);
    }

    /**
     * Get the message that should be displayed.  This is initialized
     * in the constructor.
     *
     * @return A String.
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Get the screen that should be displayed.  This is initialized
     * in the constructor.
     *
     * @return A String.
     */
    public String getFailScreen()
    {
        return failScreen;
    }
}
