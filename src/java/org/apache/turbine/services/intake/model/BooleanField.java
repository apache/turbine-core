package org.apache.turbine.services.intake.model;

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

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.BooleanValidator;
import org.apache.turbine.services.intake.xmlmodel.XmlField;

/**
 * Processor for boolean fields.
 *
 * @deprecated Use the Fulcrum Intake component instead.
 * @version $Id$
 */
public class BooleanField
        extends Field
{
    public BooleanField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);
    }

    /**
     * Sets the default value for a Boolean field
     *
     * @param prop Parameter for the default values
     */
    public void setDefaultValue(String prop)
    {
        defaultValue = null;

        if (prop == null)
        {
            return;
        }

        defaultValue = new Boolean(prop);
    }

    /**
     * Set the empty Value. This value is used if Intake
     * maps a field to a parameter returned by the user and
     * the corresponding field is either empty (empty string)
     * or non-existant.
     *
     * @param prop The value to use if the field is empty.
     */
    public void setEmptyValue(String prop)
    {
        emptyValue = null;

        if (prop == null)
        {
            return;
        }

        emptyValue = new Boolean(prop);
    }

    /**
     * Provides access to emptyValue such that the value returned will be 
     * acceptable as an argument parameter to Method.invoke.  Subclasses
     * that deal with primitive types should ensure that they return an 
     * appropriate value wrapped in the object wrapper class for the 
     * primitive type.
     *   
     * @return the value to use when the field is empty or an Object that
     * wraps the empty value for primitive types.
     */
    protected Object getSafeEmptyValue()
    {
        return (null == getEmptyValue()) ? Boolean.FALSE : getEmptyValue(); 
    }

    /**
     * A suitable validator.
     *
     * @return class name of the validator
     */
    protected String getDefaultValidator()
    {
        return BooleanValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            boolean[] values = new boolean[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                values[i] = StringUtils.isNotEmpty(inputs[i])
                        ? getBoolean(inputs[i]).booleanValue() 
                        : ((Boolean) getEmptyValue()).booleanValue();
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            setTestValue(StringUtils.isNotEmpty(val) ? getBoolean(val) : (Boolean) getEmptyValue());
        }
    }

    /**
     * Parses a string into a Boolean object.  If the field has a validator
     * and the validator is an instance of BooleanValidator, the parse()
     * method is used to convert the string into the Boolean.  Otherwise,
     * the string value is passed to the constructor to the Boolean
     * object.
     *
     * @param stringValue string to parse
     * @return a <code>Boolean</code> object
     */
    private Boolean getBoolean(String stringValue)
    {
        Boolean result = null;

        if (validator != null && validator instanceof BooleanValidator)
        {
            BooleanValidator bValidator = (BooleanValidator) validator;
            try
            {
                result = bValidator.parse(stringValue);
            }
            catch (ParseException e)
            {
                // do nothing.  This should never be thrown since this method will not be
                // executed unless the Validator has already been able to parse the
                // string value
            }
        }
        else
        {
            result = new Boolean(stringValue);
        }

        return result;
    }

    /**
     * Gets the boolean value of the field.  A value of false will be returned
     * if the value of the field is null.
     *
     * @return value of the field.
     */
    public boolean booleanValue()
    {
        boolean result = false;
        try
        {
            result = ((Boolean) getValue()).booleanValue();
        }
        catch (Exception e)
        {
            log.error(e);
        }
        return result;
    }
    
}
