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
import java.util.Vector;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.TurbineException;

/**
 * Service for a cron like scheduler that uses the
 * TurbineResources.properties file instead of the database.
 * The methods that operate on jobs ( get,add,update,remove )
 * only operate on the queue in memory and changes are not reflected
 * to the properties file which was used to initilize the jobs.
 * An example is given below.  The job names are the class names that
 * extend ScheduledJob.
 *
 * <PRE>
 *
 * services.SchedulerService.scheduler.jobs=scheduledJobName,scheduledJobName2
 *
 * services.SchedulerService.scheduler.job.scheduledJobName.ID=1
 * services.SchedulerService.scheduler.job.scheduledJobName.SECOND=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.MINUTE=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.HOUR=7
 * services.SchedulerService.scheduler.job.scheduledJobName.WEEKDAY=-1
 * services.SchedulerService.scheduler.job.scheduledJobName.DAY_OF_MONTH=-1
 *
 * services.SchedulerService.scheduler.job.scheduledJobName2.ID=1
 * services.SchedulerService.scheduler.job.scheduledJobName2.SECOND=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.MINUTE=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.HOUR=7
 * services.SchedulerService.scheduler.job.scheduledJobName2.WEEKDAY=-1
 * services.SchedulerService.scheduler.job.scheduledJobName2.DAY_OF_MONTH=-1
 *
 * </PRE>
 *
 * Based on TamboraSchedulerService written by John Thorhauer.
 *
 * @author <a href="mailto:ekkerbj@netscpae.net">Jeff Brekke</a>
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TurbineNonPersistentSchedulerService.java 534527 2007-05-02 16:10:59Z tv $
 */
public class TurbineNonPersistentSchedulerService
        extends TurbineSchedulerService
{
    /** Logging */
    private static Log log = LogFactory.getLog(ScheduleService.LOGGER_NAME);

    /**
     * Constructor.
     *
     * @exception TurbineException a generic exception.
     */
    public TurbineNonPersistentSchedulerService()
            throws TurbineException
    {
        super();
    }

    /**
     * Called the first time the Service is used.<br>
     *
     * Load all the jobs from cold storage.  Add jobs to the queue
     * (sorted in ascending order by runtime) and start the scheduler
     * thread.
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = getConfiguration();

        try
        {
            scheduleQueue = new JobQueue();
            mainLoop = new MainLoop();

            List jobProps = conf.getList("scheduler.jobs");
            List jobs = new Vector();
            // If there are scheduler.jobs defined then set up a job vector
            // for the scheduleQueue
            if (!jobProps.isEmpty())
            {
                for (int i = 0; i < jobProps.size(); i++)
                {
                    String jobName = (String) jobProps.get(i);
                    String jobPrefix = "scheduler.job." + jobName;

                    String jobId = conf.getString(jobPrefix + ".ID", null);
                    if (StringUtils.isEmpty(jobId))
                    {
                        throw new Exception(
                                "There is an error in the TurbineResources.properties file. \n"
                                + jobPrefix + ".ID is not found.\n");
                    }

                    int sec = conf.getInt(jobPrefix + ".SECOND", -1);
                    int min = conf.getInt(jobPrefix + ".MINUTE", -1);
                    int hr = conf.getInt(jobPrefix + ".HOUR", -1);
                    int wkday = conf.getInt(jobPrefix + ".WEEKDAY", -1);
                    int dayOfMonth = conf.getInt(jobPrefix + ".DAY_OF_MONTH", -1);

                    JobEntry je = new JobEntry(
                            sec,
                            min,
                            hr,
                            wkday,
                            dayOfMonth,
                            jobName);
                    je.setJobId(Integer.parseInt(jobId));
                    jobs.add(je);

                }
            }

            if (jobs != null && jobs.size() > 0)
            {
                scheduleQueue.batchLoad(jobs);
            }

            setEnabled(getConfiguration().getBoolean("enabled", true));
            restart();

            setInit(true);
        }
        catch (Exception e)
        {
            String errorMessage = "Could not initialize the scheduler service";
            log.error(errorMessage, e);
            throw new InitializationException(errorMessage, e);
        }
    }

    /**
     * Called the first time the Service is used.<br>
     *
     * Load all the jobs from cold storage.  Add jobs to the queue
     * (sorted in ascending order by runtime) and start the scheduler
     * thread.
     *
     * @param config A ServletConfig.
     * @deprecated use init() instead.
     */
    public void init(ServletConfig config)
            throws InitializationException
    {
        init();
    }

    /**
     * This method returns the job element from the internal queue.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException could not retrieve job
     */
    public JobEntry getJob(int oid)
            throws TurbineException
    {
        JobEntry je = new JobEntry();
        je.setJobId(oid);
        return scheduleQueue.getJob(je);
    }

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @throws TurbineException job could not be added
     */
    public void addJob(JobEntry je)
            throws TurbineException
    {
        updateJob(je);
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     */
    public void removeJob(JobEntry je)
    {
        // Remove from the queue.
        scheduleQueue.remove(je);
        restart();
    }

    /**
     * Add/update a job
     *
     * @param je A JobEntry with the job to modify
     * @throws TurbineException job could not be updated
     */
    public void updateJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            je.calcRunTime();

            // Update the queue.
            scheduleQueue.modify(je);
            restart();
        }
        catch (Exception e)
        {
            String errorMessage = "Problem updating Scheduled Job: " + je.getTask();
            log.error(errorMessage, e);
            throw new TurbineException(errorMessage, e);
        }
    }
}
