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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.quartz.QuartzScheduler;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;

/**
 * Service for a quartz scheduler.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class QuartzSchedulerService
        extends TurbineBaseService
        implements ScheduleService
{
    /** Logging */
    protected static Log log = LogFactory.getLog(ScheduleService.LOGGER_NAME);

    /** Current status of the scheduler */
    protected boolean enabled = false;

    /** The Quartz scheduler instance */
    private Scheduler scheduler;

    /**
     * Initializes the SchedulerService.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */
    @Override
    public void init()
            throws InitializationException
    {
        setEnabled(getConfiguration().getBoolean("enabled", true));
        QuartzScheduler qs = (QuartzScheduler) TurbineServices.getInstance()
            .getService(QuartzScheduler.class.getName());
        this.scheduler = qs.getScheduler();

        restart();
        setInit(true);
    }

    /**
     * Shutdowns the service.
     *
     * This methods interrupts the housekeeping thread.
     */
    @Override
    public void shutdown()
    {
        try
        {
            this.scheduler.shutdown();
        }
        catch (SchedulerException e)
        {
            log.error("Could not shut down the scheduler service", e);
        }
    }

    /**
     * @see org.apache.turbine.services.schedule.ScheduleService#newJob(int, int, int, int, int, java.lang.String)
     */
    @Override
    public JobEntry newJob(int sec, int min, int hour, int wd, int day_mo, String task) throws TurbineException
    {
        try
        {
            JobDetail jd = JobBuilder.newJob(JobEntryQuartz.class)
                    .withIdentity(task, JobEntryQuartz.DEFAULT_JOB_GROUP_NAME)
                    .build();

            CronScheduleBuilder csb = createCronExpression(sec, min, hour, wd, day_mo);

            Trigger t = TriggerBuilder.newTrigger()
                    .withIdentity(task, JobEntryQuartz.DEFAULT_JOB_GROUP_NAME)
                    .withSchedule(csb)
                    .forJob(jd)
                    .build();

            JobEntryQuartz jeq = new JobEntryQuartz(t, jd);

            return jeq;
        }
        catch (ParseException e)
        {
            throw new TurbineException("Could not create scheduled job " + task, e);
        }
    }

    /**
     * Create a Cron expression from separate elements
     *
     * @param sec Value for entry "seconds".
     * @param min Value for entry "minutes".
     * @param hour Value for entry "hours".
     * @param wd Value for entry "week days".
     * @param day_mo Value for entry "month days".
     * @return a CronScheduleBuilder
     * @throws ParseException if the expression is invalid
     */
    private CronScheduleBuilder createCronExpression(int sec, int min, int hour, int wd, int day_mo) throws ParseException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(sec == -1 ? "*" : String.valueOf(sec)).append(' ');
        sb.append(min == -1 ? "*" : String.valueOf(min)).append(' ');
        sb.append(hour == -1 ? "*" : String.valueOf(hour)).append(' ');
        if (day_mo == -1)
        {
            sb.append(wd == -1 ? "*" : "?").append(' ');
        }
        else
        {
            sb.append(day_mo).append(' ');
        }
        sb.append("* "); // Month not supported
        if (day_mo == -1)
        {
            sb.append(wd == -1 ? "?" : String.valueOf(wd));
        }
        else
        {
            sb.append("*");
        }

        return CronScheduleBuilder.cronSchedule(sb.toString());
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @throws TurbineException job could not be retrieved.
     */
    @Override
    public JobEntry getJob(int oid)
            throws TurbineException
    {
        for (JobEntry je : listJobs())
        {
            if (je.getJobId() == oid)
            {
                return je;
            }
        }

        throw new TurbineException("Could not retrieve scheduled job with id " + oid);
    }

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @throws TurbineException job could not be added
     */
    @Override
    public void addJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            // Update the scheduler.
            JobEntryQuartz jq = downCast(je);
            this.scheduler.scheduleJob(jq.getJobDetail(), jq.getJobTrigger());
        }
        catch (SchedulerException e)
        {
            throw new TurbineException("Problem adding Scheduled Job: " + je.getTask(), e);
        }
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @throws TurbineException job could not be removed
     */
    @Override
    public void removeJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            JobEntryQuartz jq = downCast(je);
            this.scheduler.deleteJob(jq.getJobTrigger().getJobKey());

        }
        catch (SchedulerException e)
        {
            throw new TurbineException("Problem removing Scheduled Job: " + je.getTask(), e);
        }
    }

    /**
     * Add or update a job.
     *
     * @param je A JobEntry with the job to modify
     * @throws TurbineException job could not be updated
     */
    @Override
    public void updateJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            // Update the scheduler.
            JobEntryQuartz jq = downCast(je);
            this.scheduler.rescheduleJob(jq.getJobTrigger().getKey(), jq.getJobTrigger());
        }
        catch (SchedulerException e)
        {
            throw new TurbineException("Problem updating Scheduled Job: " + je.getTask(), e);
        }
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A List of jobs.
     */
    @Override
    public List<? extends JobEntry> listJobs()
    {
        List<JobEntryQuartz> jobs = new ArrayList<JobEntryQuartz>();

        try
        {
            @SuppressWarnings("unchecked") // See QTZ-184
            GroupMatcher<JobKey> groupMatcher = GroupMatcher.groupEquals(JobEntryQuartz.DEFAULT_JOB_GROUP_NAME);
            Set<JobKey> jobKeys = scheduler.getJobKeys(groupMatcher);
            for (JobKey jk : jobKeys)
            {
                List<? extends Trigger> triggers = this.scheduler.getTriggersOfJob(jk);

                if (triggers == null || triggers.isEmpty())
                {
                    continue; // skip
                }
                JobDetail jd = this.scheduler.getJobDetail(jk);
                JobEntryQuartz job = new JobEntryQuartz(triggers.get(0), jd);
                job.setJobId(jk.hashCode());
                jobs.add(job);
            }
        }
        catch (SchedulerException e)
        {
            log.error("Problem listing Scheduled Jobs", e);
        }

        return jobs;
    }


    /**
     * Sets the enabled status of the scheduler
     *
     * @param enabled
     *
     */
    protected void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Determines if the scheduler service is currently enabled.
     *
     * @return Status of the scheduler service.
     */
    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Starts or restarts the scheduler if not already running.
     */
    @Override
    public synchronized void startScheduler()
    {
        setEnabled(true);
        restart();
    }

    /**
     * Stops the scheduler if it is currently running.
     */
    @Override
    public synchronized void stopScheduler()
    {
        log.info("Stopping job scheduler");
        try
        {
            this.scheduler.standby();
            enabled = false;
        }
        catch (SchedulerException e)
        {
            log.error("Could not stop scheduler", e);
        }
    }

    /**
     * Start (or restart) a thread to process commands, or wake up an
     * existing thread if one is already running.  This method can be
     * invoked if the background thread crashed due to an
     * unrecoverable exception in an executed command.
     */
    public synchronized void restart()
    {
        if (enabled)
        {
            log.info("Starting job scheduler");
            try
            {
                if (!this.scheduler.isStarted())
                {
                    this.scheduler.start();
                }
                else
                {
                    notify();
                }
            }
            catch (SchedulerException e)
            {
                log.error("Could not start scheduler", e);
            }
        }
    }

    /**
     * @param je a generic job entry
     * @throws TurbineException
     *
     * @return A downcasted JobEntry type
     */
    private JobEntryQuartz downCast(JobEntry je) throws TurbineException
    {
        if (je instanceof JobEntryQuartz)
        {
            return (JobEntryQuartz)je;
        }
        else
        {
            throw new TurbineException("Invalid job type for this scheduler " + je.getClass());
        }
    }
}

