package org.apache.turbine.services.intake.xmlmodel;


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


import java.io.Serializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.xml.sax.Attributes;

/**
 * A Class for holding data about a grouping of inputs used in an Application.
 *
 * @deprecated Use the Fulcrum Intake component instead.
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
