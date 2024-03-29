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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public abstract class AbstractSchedulerService extends TurbineBaseService implements ScheduleService
{
    /** Logging */
    protected static final Logger log = LogManager.getLogger(ScheduleService.LOGGER_NAME);

    /** The queue */
    protected JobQueue<JobEntry> scheduleQueue = null;

    /** Current status of the scheduler */
    private AtomicBoolean enabled = new AtomicBoolean(false);

    /** The housekeeping thread. */
    protected Thread houseKeepingThread;

    /** The thread pool used to process jobs. */
    protected ExecutorService threadPool;

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
            scheduleQueue = new JobQueue<>();
            threadPool = Executors.newCachedThreadPool(
                    new BasicThreadFactory.Builder()
                        .namingPattern("Turbine-ScheduledJob-")
                        .daemon(true)
                        .build());

            @SuppressWarnings("unchecked") // Why is this cast necessary?
            List<JobEntry> jobs = (List<JobEntry>)loadJobs();
            scheduleQueue.batchLoad(jobs);
            restart();

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException("Could not initialize the scheduler service", e);
        }
    }

    /**
     * Load all jobs from configuration storage
     *
     * @return the list of pre-configured jobs
     * @throws TurbineException if jobs could not be loaded
     */
    protected abstract List<? extends JobEntry> loadJobs() throws TurbineException;

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

        threadPool.shutdownNow();
    }

    /**
     * @see org.apache.turbine.services.schedule.ScheduleService#newJob(int, int, int, int, int, java.lang.String)
     */
    @Override
    public abstract JobEntry newJob(int sec, int min, int hour, int wd, int day_mo, String task) throws TurbineException;

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
    public abstract JobEntry getJob(int oid) throws TurbineException;

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
     * @throws TurbineException
     *                job could not be removed
     */
    @Override
    public abstract void removeJob(JobEntry je) throws TurbineException;

    /**
     * Add or update a job.
     *
     * @param je
     *            A JobEntry with the job to modify
     * @throws TurbineException
     *             job could not be updated
     */
    @Override
    public abstract void updateJob(JobEntry je) throws TurbineException;

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
     * @param enabled true to enable the scheduler
     *
     */
    protected void setEnabled(boolean enabled)
    {
        this.enabled.set(enabled);
    }

    /**
     * Determines if the scheduler service is currently enabled.
     *
     * @return Status of the scheduler service.
     */
    @Override
    public boolean isEnabled()
    {
        return enabled.get();
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
        setEnabled(false);
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
        return houseKeepingThread;
    }

    /**
     * Set thread to null to indicate termination.
     */
    protected synchronized void clearThread()
    {
        houseKeepingThread = null;
    }

    /**
     * Start (or restart) a thread to process commands, or wake up an existing
     * thread if one is already running. This method can be invoked if the
     * background thread crashed due to an unrecoverable exception in an
     * executed command.
     */
    public synchronized void restart()
    {
        if (enabled.get())
        {
            log.info("Starting job scheduler");
            if (houseKeepingThread == null)
            {
                // Create the the housekeeping thread of the scheduler. It will
                // wait for the time when the next task needs to be started,
                // and then launch a worker thread to execute the task.
                houseKeepingThread = new Thread(this::houseKeeping, ScheduleService.SERVICE_NAME);
                // Indicate that this is a system thread. JVM will quit only
                // when there are no more enabled user threads. Settings threads
                // spawned internally by Turbine as daemons allows commandline
                // applications using Turbine to terminate in an orderly manner.
                houseKeepingThread.setDaemon(true);
                houseKeepingThread.start();
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
     * @throws TurbineException
     *                a generic exception.
     */
    protected synchronized JobEntry nextJob() throws TurbineException
    {
        try
        {
            while (!Thread.interrupted())
            {
                // Grab the next job off the queue.
                //JobEntry je = scheduleQueue.getNext();
                JobEntry je = scheduleQueue.getFirst();

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
     * Create the the housekeeping thread of the scheduler. It will
     * wait for the time when the next task needs to be started,
     * and then launch a worker thread to execute the task.
     */
    private void houseKeeping()
    {
        String taskName = null;
        try
        {
            while (enabled.get())
            {
                JobEntry je = nextJob();
                if (je != null)
                {
                    taskName = je.getTask();

                    // Get a thread to run the job.
                    threadPool.execute(new WorkerThread(je));
                }
                else
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            log.error("Error running a Scheduled Job: {}", taskName, e);
            setEnabled(false);
        }
        finally
        {
            clearThread();
        }
    }
}
