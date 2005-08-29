package org.apache.turbine.services.intake.model;

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

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.ShortValidator;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * Processor for short fields.
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class ShortField
        extends Field
{
    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public ShortField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for an Short Field
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

        defaultValue = new Short(prop);
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

        emptyValue = new Short(prop);
    }

    /**
     * Provides access to emptyValue such that the value returned will be
     * acceptable as an argument parameter to Method.invoke.  Subclasses
     * that deal with primitive types should ensure that they return an
     * appropriate value wrapped in the object wrapper class for the
     * primitive type.
     *
     * @return the value to use when the field is empty or an Object that
     * wraps the empty value for primitive types.
     */
    protected Object getSafeEmptyValue()
    {
        if (isMultiValued)
        {
            return new short[0];
        }
        else
        {
            return (null == getEmptyValue())
                    ? new Short((short) 0) : getEmptyValue();
        }
    }

    /**
     * A suitable validator.
     *
     * @return A suitable validator
     */
    protected String getDefaultValidator()
    {
        return ShortValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            short[] values = new short[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                values[i] = StringUtils.isNotEmpty(inputs[i])
                        ? new Short(inputs[i]).shortValue()
                        : ((Short) getEmptyValue()).shortValue();
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            setTestValue(StringUtils.isNotEmpty(val)
                    ? new Short(val) : (Short) getEmptyValue());
        }
    }

}
