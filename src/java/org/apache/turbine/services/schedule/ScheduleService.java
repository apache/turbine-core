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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.TurbineException;

/**
 * ScheduleService interface.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface ScheduleService
        extends Service
{
    /** Name of service */
    String SERVICE_NAME = "SchedulerService";

    /** TR.props key for initially activating the scheduler thread */
    String INTIALLY_ACTIVE = "enabled";

    /** TR.props key for the logger */
    String LOGGER_NAME = "scheduler";

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
    JobEntry newJob(int sec,
            int min,
            int hour,
            int wd,
            int day_mo,
            String task) throws TurbineException;

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException could not retrieve job
     */
    JobEntry getJob(int oid)
            throws TurbineException;

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @throws TurbineException job could not be added
     */
    void addJob(JobEntry je)
            throws TurbineException;

    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @throws TurbineException job could not be updated
     */
    void updateJob(JobEntry je)
            throws TurbineException;

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception TurbineException job could not be removed
     */
    void removeJob(JobEntry je)
            throws TurbineException;

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A List of jobs.
     */
    List<? extends JobEntry> listJobs();

    /**
     * Determines if the scheduler service is currently active.
     *
     * @return Status of the scheduler service.
     */
    boolean isEnabled();

    /**
     * Starts the scheduler if not already running.
     */
    void startScheduler();

    /**
     * Stops the scheduler if ti is currently running.
     */
    void stopScheduler();

}
