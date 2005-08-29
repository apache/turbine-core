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

import java.util.Map;

import org.apache.torque.om.NumberKey;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Integer.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than BigDecimal.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>notANumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 * @deprecated No replacement
 */
public class NumberKeyValidator
        extends NumberValidator
{
    private static String INVALID_NUMBER = "Entry was not valid.";

    private NumberKey minValue;
    private NumberKey maxValue;

    public NumberKeyValidator(Map paramMap)
            throws InvalidMaskException
    {
        this();
        init(paramMap);
    }

    public NumberKeyValidator()
    {
        // sets the default invalid number message
        super();
    }

    protected void doInit(Map paramMap)
    {
        minValue = null;
        maxValue = null;

        Constraint constraint = (Constraint) paramMap.get(MIN_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            minValue = new NumberKey(param);
            minValueMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MAX_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxValue = new NumberKey(param);
            maxValueMessage = constraint.getMessage();
        }
    }

    protected String getDefaultInvalidNumberMessage()
    {
        return INVALID_NUMBER;
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
        NumberKey nk = null;
        try
        {
            nk = new NumberKey(testValue);
        }
        catch (RuntimeException e)
        {
            errorMessage = invalidNumberMessage;
            throw new ValidationException(invalidNumberMessage);
        }
        if (minValue != null && nk.compareTo(minValue) < 0)
        {
            errorMessage = minValueMessage;
            throw new ValidationException(minValueMessage);
        }
        if (maxValue != null && nk.compareTo(maxValue) > 0)
        {
            errorMessage = maxValueMessage;
            throw new ValidationException(maxValueMessage);
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
    public NumberKey getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     *
     * @param minValue Value to assign to minValue.
     */
    public void setMinValue(NumberKey minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Get the value of maxValue.
     *
     * @return value of maxValue.
     */
    public NumberKey getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     *
     * @param maxValue Value to assign to maxValue.
     */
    public void setMaxValue(NumberKey maxValue)
    {
        this.maxValue = maxValue;
    }
}
