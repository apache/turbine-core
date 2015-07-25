package org.apache.turbine.services.schedule;

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

import org.apache.turbine.util.TurbineException;

/**
 * This is an implementation of a JobEntry with no persistence. It is used by the
 * {@link TurbineNonPersistentSchedulerService}
 *
 */
public class JobEntryNonPersistent extends AbstractJobEntry
{
    private int jobId;
    private int sec;
    private int min;
    private int hour;
    private int wd;
    private int day_mo;
    private String task;
    private boolean isnew = true;

    /**
     * Default constructor
     */
    public JobEntryNonPersistent()
    {
        super();
    }

    /**
     * Constructor.
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
     * @exception TurbineException a generic exception.
     */
    public JobEntryNonPersistent(int sec,
                    int min,
                    int hour,
                    int wd,
                    int day_mo,
                    String task)
            throws TurbineException
    {
        super(sec, min, hour, wd, day_mo, task);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Integer.valueOf(jobId).hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof JobEntry)
        {
            return compareTo((JobEntry)obj) == 0;
        }

        return false;
    }

    /**
     * Return true, if the entry is not yet persisted
     */
    @Override
    public boolean isNew()
    {
        boolean _isnew = isnew;
        isnew = false;
        return _isnew;
    }

    /**
     * Get the value of jobId.
     *
     * @return int
     */
    @Override
    public int getJobId()
    {
        return jobId;
    }

    /**
     * Set the value of jobId.
     *
     * @param v new value
     */
    @Override
    public void setJobId(int v)
    {
        this.jobId = v;
    }

    /**
     * Get the value of second.
     *
     * @return int
     */
    @Override
    public int getSecond()
    {
        return sec;
    }

    /**
     * Set the value of second.
     *
     * @param v new value
     */
    @Override
    public void setSecond(int v)
    {
        this.sec = v;
    }

    /**
     * Get the value of minute.
     *
     * @return int
     */
    @Override
    public int getMinute()
    {
        return min;
    }

    /**
     * Set the value of minute.
     *
     * @param v new value
     */
    @Override
    public void setMinute(int v)
    {
        this.min = v;
    }

    /**
     * Get the value of hour.
     *
     * @return int
     */
    @Override
    public int getHour()
    {
        return hour;
    }

    /**
     * Set the value of hour.
     *
     * @param v new value
     */
    @Override
    public void setHour(int v)
    {
        this.hour = v;
    }

    /**
     * Get the value of weekDay.
     *
     * @return int
     */
    @Override
    public int getWeekDay()
    {
        return wd;
    }

    /**
     * Set the value of weekDay.
     *
     * @param v new value
     */
    @Override
    public void setWeekDay(int v)
    {
        this.wd = v;
    }

    /**
     * Get the value of dayOfMonth.
     *
     * @return int
     */
    @Override
    public int getDayOfMonth()
    {
        return day_mo;
    }

    /**
     * Set the value of dayOfMonth.
     *
     * @param v new value
     */
    @Override
    public void setDayOfMonth(int v)
    {
        this.day_mo = v;
    }

    /**
     * Get the value of task.
     *
     * @return String
     */
    @Override
    public String getTask()
    {
        return task;
    }

    /**
     * Set the value of task.
     *
     * @param v new value
     */
    @Override
    public void setTask(String v)
    {
        this.task = v;
    }
}
