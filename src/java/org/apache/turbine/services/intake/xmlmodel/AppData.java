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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.turbine.services.intake.IntakeException;

import org.xml.sax.Attributes;

/**
 * A class for holding application data structures.
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class AppData
        implements java.io.Serializable
{
    /** List of groups */
    private List inputs;

    /** Package that will be used for all mapTo objects */
    private String basePackage;

    /** Prefix string that will be used to qualify &lt;prefix&gt;:&lt;intakegroup&gt; names */
    private String groupPrefix;

    /**
     * Default Constructor
     */
    public AppData()
    {
        inputs = new ArrayList();
    }

    /**
     * Imports the top level element from an XML specification
     */
    public void loadFromXML(Attributes attrib)
    {
        String basePkg = attrib.getValue("basePackage");
        if (basePkg == null)
        {
            setBasePackage("");
        }
        else
        {
            if (basePkg.charAt(basePkg.length() - 1) != '.')
            {
                setBasePackage(basePkg + '.');
            }
            else
            {
                setBasePackage(basePkg);
            }
        }

        setGroupPrefix(attrib.getValue("groupPrefix"));
    }

    /**
     * Return a collection of input sections (&lt;group&gt;).
     * The names of the groups returned here are only unique
     * to this AppData object and not qualified with the groupPrefix.
     * This method is used in the IntakeService to register all the
     * groups with and without prefix in the service.
     *
     */
    public List getGroups()
    {
        return inputs;
    }

    /**
     * Get a XmlGroup with the given name. It finds both
     * qualified and unqualified names in this package.
     *
     * @param groupName a <code>String</code> value
     * @return a <code>XmlGroup</code> value
     * @throws IntakeException indicates that the groupName was null
     */
    public XmlGroup getGroup(String groupName)
            throws IntakeException
    {
        if (groupName == null)
        {
            throw new IntakeException(
                    "Intake AppData.getGroup(groupName) is null");
        }

        String groupPrefix = getGroupPrefix();

        for (Iterator it = inputs.iterator(); it.hasNext();)
        {
            XmlGroup group = (XmlGroup) it.next();

            if (group.getName().equals(groupName))
            {
                return group;
            }
            if (groupPrefix != null)
            {
                StringBuffer qualifiedGroupName = new StringBuffer();

                qualifiedGroupName.append(groupPrefix)
                        .append(":")
                        .append(group.getName());

                if (qualifiedGroupName.toString().equals(groupName))
                {
                    return group;
                }
            }
        }
        return null;
    }

    /**
     * An utility method to add a new input group from
     * an xml attribute.
     */
    public XmlGroup addGroup(Attributes attrib)
    {
        XmlGroup input = new XmlGroup();
        input.loadFromXML(attrib);
        addGroup(input);
        return input;
    }

    /**
     * Add an input group to the vector and sets the
     * AppData property to this AppData
     */
    public void addGroup(XmlGroup input)
    {
        input.setAppData(this);
        inputs.add(input);
    }

    /**
     * Get the base package String that will be appended to
     * any mapToObjects
     *
     * @return value of basePackage.
     */
    public String getBasePackage()
    {
        return basePackage;
    }

    /**
     * Set the base package String that will be appended to
     * any mapToObjects
     *
     * @param v  Value to assign to basePackage.
     */
    public void setBasePackage(String v)
    {
        this.basePackage = v;
    }

    /**
     * Get the prefix String that will be used to qualify
     * intake groups when using multiple XML files
     *
     * @return value of groupPrefix
     */
    public String getGroupPrefix()
    {
        return groupPrefix;
    }

    /**
     * Set the prefix String that will be used to qualify
     * intake groups when using multiple XML files
     *
     * @param groupPrefix  Value to assign to basePackage.
     */
    public void setGroupPrefix(String groupPrefix)
    {
        this.groupPrefix = groupPrefix;
    }

    /**
     * Creats a string representation of this AppData.
     * The representation is given in xml format.
     */
    public String toString()
    {
        StringBuffer result = new StringBuffer();

        result.append("<input-data>\n");
        for (Iterator iter = inputs.iterator(); iter.hasNext();)
        {
            result.append(iter.next());
        }
        result.append("</input-data>");
        return result.toString();
    }
}
