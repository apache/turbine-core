package org.apache.turbine.services.intake.xmlmodel;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.xml.sax.Attributes;

/**
 * A Class for holding data about a grouping of inputs used in an Application.
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @version $Id$
 */
public class XmlGroup
        implements Serializable
{
    private List fields;
    private List mapToObjects;
    private String defaultMapToObject;
    private AppData parent;
    private String groupName;
    private String key;
    private String poolCapacity;

    /**
     * Constructs a input group object
     */
    public XmlGroup()
    {
        fields = new ArrayList();
        mapToObjects = new ArrayList(2);
    }

    /**
     * Load the input group object from an xml tag.
     */
    public void loadFromXML(Attributes attrib)
    {
        groupName = attrib.getValue("name");
        key = attrib.getValue("key");
        poolCapacity = attrib.getValue("pool-capacity");

        String objName = attrib.getValue("mapToObject");
        if (StringUtils.isNotEmpty(objName))
        {
            defaultMapToObject = objName;
        }
    }

    /**
     * Get the name that handles this group
     */
    public String getName()
    {
        return groupName;
    }

    /**
     * Set the name that handles this group
     */
    public void setName(String newGroupName)
    {
        groupName = newGroupName;
    }

    /**
     * Get the key used to reference this group in input (form)
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Set the key used to reference this group in input (form)
     */
    public void setKey(String newKey)
    {
        key = newKey;
    }

    /**
     * The maximum number of classes specific to this group
     * allowed at one time.
     *
     * @return an <code>String</code> value
     */
    public String getPoolCapacity()
    {
        if (poolCapacity == null)
        {
            return "128";
        }

        return poolCapacity;
    }

    /**
     * A utility function to create a new field
     * from attrib and add it to this input group.
     */
    public XmlField addField(Attributes attrib)
    {
        XmlField field = new XmlField();
        field.loadFromXML(attrib);
        addField(field);

        return field;
    }

    /**
     * Adds a new field to the fields list and set the
     * parent group of the field to the current group
     */
    public void addField(XmlField field)
    {
        field.setGroup(this);

        // if this field has an object defined for mapping,
        // add it to the list
        if (field.getMapToObject() != null)
        {
            boolean isNewObject = true;
            for (int i = 0; i < mapToObjects.size(); i++)
            {
                if (mapToObjects.get(i).equals(field.getMapToObject()))
                {
                    isNewObject = false;
                    break;
                }
            }
            if (isNewObject)
            {
                mapToObjects.add(field.getMapToObject());
            }
        }
        // if a mapToProperty exists, set the object to this group's default
        else if (field.getMapToProperty() != null
                && !"".equals(field.getMapToProperty())
                && defaultMapToObject != null)
        {
            field.setMapToObject(defaultMapToObject);
        }

        fields.add(field);
    }

    /**
     * Returns a collection of fields in this input group
     */
    public List getFields()
    {
        return fields;
    }

    /**
     * Utility method to get the number of fields in this input group
     */
    public int getNumFields()
    {
        return fields.size();
    }

    /**
     * Returns a Specified field.
     * @return Return a XmlField object or null if it does not exist.
     */
    public XmlField getField(String name)
    {
        String curName;

        for (Iterator iter = fields.iterator(); iter.hasNext();)
        {
            XmlField field = (XmlField) iter.next();
            curName = field.getRawName();
            if (curName.equals(name))
            {
                return field;
            }
        }
        return null;
    }

    /**
     * Returns true if the input group contains a spesified field
     */
    public boolean containsField(XmlField field)
    {
        return fields.contains(field);
    }

    /**
     * Returns true if the input group contains a specified field
     */
    public boolean containsField(String name)
    {
        return (getField(name) != null);
    }

    public List getMapToObjects()
    {
        return mapToObjects;
    }

    /**
     * Set the parent of the group
     */
    public void setAppData(AppData parent)
    {
        this.parent = parent;
        if (defaultMapToObject != null)
        {
            defaultMapToObject = parent.getBasePackage() + defaultMapToObject;
            mapToObjects.add(defaultMapToObject);
        }
    }

    /**
     * Get the parent of the input group
     */
    public AppData getAppData()
    {
        return parent;
    }

    /**
     * A String which might be used as a variable of this class
     */
    public String getVariable()
    {
        String firstChar = getName().substring(0, 1).toLowerCase();
        return firstChar + getName().substring(1);
    }

    /**
     * Creates a string representation of this input group. This
     * is an xml representation.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append("<group name=\"").append(getName());
        result.append(" key=\"" + key + "\"");
        result.append(">\n");

        if (fields != null)
        {
            for (Iterator iter = fields.iterator(); iter.hasNext();)
            {
                result.append(iter.next());
            }
        }

        result.append("</group>\n");

        return result.toString();
    }
}
