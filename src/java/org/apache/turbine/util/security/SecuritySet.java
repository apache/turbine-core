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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents a set of Security Entities.
 * It makes it easy to build a UI.
 * It wraps a TreeSet object to enforce that only relevant
 * methods are available.
 * TreeSet's contain only unique Objects (no duplicates).
 *
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class SecuritySet<T>
        implements Serializable, Iterable<T>
{
    /** Serial version */
	private static final long serialVersionUID = -1315871299888969431L;

	/** Map for "name" -> "security object" */
    protected Map<String, T> nameMap = null;

    /** Map for "id" -> "security object" */
    protected Map<Integer, T> idMap = null;

    /**
     * Constructs an empty Set
     */
    public SecuritySet()
    {
        nameMap = new TreeMap<String, T>();
        idMap = new TreeMap<Integer, T>();
    }

    /**
     * Returns a set of security objects in this object.
     *
     * @return A Set Object
     *
     */
    public Set<? extends T> getSet()
    {
        return new HashSet<T>(nameMap.values());
    }

    /**
     * Returns a set of Names in this Object.
     *
     * @return The Set of Names in this Object,
     *         backed by the actual data.
     */
    public Set<String> getNames()
    {
        return nameMap.keySet();
    }

    /**
     * Returns a set of Id values in this Object.
     *
     * @return The Set of Ids in this Object,
     *         backed by the actual data.
     */
    public Set<Integer> getIds()
    {
        return idMap.keySet();
    }

    /**
     * Removes all Objects from this Set.
     */
    public void clear()
    {
        nameMap.clear();
        idMap.clear();
    }

    /**
     * Searches if an Object with a given name is in the
     * Set
     *
     * @param roleName Name of the Security Object.
     * @return True if argument matched an Object in this Set; false
     * if no match.
     */
    public boolean containsName(String name)
    {
        return (StringUtils.isNotEmpty(name)) ? nameMap.containsKey(name) : false;
    }

    /**
     * Searches if an Object with a given Id is in the
     * Set
     *
     * @param id Id of the Security Object.
     * @return True if argument matched an Object in this Set; false
     * if no match.
     */
    public boolean containsId(int id)
    {
        return (id == 0) ? false:  idMap.containsKey(Integer.valueOf(id));
    }

    /**
     * Returns an Iterator for Objects in this Set.
     *
     * @return An iterator for the Set
     */
    public Iterator<T> iterator()
    {
        return nameMap.values().iterator();
    }

    /**
     * Returns size (cardinality) of this set.
     *
     * @return The cardinality of this Set.
     */
    public int size()
    {
        return nameMap.size();
    }

    /**
     * list of role names in this set
     *
     * @return The string representation of this Set.
     */
    @Override
    public String toString()
    {
        StringBuffer sbuf = new StringBuffer(12 * size());
        for(Iterator<String> it = nameMap.keySet().iterator(); it.hasNext(); )
        {
            sbuf.append(it.next());

            if(it.hasNext())
            {
                sbuf.append(", ");
            }
        }
        return sbuf.toString();
    }
}

