package org.apache.turbine.util;

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

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.ecs.ConcreteElement;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.html.Comment;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Select;

/**
 * TimeSelector is a utility class to handle the creation of a set of
 * time drop-down menus.  The code is broken into a set of static methods
 * for quick and easy access to the individual select objects:
 *
 *  <pre>
 *  ElementContainer ec timeSelect = new ElementContainer();
 *  String myName = "mytime";
 *  ec.addElement(TimeSelector.getHourSelector(myName));
 *  ec.addElement(TimeSelector.getMinuteSelector(myName));
 *  ec.addElement(TimeSelector.getAMPMSelector(myName));
 *  </pre>
 *
 * There are also methods which will use attributes to build a
 * complete time selector in the default 12hr format (eg HH:MM am/pm):
 *
 *  <pre>
 *  TimeSelector ts = new TimeSelector(myName);
 *  timeSelect = ts.ecsOutput();
 *  </pre>
 *
 * Minutes/Seconds are by default rounded to the nearest 5 units
 * although this can be easily changed.
 *
 * 24hr TimeSelectors can also be produced. The following example
 * creates a full precision TimeSelector (eg HH:MM:SS):
 *
 *  <pre>
 *  TimeSelector ts = new TimeSelector(myName);
 *  ts.setTimeFormat(TimeSelector.TWENTY_FOUR_HOUR);
 *  ts.setMinuteInterval(1);
 *  ts.setSecondInterval(1);
 *  ts.setShowSeconds(true);
 *  timeSelect = ts.toString();
 *  </pre>
 *
 * @author <a href="mailto:ekkerbj@netscape.net">Jeffrey D. Brekke</a>
 * @author <a href="mailto:rich@thenetrevolution.com">Rich Aston</a>
 * @version $Id$
 */
public class TimeSelector
{
    /** Prefix for time names. */
    public static final String DEFAULT_PREFIX = "TimeSelector";

    /** Suffix for hour parameter. */
    public static final String HOUR_SUFFIX = "_hour";

    /** Suffix for minute parameter. */
    public static final String MINUTE_SUFFIX = "_minute";

    /** Suffix for second parameter. */
    public static final String SECOND_SUFFIX = "_second";

    /** Suffix for am/pm parameter. */
    public static final String AMPM_SUFFIX = "_ampm";

    /** Constant for 12hr format */
    public static final int TWELVE_HOUR = 0;

    /** Constant for 24hr format */
    public static final int TWENTY_FOUR_HOUR = 1;

    /** TODO: Add ability to specify Locale. */
    private static final NumberFormat nbrFmt;

    private static final int DEFAULT_MINUTE_INTERVAL = 5;
    private static final int DEFAULT_SECOND_INTERVAL = 5;
    private static final int DEFAULT_TIME_FORMAT = TWELVE_HOUR;

    private int timeFormat = DEFAULT_TIME_FORMAT;
    private int minuteInterval = DEFAULT_MINUTE_INTERVAL;
    private int secondInterval = DEFAULT_SECOND_INTERVAL;

    private Calendar useDate = null;
    private String selName = null;
    private String onChange = null;
    private boolean onChangeSet = false;
    private boolean showSeconds = false;
    private int setSeconds = 0;

    static
    {
        nbrFmt = NumberFormat.getInstance();
        nbrFmt.setMinimumIntegerDigits(2);
        nbrFmt.setMaximumIntegerDigits(2);
    }

    /**
     * Constructor defaults to current date/time and uses the default
     * prefix: <pre>TimeSelector.DEFAULT</pre>
     */
    public TimeSelector()
    {
        this.selName = DEFAULT_PREFIX;
        this.useDate = Calendar.getInstance();
        this.useDate.setTime(new Date());
    }

    /**
     * Constructor, uses the date/time set in the calendar
     * passed in (with the date/time set correctly).
     *
     * @param selName A String with the selector name.
     * @param useDate A Calendar with a date/time.
     */
    public TimeSelector(String selName, Calendar useDate)
    {
        this.useDate = useDate;
        this.selName = selName;
    }

    /**
     * Constructor defaults to current date/time.
     *
     * @param selName A String with the selector name.
     */
    public TimeSelector(String selName)
    {
        this.selName = selName;
        this.useDate = Calendar.getInstance();
        this.useDate.setTime(new Date());
    }

    /**
     * Adds the onChange to all of <code>&lt;SELECT&gt;</code> tags.
     * This is limited to one function for all three popups and is only
     * used when the output() methods are used.  Individual getHour,
     * getMinute, getSecond, getAMPM static methods will not use this
     * setting.
     *
     * @param onChange A String to use for onChange attribute.  If null,
     * then nothing will be set.
     * @return A TimeSelector (self).
     */
    public TimeSelector setOnChange(String onChange)
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
     * Select the second to be selected if the showSeconds(false) behavior
     * is used.  Individual getHour, getMinute, getSecond, getAMPM
     * static methods will not use this setting.
     *
     * @param seconds The second.
     * @return A TimeSelector (self).
     */
    public TimeSelector setSeconds(int seconds)
    {
        this.setSeconds = seconds;
        this.showSeconds = false;
        return this;
    }

    /**
     * Set the interval between options in the minute select box.
     * Individual getHour, getMinute, getSecond, getAMPM static methods
     * will not use this setting.
     *
     * @param minutes Interval in minutes.
     * @return A TimeSelector (self).
     */
    public TimeSelector setMinuteInterval(int minutes)
    {
        this.minuteInterval = minutes;
        return this;
    }

    /**
     * Set the interval between options in the second select box.
     * Individual getHour, getMinute, getSecond, getAMPM static methods
     * will not use this setting.
     *
     * @param seconds Interval in seconds.
     * @return A TimeSelector (self).
     */
    public TimeSelector setSecondInterval(int seconds)
    {
        this.secondInterval = seconds;
        return this;
    }

    /**
     * Set the time format to 12 or 24 hour. Individual getHour,
     * getMinute, getSecond, getAMPM static methods
     * will not use this setting.
     *
     * @param format Time format.
     * @return A TimeSelector (self).
     */
    public TimeSelector setTimeFormat(int format)
    {
        this.timeFormat = format;
        return this;
    }

    /**
     * Whether or not to show the seconds as a popup menu.  The seconds will
     * be a hidden parameter and the value set with setSeconds is used.
     * Individual getHour, getMinute, getSecond, getAMPM static methods
     * will not use this setting.
     *
     * @param show True if the second should be shown.
     * @return A TimeSelector (self).
     */
    public TimeSelector setShowSeconds(boolean show)
    {
        this.showSeconds = show;
        return this;
    }

    /**
     * Set the selector name prefix.  Individual getHour, getMinute,
     * getSeconds, getAMPM static methods will not use this setting.
     *
     * @param selname A String with the select name prefix.
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
     * Return a second selector.
     *
     * @param name The name to use for the selected second.
     * @return A select object with second options.
     */
    public static Select getSecondSelector(String name)
    {
        return (getSecondSelector(name, Calendar.getInstance()));
    }

    /**
     * Return a second selector.
     *
     * @param name The name to use for the selected second.
     * @param now Calendar to start with.
     * @return A select object with second options.
     */
    public static Select getSecondSelector(String name, Calendar now)
    {
        return (getSecondSelector(name, Calendar.getInstance(),
                DEFAULT_SECOND_INTERVAL));
    }

    /**
     * Return a second selector.
     *
     * @param name The name to use for the selected second.
     * @param now Calendar to start with.
     * @param interval Interval between options.
     * @return A select object with second options.
     */
    public static Select getSecondSelector(String name, Calendar now,
                                           int interval)
    {
        Select secondSelect = new Select().setName(name);

        for (int currentSecond = 0; currentSecond <= 59; currentSecond += interval)
        {
            Option o = new Option();
            o.addElement(nbrFmt.format(currentSecond));
            o.setValue(currentSecond);
            int nearestSecond =
                    ((now.get(Calendar.SECOND) / interval) * interval);

            if (nearestSecond == currentSecond)
            {
                o.setSelected(true);
            }
            secondSelect.addElement(o);
        }
        return (secondSelect);
    }

    /**
     * Return a minute selector.
     *
     * @param name The name to use for the selected minute.
     * @return A select object with minute options.
     */
    public static Select getMinuteSelector(String name)
    {
        return (getMinuteSelector(name, Calendar.getInstance()));
    }

    /**
     * Return a minute selector.
     *
     * @param name The name to use for the selected minute.
     * @return A select object with minute options.
     */
    public static Select getMinuteSelector(String name, Calendar now)
    {
        return (getMinuteSelector(name, now, DEFAULT_MINUTE_INTERVAL));
    }

    /**
     * Return a minute selector.
     *
     * @param name The name to use for the selected minute.
     * @param now Calendar to start with.
     * @param interval Interval between options.
     * @return A select object with minute options.
     */
    public static Select getMinuteSelector(String name, Calendar now,
                                           int interval)
    {
        Select minuteSelect = new Select().setName(name);

        for (int curMinute = 0; curMinute <= 59; curMinute += interval)
        {
            Option o = new Option();
            o.addElement(nbrFmt.format(curMinute));
            o.setValue(curMinute);
            int nearestMinute =
                    ((now.get(Calendar.MINUTE)) / interval) * interval;

            if (nearestMinute == curMinute)
            {
                o.setSelected(true);
            }
            minuteSelect.addElement(o);
        }
        return (minuteSelect);
    }

    /**
     * Return an 12 hour selector.
     *
     * @param name The name to use for the selected hour.
     * @return A select object with all the hours.
     */
    public static Select getHourSelector(String name)
    {
        return (getHourSelector(name, Calendar.getInstance()));
    }

    /**
     * Return an 12 hour selector.
     *
     * @param name The name to use for the selected hour.
     * @param now Calendar to start with.
     * @return A select object with all the hours.
     */
    public static Select getHourSelector(String name, Calendar now)
    {
        return (getHourSelector(name, Calendar.getInstance(), TWELVE_HOUR));
    }

    /**
     * Return an hour selector (either 12hr or 24hr depending on
     * <code>format</code>.
     *
     * @param name The name to use for the selected hour.
     * @param now Calendar to start with.
     * @param format Time format.
     * @return A select object with all the hours.
     */
    public static Select getHourSelector(String name, Calendar now, int format)
    {
        Select hourSelect = new Select().setName(name);

        if (format == TWENTY_FOUR_HOUR)
        {
            for (int currentHour = 0; currentHour <= 23; currentHour++)
            {
                Option o = new Option();
                o.addElement(nbrFmt.format(currentHour));
                o.setValue(currentHour);
                if (now.get(Calendar.HOUR_OF_DAY) == currentHour)
                {
                    o.setSelected(true);
                }
                hourSelect.addElement(o);
            }
        }
        else
        {
            for (int curHour = 1; curHour <= 12; curHour++)
            {
                Option o = new Option();

                o.addElement(nbrFmt.format((long) curHour));
                o.setValue(curHour);
                if (now.get(Calendar.AM_PM) == Calendar.AM)
                {
                    if (((now.get(Calendar.HOUR_OF_DAY)) == 0) &&
                            (curHour == 12))
                    {
                        o.setSelected(true);
                    }
                    else
                    {
                        if (now.get(Calendar.HOUR_OF_DAY) == curHour)
                        {
                            o.setSelected(true);
                        }
                    }
                }
                else
                {
                    if (((now.get(Calendar.HOUR_OF_DAY)) == 12) &&
                            (curHour == 12))
                    {
                        o.setSelected(true);
                    }
                    else
                    {
                        if (now.get(Calendar.HOUR_OF_DAY) == curHour + 12)
                        {
                            o.setSelected(true);
                        }
                    }
                }
                hourSelect.addElement(o);
            }
        }
        return (hourSelect);
    }

    /**
     * Return an am/pm selector.
     *
     * @param name The name to use for the selected am/pm.
     * @return A select object with am/pm
     */
    public static Select getAMPMSelector(String name)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return (getAMPMSelector(name, c));
    }

    /**
     * Return an am/pm selector.
     *
     * @param name The name to use for the selected am/pm.
     * @param now Calendar to start with.
     * @return A select object with am/pm.
     */
    public static Select getAMPMSelector(String name,
                                         Calendar now)
    {
        Select ampmSelect = new Select().setName(name);

        Option o = new Option();
        o.addElement("am");
        o.setValue(Calendar.AM);
        if (now.get(Calendar.AM_PM) == Calendar.AM)
        {
            o.setSelected(true);
        }
        ampmSelect.addElement(o);

        o = new Option();
        o.addElement("pm");
        o.setValue(Calendar.PM);
        if (now.get(Calendar.AM_PM) == Calendar.PM)
        {
            o.setSelected(true);
        }
        ampmSelect.addElement(o);

        return (ampmSelect);
    }

    /**
     * Used to build the popupmenu in HTML.  The properties set in the
     * object are used to generate the correct HTML.  The selName
     * attribute is used to seed the names of the select lists.  The
     * names will be generated as follows:
     *
     * <ul>
     *  <li>selName + "_hour"</li>
     *  <li>selName + "_minute"</li>
     *  <li>selName + "_ampm"</li>
     * </ul>
     *
     * If onChange was set it is also used in the generation of the
     * output.  The output HTML will list the select lists in the
     * following order: hour minute ampm.
     *
     * If setShowSeconds(true) is used then an addition second select box
     * is produced after the minute select box.
     *
     * If setTimeFormat(TimeSelector.TWENTY_FOUR_HOUR) is used then
     * the ampm select box is omitted.
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
     *  <li>selName + "_hour"</li>
     *  <li>selName + "_minute"</li>
     *  <li>selName + "_ampm"</li>
     * </ul>
     *
     * If onChange was set it is also used in the generation of the
     * output.  The output HTML will list the select lists in the
     * following order: hour minute ampm.
     *
     * If setShowSeconds(true) is used then an addition second select box
     * is produced after the minute select box.
     *
     * If setTimeFormat(TimeSelector.TWENTY_FOUR_HOUR) is used then
     * the ampm select box is omitted.
     *
     * @return A String with the correct HTML for the date selector.
     */
    public String toString()
    {
        return (ecsOutput().toString());
    }

    /**
     * Return an ECS container with the select objects inside.
     *
     * @return An ECS container.
     */
    public ElementContainer ecsOutput()
    {
        if (this.useDate == null)
        {
            this.useDate = Calendar.getInstance();
            this.useDate.setTime(new Date());
        }

        ConcreteElement secondSelect = null;

        Select ampmSelect = getAMPMSelector(selName + AMPM_SUFFIX, useDate);

        Select hourSelect = getHourSelector(selName + HOUR_SUFFIX,
                useDate, this.timeFormat);

        Select minuteSelect = getMinuteSelector(selName + MINUTE_SUFFIX,
                useDate, this.minuteInterval);

        if (this.showSeconds)
        {
            Select tmp = getSecondSelector(selName + SECOND_SUFFIX, useDate,
                    this.secondInterval);
            if (onChangeSet)
                tmp.setOnChange(onChange);
            secondSelect = tmp;
        }
        else
        {
            secondSelect = new Input(Input.hidden,
                    selName + SECOND_SUFFIX,
                    setSeconds);
        }

        if (onChangeSet)
        {
            hourSelect.setOnChange(onChange);
            minuteSelect.setOnChange(onChange);
            ampmSelect.setOnChange(onChange);
        }

        ElementContainer ec = new ElementContainer();
        ec.addElement(new Comment(
                "== BEGIN org.apache.turbine.util.TimeSelector.ecsOutput() =="));
        ec.addElement(hourSelect);
        ec.addElement(":");
        ec.addElement(minuteSelect);
        if (this.showSeconds == true)
            ec.addElement(":");
        ec.addElement(secondSelect);
        if (this.timeFormat == TimeSelector.TWELVE_HOUR)
        {
            ec.addElement(ampmSelect);
        }
        ec.addElement(new Comment(
                "== END org.apache.turbine.util.TimeSelector.ecsOutput() =="));
        return (ec);
    }
}
