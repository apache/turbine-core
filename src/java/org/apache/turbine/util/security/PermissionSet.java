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
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.SecurityObject;

/**
 * This class represents a set of Permissions.  It makes it easy to
 * build a UI that would allow someone to add a group of Permissions
 * to a Role.  It wraps a TreeSet object to enforce that only
 * Permission objects are allowed in the set and only relevant methods
 * are available.  TreeSet's contain only unique Objects (no
 * duplicates).
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class PermissionSet implements Serializable
{
    /** Set to hold the Permission Set */
    private TreeSet set;

    /**
     * Constructs an empty PermissionSet
     */
    public PermissionSet()
    {
        set = new TreeSet();
    }

    /**
     * Constructs a new PermissionSet with specifed contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param permissions A collection of permissions to be contained in the set.
     */
    public PermissionSet(Collection permissions)
    {
        this();
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
        return set.add((Object) permission);
    }

    /**
     * Adds the Permissions in a Collection to this PermissionSet.
     *
     * @param permissions A Permission.
     * @return True if this PermissionSet changed as a result; false
     * if no change to this PermissionSet occurred (this PermissionSet
     * already contained all members of the added PermissionSet).
     */
    public boolean add(Collection permissions)
    {
        return set.addAll(permissions);
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
        return set.addAll(permissionSet.set);
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
        return set.remove((Object) permission);
    }

    /**
     * Removes all Permissions from this PermissionSet.
     */
    public void clear()
    {
        set.clear();
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
        return set.contains((Object) permission);
    }

    /**
     * Returns a Permission with the given name, if it is contained in
     * this PermissionSet.
     *
     * @param permissionName Name of Permission.
     * @return True if argument matched a Permission in this
     * PermissionSet; false if no match.
     */
    public boolean contains(String permissionName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Permission permission = (Permission) iter.next();
            if (permissionName != null &&
                    permissionName.equals(((SecurityObject) permission).getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Permission with the given name, if it is contained in
     * this PermissionSet.
     *
     * @param permissionName Name of Permission.
     * @return Permission if argument matched a Permission in this
     * PermissionSet; null if no match.
     */
    public Permission getPermission(String permissionName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Permission permission = (Permission) iter.next();
            if (permissionName != null &&
                    permissionName.equals(((SecurityObject) permission).getName()))
            {
                return permission;
            }
        }
        return null;
    }

    /**
     * Returns an Array of Permissions in this PermissionSet.
     *
     * @return An Array of Permission Objects.
     */
    public Permission[] getPermissionsArray()
    {
        return (Permission[]) set.toArray(new Permission[0]);
    }

    /**
     * Returns an Iterator for Permissions in this PermissionSet.
     */
    public Iterator elements()
    {
        return set.iterator();
    }

    /**
     * Returns size (cardinality) of this set.
     *
     * @return The cardinality of this PermissionSet.
     */
    public int size()
    {
        return set.size();
    }
}
