package org.apache.turbine.services.intake.model;

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

import org.apache.commons.lang.StringUtils;

import org.apache.torque.om.ComboKey;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class ComboKeyField
        extends Field
{
    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public ComboKeyField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for a ComboKey field
     *
     * @param prop Parameter for the default values
     */
    public void setDefaultValue(String prop)
    {
        defaultValue = null;

        if (prop == null)
        {
            return;
        }

        defaultValue = new ComboKey(prop);
    }

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existant.
     *
     * @param prop The value to use if the field is empty.
     */
    public void setEmptyValue(String prop)
    {
        emptyValue = null;

        if (prop == null)
        {
            return;
        }

        emptyValue = new ComboKey(prop);
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            ComboKey[] values = new ComboKey[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                values[i] = StringUtils.isNotEmpty(inputs[i])
                        ? new ComboKey(inputs[i]) : (ComboKey) getEmptyValue();
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            setTestValue(StringUtils.isNotEmpty(val) ? new ComboKey(val) : (ComboKey) getEmptyValue());
        }
    }
}