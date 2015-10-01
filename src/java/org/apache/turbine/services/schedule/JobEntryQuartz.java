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

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.modules.ScheduledJobLoader;
import org.apache.turbine.util.TurbineException;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.core.QuartzScheduler;

/**
 * This implements a Turbine scheduled job model for the {@link QuartzScheduler}.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class JobEntryQuartz implements JobEntry, Job
{
    private int jobId;
    private Trigger jobTrigger;
    private JobDetail jobDetail;
    private String task;
    private boolean isnew = true;
    private AtomicBoolean active = new AtomicBoolean(false);

    /**
     * the default Quartz schedule group name for Turbine jobs
     */
    public static final String DEFAULT_JOB_GROUP_NAME = "TURBINE";

    /**
     * Default constructor
     */
    public JobEntryQuartz()
    {
        super();
    }

    /**
     * Constructor
     *
     * @param jobTrigger Job time table
     */
    public JobEntryQuartz(Trigger jobTrigger)
    {
        this(jobTrigger, JobBuilder
                .newJob(JobEntryQuartz.class)
                .withIdentity(jobTrigger.getJobKey().getName(), DEFAULT_JOB_GROUP_NAME).build());
    }

    /**
     * Constructor
     *
     * @param jobTrigger Job time table
     * @param jobDetail job details
     */
    public JobEntryQuartz(Trigger jobTrigger, JobDetail jobDetail)
    {
        this();
        setTask(jobTrigger.getJobKey().getName());
        this.jobTrigger = jobTrigger;
        this.jobDetail = jobDetail;
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

    /**
     * @return the jobTrigger
     */
    public Trigger getJobTrigger()
    {
        return jobTrigger;
    }

    /**
     * @param jobTrigger the jobTrigger to set
     */
    public void setJobTrigger(Trigger jobTrigger)
    {
        this.jobTrigger = jobTrigger;
    }

    /**
     * @return the jobDetail
     */
    public JobDetail getJobDetail()
    {
        return jobDetail;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(JobEntry o)
    {
        return jobTrigger.compareTo(((JobEntryQuartz)o).getJobTrigger());
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#setActive(boolean)
     */
    @Override
    public void setActive(boolean isActive)
    {
        this.active.set(isActive);
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#isActive()
     */
    @Override
    public boolean isActive()
    {
        return active.get();
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#getNextRuntime()
     */
    @Override
    public long getNextRuntime()
    {
        return getNextRunDate().getTime();
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#getNextRunDate()
     */
    @Override
    public Date getNextRunDate()
    {
        return jobTrigger.getNextFireTime();
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#getNextRunAsString()
     */
    @Override
    public String getNextRunAsString()
    {
        return getNextRunDate().toString();
    }

    /**
     * @see org.apache.turbine.services.schedule.JobEntry#calcRunTime()
     */
    @Override
    public void calcRunTime() throws TurbineException
    {
        // do nothing
    }

    /**
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        if (active.compareAndSet(false, true) == false)
        {
            return;
        }

        try
        {
            String task = getTask();
            if (StringUtils.isEmpty(task))
            {
                // This happens when the job is configured in the Quartz configuration file
                task = context.getJobDetail().getKey().getName();
            }
            ScheduledJobLoader.getInstance().exec(this, task);
        }
        catch (Exception e)
        {
            throw new JobExecutionException("Error executing scheduled job #" +
                    getJobId() + ", task: " + getTask(), e);
        }
        finally
        {
            active.compareAndSet(true, false);
        }
    }
}
