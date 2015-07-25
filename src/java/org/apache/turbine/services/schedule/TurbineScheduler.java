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


import java.util.List;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;

/**
 * This is a fascade class to provide easy access to the Scheduler
 * service.  All access methods are static and act upon the current
 * instance of the scheduler service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 * @see org.apache.turbine.services.schedule.ScheduleService
 */
public abstract class TurbineScheduler
{
    /**
     * Factory method for a new Job
     *
     * Schedule a job to run on a certain point of time.<br>
     *
     * Example 1: Run the DefaultScheduledJob at 8:00am every 15th of
     * the month - <br>
     *
     * JobEntry je = newJob(0,0,8,-1,15,"DefaultScheduledJob");<br>
     *
     * Example 2: Run the DefaultScheduledJob at 8:00am every day -
     * <br>
     *
     * JobEntry je = newJob(0,0,8,-1,-1,"DefaultScheduledJob");<br>
     *
     * Example 3: Run the DefaultScheduledJob every 2 hours. - <br>
     *
     * JobEntry je = newJob(0,120,-1,-1,-1,"DefaultScheduledJob");<br>
     *
     * Example 4: Run the DefaultScheduledJob every 30 seconds. - <br>
     *
     * JobEntry je = newJob(30,-1,-1,-1,-1,"DefaultScheduledJob");<br>
     *
     * @param sec Value for entry "seconds".
     * @param min Value for entry "minutes".
     * @param hour Value for entry "hours".
     * @param wd Value for entry "week days".
     * @param day_mo Value for entry "month days".
     * @param task Task to execute.
     *
     * @return A JobEntry.
     * @exception TurbineException could not create job
     */
    public static JobEntry newJob(int sec,
            int min,
            int hour,
            int wd,
            int day_mo,
            String task) throws TurbineException
    {
        return getService().newJob(sec, min, hour, wd, day_mo, task);
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException job could not be retrieved
     */
    public static JobEntry getJob(int oid)
            throws TurbineException
    {
        return getService().getJob(oid);
    }

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @exception TurbineException job could not be added
     */
    public static void addJob(JobEntry je)
            throws TurbineException
    {
        getService().addJob(je);
    }

    /**
     * Add or update a job
     *
     * @param je A JobEntry with the job to modify
     * @exception TurbineException job could not be updated
     */
    public static void updateJob(JobEntry je)
            throws TurbineException
    {
        getService().updateJob(je);
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception TurbineException job could not be removed
     */
    public static void removeJob(JobEntry je)
            throws TurbineException
    {
        getService().removeJob(je);
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A Vector of jobs.
     */
    public static List<? extends JobEntry> listJobs()
    {
        return getService().listJobs();
    }

    /**
     * Determines if the scheduler service is currently active.
     *
     * @return Status of the scheduler service.
     */
    public static boolean isEnabled()
    {
        return getService().isEnabled();
    }

    /**
     * Starts the scheduler if not already running.
     */
    public static void startScheduler()
    {
        getService().startScheduler();
    }

    /**
     * Stops the scheduler if ti is currently running.
     */
    public static void stopScheduler()
    {
        getService().stopScheduler();
    }

    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a ScheduleService implementation instance
     */
    private static ScheduleService getService()
    {
        return (ScheduleService) TurbineServices
                .getInstance().getService(ScheduleService.SERVICE_NAME);
    }

}
