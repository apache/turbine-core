package org.apache.turbine.services.schedule;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * @version $Id$
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

            List jobProps = conf.getVector("scheduler.jobs");
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
