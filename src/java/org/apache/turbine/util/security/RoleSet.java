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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.SecurityEntity;

/**
 * This class represents a set of Roles.  It makes it easy to build a
 * UI that would allow someone to add a group of Roles to a User.  It
 * wraps a TreeSet object to enforce that only Role objects are
 * allowed in the set and only relevant methods are available.
 * TreeSet's contain only unique Objects (no duplicates).
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class RoleSet implements Serializable
{
    /**
     * Role storage.
     */
    private TreeSet set;

    /**
     * Constructs an empty RoleSet
     */
    public RoleSet()
    {
        set = new TreeSet();
    }
    
    /**
     * Constructs a new RoleSet with specifed contents.
     * 
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param roles A collection of roles to be contained in the set.
     */
    public RoleSet(Collection roles)
    {
        this();
        add(roles);
    }

    /**
     * Adds a Role to this RoleSet.
     *
     * @param role A Role.
     * @return True if Role was added; false if RoleSet already
     * contained the Role.
     */
    public boolean add(Role role)
    {
        return set.add( (Object)role );
    }

    /**
     * Adds the Roles in a Collection to this RoleSet.
     *
     * @param roleSet A Collection of Roles.
     * @return True if this RoleSet changed as a result; false
     * if no change to this RoleSet occurred (this RoleSet
     * already contained all members of the added RoleSet).
     */
    public boolean add(Collection roles)
    {
        return set.addAll(roles);
    }

    /**
     * Adds the Roles in another RoleSet to this RoleSet.
     *
     * @param roleSet A RoleSet.
     * @return True if this RoleSet changed as a result; false if no
     * change to this RoleSet occurred (this RoleSet already contained
     * all members of the added RoleSet).
     */
    public boolean add(RoleSet roleSet)
    {
        return set.addAll(roleSet.set);
    }

    /**
     * Removes a Role from this RoleSet.
     *
     * @param role A Role.
     * @return True if this RoleSet contained the Role before it was
     * removed.
     */
    public boolean remove(Role role)
    {
        return set.remove( (Object)role );
    }

    /**
     * Removes all Roles from this RoleSet.
     */
    public void clear()
    {
        set.clear();
    }

    /**
     * Checks whether this RoleSet contains a Role.
     *
     * @param role A Role.
     * @return True if this RoleSet contains the Role, false
     * otherwise.
     */
    public boolean contains(Role role)
    {
        return set.contains( (Object)role );
    }

    /**
     * Compares by name a Role with the Roles contained in this
     * RoleSet.
     *
     * @param roleName Name of Role.
     * @return True if argument matched a Role in this RoleSet; false
     * if no match.
     */
    public boolean contains(String roleName)
    {
        Iterator iter = set.iterator();
        while ( iter.hasNext() )
        {
            Role role = (Role)iter.next();
            if ( roleName != null  &&
                 roleName.equals( role.getName() ) )
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Role with the given name, if it is contained in this
     * RoleSet.
     *
     * @param roleName Name of Role.
     * @return Role if argument matched a Role in this RoleSet; null
     * if no match.
     */
    public Role getRole(String roleName)
    {
        Iterator iter = set.iterator();
        while ( iter.hasNext() )
        {
            Role role = (Role)iter.next();
            if ( roleName != null  &&
                 roleName.equals( role.getName() ) )
            {
                return role;
            }
        }
        return null;
    }

    /**
     * Returns an Roles[] of Roles in this RoleSet.
     *
     * @return A Role[].
     */
    public Role[] getRolesArray()
    {
        return (Role[])set.toArray(new Role[0]);
    }

    /**
     * Returns an Iterator for Roles in this RoleSet.  
     */
    public Iterator elements()
    {
        return set.iterator();
    }

    /**
     * Returns size (cardinality) of this set.
     *
     * @return The cardinality of this RoleSet.
     */
    public int size()
    {
        return set.size();
    }
}
