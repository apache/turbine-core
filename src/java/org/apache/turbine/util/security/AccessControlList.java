package org.apache.turbine.util.security;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and 
 *    "Apache Turbine" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. For 
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without 
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.Serializable;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;

import org.apache.turbine.services.security.TurbineSecurity;

/**
 * This is a control class that makes it easy to find out if a
 * particular User has a given Permission.  It also determines if a
 * User has a a particular Role.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:greg@shwoop.com">Greg Ritter</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class AccessControlList implements Serializable
{
    /** The sets of roles that the user has in different groups */
    private Map roleSets;

    /** The sets of permissions that the user has in different groups */
    private Map permissionSets;

    public static java.lang.String SESSION_KEY = "turbine.AccessControlList";

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
    public AccessControlList( Map roleSets, Map permissionSets )
    {
        this.roleSets = roleSets;
        this.permissionSets = permissionSets;
    }

    /**
     * Retrieves a set of Roles an user is assigned in a Group.
     *
     * @param group the Group 
     * @return the set of Roles this user has within the Group.
     */
    public RoleSet getRoles( Group group )
    {
        if(group == null)
            return null;
        return (RoleSet)roleSets.get(group);
    }

    /**
     * Retrieves a set of Roles an user is assigned in the global Group.
     *
     * @param group the Group 
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
    public PermissionSet getPermissions( Group group )
    {
        if(group == null)
            return null;
        return (PermissionSet)permissionSets.get(group);
    }

    /**
     * Retrieves a set of Permissions an user is assigned in the global Group.
     *
     * @param group the Group 
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
    public boolean hasRole( Role role, Group group )
    {
        RoleSet set = getRoles(group);
        if(set == null || role == null)
            return false;
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
    public boolean hasRole( Role role, GroupSet groupset )
    {
        if(role == null)
        {
            return false;
        }
        Iterator groups = groupset.elements();
        while(groups.hasNext()) 
        {
            Group group = (Group)groups.next();
            RoleSet roles = getRoles(group);
            if(roles != null) 
            {
                if(roles.contains(role))
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
    public boolean hasRole( String role, String group )
    {
        try
        {
            return hasRole(TurbineSecurity.getRole(role), TurbineSecurity.getGroup(group));
        }
        catch(Exception e)
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
    public boolean hasRole( String rolename, GroupSet groupset )
    {
        Role role;
        try
        {
            role = TurbineSecurity.getRole(rolename);
        }
        catch(TurbineSecurityException e)
        {
            return false;
        }
        if(role == null)
        {
            return false;
        }
        Iterator groups = groupset.elements();
        while(groups.hasNext()) 
        {
            Group group = (Group)groups.next();
            RoleSet roles = getRoles(group);
            if(roles != null) 
            {
                if(roles.contains(role))
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
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the global Group.
     */
    public boolean hasRole( Role role )
    {
        return hasRole(role, TurbineSecurity.getGlobalGroup());
    }

    /**
     * Checks if the user is assigned a specific Role in the global Group.
     * 
     * @param role the Role
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Role in the global Group.
     */
    public boolean hasRole( String role )
    {
        try
        {
            return hasRole(TurbineSecurity.getRole(role));
        }
        catch(Exception e)
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
    public boolean hasPermission( Permission permission, Group group )
    {
        PermissionSet set = getPermissions(group);
        if(set == null || permission == null)
            return false;
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
    public boolean hasPermission( Permission permission, GroupSet groupset )
    {
        if(permission == null)
        {
            return false;
        }
        Iterator groups = groupset.elements();
        while(groups.hasNext()) 
        {
            Group group = (Group)groups.next();
            PermissionSet permissions = getPermissions(group);
            if(permissions != null) 
            {
                if(permissions.contains(permission))
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
    public boolean hasPermission( String permission, String group )
    {
        try
        {
            return hasPermission(TurbineSecurity.getPermission(permission), 
                                 TurbineSecurity.getGroup(group));
        }
        catch(Exception e)
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
    public boolean hasPermission( String permission, Group group )
    {
        try
        {
            return hasPermission(
                TurbineSecurity.getPermission(permission), group);
        }
        catch(Exception e)
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
    public boolean hasPermission( String permissionName, GroupSet groupset )
    {
        Permission permission;
        try
        {
            permission = TurbineSecurity.getPermission(permissionName);
        }
        catch(TurbineSecurityException e)
        {
            return false;
        }
        if(permission == null)
        {
            return false;
        }
        Iterator groups = groupset.elements();
        while(groups.hasNext()) 
        {
            Group group = (Group)groups.next();
            PermissionSet permissions = getPermissions(group);
            if(permissions != null) 
            {
                if(permissions.contains(permission))
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
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the global Group.
     */
    public boolean hasPermission( Permission permission )
    {
        return hasPermission(permission, TurbineSecurity.getGlobalGroup());
    }

    /**
     * Checks if the user is assigned a specific Permission in the global Group.
     * 
     * @param permission the Permission
     * @param group the Group
     * @return <code>true</code> if the user is assigned the Permission in the global Group.
     */
    public boolean hasPermission( String permission )
    {
        try
        {
            return hasPermission(TurbineSecurity.getPermission(permission));
        }
        catch(Exception e)
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
     */
    public Group[] getAllGroups()
    {
        try 
        {
            return TurbineSecurity.getAllGroups().getGroupsArray();
        }
        catch(TurbineSecurityException e) 
        {
            return new Group[0];
        }
    }
}
