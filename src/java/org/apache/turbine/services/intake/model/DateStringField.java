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

import java.text.DateFormat;
import java.text.ParseException;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.intake.IntakeException;
import org.apache.turbine.services.intake.validator.DateStringValidator;
import org.apache.turbine.services.intake.xmlmodel.XmlField;
import org.apache.turbine.util.TurbineRuntimeException;

/**
 * Field for date inputs as free form text.  The parsing of date strings
 * is dependent on any rules that are defined, so this field will expect that
 * any validator will be (or extend) DateStringValidator.
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class DateStringField
        extends Field
{
    /** date format */
    private DateFormat df = null;

    /**
     * Constructor.
     *
     * @param field xml field definition object
     * @param group xml group definition object
     * @throws IntakeException thrown by superclass
     */
    public DateStringField(XmlField field, Group group)
            throws IntakeException
    {
        super(field, group);

        if (validator == null || !(validator instanceof DateStringValidator))
        {
            df = DateFormat.getInstance();
            df.setLenient(true);
        }
    }

    /**
     * Sets the default value for a DateString field
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

        try
        {
            defaultValue = getDate(prop);
        }
        catch (ParseException e)
        {
            throw new TurbineRuntimeException("Could not parse " + prop
                    + " into a valid Date", e);
        }
    }

    /**
     * A suitable validator.
     *
     * @return "DateStringValidator"
     */
    protected String getDefaultValidator()
    {
        return DateStringValidator.class.getName();
    }

    /**
     * Sets the value of the field from data in the parser.
     */
    protected void doSetValue()
    {
        if (isMultiValued)
        {
            String[] inputs = parser.getStrings(getKey());
            Date[] values = new Date[inputs.length];
            for (int i = 0; i < inputs.length; i++)
            {
                try
                {
                    values[i] = StringUtils.isNotEmpty(inputs[i]) 
                            ? getDate(inputs[i]) : null;
                }
                catch (ParseException e)
                {
                    values[i] = null;
                }
            }
            setTestValue(values);
        }
        else
        {
            String val = parser.getString(getKey());
            try
            {
                setTestValue(StringUtils.isNotEmpty(val) ? getDate(val) : null);
            }
            catch (ParseException e)
            {
                setTestValue(null);
            }
        }
    }

    /**
     * Parses a test date string using the Validator if is exists and
     * is an instance of DateStringValidator.  Otherwise, DateFormat.parse()
     * is used.
     *
     * @param dateString The string date to parse
     * @return A <code>Date</code> object
     * @throws ParseException The date could not be parsed.
     */
    private Date getDate(String dateString)
            throws ParseException
    {
        Date date = null;
        // FIXME: Canonicalize user-entered date strings.
        if (validator != null && validator instanceof DateStringValidator)
        {
            date = ((DateStringValidator) validator).parse(dateString);
        }
        else
        {
            date = df.parse(dateString);
        }
        return date;
    }

    /**
     * returns a String representation
     *
     * @return a String representation
     */
    public String toString()
    {
        String s = null;
        Object value = getValue();
        if (value == null)
        {
            s = "";
        }
        else if (value instanceof String)
        {
            s = (String) value;
        }
        else if (validator != null && validator instanceof DateStringValidator)
        {
            s = ((DateStringValidator) validator).format((Date) value);
        }
        else
        {
            s = df.format((Date) value);
        }
        return s;
    }
}
