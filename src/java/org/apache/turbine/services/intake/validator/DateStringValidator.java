package org.apache.turbine.services.intake.validator;


/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import org.apache.turbine.util.TurbineException;


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
 * <tr><td colspan=3>where x is &gt;= 0 to specify multiple date
 *         formats.  Only one format rule should have a message</td></tr>
 * <tr><td>flexible</td><td>true, as long as DateFormat can parse the date,
 *                            allow it, and false</td>
 * <td>false</td></tr>
 * </table>
 *
 * @author <a href="mailto:jmcnally@collab.net>John McNally</a>
 * @version $Id$
 */
public class DateStringValidator
    extends DefaultValidator
{
    private static final String DEFAULT_DATE_MESSAGE = "Date could not be parsed";
    private List dateFormats;
    private String dateFormatMessage;
    private boolean flexible;
    private DateFormat df;
    private SimpleDateFormat sdf;

    public DateStringValidator(Map paramMap)
        throws TurbineException
    {
        this();
        init(paramMap);
    }

    public DateStringValidator()
    {
        super();
    }

    public void init(Map paramMap)
        throws TurbineException
    {
        super.init(paramMap);
        dateFormats = new ArrayList(5);

        Constraint constraint = (Constraint)paramMap.get("format");

        if (constraint != null)
        {
            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
        }

        int i = 1;
        constraint = (Constraint)paramMap.get("format" + i);

        while (constraint != null)
        {
            dateFormats.add(constraint.getValue());
            setDateFormatMessage(constraint.getMessage());
            constraint = (Constraint)paramMap.get("format" + (++i));
        }

        if (dateFormatMessage == null || dateFormatMessage.equals(""))
        {
            dateFormatMessage = DEFAULT_DATE_MESSAGE;
        }

        constraint = (Constraint)paramMap.get("flexible");

        if (constraint != null)
        {
            flexible = Boolean.valueOf(constraint.getValue()).booleanValue();
        }

        if ((dateFormats.size() == 0) || (flexible))
        {
            df = DateFormat.getInstance();
            df.setLenient(true);
        }

        if (dateFormats.size() != 0)
        {
            sdf = new SimpleDateFormat();
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
    protected void doAssertValidity(String testValue)
        throws ValidationException
    {
        try
        {
            parse(testValue);
        }
        catch (ParseException e)
        {
            message = dateFormatMessage;
            throw new ValidationException(dateFormatMessage);
        }
    }

    /**
     * Parses the String s according to the rules/formats for this
     * validator.
     */
    public Date parse(String s)
        throws ParseException
    {
        Date date = null;

        if (s == null)
        {
            throw new ParseException("Input string was null", -1);
        }

        for (int i = 0; i < dateFormats.size(); i++)
        {
            sdf.applyPattern((String)dateFormats.get(i));

            try
            {
                date = sdf.parse(s);
            }
            catch (ParseException e)
            {
                // ignore
            }

            if (date != null)
            {
                break;
            }
        }

        if ((date == null) && (df != null))
        {
            date = df.parse(s);
        }

        return date;
    }

    // ************************************************************
    // **                Bean accessor methods                   **
    // ************************************************************

    /**
     * Get the value of minLengthMessage.
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
     * @param v  Value to assign to minLengthMessage.
     */
    public void setDateFormatMessage(String v)
    {
        if ((v != null) && (!v.equals("")))
        {
            dateFormatMessage = v;
        }
    }

    /**
     * Get the value of dateFormats.
     * @return value of dateFormats.
     */
    public List getDateFormats()
    {
        return dateFormats;
    }

    /**
     * Set the value of dateFormats.
     * @param v  Value to assign to dateFormats.
     */
    public void setDateFormats(List v)
    {
        this.dateFormats = v;
    }

    /**
     * Get the value of flexible.
     * @return value of flexible.
     */
    public boolean isFlexible()
    {
        return flexible;
    }

    /**
     * Set the value of flexible.
     * @param v  Value to assign to flexible.
     */
    public void setFlexible(boolean v)
    {
        this.flexible = v;
    }
}
