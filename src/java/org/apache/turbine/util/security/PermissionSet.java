package org.apache.turbine.util.security;

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

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.om.security.Permission;

/**
 * This class represents a set of Permissions.  It makes it easy to
 * build a UI that would allow someone to add a group of Permissions
 * to a Role.  It enforces that only
 * Permission objects are allowed in the set and only relevant methods
 * are available.
 *
 * @version $Id$
 */
public class PermissionSet
    extends SecuritySet
{
    /**
     * Constructs an empty PermissionSet
     */
    public PermissionSet()
    {
        super();
    }

    /**
     * Constructs a new PermissionSet with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwritten.
     *
     * @param permissions A collection of permissions to be contained in the set.
     */
    public PermissionSet(Collection permissions)
    {
        super();
        add(permissions);
    }

    /**
     * Adds a Permission to this PermissionSet.
     *
     * @param permission A Permission.
     * @return True if Permission was added; false if PermissionSet
     * already contained the Permission.
     */
    public boolean add(Permission permission)
    {
        boolean res = contains(permission);
        nameMap.put(permission.getName(), permission);
        idMap.put(permission.getIdAsObj(), permission);
        return res;
    }

    /**
     * Adds the Permissions in a Collection to this PermissionSet.
     *
     * @param permissions A Collection of Permissions.
     * @return True if this PermissionSet changed as a result; false
     * if no change to this PermissionSet occurred (this PermissionSet
     * already contained all members of the added PermissionSet).
     */
    public boolean add(Collection permissions)
    {
        boolean res = false;
        for (Iterator it = permissions.iterator(); it.hasNext();)
        {
            Permission p = (Permission) it.next();
            res |= add(p);
        }
        return res;
    }

    /**
     * Adds the Permissions in another PermissionSet to this
     * PermissionSet.
     *
     * @param permissionSet A PermissionSet.
     * @return True if this PermissionSet changed as a result; false
     * if no change to this PermissionSet occurred (this PermissionSet
     * already contained all members of the added PermissionSet).
     */
    public boolean add(PermissionSet permissionSet)
    {
        boolean res = false;
        for( Iterator it = permissionSet.iterator(); it.hasNext();)
        {
            Permission p = (Permission) it.next();
            res |= add(p);
        }
        return res;
    }

    /**
     * Removes a Permission from this PermissionSet.
     *
     * @param permission A Permission.
     * @return True if this PermissionSet contained the Permission
     * before it was removed.
     */
    public boolean remove(Permission permission)
    {
        boolean res = contains(permission);
        nameMap.remove(permission.getName());
        idMap.remove(permission.getIdAsObj());
        return res;
    }

    /**
     * Checks whether this PermissionSet contains a Permission.
     *
     * @param permission A Permission.
     * @return True if this PermissionSet contains the Permission,
     * false otherwise.
     */
    public boolean contains(Permission permission)
    {
        return nameMap.containsValue((Object) permission);
    }

    /**
     * Returns a Permission with the given name, if it is contained in
     * this PermissionSet.
     *
     * @param permissionName Name of Permission.
     * @return Permission if argument matched a Permission in this
     * PermissionSet; null if no match.
     * @deprecated Use <a href="#getPermissionByName">getPermissionByName</a> instead.
     */
    public Permission getPermission(String permissionName)
    {
        return getPermissionByName(permissionName);
    }

    /**
     * Returns a Permission with the given name, if it is contained in
     * this PermissionSet.
     *
     * @param permissionName Name of Permission.
     * @return Permission if argument matched a Permission in this
     * PermissionSet; null if no match.
     */
    public Permission getPermissionByName(String permissionName)
    {
        return (StringUtils.isNotEmpty(permissionName))
                ? (Permission) nameMap.get(permissionName) : null;
    }

    /**
     * Returns a Permission with the given id, if it is contained in
     * this PermissionSet.
     *
     * @param permissionId Id of the Permission.
     * @return Permission if argument matched a Permission in this
     * PermissionSet; null if no match.
     */
    public Permission getPermissionById(int permissionId)
    {
        return (permissionId != 0) 
                ? (Permission) idMap.get(new Integer(permissionId)) : null;
    }

    /**
     * Returns an Array of Permissions in this PermissionSet.
     *
     * @return An Array of Permission Objects.
     */
    public Permission[] getPermissionsArray()
    {
        return (Permission[]) getSet().toArray(new Permission[0]);
    }

    /**
     * Print out a PermissionSet as a String
     *
     * @returns The Permission Set as String
     *
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("PermissionSet: ");

        for(Iterator it = iterator(); it.hasNext();)
        {
            Permission p = (Permission) it.next();
            sb.append('[');
            sb.append(p.getName());
            sb.append(" -> ");
            sb.append(p.getIdAsObj());
            sb.append(']');
            if (it.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
