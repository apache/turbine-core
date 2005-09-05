package org.apache.turbine.services.intake.xmlmodel;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.turbine.services.intake.validator.Constraint;

import org.xml.sax.Attributes;

/**
 * A Class for holding data about a constraint on a property.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public class Rule
        implements Constraint, Serializable
{
    /** Serial Version UID */
    private static final long serialVersionUID = 3662886424992562964L;

    private String name;
    private String value;
    private String message;
    private XmlField parent;

    /**
     * Default Constructor
     */
    public Rule()
    {
    }

    /**
     * Imports a column from an XML specification
     */
    public void loadFromXML(Attributes attrib)
    {
        setName(attrib.getValue("name"));
        setValue(attrib.getValue("value"));
    }

    /**
     * Set the name of the parameter
     */
    public void setName(String newName)
    {
        name = newName;
    }

    /**
     * Get the name of the parameter
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the value of the parameter
     */
    public void setValue(String newValue)
    {
        value = newValue;
    }

    /**
     * Get the value of the parameter
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Set the error message
     */
    public void setMessage(String newMessage)
    {
        message = newMessage;
    }

    /**
     * Get the error message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * Set the parent Field of the rule
     */
    public void setField(XmlField parent)
    {
        this.parent = parent;
    }

    /**
     * Get the parent Field of the rule
     */
    public XmlField getField()
    {
        return parent;
    }

    /**
     * String representation of the column. This
     * is an xml representation.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer(100);

        result.append("<rule name=\"" + name + "\"")
                .append(" value=\"" + value + "\"");

        if (message == null)
        {
            result.append(" />\n");
        }
        else
        {
            result.append(">")
                    .append(message)
                    .append("</rule>\n");
        }

        return result.toString();
    }

}



