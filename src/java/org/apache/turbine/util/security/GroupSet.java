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
import org.apache.turbine.om.security.Group;

/**
 * This class represents a set of Groups. It's useful for building
 * administration UI.  It wraps a TreeSet object to enforce that only
 * Group objects are allowed in the set and only relevant methods
 * are available.  TreeSet's contain only unique Objects (no
 * duplicates).
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class GroupSet implements Serializable
{
    /** Set to hold the Group Set */
    private TreeSet set;

    /**
     * Constructs an empty GroupSet
     */
    public GroupSet()
    {
        set = new TreeSet();
    }

    /**
     * Constructs a new GroupSet with specifed contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwriten.
     *
     * @param groups A collection of groups to be contained in the set.
     */
    public GroupSet(Collection groups)
    {
        this();
        add(groups);
    }

    /**
     * Adds a Group to this GroupSet.
     *
     * @param group A Group.
     * @return True if Group was added; false if GroupSet
     * already contained the Group.
     */
    public boolean add(Group group)
    {
        return set.add((Object) group);
    }

    /**
     * Adds the Groups in a Collection to this GroupSet.
     *
     * @param groups A Collection of Groups.
     * @return True if this GroupSet changed as a result; false
     * if no change to this GroupSet occurred (this GroupSet
     * already contained all members of the added GroupSet).
     */
    public boolean add(Collection groups)
    {
        return set.addAll(groups);
    }

    /**
     * Adds the Groups in another GroupSet to this GroupSet.
     *
     * @param groupSet A GroupSet.
     * @return True if this GroupSet changed as a result; false
     * if no change to this GroupSet occurred (this GroupSet
     * already contained all members of the added GroupSet).
     */
    public boolean add(GroupSet groupSet)
    {
        return set.addAll(groupSet.set);
    }

    /**
     * Removes a Group from this GroupSet.
     *
     * @param group A Group.
     * @return True if this GroupSet contained the Group
     * before it was removed.
     */
    public boolean remove(Group group)
    {
        return set.remove((Object) group);
    }

    /**
     * Removes all Groups from this GroupSet.
     */
    public void clear()
    {
        set.clear();
    }

    /**
     * Checks whether this GroupSet contains a Group.
     *
     * @param group A Group.
     * @return True if this GroupSet contains the Group,
     * false otherwise.
     */
    public boolean contains(Group group)
    {
        return set.contains((Object) group);
    }

    /**
     * Compares by name a Group with the Groups contained in
     * this GroupSet.
     *
     * @param groupName Name of Group.
     * @return True if argument matched a Group in this
     * GroupSet; false if no match.
     */
    public boolean contains(String groupName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Group group = (Group) iter.next();
            if (groupName != null && groupName.equals(group.getName()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a Group with the given name, if it is contained in
     * this GroupSet.
     *
     * @param groupName Name of Group.
     * @return Group if argument matched a Group in this
     * GroupSet; null if no match.
     */
    public Group getGroup(String groupName)
    {
        Iterator iter = set.iterator();
        while (iter.hasNext())
        {
            Group group = (Group) iter.next();
            if ( groupName != null && groupName.equals(group.getName()))
            {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns an Groups[] of Groups in this GroupSet.
     *
     * @return A Group[].
     */
    public Group[] getGroupsArray()
    {
        return (Group[]) set.toArray(new Group[0]);
    }

    /**
     * Returns an Iterator for Groups in this GroupSet.
     */
    public Iterator elements()
    {
        return set.iterator();
    }

    /**
     * Returns size (cardinality) of this set.
     *
     * @return The cardinality of this GroupSet.
     */
    public int size()
    {
        return set.size();
    }
}
