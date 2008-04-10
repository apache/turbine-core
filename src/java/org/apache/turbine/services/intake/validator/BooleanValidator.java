package org.apache.turbine.services.intake.validator;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.text.ParseException;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Validator for boolean field types.<br><br>
 *
 * Values are validated by attemting to match the value to
 * a list of strings for true and false values.  The string
 * values are compared without reguard to case.<br>
 *
 * Valid values for Boolean.TRUE:
 * <ul>
 * <li>TRUE</li>
 * <li>T</li>
 * <li>YES</li>
 * <li>Y</li>
 * <li>1</li>
 * <li>ON</li>
 * </ul>
 * Valid values for Boolean.FALSE:
 * <ul>
 * <li>FALSE</li>
 * <li>F</li>
 * <li>NO</li>
 * <li>N</li>
 * <li>0</li>
 * <li>OFF</li>
 * </ul>
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class BooleanValidator
        extends DefaultValidator
{
    /** String values which would evaluate to Boolean.TRUE */
    private static String[] trueValues = {"TRUE","T","YES","Y","1","ON"};

    /** String values which would evaluate to Boolean.FALSE */
    private static String[] falseValues = {"FALSE","F","NO","N","0","OFF"};

    /**
     * Default Constructor
     */
    public BooleanValidator()
    {
    }

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public BooleanValidator(Map paramMap)
            throws InvalidMaskException
    {
        super(paramMap);
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
            try
            {
                parse(testValue);
            }
            catch (ParseException e)
            {
                throw new ValidationException(e.getMessage());
            }
        }
    }

    /**
     * Parses a srting value into a Boolean object.
     *
     * @param stringValue the value to parse
     * @return a <code>Boolean</a> object
     */
    public Boolean parse(String stringValue)
            throws ParseException
    {
        Boolean result = null;

        for (int cnt = 0;
             cnt < Math.max(trueValues.length, falseValues.length); cnt++)
        {
            // Short-cut evaluation or bust!
            if ((cnt < trueValues.length) &&
                    stringValue.equalsIgnoreCase(trueValues[cnt]))
            {
                result = Boolean.TRUE;
                break;
            }

            if ((cnt < falseValues.length) &&
                    stringValue.equalsIgnoreCase(falseValues[cnt]))
            {
                result = Boolean.FALSE;
                break;
            }
        }

        if (result == null)
        {
            throw new ParseException(stringValue +
                    " could not be converted to a Boolean", 0);
        }
        return result;
    }
}
