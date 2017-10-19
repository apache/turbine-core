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

import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.turbine.util.TurbineException;

/**
 * Service for a cron like scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TorqueSchedulerService.java 534527 2007-05-02 16:10:59Z tv $
 *
 * @deprecated Use {@link QuartzSchedulerService} instead
 */
@Deprecated
public class TorqueSchedulerService extends AbstractSchedulerService
{
    /**
     * Load all jobs from configuration storage
     *
     * @return the list of pre-configured jobs
     * @throws TurbineException
     */
    @Override
    protected List<? extends JobEntry> loadJobs() throws TurbineException
    {
        // Load all from cold storage.
        try
        {
            List<JobEntryTorque> jobsTorque = JobEntryTorquePeer.doSelect(new Criteria());

            for (JobEntryTorque job : jobsTorque)
            {
                job.calcRunTime();
            }

            return jobsTorque;
        }
        catch (TorqueException e)
        {
            throw new TurbineException("Error retrieving initial job list from persistent storage.", e);
        }
    }

    /**
     * @see org.apache.turbine.services.schedule.ScheduleService#newJob(int, int, int, int, int, java.lang.String)
     */
    @Override
    public JobEntry newJob(int sec, int min, int hour, int wd, int day_mo, String task) throws TurbineException
    {
        JobEntryTorque jet = new JobEntryTorque();
        jet.setSecond(sec);
        jet.setMinute(min);
        jet.setHour(hour);
        jet.setWeekDay(wd);
        jet.setDayOfMonth(day_mo);
        jet.setTask(task);

        return jet;
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid
     *            The int id for the job.
     * @return A JobEntry.
     * @throws TurbineException
     *                job could not be retrieved.
     */
    @Override
    public JobEntry getJob(int oid) throws TurbineException
    {
        try
        {
            JobEntryTorque je = JobEntryTorquePeer.retrieveByPK(oid);
            return scheduleQueue.getJob(je);
        }
        catch (TorqueException e)
        {
            throw new TurbineException("Error retrieving job from persistent storage.", e);
        }
    }

    /**
     * Remove a job from the queue.
     *
     * @param je
     *            A JobEntry with the job to remove.
     * @throws TurbineException
     *                job could not be removed
     */
    @Override
    public void removeJob(JobEntry je) throws TurbineException
    {
        try
        {
            // First remove from DB.
            Criteria c = new Criteria().where(JobEntryTorquePeer.JOB_ID, Integer.valueOf(je.getJobId()));
            JobEntryTorquePeer.doDelete(c);

            // Remove from the queue.
            scheduleQueue.remove(je);

            // restart the scheduler
            restart();
        }
        catch (TorqueException e)
        {
            throw new TurbineException("Problem removing Scheduled Job: " + je.getTask(), e);
        }
    }

    /**
     * Add or update a job.
     *
     * @param je
     *            A JobEntry with the job to modify
     * @throws TurbineException
     *             job could not be updated
     */
    @Override
    public void updateJob(JobEntry je) throws TurbineException
    {
        try
        {
            je.calcRunTime();

            // Update the queue.
            if (je.isNew())
            {
                scheduleQueue.add(je);
            }
            else
            {
                scheduleQueue.modify(je);
            }

            if (je instanceof JobEntryTorque)
            {
                ((JobEntryTorque)je).save();
            }

            restart();
        }
        catch (TorqueException e)
        {
            throw new TurbineException("Problem persisting Scheduled Job: " + je.getTask(), e);
        }
        catch (TurbineException e)
        {
            throw new TurbineException("Problem updating Scheduled Job: " + je.getTask(), e);
        }
    }
}
