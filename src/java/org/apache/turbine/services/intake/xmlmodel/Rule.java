package org.apache.turbine.services.intake.xmlmodel;

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

import org.apache.turbine.services.intake.validator.Constraint;

import org.xml.sax.Attributes;

/**
 * A Class for holding data about a constraint on a property.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @version $Id$
 */
public class Rule
        implements Constraint, java.io.Serializable
{
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



