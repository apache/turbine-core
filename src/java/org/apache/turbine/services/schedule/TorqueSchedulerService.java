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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.TorqueException;
import org.apache.torque.criteria.Criteria;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.TurbineException;

/**
 * Service for a cron like scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TorqueSchedulerService.java 534527 2007-05-02 16:10:59Z tv $
 */
public class TorqueSchedulerService extends TurbineBaseService implements ScheduleService
{
    /** Logging */
    protected static Log log = LogFactory.getLog(ScheduleService.LOGGER_NAME);

    /** The queue */
    protected JobQueue<JobEntry> scheduleQueue = null;

    /** Current status of the scheduler */
    protected boolean enabled = false;

    /** The main loop for starting jobs. */
    protected MainLoop mainLoop;

    /** The thread used to process commands. */
    protected Thread thread;

    /**
     * Creates a new instance.
     */
    public TorqueSchedulerService()
    {
        mainLoop = null;
        thread = null;
    }

    /**
     * Initializes the SchedulerService.
     *
     * @throws InitializationException
     *             Something went wrong in the init stage
     */
    @Override
    public void init() throws InitializationException
    {
        try
        {
            setEnabled(getConfiguration().getBoolean("enabled", true));
            scheduleQueue = new JobQueue<JobEntry>();
            mainLoop = new MainLoop();

            // Load all from cold storage.
            List<JobEntryTorque> jobsTorque = JobEntryTorquePeer.doSelect(new Criteria());

            if (jobsTorque != null)
            {
                List<JobEntry> jobs = new ArrayList<JobEntry>(jobsTorque.size());

                for (JobEntryTorque job : jobsTorque)
                {
                    job.calcRunTime();
                    jobs.add(job);
                }

                scheduleQueue.batchLoad(jobs);

                restart();
            }

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException("Could not initialize the scheduler service", e);
        }
    }

    /**
     * Shutdowns the service.
     *
     * This methods interrupts the housekeeping thread.
     */
    @Override
    public void shutdown()
    {
        if (getThread() != null)
        {
            getThread().interrupt();
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
     * @exception TurbineException
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
     * Add a new job to the queue.
     *
     * @param je
     *            A JobEntry with the job to add.
     * @throws TurbineException
     *             job could not be added
     */
    @Override
    public void addJob(JobEntry je) throws TurbineException
    {
        updateJob(je);
    }

    /**
     * Remove a job from the queue.
     *
     * @param je
     *            A JobEntry with the job to remove.
     * @exception TurbineException
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

    /**
     * List jobs in the queue. This is used by the scheduler UI.
     *
     * @return A List of jobs.
     */
    @Override
    public List<JobEntry> listJobs()
    {
        return scheduleQueue.list();
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
        Thread thread = getThread();
        if (thread != null)
        {
            thread.interrupt();
        }
        enabled = false;
    }

    /**
     * Return the thread being used to process commands, or null if there is no
     * such thread. You can use this to invoke any special methods on the
     * thread, for example, to interrupt it.
     *
     * @return A Thread.
     */
    public synchronized Thread getThread()
    {
        return thread;
    }

    /**
     * Set thread to null to indicate termination.
     */
    protected synchronized void clearThread()
    {
        thread = null;
    }

    /**
     * Start (or restart) a thread to process commands, or wake up an existing
     * thread if one is already running. This method can be invoked if the
     * background thread crashed due to an unrecoverable exception in an
     * executed command.
     */
    public synchronized void restart()
    {
        if (enabled)
        {
            log.info("Starting job scheduler");
            if (thread == null)
            {
                // Create the the housekeeping thread of the scheduler. It will
                // wait for the time when the next task needs to be started,
                // and then launch a worker thread to execute the task.
                thread = new Thread(mainLoop, ScheduleService.SERVICE_NAME);
                // Indicate that this is a system thread. JVM will quit only
                // when there are no more enabled user threads. Settings threads
                // spawned internally by Turbine as daemons allows commandline
                // applications using Turbine to terminate in an orderly manner.
                thread.setDaemon(true);
                thread.start();
            }
            else
            {
                notify();
            }
        }
    }

    /**
     * Return the next Job to execute, or null if thread is interrupted.
     *
     * @return A JobEntry.
     * @exception TurbineException
     *                a generic exception.
     */
    protected synchronized JobEntry nextJob() throws TurbineException
    {
        try
        {
            while (!Thread.interrupted())
            {
                // Grab the next job off the queue.
                JobEntry je = scheduleQueue.getNext();

                if (je == null)
                {
                    // Queue must be empty. Wait on it.
                    wait();
                }
                else
                {
                    long now = System.currentTimeMillis();
                    long when = je.getNextRuntime();

                    if (when > now)
                    {
                        // Wait till next runtime.
                        wait(when - now);
                    }
                    else
                    {
                        // Update the next runtime for the job.
                        scheduleQueue.updateQueue(je);
                        // Return the job to run it.
                        return je;
                    }
                }
            }
        }
        catch (InterruptedException ex)
        {
            // ignore
        }

        // On interrupt.
        return null;
    }

    /**
     * Inner class. This is isolated in its own Runnable class just so that the
     * main class need not implement Runnable, which would allow others to
     * directly invoke run, which is not supported.
     */
    protected class MainLoop implements Runnable
    {
        /**
         * Method to run the class.
         */
        @Override
        public void run()
        {
            String taskName = null;
            try
            {
                while (enabled)
                {
                    JobEntry je = nextJob();
                    if (je != null)
                    {
                        taskName = je.getTask();

                        // Start the thread to run the job.
                        Runnable wt = new WorkerThread(je);
                        Thread helper = new Thread(wt);
                        helper.start();
                    }
                    else
                    {
                        break;
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Error running a Scheduled Job: " + taskName, e);
                enabled = false;
            }
            finally
            {
                clearThread();
            }
        }
    }
}
