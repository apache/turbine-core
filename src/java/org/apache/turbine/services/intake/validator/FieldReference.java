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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.model.Field;
import org.apache.turbine.services.intake.model.Group;

/**
 * Helper Class to manage relations between fields. The following
 * comparisons are supported:
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
public class FieldReference
{
    /** a local logger */
    protected static final Log log = LogFactory.getLog(FieldReference.class);
    
    /** Rule name for "&lt;" comparison */
    public static final String RANGE_LT = "less-than";

    /** Rule name for "&gt;" comparison */
    public static final String RANGE_GT = "greater-than";

    /** Rule name for "&lt;=" comparison */
    public static final String RANGE_LTE = "less-than-or-equal";

    /** Rule name for "&gt;=" comparison */
    public static final String RANGE_GTE = "greater-than-or-equal";

    /** Integer value for "&lt;" comparison */
    public static final int COMPARE_LT = 1;

    /** Integer value for "&gt;" comparison */
    public static final int COMPARE_GT = 2;

    /** Integer value for "&lt;=" comparison */
    public static final int COMPARE_LTE = 3;

    /** Integer value for "&gt;=" comparison */
    public static final int COMPARE_GTE = 4;

    /** Numeric comparison */
    private int compare = 0;
    
    /** Name of referenced field */
    private String fieldName = null;

    /** Error message */
    private String message = null;
    
    /**
     *  Constructor
     */
    public FieldReference()
    {
    }

    /**
     * @return the comparison type
     */
    public int getCompare()
    {
        return compare;
    }

    /**
     * @param compare the comparison type to set
     */
    public void setCompare(int compare)
    {
        this.compare = compare;
    }

    /**
     * @return the field name
     */
    public String getFieldName()
    {
        return fieldName;
    }

    /**
     * @param fieldName the field name to set
     */
    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    /**
     * @return the message
     */
    public String getMessage()
    {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    /**
     * Map the comparison strings to their numeric counterparts
     * 
     * @param key the 
     * @return
     */
    public static int getCompareType(String key)
    {
        int compareType = 0;
        
        if (key.equals(RANGE_LT))
        {
            compareType = COMPARE_LT;
        }
        else if (key.equals(RANGE_LTE))
        {
            compareType = COMPARE_LTE;
        }
        else if (key.equals(RANGE_GT))
        {
            compareType = COMPARE_GT;
        }
        else if (key.equals(RANGE_GTE))
        {
            compareType = COMPARE_GTE;
        }
        
        return compareType;
    }
    
    /**
     * Check the parsed value against the referenced fields
     * 
     * @param fieldReferences List of field references to check
     * @param compareCallback Callback to the actual compare operation
     * @param value the parsed value of the related field
     * @param group the group the related field belongs to
     * 
     * @throws ValidationException
     */
    public static void checkReferences(List fieldReferences, CompareCallback compareCallback, 
            Object value, Group group)
        throws ValidationException
    {
        for (Iterator i = fieldReferences.iterator(); i.hasNext();)
        {
            FieldReference ref = (FieldReference)i.next();
            boolean comp_true = true;

            try
            {
                Field refField = group.get(ref.getFieldName());
                
                if (refField.isSet())
                {
                    /*
                     * Fields are processed in sequence so that our
                     * reference field might have been set but not
                     * yet validated. We check this here.
                     */
                    if (!refField.isValidated())
                    {
                        refField.validate();
                    }
                    
                    if (refField.isValid())
                    {
                        try
                        {
                            comp_true = compareCallback.compareValues(ref.getCompare(), 
                                    value, 
                                    refField.getValue());
                        }
                        catch (ClassCastException e)
                        {
                            throw new IntakeException("Type mismatch comparing " +
                                    value + " with " + refField.getValue(), e);
                        }
                    }
                }
            }
            catch (IntakeException e)
            {
                log.error("Validate operation failed.", e);
                throw new ValidationException(ref.getMessage());
            }

            if (comp_true == false)
            {
                throw new ValidationException(ref.getMessage());
            }
        }
    }
}
