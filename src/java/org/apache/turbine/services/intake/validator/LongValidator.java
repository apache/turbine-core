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
 * Validates Longs with the following constraints in addition to those
 * listed in NumberValidator and DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than Long.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than Long.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>invalidNumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class LongValidator
        extends NumberValidator
{
    /* Init the minValue to that for a Long */
    private long minValue = Long.MIN_VALUE;

    /* Init the maxValue to that for a Long */
    private long maxValue = Long.MAX_VALUE;

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public LongValidator(Map paramMap)
            throws InvalidMaskException
    {
        invalidNumberMessage = "Entry was not a valid Long";
        init(paramMap);
    }

    /**
     * Default Constructor
     */
    public LongValidator()
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
            minValue = Long.parseLong(param);
            minValueMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MAX_VALUE_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxValue = Long.parseLong(param);
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

        if (required || StringUtils.isNotEmpty(testValue))
        {
            long l = 0L;
            try
            {
                l = Long.parseLong(testValue);
            }
            catch (RuntimeException e)
            {
                errorMessage = invalidNumberMessage;
                throw new ValidationException(invalidNumberMessage);
            }
            
            if (l < minValue)
            {
                errorMessage = minValueMessage;
                throw new ValidationException(minValueMessage);
            }
            if (l > maxValue)
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
    public long getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     *
     * @param minValue  Value to assign to minValue.
     */
    public void setMinValue(long minValue)
    {
        this.minValue = minValue;
    }

    /**
     * Get the value of maxValue.
     *
     * @return value of maxValue.
     */
    public long getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     *
     * @param maxValue  Value to assign to maxValue.
     */
    public void setMaxValue(long maxValue)
    {
        this.maxValue = maxValue;
    }
}
