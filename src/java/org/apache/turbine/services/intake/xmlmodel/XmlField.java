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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.xml.sax.Attributes;

/**
 * A Class for holding data about a property used in an Application.
 *
 * @version $Id$
 */
public class XmlField
        implements Serializable
{
    private String name;
    private String key;
    private String type;
    private String displayName;
    private String multiValued;
    private XmlGroup parent;
    private List rules;
    private Map ruleMap;
    private String ifRequiredMessage;
    private String mapToObject;
    private String mapToProperty;
    private String validator;
    private String defaultValue;
    private String emptyValue;
    private String displaySize;

    /**
     * Default Constructor
     */
    public XmlField()
    {
        rules = new ArrayList();
        ruleMap = new HashMap();
    }

    /**
     * Creates a new column and set the name
     */
    public XmlField(String name)
    {
        this.name = name;
        rules = new ArrayList();
        ruleMap = new HashMap();
    }

    /**
     * Imports a column from an XML specification
     */
    public void loadFromXML(Attributes attrib)
    {
        setName(attrib.getValue("name"));
        setKey(attrib.getValue("key"));
        setType(attrib.getValue("type"));
        setDisplayName(attrib.getValue("displayName"));
        setDisplaySize(attrib.getValue("displaySize"));
        setMultiValued(attrib.getValue("multiValued"));

        String mapObj = attrib.getValue("mapToObject");
        if (mapObj != null && mapObj.length() != 0)
        {
            setMapToObject(mapObj);
        }

        setMapToProperty(attrib.getValue("mapToProperty"));
        setValidator(attrib.getValue("validator"));
        setDefaultValue(attrib.getValue("defaultValue"));
        setEmptyValue(attrib.getValue("emptyValue"));
    }

    /**
     * Get the name of the property
     */
    public String getRawName()
    {
        return name;
    }

    /**
     * Get the name of the property
     */
    public String getName()
    {
        return StringUtils.replace(name, "_", "");
    }

    /**
     * Set the name of the property
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Get the display name of the property
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * Set the display name of the property
     */
    public void setDisplayName(String newDisplayName)
    {
        displayName = newDisplayName;
    }

    /**
     * Sets the display size of the field.
     */
    private void setDisplaySize(String size)
    {
        this.displaySize = size;
    }

    /**
     * Gets the display size of the field.  This is
     * useful for constructing the HTML input tag.
     */
    public String getDisplaySize()
    {
        return this.displaySize;
    }

    /**
     * Set the parameter key of the property
     */
    public void setKey(String newKey)
    {
        key = newKey;
    }

    /**
     * Get the parameter key of the property
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Set the type of the property
     */
    public void setType(String newType)
    {
        type = newType;
    }

    /**
     * Get the type of the property
     */
    public String getType()
    {
        return type;
    }

    /**
     * Set whether this class can have multiple values
     */
    public void setMultiValued(String newMultiValued)
    {
        multiValued = newMultiValued;
    }

    /**
     * can this field have several values?
     */
    public boolean isMultiValued()
    {
        if (multiValued != null && multiValued.equals("true"))
        {
            return true;
        }
        return false;
    }

    /**
     * Set the name of the object that takes this input
     *
     * @param objectName name of the class.
     */
    public void setMapToObject(String objectName)
    {
        mapToObject = objectName;
    }

    /**
     * Get the name of the object that takes this input
     */
    public String getMapToObject()
    {
        return mapToObject;
    }

    /**
     * Set the property method that takes this input
     *
     * @param prop Name of the property to which the field will be mapped.
     */
    public void setMapToProperty(String prop)
    {
        mapToProperty = prop;
    }

    /**
     * Get the property method that takes this input
     */
    public String getMapToProperty()
    {
        if (mapToProperty == null)
        {
            return getName();
        }
        else
        {
            return mapToProperty;
        }
    }

    /**
     * Set the class name of the validator
     */
    public void setValidator(String prop)
    {
        validator = prop;
    }

    /**
     * Get the className of the validator
     */
    public String getValidator()
    {
        return validator;
    }

    /**
     * Set the default Value.
     *
     * @param prop The parameter to use as default value.
     */
    public void setDefaultValue(String prop)
    {
        defaultValue = prop;
    }

    /**
     * Get the default Value.
     *
     * @return The default value for this field.
     */
    public String getDefaultValue()
    {
        return defaultValue;
    }

    /**
     * Set the empty Value.
     *
     * @param prop The parameter to use as empty value.
     */
    public void setEmptyValue(String prop)
    {
        emptyValue = prop;
    }

    /**
     * Get the empty Value.
     *
     * @return The empty value for this field.
     */
    public String getEmptyValue()
    {
        return emptyValue;
    }

    /**
     * The name of the field making sure the first letter is lowercase.
     *
     * @return a <code>String</code> value
     * @deprecated No replacement
     */
    public String getVariable()
    {
        String firstChar = getName().substring(0, 1).toLowerCase();
        return firstChar + getName().substring(1);
    }

    /**
     * Set the parent XmlGroup of the property
     */
    public void setGroup(XmlGroup parent)
    {
        this.parent = parent;
        if (mapToObject != null && mapToObject.length() != 0)
        {
            mapToObject = parent.getAppData().getBasePackage() + mapToObject;
        }
    }

    /**
     * Get the parent XmlGroup of the property
     */
    public XmlGroup getGroup()
    {
        return parent;
    }

    /**
     * Get the value of ifRequiredMessage.
     *
     * @return value of ifRequiredMessage.
     */
    public String getIfRequiredMessage()
    {
        return ifRequiredMessage;
    }

    /**
     * Set the value of ifRequiredMessage.
     *
     * @param v  Value to assign to ifRequiredMessage.
     */
    public void setIfRequiredMessage(String v)
    {
        this.ifRequiredMessage = v;
    }

    /**
     * A utility function to create a new input parameter
     * from attrib and add it to this property.
     */
    public Rule addRule(Attributes attrib)
    {
        Rule rule = new Rule();
        rule.loadFromXML(attrib);
        addRule(rule);

        return rule;
    }

    /**
     * Adds a new rule to the parameter Map and set the
     * parent property of the Rule to this property
     */
    public void addRule(Rule rule)
    {
        rule.setField(this);
        rules.add(rule);
        ruleMap.put(rule.getName(), rule);
    }

    /**
     * The collection of rules for this field.
     *
     * @return a <code>List</code> value
     */
    public List getRules()
    {
        return rules;
    }

    /**
     * The collection of rules for this field keyed by
     * parameter name.
     *
     * @return a <code>Map</code> value
     */
    public Map getRuleMap()
    {
        return ruleMap;
    }

    /**
     * String representation of the column. This
     * is an xml representation.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();
        result.append(" <field name=\"" + name + "\"");
        result.append(" key=\"" + key + "\"");
        result.append(" type=\"" + type + "\"");

        if (displayName != null)
        {
            result.append(" displayName=\"" + displayName + "\"");
        }
        if (mapToObject != null)
        {
            result.append(" mapToObject=\"" + mapToObject + "\"");
        }
        if (mapToProperty != null)
        {
            result.append(" mapToProperty=\"" + mapToProperty + "\"");
        }
        if (validator != null)
        {
            result.append(" validator=\"" + validator + "\"");
        }
        if (defaultValue != null)
        {
            result.append(" defaultValue=\"" + defaultValue + "\"");
        }

        if (emptyValue != null)
        {
            result.append(" emptyValue=\"" + emptyValue + "\"");
        }

        if (rules.size() == 0)
        {
            result.append(" />\n");
        }
        else
        {
            result.append(">\n");
            for (Iterator i = rules.iterator(); i.hasNext();)
            {
                result.append(i.next());
            }
            result.append("</field>\n");
        }

        return result.toString();
    }

    // this methods are called during serialization
    private void writeObject(ObjectOutputStream stream)
            throws IOException
    {
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException
    {
        stream.defaultReadObject();
    }
}
