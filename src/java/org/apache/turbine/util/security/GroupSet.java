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

import org.apache.turbine.om.security.Group;

/**
 * This class represents a set of Groups. It's useful for building
 * administration UI.  It enforces that only
 * Group objects are allowed in the set and only relevant methods
 * are available.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class GroupSet
        extends SecuritySet
{
    /**
     * Constructs an empty GroupSet
     */
    public GroupSet()
    {
        super();
    }

    /**
     * Constructs a new GroupSet with specified contents.
     *
     * If the given collection contains multiple objects that are
     * identical WRT equals() method, some objects will be overwritten.
     *
     * @param groups A collection of groups to be contained in the set.
     */
    public GroupSet(Collection groups)
    {
        super();
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
        boolean res = contains(group);
        nameMap.put(group.getName(), group);
        idMap.put(group.getIdAsObj(), group);
        return res;
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
        boolean res = false;
        for (Iterator it = groups.iterator(); it.hasNext();)
        {
            Group g = (Group) it.next();
            res |= add(g);
        }
        return res;
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
        boolean res = false;
        for( Iterator it = groupSet.iterator(); it.hasNext();)
        {
            Group g = (Group) it.next();
            res |= add(g);
        }
        return res;
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
        boolean res = contains(group);
        nameMap.remove(group.getName());
        idMap.remove(group.getIdAsObj());
        return res;
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
        return nameMap.containsValue((Object) group);
    }

    /**
     * Returns a Group with the given name, if it is contained in
     * this GroupSet.
     *
     * @param groupName Name of Group.
     * @return Group if argument matched a Group in this
     * GroupSet; null if no match.
     * @deprecated Use <a href="#getGroupByName">getGroupByName</a> instead.
     */
    public Group getGroup(String groupName)
    {
        return getGroupByName(groupName);
    }

    /**
     * Returns a Group with the given name, if it is contained in
     * this GroupSet.
     *
     * @param groupName Name of Group.
     * @return Group if argument matched a Group in this
     * GroupSet; null if no match.
     */
    public Group getGroupByName(String groupName)
    {
        return (StringUtils.isNotEmpty(groupName))
                ? (Group) nameMap.get(groupName) : null;
    }

    /**
     * Returns a Group with the given id, if it is contained in
     * this GroupSet.
     *
     * @param groupId Id of the group
     * @return Group if argument matched a Group in this
     * GroupSet; null if no match.
     */
    public Group getGroupById(int groupId)
    {
        return (groupId != 0)
                ? (Group) idMap.get(new Integer(groupId)) : null;
    }

    /**
     * Returns an Array of Groups in this GroupSet.
     *
     * @return An Array of Group objects.
     */
    public Group[] getGroupsArray()
    {
        return (Group[]) getSet().toArray(new Group[0]);
    }

    /**
     * Print out a GroupSet as a String
     *
     * @returns The Group Set as String
     *
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("GroupSet: ");

        for(Iterator it = iterator(); it.hasNext();)
        {
            Group g = (Group) it.next();
            sb.append('[');
            sb.append(g.getName());
            sb.append(" -> ");
            sb.append(g.getIdAsObj());
            sb.append(']');
            if (it.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
