package org.apache.turbine.services.intake.validator;

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
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.apache.turbine.services.intake.IntakeException;

/**
 * Validates numbers with the following constraints in addition to those
 * listed in DefaultValidator.
 *
 * <table>
 * <tr><th>Name</th><th>Valid Values</th><th>Default Value</th></tr>
 * <tr><td>format</td><td>see SimpleDateFormat javadoc</td>
 * <td>&nbsp;</td></tr>
 * <tr><td>formatx</td><td>see SimpleDateFormat javadoc</td>
 * <td>&nbsp;</td></tr>
 * <tr><td colspan=3>where x is &gt;= 1 to specify multiple date
 *         formats.  Only one format rule should have a message</td></tr>
 * <tr><td>flexible</td><td>true, as long as DateFormat can parse the date,
 *                            allow it, and false</td>
 * <td>false</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net">John McNally</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:Colin.Chalmers@maxware.nl">Colin Chalmers</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DateStringValidator
        extends DefaultValidator
{
    private static final String DEFAULT_DATE_MESSAGE =
            "Date could not be parsed";

    /**  */
    private List dateFormats = null;

    /**  */
    private String dateFormatMessage = null;

    /**  */
    private boolean flexible = false;

    /**  */
    private DateFormat df = null;

    /**  */
    private SimpleDateFormat sdf = null;

    public DateStringValidator(final Map paramMap)
            throws IntakeException
    {
        init(paramMap);
    }

    /**
     * Default Constructor
     */
    public DateStringValidator()
    {
        dateFormats = new ArrayList(5);
    }

    /**
     * Constructor to use when initialising Object
     *
     * @param paramMap
     * @throws InvalidMaskException
     */
    public void init(final Map paramMap)
            throws InvalidMaskException
    {
        super.init(paramMap);

        Constraint constraint = (Constraint) paramMap.get(FORMAT_RULE_NAME);

        if (constraint != null)
        {
            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
        }

        for(int i = 1 ;; i++)
        {
            constraint = (Constraint) paramMap.get(FORMAT_RULE_NAME + i);

            if (constraint == null)
            {
                break; // for
            }

            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
        }

        if (StringUtils.isEmpty(dateFormatMessage))
        {
            dateFormatMessage = DEFAULT_DATE_MESSAGE;
        }

        constraint = (Constraint) paramMap.get(FLEXIBLE_RULE_NAME);

        if (constraint != null)
        {
            flexible = Boolean.valueOf(constraint.getValue()).booleanValue();
        }

        if (dateFormats.size() == 0)
        {
            df = DateFormat.getInstance();
            sdf = null;
            df.setLenient(flexible);
        }
        else
        {
            sdf = new SimpleDateFormat();
            df = null;
            sdf.setLenient(flexible);
        }
    }

    /**
     * Determine whether a testValue meets the criteria specified
     * in the constraints defined for this validator
     *
     * @param testValue a <code>String</code> to be tested
     * @exception ValidationException containing an error message if the
     * testValue did not pass the validation tests.
     */
    public void assertValidity(final String testValue)
            throws ValidationException
    {
        super.assertValidity(testValue);

        if (required || StringUtils.isNotEmpty(testValue))
        {
            try
            {
                parse(testValue);
            }
            catch (ParseException e)
            {
                errorMessage = dateFormatMessage;
                throw new ValidationException(dateFormatMessage);
            }
        }
    }

    /**
     * Parses the String s according to the rules/formats for this validator.
     * The formats provided by the "formatx" rules (where x is &gt;= 1) are
     * used <strong>before</strong> the "format" rules to allow for a display
     * format that includes a 4 digit year, but that will parse the date using
     * a format that accepts 2 digit years.
     *
     * @throws ParseException indicates that the string could not be
     * parsed into a date.
     */
    public Date parse(final String s)
            throws ParseException
    {
        Date date = null;

        if (s == null)
        {
            throw new ParseException("Input string was null", -1);
        }

        if (sdf != null) // This implies dateFormats.size() > 0
        {
            // First test the FORMATx patterns. If any of these match, break
            // the loop.
            for (int i = 1 ; i < dateFormats.size() - 1; i++)
            {
                sdf.applyPattern((String) dateFormats.get(i));

                try
                {
                    date = sdf.parse(s);
                    break; // We got a matching date. Break the loop
                }
                catch (ParseException e)
                {
                    // ignore
                }
            }

            // Now test the FORMAT pattern which is the first one in the array.
            // if no format but just FORMATx has been given, all of the patterns
            // have been shifted "one down", e.g. tested as format2, format3, format4, format1
            // in sequence.
            if (date == null)
            {
                sdf.applyPattern((String) dateFormats.get(0));

                try
                {
                    date = sdf.parse(s);
                }
                catch (ParseException e)
                {
                    // ignore
                }
            }
        }

        // Still no match. Either we had no format patterns or no pattern matched.
        // See if we have a DateFormat object around. If there were patterns given
        // and just none matched, that we might have date==null and df==null...
        if (date == null && df != null)
        {
            date = df.parse(s);
        }

        // if the date still has not been parsed at this point, throw
        // a ParseException.
        if (date == null)
        {
            throw new ParseException("Could not parse the date", 0);
        }

        return date;
    }

    /**
     * Formats a date into a String.  The format used is from
     * the first format rule found for the field.
     *
     * @param date the Date object to convert into a string.
     * @return formatted date
     */
    public String format(final Date date)
    {
        String s = null;
        if (date != null)
        {
            if (sdf != null) // implies dateFormats.size() > 0
            {
                sdf.applyPattern((String) dateFormats.get(0));
                s = sdf.format(date);
            }
            else // implied df != null
            {
                s = df.format(date);
            }
        }
        return s;
    }


    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minLengthMessage.
     *
     * @return value of minLengthMessage.
     */
    public String getDateFormatMessage()
    {
        return dateFormatMessage;
    }

    /**
     * Only sets the message if the new message has some information.
     * So the last setMessage call with valid data wins.  But later calls
     * with null or empty string will not affect a previous valid setting.
     *
     * @param message  Value to assign to minLengthMessage.
     */
    public void setDateFormatMessage(final String message)
    {
        if (StringUtils.isNotEmpty(message))
        {
            dateFormatMessage = message;
        }
    }

    /**
     * Get the value of dateFormats.
     *
     * @return value of dateFormats.
     */
    public List getDateFormats()
    {
        return dateFormats;
    }

    /**
     * Set the value of dateFormats.
     *
     * @param formats  Value to assign to dateFormats.
     */
    public void setDateFormats(final List formats)
    {
        this.dateFormats = formats;
    }

    /**
     * Get the value of flexible.
     *
     * @return value of flexible.
     */
    public boolean isFlexible()
    {
        return flexible;
    }

    /**
     * Set the value of flexible.
     *
     * @param flexible  Value to assign to flexible.
     */
    public void setFlexible(final boolean flexible)
    {
        this.flexible = flexible;
    }
}
