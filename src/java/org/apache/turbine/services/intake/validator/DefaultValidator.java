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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.oro.text.regex.MalformedPatternException;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Compiler;
import org.apache.oro.text.regex.Perl5Matcher;

/**
 * A validator that will compare a testValue against the following
 * constraints:
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
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DefaultValidator
        implements Validator, InitableByConstraintMap
{
    protected boolean required;
    protected String requiredMessage;

    /** The matching mask String as supplied by the XML input */
    protected String maskString;

    /** The compiled perl5 Regular expression from the ORO Perl5Compiler */
    protected Pattern maskPattern;

    /** The message to report if the mask constraint is not satisfied */
    protected String maskMessage;
    protected int minLength;
    protected String minLengthMessage;
    protected int maxLength;
    protected String maxLengthMessage;

    protected String message;

    /** perl5 compiler, needed for setting up the masks */
    private Perl5Compiler patternCompiler = new Perl5Compiler();

    /** perl5 matcher */
    private Perl5Matcher patternMatcher = new Perl5Matcher();

    /** Logging */
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Constructor
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public DefaultValidator(Map paramMap)
            throws InvalidMaskException
    {
        init(paramMap);
    }

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
        // Init Mask stuff
        maskString = null;
        maskPattern = null;
        maskMessage = null;

        // Init minLength stuff
        minLength = 0;
        minLengthMessage = null;

        // Init maxLength stuff
        maxLength = 0;
        maxLengthMessage = null;

        Constraint constraint = (Constraint) paramMap.get("mask");
        if (constraint != null)
        {
            String param = constraint.getValue();
            setMask(param);
            maskMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get("minLength");
        if (constraint != null)
        {
            String param = constraint.getValue();
            minLength = Integer.parseInt(param);
            minLengthMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get("maxLength");
        if (constraint != null)
        {
            String param = constraint.getValue();
            maxLength = Integer.parseInt(param);
            maxLengthMessage = constraint.getMessage();
        }

        constraint = (Constraint) paramMap.get("required");
        if (constraint == null)
        {
            required = false;
        }
        else
        {
            String param = constraint.getValue();
            required = new Boolean(param).booleanValue();
            requiredMessage = constraint.getMessage();
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
        message = null;

        if ((!required && minLength == 0)
                && (testValue == null || testValue.length() == 0))
        {
            return;
        }
        else if (required
                && (testValue == null || testValue.length() == 0))
        {
            message = requiredMessage;
            throw new ValidationException(requiredMessage);
        }

        // allow subclasses first chance at validation
        doAssertValidity(testValue);

        if (maskPattern != null)
        {
            boolean patternMatch =
                    patternMatcher.matches(testValue, maskPattern);

            log.debug("Trying to match " + testValue
                    + " to pattern " + maskString);

            if (!patternMatch)
            {
                message = maskMessage;
                throw new ValidationException(maskMessage);
            }
        }

        if (minLength > 0 && testValue.length() < minLength)
        {
            message = minLengthMessage;
            throw new ValidationException(minLengthMessage);
        }
        if (maxLength > 0 && testValue.length() > maxLength)
        {
            message = maxLengthMessage;
            throw new ValidationException(maxLengthMessage);
        }
    }

    /**
     * Get the last error message resulting from invalid input.
     *
     * @return a <code>String</code> message, or the empty String "".
     */
    public String getMessage()
    {
        if (message == null)
        {
            return "";
        }
        return message;
    }

    /**
     * Method to allow subclasses to add additional validation
     *
     * @param testValue Value to validate
     * @throws ValidationException validation failed
     */
    protected void doAssertValidity(String testValue)
            throws ValidationException
    {
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
     * @param message  Value to assign to requiredMessage.
     */
    public void setRequiredMessage(String message)
    {
        this.requiredMessage = message;
    }

    /**
     * Get the value of mask.
     *
     * @return value of mask.
     */
    public String getMask()
    {
        return maskString;
    }

    /**
     * Set the value of mask.
     *
     * @param mask  Value to assign to mask.
     * @throws InvalidMaskException the mask could not be compiled.
     */
    public void setMask(String mask)
            throws InvalidMaskException
    {
        maskString = mask;

        // Fixme. We should make this configureable by the XML file -- hps
        int maskOptions = Perl5Compiler.DEFAULT_MASK;

        try
        {
            log.debug("Compiling pattern " + maskString);
            maskPattern = patternCompiler.compile(maskString, maskOptions);
        }
        catch (MalformedPatternException mpe)
        {
            throw new InvalidMaskException("Could not compile pattern " + maskString, mpe);
        }
    }

    /**
     * Get the value of maskMessage.
     *
     * @return value of maskMessage.
     */
    public String getMaskMessage()
    {
        return maskMessage;
    }

    /**
     * Set the value of maskMessage.
     *
     * @param message  Value to assign to maskMessage.
     */
    public void setMaskMessage(String message)
    {
        this.maskMessage = message;
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
     * @param length  Value to assign to minLength.
     */
    public void setMinLength(int length)
    {
        this.minLength = length;
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
     * @param message  Value to assign to minLengthMessage.
     */
    public void setMinLengthMessage(String message)
    {
        this.minLengthMessage = message;
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
     * @param length  Value to assign to maxLength.
     */
    public void setMaxLength(int length)
    {
        this.maxLength = length;
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
     * @param message  Value to assign to maxLengthMessage.
     */
    public void setMaxLengthMessage(String message)
    {
        this.maxLengthMessage = message;
    }
}
