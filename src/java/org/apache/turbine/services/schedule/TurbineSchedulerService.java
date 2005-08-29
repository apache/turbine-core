package org.apache.turbine.services.schedule;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.util.TurbineException;

/**
 * Service for a cron like scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineSchedulerService
        extends TurbineBaseService
        implements ScheduleService
{
    /** Logging */
    private static Log log = LogFactory.getLog(ScheduleService.LOGGER_NAME);

    /** The queue */
    protected JobQueue scheduleQueue = null;

    /** Current status of the scheduler */
    private boolean enabled = false;

    /** The main loop for starting jobs. */
    protected MainLoop mainLoop;

    /** The thread used to process commands.  */
    protected Thread thread;

    /**
     * Creates a new instance.
     */
    public TurbineSchedulerService()
    {
        mainLoop = null;
        thread = null;
    }

    /**
     * Initializes the SchedulerService.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */
    public void init()
            throws InitializationException
    {
        try
        {
            setEnabled(getConfiguration().getBoolean("enabled", true));
            scheduleQueue = new JobQueue();
            mainLoop = new MainLoop();

            // Load all from cold storage.
            List jobs = JobEntryPeer.doSelect(new Criteria());

            if (jobs != null && jobs.size() > 0)
            {
                Iterator it = jobs.iterator();
                while (it.hasNext())
                {
                    ((JobEntry) it.next()).calcRunTime();
                }
                scheduleQueue.batchLoad(jobs);

                restart();
            }

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
    public void init(ServletConfig config) throws InitializationException
    {
        init();
    }

    /**
     * Shutdowns the service.
     *
     * This methods interrupts the housekeeping thread.
     */
    public void shutdown()
    {
        if (getThread() != null)
        {
            getThread().interrupt();
        }
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException job could not be retreived.
     */
    public JobEntry getJob(int oid)
            throws TurbineException
    {
        try
        {
            JobEntry je = JobEntryPeer.retrieveByPK(oid);
            return scheduleQueue.getJob(je);
        }
        catch (TorqueException e)
        {
            String errorMessage = "Error retrieving job from persistent storage.";
            log.error(errorMessage, e);
            throw new TurbineException(errorMessage, e);
        }
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
     * @exception TurbineException job could not be removed
     */
    public void removeJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            // First remove from DB.
            Criteria c = new Criteria().add(JobEntryPeer.JOB_ID, je.getPrimaryKey());
            JobEntryPeer.doDelete(c);

            // Remove from the queue.
            scheduleQueue.remove(je);

            // restart the scheduler
            restart();
        }
        catch (Exception e)
        {
            String errorMessage = "Problem removing Scheduled Job: " + je.getTask();
            log.error(errorMessage, e);
            throw new TurbineException(errorMessage, e);
        }
    }

    /**
     * Add or update a job.
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
            if (je.isNew())
            {
                scheduleQueue.add(je);
            }
            else
            {
                scheduleQueue.modify(je);
            }

            je.save();

            restart();
        }
        catch (Exception e)
        {
            String errorMessage = "Problem updating Scheduled Job: " + je.getTask();
            log.error(errorMessage, e);
            throw new TurbineException(errorMessage, e);
        }
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A List of jobs.
     */
    public List listJobs()
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
    public boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Starts or restarts the scheduler if not already running.
     */
    public synchronized void startScheduler()
    {
        setEnabled(true);
        restart();
    }

    /**
     * Stops the scheduler if it is currently running.
     */
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
     * Return the thread being used to process commands, or null if
     * there is no such thread.  You can use this to invoke any
     * special methods on the thread, for example, to interrupt it.
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
    private synchronized void clearThread()
    {
        thread = null;
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
            if (thread == null)
            {
                // Create the the housekeeping thread of the scheduler. It will wait
                // for the time when the next task needs to be started, and then
                // launch a worker thread to execute the task.
                thread = new Thread(mainLoop, ScheduleService.SERVICE_NAME);
                // Indicate that this is a system thread. JVM will quit only when there
                // are no more enabled user threads. Settings threads spawned internally
                // by Turbine as daemons allows commandline applications using Turbine
                // to terminate in an orderly manner.
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
     *  Return the next Job to execute, or null if thread is
     *  interrupted.
     *
     * @return A JobEntry.
     * @exception TurbineException a generic exception.
     */
    private synchronized JobEntry nextJob()
            throws TurbineException
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
        }

        // On interrupt.
        return null;
    }

    /**
     * Inner class.  This is isolated in its own Runnable class just
     * so that the main class need not implement Runnable, which would
     * allow others to directly invoke run, which is not supported.
     */
    protected class MainLoop
            implements Runnable
    {
        /**
         * Method to run the class.
         */
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
