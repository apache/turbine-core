package org.apache.turbine.om.security;


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


import java.sql.Connection;
import java.util.Iterator;

import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a role played by the User associated with the
 * current Session.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class TurbineRole extends SecurityObject<Role> implements Role
{
    /**
     * Constructs a new Role
     */
    public TurbineRole()
    {
        super();
    }

    /**
     * Constructs a new Role with the sepcified name.
     *
     * @param name The name of the new object.
     */
    public TurbineRole(String name)
    {
        super(name);
    }

    /** The permissions for this role. */
    private PermissionSet permissionSet = null;

    /**
     * Returns the set of Permissions associated with this Role.
     *
     * @return A PermissionSet.
     * @exception Exception a generic exception.
     */
    public PermissionSet getPermissions()
            throws Exception
    {
        return permissionSet;
    }

    /**
     * Sets the Permissions associated with this Role.
     *
     * @param permissionSet A PermissionSet.
     */
    public void setPermissions(PermissionSet permissionSet)
    {
        this.permissionSet = permissionSet;
    }

    // These following methods are wrappers around TurbineSecurity

    /**
     * Creates a new Role in the system.
     *
     * @param name The name of the new Role.
     * @return An object representing the new Role.
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public Role create(String name)
            throws TurbineSecurityException
    {
        //Role role = new Role(name);
        Role role = new TurbineRole(name);
        TurbineSecurity.addRole(role);
        return role;
    }

    /**
     * Makes changes made to the Role attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
            throws TurbineSecurityException
    {
        TurbineSecurity.saveRole(this);
    }

    /**
     * not implemented
     *
     * @param conn
     * @throws Exception
     */
    public void save(Connection conn) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * not implemented
     *
     * @param dbname
     * @throws Exception
     */
    public void save(String dbname) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * Removes a role from the system.
     *
     * @throws TurbineSecurityException if the Role could not be removed.
     */
    public void remove()
            throws TurbineSecurityException
    {
        TurbineSecurity.removeRole(this);
    }

    /**
     * Renames the role.
     *
     * @param name The new Role name.
     * @throws TurbineSecurityException if the Role could not be renamed.
     */
    public void rename(String name)
            throws TurbineSecurityException
    {
        TurbineSecurity.renameRole(this, name);
    }

    /**
     * Grants a Permission to this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permission.
     */
    public void grant(Permission permission)
            throws TurbineSecurityException
    {
        TurbineSecurity.grant(this, permission);
    }

    /**
     * Grants Permissions from a PermissionSet to this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permissions.
     */
    public void grant(PermissionSet permissionSet)
            throws TurbineSecurityException
    {
        for (Iterator permissions = permissionSet.iterator(); permissions.hasNext();)
        {
            TurbineSecurity.grant(this, (Permission) permissions.next());
        }
    }

    /**
     * Revokes a Permission from this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permission.
     */
    public void revoke(Permission permission)
            throws TurbineSecurityException
    {
        TurbineSecurity.revoke(this, permission);
    }

    /**
     * Revokes Permissions from a PermissionSet from this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permissions.
     */
    public void revoke(PermissionSet permissionSet)
            throws TurbineSecurityException
    {
        for (Iterator permissions = permissionSet.iterator(); permissions.hasNext();)
        {
            TurbineSecurity.revoke(this, (Permission) permissions.next());
        }
    }
}
