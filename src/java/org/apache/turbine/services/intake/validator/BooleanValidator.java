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

import java.text.ParseException;
import java.util.Map;

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
 * </ul>
 * Valid values for Boolean.FALSE:
 * <ul>
 * <li>FALSE</li>
 * <li>F</li>
 * <li>NO</li>
 * <li>N</li>
 * <li>0</li>
 * </ul>
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:jh@byteaction.de">J&uuml;rgen Hoffmann</a>
 * @version $Id$
 */
public class BooleanValidator
        extends DefaultValidator
{
    /** String values which would evaluate to Boolean.TRUE */
    private static String[] trueValues = {"TRUE","T","YES","Y","1"};

    /** String values which would evaluate to Boolean.FALSE */
    private static String[] falseValues = {"FALSE","F","NO","N","0"};

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

        if ((required) || ((testValue != null) && (testValue.length() > 0)))
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
