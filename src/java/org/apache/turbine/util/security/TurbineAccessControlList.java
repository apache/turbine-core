package org.apache.turbine.util.security;


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
import java.util.Map;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.security.TurbineSecurity;

/**
 * This is a control class that makes it easy to find out if a
 * particular User has a given Permission.  It also determines if a
 * User has a a particular Role.
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public class TurbineAccessControlList
        implements AccessControlList
{
    /** The sets of roles that the user has in different groups */
    private Map roleSets;

    /** The sets of permissions that the user has in different groups */
    private Map permissionSets;

    /** The name of this ACL. Needed for the SecurityEntity Interface */
    private String name;

    /**
     * Constructs a new AccessControlList.
     *
     * This class follows 'immutable' pattern - it's objects can't be modified
     * once they are created. This means that the permissions the users have are
     * in effect form the moment they log in to the moment they log out, and
     * changes made to the security settings in that time are not reflected
     * in the state of this object. If you need to reset an user's permissions
     * you need to invalidate his session. <br>
     * The objects that constructs an AccessControlList must supply hashtables
     * of role/permission sets keyed with group objects. <br>
     *
     * @param roleSets a hashtable containing RoleSet objects keyed with Group objects
     * @param permissionSets a hashtable containing PermissionSet objects keyed with Group objects
     */
    public TurbineAccessControlList(Map roleSets, Map permissionSets)
    {
        this.roleSets = roleSets;
        this.permissionSets = permissionSets;
    }

    /**
     * Returns the name of this ACL.
     *
     * @return The ACL Name
     *
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Sets the name of this ACL.
     *
     * @param name The new ACL name.
     *
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Retrieves a set of Roles an user is assigned in a Group.
     *
     * @param group the Group
     * @return the set of Roles this user has within the Group.
     */
    public RoleSet getRoles(Group group)
    {
        if (group == null)
        {
            return null;
        }
        return (RoleSet) roleSets.get(group);
    }

    /**
     * Retrieves a set of Roles an user is assigned in the global Group.
     *
     * @return the set of Roles this user has within the global Group.
     */
    public RoleSet getRoles()
    {
        return getRoles(TurbineSecurity.getGlobalGroup());
    }

    /**
     * Retrieves a set of Permissions an user is assigned in a Group.
     *
     * @param group the Group
     * @return the set of Permissions this user has within the Group.
     */
    public PermissionSet getPermissions(Group group)
    {
        if (group == null)
        {
            return null;
        }
        return (PermissionSet) permissionSets.get(group);
    }

    /**
     * Retrieves a set of Permissions an user is assigned in the global Group.
     *
     * @return the set of Permissions this user has within the global Group.
     */
    public PermissionSet getPermissions()
    {
        return getPermissions(TurbineSecurity.getGlobalGroup());
    }

    /**
     * Checks if the user is assigned a specific Role in the Group.
     *
     * @param role the Role
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the Group.
     */
    public boolean hasRole(Role role, Group group)
    {
        RoleSet set = getRoles(group);
        if (set == null || role == null)
        {
            return false;
        }
        return set.contains(role);
    }

    /**
     * Checks if the user is assigned a specific Role in any of the given
     * Groups
     *
     * @param role the Role
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Role in any of
     *         the given Groups.
     */
    public boolean hasRole(Role role, GroupSet groupset)
    {
        if (role == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            RoleSet roles = getRoles(group);
            if (roles != null)
            {
                if (roles.contains(role))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the user is assigned a specific Role in the Group.
     *
     * @param role the Role
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the Group.
     */
    public boolean hasRole(String role, String group)
    {
        try
        {
            return hasRole(TurbineSecurity.getRoleByName(role),
                    TurbineSecurity.getGroupByName(group));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Checks if the user is assigned a specifie Role in any of the given
     * Groups
     *
     * @param rolename the name of the Role
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Role in any of
     *         the given Groups.
     */
    public boolean hasRole(String rolename, GroupSet groupset)
    {
        Role role;
        try
        {
            role = TurbineSecurity.getRoleByName(rolename);
        }
        catch (TurbineSecurityException e)
        {
            return false;
        }
        if (role == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            RoleSet roles = getRoles(group);
            if (roles != null)
            {
                if (roles.contains(role))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the user is assigned a specific Role in the global Group.
     *
     * @param role the Role
     * @return <code>true</code> if the user is assigned the Role in the global Group.
     */
    public boolean hasRole(Role role)
    {
        return hasRole(role, TurbineSecurity.getGlobalGroup());
    }

    /**
     * Checks if the user is assigned a specific Role in the global Group.
     *
     * @param role the Role
     * @return <code>true</code> if the user is assigned the Role in the global Group.
     */
    public boolean hasRole(String role)
    {
        try
        {
            return hasRole(TurbineSecurity.getRoleByName(role));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(Permission permission, Group group)
    {
        PermissionSet set = getPermissions(group);
        if (set == null || permission == null)
        {
            return false;
        }
        return set.contains(permission);
    }

    /**
     * Checks if the user is assigned a specific Permission in any of the given
     * Groups
     *
     * @param permission the Permission
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Permission in any
     *         of the given Groups.
     */
    public boolean hasPermission(Permission permission, GroupSet groupset)
    {
        if (permission == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            PermissionSet permissions = getPermissions(group);
            if (permissions != null)
            {
                if (permissions.contains(permission))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(String permission, String group)
    {
        try
        {
            return hasPermission(TurbineSecurity.getPermissionByName(permission),
                    TurbineSecurity.getGroupByName(group));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Checks if the user is assigned a specific Permission in the Group.
     *
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the Group.
     */
    public boolean hasPermission(String permission, Group group)
    {
        try
        {
            return hasPermission(
                    TurbineSecurity.getPermissionByName(permission), group);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Checks if the user is assigned a specifie Permission in any of the given
     * Groups
     *
     * @param permissionName the name of the Permission
     * @param groupset a Groupset
     * @return <code>true</code> if the user is assigned the Permission in any
     *         of the given Groups.
     */
    public boolean hasPermission(String permissionName, GroupSet groupset)
    {
        Permission permission;
        try
        {
            permission = TurbineSecurity.getPermissionByName(permissionName);
        }
        catch (TurbineSecurityException e)
        {
            return false;
        }
        if (permission == null)
        {
            return false;
        }
        for (Iterator groups = groupset.iterator(); groups.hasNext();)
        {
            Group group = (Group) groups.next();
            PermissionSet permissions = getPermissions(group);
            if (permissions != null)
            {
                if (permissions.contains(permission))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the user is assigned a specific Permission in the global Group.
     *
     * @param permission the Permission
     * @return <code>true</code> if the user is assigned the Permission in the global Group.
     */
    public boolean hasPermission(Permission permission)
    {
        return hasPermission(permission, TurbineSecurity.getGlobalGroup());
    }

    /**
     * Checks if the user is assigned a specific Permission in the global Group.
     *
     * @param permission the Permission
     * @return <code>true</code> if the user is assigned the Permission in the global Group.
     */
    public boolean hasPermission(String permission)
    {
        try
        {
            return hasPermission(TurbineSecurity.getPermissionByName(permission));
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Returns all groups definded in the system.
     *
     * This is useful for debugging, when you want to display all roles
     * and permissions an user is assingned. This method is needed
     * because you can't call static methods of TurbineSecurity class
     * from within WebMacro/Velocity template
     *
     * @return A Group [] of all groups in the system.
     */
    public Group[] getAllGroups()
    {
        try
        {
            return TurbineSecurity.getAllGroups().getGroupsArray();
        }
        catch (TurbineSecurityException e)
        {
            return new Group[0];
        }
    }
}
