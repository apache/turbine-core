package org.apache.turbine.services.intake.validator;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * @deprecated Use the Fulcrum Intake component instead.
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
