package org.apache.turbine.om.security;


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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//import org.apache.torque.om.BaseObject;

/**
 * This class represents a generic object used in the Access Control Lists.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public abstract class SecurityObject implements Comparable
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
