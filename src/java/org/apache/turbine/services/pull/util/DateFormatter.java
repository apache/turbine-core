package org.apache.turbine.services.pull.util;


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
