package org.apache.turbine.services.intake.validator;

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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.intake.model.Field;

/**
 * Validates Floats with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Float.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than Float.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>invalidNumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id$
 */
public class FloatValidator
        extends NumberValidator
{
    /* Init the minValue to that for a Float */
    private float minValue = Float.NEGATIVE_INFINITY;

    /* Init the maxValue to that for a Float */
    private float maxValue = Float.POSITIVE_INFINITY;

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public FloatValidator(Map paramMap)
            throws InvalidMaskException
    {
        invalidNumberMessage = "Entry was not a valid Float";
        init(paramMap);
    }

    /**
     * Default Constructor
     */
    public FloatValidator()
    {
    }

    /**
     * Method to initialise Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public void init(Map paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint = (Constraint) paramMap.get(MIN_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            minValue = Float.parseFloat(param);
            minValueMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MAX_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxValue = Float.parseFloat(param);
            maxValueMessage = constraint.getMessage();
        }
    }

    /**
     * Determine whether a field meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param field a <code>Field</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(Field field)
            throws ValidationException
    {
        Locale locale = field.getLocale();
        
        if (field.isMultiValued())
        {
            String[] stringValues = (String[])field.getTestValue();
            
            for (int i = 0; i < stringValues.length; i++)
            {
                assertValidity(stringValues[i], locale);
            }
        }
        else
        {
            assertValidity((String)field.getTestValue(), locale);
        }
    }
    
    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @param locale the Locale of the associated field
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(String testValue, Locale locale)
            throws ValidationException
    {
        super.assertValidity(testValue);

        if (required || StringUtils.isNotEmpty(testValue))
        {
            float f = 0.0f;
            NumberFormat nf = NumberFormat.getInstance(locale);

            try
            {
                f = nf.parse(testValue).floatValue();
            }
            catch (ParseException e)
            {
                errorMessage = invalidNumberMessage;
                throw new ValidationException(invalidNumberMessage);
            }

            if (f < minValue)
            {
                errorMessage = minValueMessage;
                throw new ValidationException(minValueMessage);
            }
            if (f > maxValue)
            {
                errorMessage = maxValueMessage;
                throw new ValidationException(maxValueMessage);
            }
        }
    }


    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minValue.
     *
     * @return value of minValue.
     */
    public float getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     *
     * @param minValue  Value to assign to minValue.
     */
    public void setMinValue(float minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Get the value of maxValue.
     *
     * @return value of maxValue.
     */
    public float getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     *
     * @param maxValue  Value to assign to maxValue.
     */
    public void setMaxValue(float maxValue)
    {
        this.maxValue = maxValue;
    }
}
