package org.apache.turbine.services.schedule;

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

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import org.apache.turbine.om.BaseObject;
import org.apache.turbine.om.NumberKey;
import org.apache.turbine.util.db.Criteria;

/**
 * This is a wrapper for a scheduled job.  It is modeled after the
 * Unix scheduler cron.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class JobEntry extends BaseObject
    implements Comparable
{
    /** Valid entry ( 0-60 ). **/
    private int second = -1;

    /** Valid entry ( 0-59 ). **/
    private int minute = -1;

    /** Valid entry ( 0-23 ). **/
    private int hour = -1;

    /** Valid entry ( 1-7 ). **/
    private int weekday = -1;

    /** Valid entry ( 1-31 ). **/
    private int day_of_month = -1;

    /** The Task to perform. **/
    private String task = null;

    /** Next runtime. **/
    private long runtime = 0;

    /** E-mail address to send notification of job run. **/
    private String email = "";

    /** indicates if job is currently running */
    private boolean jobIsActive = false;

    /** schedule types **/
    private static final int SECOND = 0;
    private static final int MINUTE = 1;
    private static final int WEEK_DAY = 2;
    private static final int DAY_OF_MONTH = 3;
    private static final int DAILY = 4;

    /** Storage for additional properties */
    private Hashtable jobProp = null;

    /**
     * Constuctor.
     *
     * Schedule a job to run on a certain point of time.<br>
     *
     * Example 1: Run the DefaultScheduledJob at 8:00am every 15th of
     * the month - <br>
     *
     * JobEntry je = new JobEntry(0,0,8,15,"DefaultScheduledJob");<br>
     *
     * Example 2: Run the DefaultScheduledJob at 8:00am every day -
     * <br>
     *
     * JobEntry je = new JobEntry(0,0,8,-1,"DefaultScheduledJob");<br>
     *
     * Example 3: Run the DefaultScheduledJob every 2 hours. - <br>
     *
     * JobEntry je = new JobEntry(0,120,-1,-1,"DefaultScheduledJob");<br>
     *
     * Example 4: Run the DefaultScheduledJob every 30 seconds. - <br>
     *
     * JobEntry je = new JobEntry(30,-1,-1,-1,"DefaultScheduledJob");<br>
     *
     * @param sec Value for entry "seconds".
     * @param min Value for entry "minutes".
     * @param hour Value for entry "hours".
     * @param wd Value for entry "week days".
     * @param day_mo Value for entry "month days".
     * @param task Task to execute.
     * @exception Exception, a generic exception.
     */
    public JobEntry(int sec,
                    int min,
                    int hour,
                    int wd,
                    int day_mo,
                    String task)
        throws Exception
    {
        if ( task == null || task.length() == 0 )
            throw new Exception("Error in JobEntry. Bad Job parameter. Task not set.");

        this.second = sec;
        this.minute = min;
        this.hour = hour;
        this.weekday = wd;
        this.day_of_month = day_mo;
        this.task = task;

        calcRunTime();
    }


    /**
     * Calculate how long before the next runtime.<br>
     *
     * The runtime determines it's position in the job queue.
     * Here's the logic:<br>
     *
     * 1. Create a date the represents when this job is to run.<br>
     *
     * 2. If this date has expired, them "roll" appropriate date
     * fields forward to the next date.<br>
     *
     * 3. Calculate the diff in time between the current time and the
     * next run time.<br>
     *
     * @exception Exception, a generic exception.
     */
    public void calcRunTime()
        throws Exception
    {
        Calendar schedrun = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        switch( evaluateJobType() )
        {
        case 0:
            // SECOND (every so many seconds...)
            schedrun.add (Calendar.SECOND,second);
            runtime = schedrun.getTime().getTime();
            break;
        case 1:
            // MINUTE (every so many minutes...)
            schedrun.add (Calendar.SECOND,second);
            schedrun.add(Calendar.MINUTE,minute);
            runtime = schedrun.getTime().getTime();
            break;

        case 2:
            // WEEKDAY (day of the week)
            schedrun.add (Calendar.SECOND,second);
            schedrun.set(Calendar.MINUTE,minute);
            schedrun.set(Calendar.HOUR_OF_DAY,hour);
            schedrun.set(Calendar.DAY_OF_WEEK,weekday);

            if ( now.before(schedrun) )
            {
                // Scheduled time has NOT expired.
                runtime = schedrun.getTime().getTime();
            }
            else
            {
                // Scheduled time has expired; roll to the next week.
                schedrun.add(Calendar.DAY_OF_WEEK,7);
                runtime = schedrun.getTime().getTime();
            }
            break;

        case 3:
            // DAY_OF_MONTH (date of the month)
            schedrun.add (Calendar.SECOND,second);
            schedrun.set(Calendar.MINUTE,minute);
            schedrun.set(Calendar.HOUR_OF_DAY,hour);
            schedrun.set(Calendar.DAY_OF_MONTH,day_of_month);

            if ( now.before(schedrun) )
            {
                // Scheduled time has NOT expired.
                runtime = schedrun.getTime().getTime();
            }
            else
            {
                // Scheduled time has expired; roll to the next month.
                schedrun.add(Calendar.MONTH,1);
                runtime = schedrun.getTime().getTime();
            }
            break;

        case 4:
            // DAILY (certain hour:minutes of the day)
            schedrun.add (Calendar.SECOND,second);
            schedrun.set(Calendar.MINUTE,minute);
            schedrun.set(Calendar.HOUR_OF_DAY,hour);

            // Scheduled time has NOT expired.
            if ( now.before(schedrun) )
            {
                runtime = schedrun.getTime().getTime();
            }
            else
            {
                // Scheduled time has expired; roll forward 24 hours.
                schedrun.add(Calendar.HOUR_OF_DAY,24);
                runtime = schedrun.getTime().getTime();
            }
            break;

        default:
            // Do nothing.
        }
    }

    /**
     * Get the next runtime for this job as a long.
     *
     * @return The next run time as a long.
     */
    public long getNextRuntime()
    {
        return runtime;
    }

    /**
     * Get the next runtime for this job as a String.
     *
     * @return The next run time as a String.
     */
    public String getNextRunAsString()
    {
        return new Date(runtime).toString();
    }

    /**
     * The address to send mail notifications to.  This just holds the
     * address.  The ScheduledJob should handle actually sending the
     * mail.
     *
     * @param mail The email address.
     */
    public void setEmail(String mail)
    {
        this.email = mail;
        setModified(true);
    }

    /**
     * Return the e-mail address for notification.
     *
     * @return The email address.
     */
    public String getEmail()
    {
        if ( email == null || email.length() == 0 )
        {
            return "not set";
        }
        else
        {
            return email;
        }
    }

    /**
     * Return the task for this job.  A task name is the Class name of
     * the ScheduledJob created by the programmer.
     *
     * @return A String with the name of the scheduled job.
     */
    public String getTask()
    {
        return task;
    }

    /**
     * Set the task name for this job.  A task name is the Class name
     * of the ScheduledJob created by the programmer.
     *
     * @param task A String with the name of the job.
     */
    public void setTask(String task)
    {
        this.task = task;
        setModified(true);
    }

    /**
     * Get the value of second.
     *
     * @return Value of second.
     */
    public int getSecond()
    {
        return second;
    }

    /**
     * Set the value of second.
     *
     * @param v Value to assign to second.
     */
    public void setSecond(int v)
    {
        this.second = v;
        setModified(true);
    }


    /**
     * Get the value of minute.
     *
     * @return Value of minute.
     */
    public int getMinute()
    {
        return minute;
    }

    /**
     * Set the value of minute.
     *
     * @param v Value to assign to minute.
     */
    public void setMinute(int v)
    {
        this.minute = v;
        setModified(true);
    }

    /**
     * Get the value of hour.
     *
     * @return Value of hour.
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * Set the value of hour.
     *
     * @param v Value to assign to hour.
     */
    public void setHour(int v)
    {
        this.hour = v;
        setModified(true);
    }

    /**
     * Get the value of weekday.
     *
     * @return Value of weekday.
     */
    public int getWeekday()
    {
        return weekday;
    }

    /**
     * Set the value of weekday.
     *
     * @param v Value to assign to weekday.
     */
    public void setWeekday(int v)
    {
        this.weekday = v;
        setModified(true);
    }

    /**
     * Get the value of day_of_month.
     *
     * @return Value of day_of_month.
     */
    public int getDay_of_month()
    {
        return day_of_month;
    }

    /**
     * Set the value of day_of_month.
     *
     * @param v Value to assign to day_of_month.
     */
    public void setDay_of_month(int v)
    {
        this.day_of_month = v;
        setModified(true);
    }

    /**
     * Self-preservation.
     *
     * @exception Exception, a generic exception.
     */
    public void save()
        throws Exception
    {
        Criteria criteria = new Criteria(9)
            .add(JobEntryPeer.SECOND, getSecond())
            .add(JobEntryPeer.MINUTE, getMinute())
            .add(JobEntryPeer.HOUR, getHour())
            .add(JobEntryPeer.WEEKDAY, getWeekday())
            .add(JobEntryPeer.DAY_OF_MONTH, getDay_of_month())
            .add(JobEntryPeer.TASK, getTask())
            .add(JobEntryPeer.EMAIL, getEmail())
            .add(JobEntryPeer.PROPERTY, getProperty());

        NumberKey nk = (NumberKey)getPrimaryKey();
        long key = 0;
        if (nk != null)
        {
            key = ((NumberKey)getPrimaryKey()).getBigDecimal().longValue();
        }
        if ( isModified() && key > 0)
        {
            // This is an update.
            criteria.add(JobEntryPeer.OID, getPrimaryKey());
            JobEntryPeer.doUpdate(criteria);
            setModified(false);
        }
        else
        {
            setPrimaryKey( JobEntryPeer.doInsert(criteria) );
        }
    }

    /**
     * What schedule am I on?
     *
     * I know this is kinda ugly!  If you can think of a cleaner way
     * to do this, please jump in!
     *
     * @return A number specifying the type of schedule. See
     * calcRunTime().
     * @exception Exception, a generic exception.
     */
    private int evaluateJobType()
        throws Exception
    {

        // First start by checking if it's a day of the month job.
        if ( day_of_month < 0 )
        {
            // Not a day of the month job... check weekday.
            if ( weekday < 0 )
            {
                // Not a weekday job...check if by the hour.
                if ( hour < 0 )
                {
                    // Not an hourly job...check if it is by the minute
                    if ( minute < 0 )
                    {
                        // Not a by the minute job so must be by the second
                        if ( second < 0)
                            throw new Exception("Error in JobEntry. Bad Job parameter.");

                        return SECOND;
                    }
                    else
                    {
                        // Must be a job run by the minute so we need minutes and
                        // seconds.
                        if ( minute < 0 || second < 0 )
                            throw new Exception("Error in JobEntry. Bad Job parameter.");

                        return MINUTE;
                    }
                }
                else
                {
                    // Must be a daily job by hours minutes, and seconds.  In
                    // this case, we need the minute, second, and hour params.
                    if ( minute < 0 || hour < 0 || second < 0)
                        throw new Exception("Error in JobEntry. Bad Job parameter.");

                    return DAILY;
                }
            }
            else
            {
                // Must be a weekday job.  In this case, we need
                // minute, second, and hour params
                if ( minute < 0 || hour < 0 || second < 0 )
                    throw new Exception("Error in JobEntry. Bad Job parameter.");

                return WEEK_DAY;
            }
        }
        else
        {
            // Must be a day of the month job.  In this case, we need
            // minute, second, and hour params
            if ( minute < 0 || hour < 0 )
                throw new Exception("Error in JobEntry. Bad Job parameter.");

            return DAY_OF_MONTH;
        }
    }

    /**
     * Used for ordering Jobentries
     * Note: this comparator imposes orderings that are inconsistent with
     * equals.
     *
     * @param je The first <code>JobEntry</code> object.
     * @return An <code>int</code> indicating the result of the comparison.
     */
    public int compareTo(Object je)
    {
        long obj1Time = this.getNextRuntime();
        long obj2Time = ((JobEntry)je).getNextRuntime();
        if (obj1Time > obj2Time)
        {
            return 1;
        }
        else if (obj1Time < obj2Time)
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Compare this Job with another.
     *
     * @param je The JobEntry object to compare to.
     * @return True if they're the same.
     */
    public boolean equals(Object je)
    {
        if ( !( je instanceof JobEntry ) )
            return false;

        return ( ((JobEntry)je).getPrimaryKey().equals(this.getPrimaryKey()) );
    }

    /**
     * Sets whether the job is running.
     *
     * @param isActive Whether the job is running.
     */
    public void setActive(boolean isActive)
    {
        jobIsActive = isActive;
    }

    /**
     * Check to see if job is currently active/running
     *
     * @return true if job is currently geing run by the
     *  workerthread, otherwise false
     */
     public boolean isActive()
     {
        return jobIsActive;
     }

    /**
     * Set job properties
     *
     */
    public void setProperty(Hashtable prop)
    {
        jobProp = prop;
        setModified(true);
    }

    /**
     * Get extra job properties
     *
     */
    public Hashtable getProperty()
    {
        if ( jobProp == null )
        {
            return new Hashtable(89);
        }
        else
        {
            return jobProp;
        }
    }
}
