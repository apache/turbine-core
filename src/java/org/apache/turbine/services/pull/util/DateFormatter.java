package org.apache.turbine.services.pull.util;

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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.pull.ApplicationTool;

/**
 * This pull tool is used to format date objects into strings.
 *
 * @author <a href="mailto:qmccombs@nequalsone.com">Quinton McCombs</a>
 * @author <a href="mailto:seade@backstagetech.com.au">Scott Eade</a>
 * @version $Id$
 */
public class DateFormatter
        implements ApplicationTool
{
    /** Used for formatting date objects */
    private SimpleDateFormat sdf = new SimpleDateFormat();

    /** Default date format */
    private static final String DATE_FORMAT_DEFAULT = "MM/dd/yyyy";

    /**
     * Property tag for the date format that is to be used for the web 
     * application.
     */
    private static final String DATE_FORMAT_KEY = "tool.dateTool.format";

    private String dateFormat = null;

    /**
     * Initialize the application tool. The data parameter holds a different
     * type depending on how the tool is being instantiated:
     * <ul>
     * <li>For global tools data will be null
     * <li>For request tools data will be of type RunData
     * <li>For session and persistent tools data will be of type User
     *
     * @param data initialization data
     */
    public void init(Object data)
    {
        dateFormat = Turbine.getConfiguration()
                .getString(DATE_FORMAT_KEY, DATE_FORMAT_DEFAULT);
    }

    /**
     * Refresh the application tool. This is
     * necessary for development work where you
     * probably want the tool to refresh itself
     * if it is using configuration information
     * that is typically cached after initialization
     */
    public void refresh()
    {
    }

    /**
     * Formats the given date as a String using the default date format.
     * The default date format is MM/dd/yyyy
     *
     * @param theDate date to format
     * @return String value of the date
     */
    public String format(Date theDate)
    {
        return format(theDate, dateFormat);
    }

    /**
     * Formats the given date as a String.
     *
     * @param theDate date to format
     * @param dateFormatString format string to use.  See java.text.SimpleDateFormat
     * for details.
     * @return String value of the date
     */
    public String format(Date theDate, String dateFormatString)
    {
        String result = null;

        if (StringUtils.isEmpty(dateFormatString) || theDate == null)
        {
            result = "";
        }
        else
        {
            this.sdf.applyPattern(dateFormatString);
            result = this.sdf.format(theDate);
        }
        return result;
    }

}
