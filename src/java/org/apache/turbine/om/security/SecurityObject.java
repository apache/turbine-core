package org.apache.turbine.om.security;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.torque.om.BaseObject;

/**
 * This class represents a generic object used in the Access Control Lists.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class SecurityObject extends BaseObject implements Comparable
{
    /** The name of this object. */
    private String name;

    /** The id of this object */
    private int id;

    /** The attributes of this object. */
    private Map attributes;

    /**
     * Constructs a new SecurityObject
     */
    public SecurityObject()
    {
        this("");
    }

    /**
     * Constructs a new SecurityObject with the specified name.
     *
     * @param name The name of the new object.
     */
    public SecurityObject(String name)
    {
        setName(name);
        setId(0);
        setAttributes(Collections.synchronizedMap(new HashMap()));
    }

    /**
     * Returns a Map containing this object's attributes.
     *
     * @return the object's attributes.
     */
    public Map getAttributes()
    {
        return attributes;
    }

    /**
     * Replaces this object's attributes with the specified Map.
     *
     * @param attributes The new attributes of the object.
     */
    public void setAttributes(Map attributes)
    {
        this.attributes = attributes;
    }

    /**
     * Retrieves the value of specific attribute of this object.
     *
     * @param name the name of the attribute
     * @return the value of the attribute
     */
    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    /**
     * Sets the value of specific attribute of this object.
     *
     * @param name the name of the attribute
     * @param value the value of the attribute
     */
    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    /**
     * Returns the name of this object.
     *
     * @return The name of the object.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of this object.
     *
     * @param name The name of the object.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Unused. There is an ID column in the 
     * database scheme but it doesn't seem
     * to be used.
     *
     * @return 0
     */
    public int getId()
    {
        return id;
    }
            
    /**
     * Unused. There is an ID column in the 
     * database scheme but it doesn't seem
     * to be used.
     *
     * @return null
     */
    public Integer getIdAsObj()
    {
        return new Integer(id);
    }

    /**
     * Unused. There is an ID column in the 
     * database scheme but it doesn't seem
     * to be used.
     *
     * @param id The id of the User.
     */
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Used for ordering SecurityObjects.
     *
     * @param obj The Object to compare to.
     * @return -1 if the name of the other object is lexically greater than this
     *         group, 1 if it is lexically lesser, 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        if (this.getClass() != obj.getClass())
        {
            throw new ClassCastException();
        }
        String name1 = ((SecurityObject) obj).getName();
        String name2 = this.getName();

        return name2.compareTo(name1);
    }

    /**
     * Returns a textual representation of this object, consisted by
     * it's name and attributes.
     *
     * @return  a textual representation of this group.
     */
    public String toString()
    {
        return (getName() + ':' + getAttributes().toString());
    }
}
