package org.apache.turbine.util.security;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
public abstract class SecuritySet
        implements Serializable
{
    /** Map for "name" -> "security object" */
    protected Map nameMap = null;

    /** Map for "id" -> "security object" */
    protected Map idMap = null;

    /**
     * Constructs an empty Set
     */
    public SecuritySet()
    {
        nameMap = new TreeMap();
        idMap = new TreeMap();
    }

    /**
     * Returns a set of security objects in this object.
     *
     * @return A Set Object
     *
     */
    public Set getSet()
    {
        return new HashSet(nameMap.values());
    }

    /**
     * Returns a set of Names in this Object.
     *
     * @return The Set of Names in this Object,
     *         backed by the actual data.
     */
    public Set getNames()
    {
        return nameMap.keySet();
    }

    /**
     * Returns a set of Id values in this Object.
     *
     * @return The Set of Ids in this Object,
     *         backed by the actual data.
     */
    public Set getIds()
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
     * @deprecated Use containsName(groupName) instead.
     */
    public boolean contains(String groupName)
    {
        return containsName(groupName);
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
        return (id == 0) ? false:  idMap.containsKey(new Integer(id));
    }

    /**
     * Returns an Iterator for Objects in this Set.
     *
     * @return An iterator for the Set
     */
    public Iterator iterator()
    {
        return nameMap.values().iterator();
    }

    /**
     * @deprecated Use iterator() instead.
     */
    public Iterator elements()
    {
        return iterator();
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
    public String toString()
    {
        StringBuffer sbuf = new StringBuffer(12 * size());
        for(Iterator it = nameMap.keySet().iterator(); it.hasNext(); )
        {
            sbuf.append((String) it.next());

            if(it.hasNext())
            {
                sbuf.append(", ");
            }
        }
        return sbuf.toString();
    }
}

