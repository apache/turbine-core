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

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

import org.apache.torque.om.NumberKey;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.NumberKeyValidator;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * @version $Id$
 */
public class NumberKeyField
        extends BigDecimalField
{

    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public NumberKeyField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for a NumberKey field
     *
     * @param prop Parameter for the default values
     */
    public void setDefaultValue(String prop)
    {
        if (prop == null)
        {
            return;
        }

        defaultValue = new NumberKey(prop);
    }

    /**
     * A suitable validator.
     *
     * @return A suitable validator
     */
    protected String getDefaultValidator()
    {
        return NumberKeyValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            NumberKey[] values = new NumberKey[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                if (StringUtils.isNotEmpty(inputs[i]))
                {
                    values[i] = new NumberKey(
                            canonicalizeDecimalInput(inputs[i]));
                }
                else
                {
                    values[i] = null;
                }
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            if (StringUtils.isNotEmpty(val))
            {
                BigDecimal bd = canonicalizeDecimalInput(val);
                setTestValue(new NumberKey(bd));
            }
            else
            {
                setTestValue(null);
            }
        }
    }
}
