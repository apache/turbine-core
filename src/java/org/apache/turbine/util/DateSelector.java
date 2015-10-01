package org.apache.turbine.util;


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


import java.text.DateFormatSymbols;
import java.util.Calendar;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Select;

/**
 * DateSelector is a utility class to handle the creation of a set of
 * date popup menus.  The code is broken into a set of static methods
 * for quick and easy access to the individual select objects:
 *
 *  <pre>
 *  ElementContainer ec dateSelect = new ElementContainer();
 *  String myName = "mydate";
 *  ec.addElement(DateSelector.getMonthSelector(myName));
 *  ec.addElement(DateSelector.getDaySelector(myName));
 *  ec.addElement(DateSelector.getYearSelector(myName));
 *  </pre>
 *
 * There are also methods which will use attributes to build a
 * complete month,day,year selector:
 *
 *  <pre>
 *  DateSelector ds = new DateSelector(myName);
 *  dateSelect = ds.ecsOutput();
 *  </pre>
 *
 * The above element container would use the onChange setting and may
 * hide the selected day if set via showDays().<br>
 *
 * @author <a href="mailto:ekkerbj@netscape.net">Jeffrey D. Brekke</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:leon@clearink.com">Leon Atkinson</a>
 * @version $Id$
 */
public class DateSelector
{
    /** Prefix for date names. */
    public static final String DEFAULT_PREFIX = "DateSelector";

    /** Suffix for day parameter. */
    public static final String DAY_SUFFIX = "_day";

    /** Suffix for month parameter. */
    public static final String MONTH_SUFFIX = "_month";

    /** Suffix for year parameter. */
    public static final String YEAR_SUFFIX = "_year";

    private Calendar useDate = null;
    private String selName = null;
    private static final String[] monthName =
            new DateFormatSymbols().getMonths();
    private String onChange = null;
    private boolean onChangeSet = false;
    private boolean showDays = true;
    private int setDay = 0;
    private boolean useYears = false;
    private int firstYear = 0;
    private int lastYear = 0;
    private int selectedYear = 0;

    /**
     * Constructor defaults to current date and uses the default
     * prefix: <pre>DateSelector.DEFAULT</pre>
     */
    public DateSelector()
    {
        this(DEFAULT_PREFIX);
    }

    /**
     * Constructor defaults to current date.
     *
     * @param selName A String with the selector name.
     */
    public DateSelector(String selName)
    {
        this(selName, Calendar.getInstance());
    }

    /**
     * Constructor, uses the date set in a calendar that has been
     * already passed in (with the date set correctly).
     *
     * @param selName A String with the selector name.
     * @param useDate A Calendar with a date.
     */
    public DateSelector(String selName, Calendar useDate)
    {
        this.useDate = useDate;
        this.selName = selName;
    }

    /**
     * Adds the onChange to all of &lt;SELECT&gt; tags.  This is limited to
     * one function for all three popups and is only used when the
     * output() methods are used.  Individual getMonth, getDay,
     * getYear static methods will not use this setting.
     *
     * @param onChange A String to use for onChange attribute.  If null,
     * then nothing will be set.
     * @return A DateSelector (self).
     */
    public DateSelector setOnChange(String onChange)
    {
        if (onChange != null)
        {
            this.onChange = onChange;
            this.onChangeSet = true;
        }
        else
        {
            this.onChange = null;
            this.onChangeSet = false;
        }
        return this;
    }

    /**
     * Select the day to be selected if the showDays(false) behavior
     * is used.  Individual getMonth, getDay, getYear static methods
     * will not use this setting.
     *
     * @param day The day.
     * @return A DateSelector (self).
     */
    public DateSelector setDay(int day)
    {
        this.setDay = day;
        this.showDays = false;
        return this;
    }

    /**
     * Whether or not to show the days as a popup menu.  The days will
     * be a hidden parameter and the value set with setDay is used.
     * Individual getMonth, getDay, getYear static methods will not
     * use this setting.
     *
     * @param show True if the day should be shown.
     * @return A DateSelector (self).
     */
    public DateSelector setShowDay(boolean show)
    {
        this.showDays = false;
        return this;
    }

    /**
     * Set the selector name prefix.  Individual getMonth, getDay,
     * getYear static methods will not use this setting.
     *
     * @param selName A String with the select name prefix.
     */
    public void setSelName(String selName)
    {
        this.selName = selName;
    }

    /**
     * Get the selector name prefix.
     *
     * @return A String with the select name prefix.
     */
    public String getSelName()
    {
        return selName;
    }

    /**
     * Return a month selector.
     *
     * @param name The name to use for the selected month.
     * @return A select object with all the months.
     */
    public static Select getMonthSelector(String name)
    {
        return (getMonthSelector(name, Calendar.getInstance()));
    }

    /**
     * Return a month selector.
     *
     * Note: The values of the month placed into the select list are
     * the month integers starting at 0 (ie: if the user selects
     * February, the selected value will be 1).
     *
     * @param name The name to use for the selected month.
     * @param now Calendar to start with.
     * @return A select object with all the months.
     */
    public static Select getMonthSelector(String name, Calendar now)
    {
        Select monthSelect = new Select().setName(name);

        for (int curMonth = 0; curMonth <= 11; curMonth++)
        {
            Option o = new Option();
            o.addElement(monthName[curMonth]);
            o.setValue(curMonth);
            if ((now.get(Calendar.MONTH)) == curMonth)
            {
                o.setSelected(true);
            }
            monthSelect.addElement(o);
        }
        return (monthSelect);
    }

    /**
     * Return a day selector.
     *
     * @param name The name to use for the selected day.
     * @return A select object with all the days in a month.
     */
    public static Select getDaySelector(String name)
    {
        return (getDaySelector(name, Calendar.getInstance()));
    }

    /**
     * Return a day selector.
     *
     * @param name The name to use for the selected day.
     * @param now Calendar to start with.
     * @return A select object with all the days in a month.
     */
    public static Select getDaySelector(String name, Calendar now)
    {
        Select daySelect = new Select().setName(name);

        for (int currentDay = 1; currentDay <= 31; currentDay++)
        {
            Option o = new Option();
            o.addElement(Integer.toString(currentDay));
            o.setValue(currentDay);
            if (now.get(Calendar.DAY_OF_MONTH) == currentDay)
            {
                o.setSelected(true);
            }
            daySelect.addElement(o);
        }
        return (daySelect);
    }

    /**
     * Return a year selector.
     *
     * @param name The name to use for the selected year.
     * @return A select object with all the years starting five years
     * from now and five years before this year.
     */
    public static Select getYearSelector(String name)
    {
        return (getYearSelector(name, Calendar.getInstance()));
    }

    /**
     * Return a year selector.
     *
     * @param name The name to use for the selected year.
     * @param now Calendar to start with.
     * @return A select object with all the years starting five years
     * from now and five years before this year.
     */
    public static Select getYearSelector(String name, Calendar now)
    {
        int startYear = now.get(Calendar.YEAR);
        return (getYearSelector(name, startYear - 5, startYear + 5, startYear));
    }

    /**
     * Return a year selector.
     *
     * @param name The name to use for the selected year.
     * @param firstYear the first (earliest) year in the selector.
     * @param lastYear the last (latest) year in the selector.
     * @param selectedYear the year initially selected in the Select html.
     * @return A select object with all the years from firstyear
     * to lastyear..
     */
    public static Select getYearSelector(String name,
                                         int firstYear, int lastYear,
                                         int selectedYear)
    {
        Select yearSelect = new Select().setName(name);

        for (int currentYear = firstYear;
             currentYear <= lastYear;

             currentYear++)
        {
            Option o = new Option();
            o.addElement(Integer.toString(currentYear));
            o.setValue(currentYear);
            if (currentYear == selectedYear)
            {
                o.setSelected(true);
            }
            yearSelect.addElement(o);
        }
        return (yearSelect);
    }

    /**
     * Set a year range to be displayed
     * @param firstYear start of year range
     * @param lastYear end of year range
     * @param selectedYear entry to select
     * @return true if the range settings are valid
     */
    public boolean setYear(int firstYear, int lastYear, int selectedYear)
    {
        if (firstYear <= lastYear && firstYear <= selectedYear
                && selectedYear <= lastYear)
        {
            this.useYears = true;
            this.firstYear = firstYear;
            this.lastYear = lastYear;
            this.selectedYear = selectedYear;
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Used to build the popupmenu in HTML.  The properties set in the
     * object are used to generate the correct HTML.  The selName
     * attribute is used to seed the names of the select lists.  The
     * names will be generated as follows:
     *
     * <ul>
     *  <li>selName + "_month"</li>
     *  <li>selName + "_day"</li>
     *  <li>selName + "_year"</li>
     * </ul>
     *
     * If onChange was set it is also used in the generation of the
     * output.  The output HTML will list the select lists in the
     * following order: month day year.
     *
     * @return A String with the correct HTML for the date selector.
     */
    public String output()
    {
        return (ecsOutput().toString());
    }

    /**
     * Used to build the popupmenu in HTML.  The properties set in the
     * object are used to generate the correct HTML.  The selName
     * attribute is used to seed the names of the select lists.  The
     * names will be generated as follows:
     *
     * <ul>
     *  <li>selName + "_month"</li>
     *  <li>selName + "_day"</li>
     *  <li>selName + "_year"</li>
     * </ul>
     *
     * The output HTML will list the select lists in the following
     * order: month day year.
     *
     * @return A String with the correct HTML for the date selector.
     */
    @Override
    public String toString()
    {
        return (ecsOutput().toString());
    }

    /**
     * Return an ECS container with the month, day, and year select
     * objects inside.
     *
     * @return An ECS container.
     */
    public ElementContainer ecsOutput()
    {
        if (this.useDate == null)
        {
            this.useDate = Calendar.getInstance();
        }

        Select monthSelect = getMonthSelector(selName + MONTH_SUFFIX, useDate);
        ConcreteElement daySelect = null;
        if (!showDays)
        {
            daySelect = new Input(Input.hidden, selName + DAY_SUFFIX, setDay);
        }
        else
        {
            Select tmp = getDaySelector(selName + DAY_SUFFIX, useDate);
            if (onChangeSet)
            {
                tmp.setOnChange(onChange);
            }
            daySelect = tmp;
        }
        Select yearSelect = null;
        if (useYears)
        {
            yearSelect = getYearSelector(selName + YEAR_SUFFIX,
                    firstYear, lastYear, selectedYear);
        }
        else
        {
            yearSelect = getYearSelector(selName + YEAR_SUFFIX, useDate);
        }
        if (onChangeSet)
        {
            monthSelect.setOnChange(onChange);
            yearSelect.setOnChange(onChange);
        }
        ElementContainer ec = new ElementContainer();
        // ec.addElement(new Comment("== BEGIN org.apache.turbine.util.DateSelector.ecsOutput() =="));
        ec.addElement(monthSelect);
        ec.addElement(daySelect);
        ec.addElement(yearSelect);
        // ec.addElement(new Comment("== END org.apache.turbine.util.DateSelector.ecsOutput() =="));
        return (ec);
    }
}
