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
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.turbine.util.TurbineException;

/**
 * Queue for the scheduler.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: JobQueue.java 615328 2008-01-25 20:25:05Z tv $
 * @param <J> a specialized job entry type
 */
public class JobQueue<J extends JobEntry>
{
    /**
     * The queue of <code>JobEntry</code> objects.
     */
    private ConcurrentSkipListSet<J> queue = null;

    /**
     * Creates a new instance.
     */
    public JobQueue()
    {
        queue = new ConcurrentSkipListSet<J>((o1, o2) -> Long.compare(o1.getNextRuntime(), o2.getNextRuntime()));
    }

    /**
     * Return the next job off the top of the queue and remove it from the queue, or <code>null</code> if
     * there are no jobs in the queue.
     *
     * @return The next job in the queue.
     */
    public J getNext()
    {
        return queue.pollFirst();
    }

    /**
     * Return a specific job.
     *
     * @param je The JobEntry we are looking for.
     * @return A JobEntry.
     */
    public J getJob(J je)
    {
        if (je != null)
        {
            J job = queue.floor(je);
            if (je.equals(job))
            {
                return job;
            }
        }

        return null;
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A Vector of <code>JobEntry</code> objects.
     */
    public Vector<J> list()
    {
        if (!queue.isEmpty())
        {
            return new Vector<>(queue);
        }
        else
        {
            return null;
        }
    }

    /**
     * Add a job to the queue.
     *
     * @param je A JobEntry job.
     */
    public void add(J je)
    {
        queue.add(je);
    }

    /**
     * Batch load jobs.  Retains any already enqueued jobs.  Called on
     * <code>SchedulerService</code> start-up.
     *
     * @param jobEntries A list of the <code>JobEntry</code> objects to load.
     */
    public void batchLoad(List<J> jobEntries)
    {
        if (jobEntries != null)
        {
            queue.addAll(jobEntries);
        }
    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     */
    public void remove(J je)
    {
        queue.remove(je);
    }

    /**
     * Modify a job on the queue.
     *
     * @param je A JobEntry with the job to modify
     * @throws TurbineException if the runtime calculation fails
     */
    public void modify(J je) throws TurbineException
    {
        remove(je);
        je.calcRunTime();
        add(je);
    }

    /**
     * Update the job for its next run time.
     *
     * @param je A JobEntry to be updated.
     * @throws TurbineException a generic exception.
     */
    public void updateQueue(J je)
            throws TurbineException
    {
        modify(je);
    }
}
