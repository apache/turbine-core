package org.apache.turbine.services.intake.model;

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
                    + " into a valid Date for the default value", e);
        }
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

        try
        {
            emptyValue = getDate(prop);
        }
        catch (ParseException e)
        {
            throw new TurbineRuntimeException("Could not parse " + prop
                    + " into a valid Date for the empty value", e);
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
                            ? getDate(inputs[i]) : (Date) getEmptyValue();
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
                setTestValue(StringUtils.isNotEmpty(val) ? getDate(val) : (Date) getEmptyValue());
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
