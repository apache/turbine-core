package org.apache.turbine.services.intake.validator;

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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Validates Doubles with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Double.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than Double.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>invalidNumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class DoubleValidator
        extends NumberValidator
{
    /* Init the minValue to that for a Double */
    private double minValue = Double.NEGATIVE_INFINITY;

    /* Init the maxValue to that for a Double */
    private double maxValue = Double.POSITIVE_INFINITY;

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public DoubleValidator(Map paramMap)
            throws InvalidMaskException
    {
        invalidNumberMessage = "Entry was not a valid Double";
        init(paramMap);
    }

    /**
     * Default Constructor
     */
    public DoubleValidator()
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
            minValue = Double.parseDouble(param);
            minValueMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MAX_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxValue = Double.parseDouble(param);
            maxValueMessage = constraint.getMessage();
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(String testValue)
            throws ValidationException
    {
        super.assertValidity(testValue);

        double d = 0.0D;

        if (required || StringUtils.isNotEmpty(testValue))
        {
            try
            {
                d = Double.parseDouble(testValue);
            }
            catch (RuntimeException e)
            {
                errorMessage = invalidNumberMessage;
                throw new ValidationException(invalidNumberMessage);
            }
            
            if (d < minValue)
            {
                errorMessage = minValueMessage;
                throw new ValidationException(minValueMessage);
            }
            if (d > maxValue)
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
    public double getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     *
     * @param value  Value to assign to minValue.
     */
    public void setMinValue(double value)
    {
        this.minValue = value;
    }

    /**
     * Get the value of maxValue.
     *
     * @return value of maxValue.
     */
    public double getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     *
     * @param value  Value to assign to maxValue.
     */
    public void setMaxValue(double value)
    {
        this.maxValue = value;
    }
}
