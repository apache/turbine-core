package org.apache.turbine.services.intake.validator;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Map;
import org.apache.turbine.util.TurbineException;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>minLength</td><td>greater than Integer.MIN_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>maxLength</td><td>less than Integer.MAX_VALUE</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>notANumberMessage</td><td>Some text</td>
 * <td>Entry was not a valid number</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @version $Id$
 */
public class IntegerValidator
    extends NumberValidator
{
    private static String INVALID_NUMBER = "Entry was not a valid integer";

    private int minValue;
    private int maxValue;

    public IntegerValidator(Map paramMap)
        throws TurbineException
    {
        this();
        init(paramMap);
    }

    public IntegerValidator()
    {
        // sets the default invalid number message
        super();
    }

    protected void doInit(Map paramMap)
    {
        minValue = Integer.MIN_VALUE;
        maxValue = Integer.MAX_VALUE;

        Constraint constraint = (Constraint)paramMap.get("minValue");
        if ( constraint != null )
        {
            String param = constraint.getValue();
            minValue = Integer.parseInt(param);
            minValueMessage = constraint.getMessage();
        }

        constraint = (Constraint)paramMap.get("maxValue");
        if ( constraint != null )
        {
            String param = constraint.getValue();
            maxValue = Integer.parseInt(param);
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
    protected void doAssertValidity(String testValue)
        throws ValidationException
    {
        int i = 0;
        try
        {
            i = Integer.parseInt(testValue);
        }
        catch (RuntimeException e)
        {
            message = invalidNumberMessage;
            throw new ValidationException(invalidNumberMessage);
        }

        if ( i < minValue )
        {
            message = minValueMessage;
            throw new ValidationException(minValueMessage);
        }
        if ( i > maxValue )
        {
            message = maxValueMessage;
            throw new ValidationException(maxValueMessage);
        }
    }


    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minValue.
     * @return value of minValue.
     */
    public int getMinValue()
    {
        return minValue;
    }

    /**
     * Set the value of minValue.
     * @param v  Value to assign to minValue.
     */
    public void setMinValue(int  v)
    {
        this.minValue = v;
    }

    /**
     * Get the value of maxValue.
     * @return value of maxValue.
     */
    public int getMaxValue()
    {
        return maxValue;
    }

    /**
     * Set the value of maxValue.
     * @param v  Value to assign to maxValue.
     */
    public void setMaxValue(int  v)
    {
        this.maxValue = v;
    }
}
