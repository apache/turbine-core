package org.apache.turbine.services.schedule;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
            scheduleQueue = new JobQueue();
            mainLoop = new MainLoop();

            // Load all from cold storage.
            List jobs = JobEntryPeer.doSelect(new Criteria());

            if(jobs != null && jobs.size() > 0)
            {
                Iterator it = jobs.iterator();
                while(it.hasNext())
                {
                    ((JobEntry) it.next()).calcRunTime();
                }
                scheduleQueue.batchLoad(jobs);
                if(getConfiguration().getBoolean("enabled", true))
                {
                    restart();
                }
            }

            setInit(true);
        }
        catch(Exception e)
        {
            log.error("Could not initialize the scheduler service", e);
            throw new InitializationException(
                    "TurbineSchedulerService failed to initialize", e);
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
     * Shutdowns the service.
     *
     * This methods interrupts the housekeeping thread.
     */
    public void shutdown()
    {
        if(getThread() != null)
        {
            getThread().interrupt();
        }
    }

    /**
     * Get a specific Job from Storage.
     *
     * @param oid The int id for the job.
     * @return A JobEntry.
     * @exception TurbineException a generic exception.
     */
    public JobEntry getJob(int oid)
            throws TurbineException
    {
        try
        {
            JobEntry je = JobEntryPeer.retrieveByPK(oid);
            return scheduleQueue.getJob(je);
        }
        catch(TorqueException e)
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
     * @exception TurbineException a generic exception.
     */
    public void addJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            // Calculate the runtime to make sure the entry will be placed
            // at the right order
            je.calcRunTime();

            // Save to DB.
            je.save();

            // Add to the queue.
            scheduleQueue.add(je);
            restart();
        }
        catch(Exception e)
        {
            // Log problems.
            log.error("Problem saving new Scheduled Job: " + e);
        }
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception TurbineException a generic exception.
     */
    public void removeJob(JobEntry je)
            throws TurbineException
    {
        // First remove from DB.
        try
        {
            Criteria c = new Criteria().add(JobEntryPeer.JOB_ID, je.getPrimaryKey());
            JobEntryPeer.doDelete(c);
        }
        catch(Exception ouch)
        {
            // Log problem.
            log.error("Problem removing Scheduled Job: " + ouch);
        }

        // Remove from the queue.
        scheduleQueue.remove(je);
        restart();
    }

    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @exception TurbineException a generic exception.
     */
    public void updateJob(JobEntry je)
            throws TurbineException
    {
        try
        {
            je.calcRunTime();
            je.save();

            // Update the queue.
            scheduleQueue.modify(je);
            restart();
        }
        catch(Exception e)
        {
            // Log problems.
            log.error("Problem updating Scheduled Job: " + e);
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
     * Determines if the scheduler service is currently enabled.
     *
     * @return Status of the scheduler service.
     */
    public boolean isEnabled()
    {
        return (getThread() == null ? false : true);
    }

    /**
     * Starts or restarts the scheduler if not already running.
     */
    public void startScheduler()
    {
        restart();
    }

    /**
     * Stops the scheduler if it is currently running.
     */
    public void stopScheduler()
    {
        log.info("Stopping job scheduler");
        Thread thread = getThread();
        if(thread != null)
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
        log.info("Starting job scheduler");
        enabled = true;
        if(thread == null)
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
            while(!Thread.interrupted())
            {
                // Grab the next job off the queue.
                JobEntry je = scheduleQueue.getNext();

                if(je == null)
                {
                    // Queue must be empty. Wait on it.
                    wait();
                }
                else
                {
                    long now = System.currentTimeMillis();
                    long when = je.getNextRuntime();

                    if(when > now)
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
        catch(InterruptedException ex)
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
            try
            {
                while(enabled)
                {
                    JobEntry je = nextJob();
                    if(je != null)
                    {
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
            catch(Exception e)
            {
                // Log error.
                log.error("Error running a Scheduled Job: " + e);
                enabled = false;
            }
            finally
            {
                clearThread();
            }
        }
    }
}
