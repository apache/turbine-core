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
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
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

        if ((required) || ((testValue != null) && (testValue.length() > 0)))
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
