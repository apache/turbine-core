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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DefaultValidator that will compare a testValue against the following
 * constraints:
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>required</td><td>true|false</td><td>false</td></tr>
 * <tr><td>mask</td><td>regexp</td><td>&nbsp;</td></tr>
 * <tr><td>minLength</td><td>integer</td><td>0</td></tr>
 * <tr><td>maxLength</td><td>integer</td><td>&nbsp;</td></tr>
 * </table>
 *
 * This validator can serve as the base class for more specific validators
 *
 * @deprecated Use the Fulcrum Intake component instead.
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
abstract public class DefaultValidator
        implements Validator, InitableByConstraintMap
{
    /** A boolean value to signify if the field is definately required or not */
    protected boolean required = false;

    /** The message to show if field fails required test */
    protected String requiredMessage = null;

    /** The minimum length of the field */
    protected int minLength = 0;

    /** The message to show if field fails min-length test */
    protected String minLengthMessage = null;

    /** The maximum length of the field */
    protected int maxLength = 0;

    /** The message to show if field fails max-length test */
    protected String maxLengthMessage = null;

    /** Error message pertaining to Rule that was broken */
    protected String errorMessage = null;

    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * Constructor
     *
     * @param paramMap a <code>Map</code> of <code>Rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException An invalid mask was specified for one of the rules

    */
    public DefaultValidator(Map paramMap)
            throws InvalidMaskException
    {
        init(paramMap);
    }

    /**
     * Default constructor
     */
    public DefaultValidator()
    {
    }

    /**
     * Extract the relevant parameters from the constraints listed
     * in <rule> tags within the intake.xml file.
     *
     * @param paramMap a <code>Map</code> of <code>Rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException An invalid mask was specified for one of the rules
     */
    public void init(Map paramMap)
            throws InvalidMaskException
    {
        Constraint constraint = (Constraint) paramMap.get(REQUIRED_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            required = new Boolean(param).booleanValue();
            requiredMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MIN_LENGTH_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            minLength = Integer.parseInt(param);
            minLengthMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get(MAX_LENGTH_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxLength = Integer.parseInt(param);
            maxLengthMessage = constraint.getMessage();
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @return true if valid, false otherwise
     */
    public boolean isValid(String testValue)
    {
        boolean valid = false;
        try
        {
            assertValidity(testValue);
            valid = true;
        }
        catch (ValidationException ve)
        {
            valid = false;
        }
        return valid;
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
        if (!required && StringUtils.isEmpty(testValue))
        {
            return;
        }
        if (required && StringUtils.isEmpty(testValue))
        {
            errorMessage = requiredMessage;
            throw new ValidationException(requiredMessage);
        }

        if (minLength > 0 && testValue.length() < minLength)
        {
            errorMessage = minLengthMessage;
            throw new ValidationException(minLengthMessage);
        }
        if (maxLength > 0 && testValue.length() > maxLength)
        {
            errorMessage = maxLengthMessage;
            throw new ValidationException(maxLengthMessage);
        }
    }


    /**
     * Get the error message resulting from invalid input.
     *
     * @return a <code>String</code> message, or the empty String "".
     */
    public String getMessage()
    {
        String retValue = "";

        if(errorMessage != null)
        {
            retValue = errorMessage;
        }

        return retValue;
    }

    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of required.
     *
     * @return value of required.
     */
    public boolean isRequired()
    {
        return required;
    }

    /**
     * Set the value of required.
     *
     * @param required  Value to assign to required.
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * Get the value of requiredMessage.
     *
     * @return value of requiredMessage.
     */
    public String getRequiredMessage()
    {
        return requiredMessage;
    }

    /**
     * Set the value of requiredMessage.
     *
     * @param requiredMessage  Value to assign to requiredMessage.
     */
    public void setRequiredMessage(String requiredMessage)
    {
        this.requiredMessage = requiredMessage;
    }

    /**
     * Get the value of minLength.
     *
     * @return value of minLength.
     */
    public int getMinLength()
    {
        return minLength;
    }

    /**
     * Set the value of minLength.
     *
     * @param minLength  Value to assign to minLength.
     */
    public void setMinLength(int minLength)
    {
        this.minLength = minLength;
    }

    /**
     * Get the value of minLengthMessage.
     *
     * @return value of minLengthMessage.
     */
    public String getMinLengthMessage()
    {
        return minLengthMessage;
    }

    /**
     * Set the value of minLengthMessage.
     *
     * @param minLengthMessage  Value to assign to minLengthMessage.
     */
    public void setMinLengthMessage(String minLengthMessage)
    {
        this.minLengthMessage = minLengthMessage;
    }

    /**
     * Get the value of maxLength.
     *
     * @return value of maxLength.
     */
    public int getMaxLength()
    {
        return maxLength;
    }

    /**
     * Set the value of maxLength.
     *
     * @param maxLength  Value to assign to maxLength.
     */
    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    /**
     * Get the value of maxLengthMessage.
     *
     * @return value of maxLengthMessage.
     */
    public String getMaxLengthMessage()
    {
        return maxLengthMessage;
    }

    /**
     * Set the value of maxLengthMessage.
     *
     * @param maxLengthMessage  Value to assign to maxLengthMessage.
     */
    public void setMaxLengthMessage(String maxLengthMessage)
    {
        this.maxLengthMessage = maxLengthMessage;
    }
}
