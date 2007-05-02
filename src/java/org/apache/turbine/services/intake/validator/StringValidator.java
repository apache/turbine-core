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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

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
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @version $Id$
 */
public class StringValidator
        extends DefaultValidator
{
    /** The matching mask String as supplied by the XML input */
    protected String maskString = null;

    /** The compiled perl5 Regular expression from the ORO Perl5Compiler */
    protected Pattern maskPattern = null;

    /** The message to report if the mask constraint is not satisfied */
    protected String maskMessage = null;


    /**
     * Constructor
     *
     * @param paramMap a <code>Map</code> of <code>Rule</code>'s
     * containing constraints on the input.
     * @exception InvalidMaskException An invalid mask was specified for one of the rules

    */
    public StringValidator(Map paramMap)
            throws InvalidMaskException
    {
        init(paramMap);
    }

    /**
     * Default constructor
     */
    public StringValidator()
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
        super.init(paramMap);

        Constraint constraint = (Constraint) paramMap.get(MASK_RULE_NAME);
        if (constraint != null)
        {
            String param = constraint.getValue();
            setMask(param);
            maskMessage = constraint.getMessage();
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
        super.assertValidity(testValue);

        if (required || StringUtils.isNotEmpty(testValue))
        {
            if (maskPattern != null)
            {
                /** perl5 matcher */
                Perl5Matcher patternMatcher = new Perl5Matcher();

                boolean patternMatch =
                        patternMatcher.matches(testValue, maskPattern);

                log.debug("Trying to match " + testValue
                        + " to pattern " + maskString);

                if (!patternMatch)
                {
                    errorMessage = maskMessage;
                    throw new ValidationException(maskMessage);
                }
            }
        }
    }

    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

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
        /** perl5 compiler, needed for setting up the masks */
        Perl5Compiler patternCompiler = new Perl5Compiler();

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
}
