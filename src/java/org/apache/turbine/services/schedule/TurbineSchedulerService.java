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

import java.util.List;
import java.util.Vector;
import javax.servlet.ServletConfig;
import org.apache.torque.util.Criteria;
import org.apache.turbine.Turbine;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.resources.TurbineResources;

/**
 * Service for a cron like scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class TurbineSchedulerService
    extends TurbineBaseService
    implements ScheduleService
{
    /**
     * The queue.
     */
    protected JobQueue scheduleQueue = null;

    /**
     * The main loop for starting jobs.
     */
    protected MainLoop mainLoop;

    /**
     * The thread used to process commands.
     */
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
     * This is a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */ 
    public void init()
        throws InitializationException
    {
        ServletConfig conf = Turbine.getTurbineServletConfig();
        init(conf);
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
        try
        {
            // START SCHEDULER HERE
            if ( TurbineResources.getBoolean("scheduler.enabled", false) )
            {
                scheduleQueue = new JobQueue();
                mainLoop = new MainLoop();

                // Load all from cold storage.
                List jobs  = JobEntryPeer.doSelect(new Criteria());

                if ( jobs != null && jobs.size() > 0 )
                {
                    scheduleQueue.batchLoad(jobs);
                    restart();
                }

                setInit(true);
            }
            else
            {
                org.apache.turbine.util.Log.info ("TurbineSchedulerService was not started " +
                "because scheduler.enabled is not 'true' in the TurbineResources.properties file.");
            }
        }
        catch (Exception e)
        {
            throw new InitializationException("TurbineSchedulerService failed to initialize", e);
        }
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
     * @exception Exception, a generic exception.
     */
    public JobEntry getJob(int oid)
        throws Exception
    {
        JobEntry je = JobEntryPeer.getJob(oid);
        return scheduleQueue.getJob(je);
    }

    /**
     * Add a new job to the queue.
     *
     * @param je A JobEntry with the job to add.
     * @exception Exception, a generic exception.
     */
    public void addJob(JobEntry je)
        throws Exception
    {
        try
        {
            // Save to DB.
            je.save();
        }
        catch(Exception e)
        {
            // Log problems.
            org.apache.turbine.util.Log.error ( "Problem saving new Scheduled Job: " + e);
        }
        // Add to the queue.
        scheduleQueue.add(je);
        restart();
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     * @exception Exception, a generic exception.
     */
    public void removeJob(JobEntry je)
        throws Exception
    {
        // First remove from DB.
        try
        {
            Criteria c = new Criteria()
                .add(JobEntryPeer.OID, je.getPrimaryKey());

            JobEntryPeer.doDelete(c);
        }
        catch(Exception ouch)
        {
            // Log problem.
            org.apache.turbine.util.Log.error ( "Problem removing Scheduled Job: " + ouch);
        }

        // Remove from the queue.
        scheduleQueue.remove(je);
        restart();
    }

    /**
     * Modify a Job.
     *
     * @param je A JobEntry with the job to modify
     * @exception Exception, a generic exception.
     */
    public void updateJob(JobEntry je)
        throws Exception
    {
        try
        {
            je.calcRunTime();
            je.save();
        }
        catch(Exception e)
        {
            // Log problems.
            org.apache.turbine.util.Log.error ( "Problem updating Scheduled Job: " + e);
        }
        // Update the queue.
       scheduleQueue.modify(je);
       restart();
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A Vector of jobs.
     */
    public Vector listJobs()
    {
        return scheduleQueue.list();
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
        if (thread == null)
        {
            // Create the the housekeeping thread of the scheduler. It will wait
            // for the time when the next task needs to be started, and then
            // launch a worker thread to execute the task.
            thread = new Thread(mainLoop, ScheduleService.SERVICE_NAME);
            // Indicate that this is a system thread. JVM will quit only when there
            // are no more active user threads. Settings threads spawned internally
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
     * @exception Exception, a generic exception.
     */
    private synchronized JobEntry nextJob()
        throws Exception
    {
        try
        {
            while ( !Thread.interrupted() )
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

                    if ( when > now )
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
            try
            {
                for(;;)
                {
                    JobEntry je = nextJob();
                    if ( je != null )
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
                org.apache.turbine.util.Log.error ( "Error running a Scheduled Job: " + e);
            }
            finally
            {
                clearThread();
            }
        }
    }
}
