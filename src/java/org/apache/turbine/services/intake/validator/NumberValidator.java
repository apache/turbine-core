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

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minValue</td><td>greater than BigDecimal.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxValue</td><td>less than BigDecimal.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>notANumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
abstract class NumberValidator
        extends DefaultValidator
{
    /** The message to show if field fails min-value test */
    String minValueMessage = null;

    /** The message to show if field fails max-value test */
    String maxValueMessage = null;

    /** The message to use for invalid numbers */
    String invalidNumberMessage = null;

    /**
     * Extract the relevant parameters from the constraints listed
     * in <rule> tags within the intake.xml file.
     *
     * @param paramMap a <code>Map</code> of <code>rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException an invalid mask was specified
     */
    public void init(Map paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint =
                (Constraint) paramMap.get(INVALID_NUMBER_RULE_NAME);

        if (constraint != null)
        {
            invalidNumberMessage = constraint.getMessage();
        }
    }

    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minValueMessage.
     *
     * @return value of minValueMessage.
     */
    public String getMinValueMessage()
    {
        return minValueMessage;
    }

    /**
     * Set the value of minValueMessage.
     *
     * @param minValueMessage  Value to assign to minValueMessage.
     */
    public void setMinValueMessage(String minValueMessage)
    {
        this.minValueMessage = minValueMessage;
    }

    /**
     * Get the value of maxValueMessage.
     *
     * @return value of maxValueMessage.
     */
    public String getMaxValueMessage()
    {
        return maxValueMessage;
    }

    /**
     * Set the value of maxValueMessage.
     *
     * @param maxValueMessage  Value to assign to maxValueMessage.
     */
    public void setMaxValueMessage(String maxValueMessage)
    {
        this.maxValueMessage = maxValueMessage;
    }

    /**
     * Get the value of invalidNumberMessage.
     *
     * @return value of invalidNumberMessage.
     */
    public String getInvalidNumberMessage()
    {
        return invalidNumberMessage;
    }

    /**
     *
     * Set the value of invalidNumberMessage.
     * @param invalidNumberMessage  Value to assign to invalidNumberMessage.
     */
    public void setInvalidNumberMessage(String invalidNumberMessage)
    {
        this.invalidNumberMessage = invalidNumberMessage;
    }

}
