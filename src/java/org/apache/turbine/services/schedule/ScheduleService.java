package org.apache.turbine.services.schedule;


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

    /** TR.props key for intially activating the scheduler thread */
    String INTIALLY_ACTIVE = "enabled";

    /** TR.props key for the logger */
    String LOGGER_NAME = "scheduler";

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException could not retreive job
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
    List listJobs();

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
