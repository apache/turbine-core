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
    public static List listJobs()
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
