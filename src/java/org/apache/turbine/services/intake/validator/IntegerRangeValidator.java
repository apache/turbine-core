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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.model.Field;
import org.apache.turbine.services.intake.model.Group;

/**
 * Validates an int field in dependency on another int field.
 *
 * <table>
 * <tr>
 *   <th>Name</th><th>Valid Values</th><th>Default Value</th>
 * </tr>
 * <tr>
 *   <td>less-than</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>greater-than</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>less-than-or-equal</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * <tr>
 *   <td>greater-than-or-equal</td>
 *   <td>&lt;name of other field&gt;</td>
 *   <td>&nbsp;</td>
 * </tr>
 * </table>
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: DateStringValidator.java 534527 2007-05-02 16:10:59Z tv $
 */
public class IntegerRangeValidator
        extends IntegerValidator
{
    /** List of FieldReferences for multiple comparisons */
    List fieldReferences; 

    /** Callback for the actual compare operation */
    CompareCallback compareCallback; 

    public IntegerRangeValidator(final Map paramMap)
            throws IntakeException
    {
        init(paramMap);
    }

    /**
     *  Default constructor
     */
    public IntegerRangeValidator()
    {
        super();
    }

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public void init(final Map paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);
        
        compareCallback = new CompareCallback()
            {
                /**
                 * Compare the given values using the compare operation provided
                 * 
                 * @param compare type of compare operation
                 * @param thisValue value of this field
                 * @param refValue value of the reference field
                 * 
                 * @return the result of the comparison
                 */
                public boolean compareValues(int compare, Object thisValue, Object refValue)
                    throws ClassCastException
                {
                    boolean result = true;
                    
                    Integer thisInt = (Integer)thisValue;
                    Integer otherInt = (Integer)refValue;
                    
                    switch (compare)
                    {
                        case FieldReference.COMPARE_LT:
                            result = thisInt.compareTo(otherInt) < 0;
                            break;
                            
                        case FieldReference.COMPARE_LTE:
                            result = thisInt.compareTo(otherInt) <= 0;
                            break;
                            
                        case FieldReference.COMPARE_GT:
                            result = thisInt.compareTo(otherInt) > 0;
                            break;
                            
                        case FieldReference.COMPARE_GTE:
                            result = thisInt.compareTo(otherInt) >= 0;
                            break;
                    }
                    
                    return result;
                }
            };
        
        fieldReferences = new ArrayList(10);

        for (Iterator i = paramMap.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            String key = (String)entry.getKey();
            Constraint constraint = (Constraint)entry.getValue();

            int compare = FieldReference.getCompareType(key);
            
            if (compare != 0)
            {
                // found matching constraint
                FieldReference fieldref = new FieldReference();
                fieldref.setCompare(compare);
                fieldref.setFieldName(constraint.getValue());
                fieldref.setMessage(constraint.getMessage());
                
                fieldReferences.add(fieldref);
            }
        }
        
        if (fieldReferences.isEmpty())
        {
            log.warn("No reference field rules have been found.");
        }
    }
    
    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testField a <code>Field</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(final Field testField)
        throws ValidationException
    {
        super.assertValidity(testField);
        
        Group thisGroup = testField.getGroup();

        if (testField.isMultiValued())
        {
            String[] stringValues = (String[])testField.getTestValue();

            for (int i = 0; i < stringValues.length; i++)
            {
                assertValidity(stringValues[i], thisGroup);
            }
        }
        else
        {
            String testValue = (String)testField.getTestValue();
        
            assertValidity(testValue, thisGroup);
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @param group the group this field belongs to
     * 
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(final String testValue, final Group group)
        throws ValidationException
    {
        if (required || StringUtils.isNotEmpty(testValue))
        {
            Integer testInt = new Integer(testValue);
            
            try
            {
                FieldReference.checkReferences(fieldReferences, compareCallback, 
                        testInt, group);
            }
            catch (ValidationException e)
            {
                errorMessage = e.getMessage();
                throw e;
            }
        }
    }
}
