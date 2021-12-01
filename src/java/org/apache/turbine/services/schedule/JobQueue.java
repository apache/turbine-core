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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

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
    private Vector<J> queue = null;

    /**
     * Creates a new instance.
     */
    public JobQueue()
    {
        queue = new Vector<>(10);
    }

    /**
     * Return the next job off the top of the queue, or <code>null</code> if
     * there are no jobs in the queue.
     *
     * @return The next job in the queue.
     */
    public J getNext()
    {
        if (queue.size() > 0)
        {
            return queue.elementAt(0);
        }
        else
        {
            return null;
        }
    }

    /**
     * Return a specific job.
     *
     * @param je The JobEntry we are looking for.
     * @return A JobEntry.
     */
    public J getJob(J je)
    {
        int index = -1;

        if (je != null)
        {
            index = queue.indexOf(je);
        }

        if (index < 0)
        {
            return null;
        }
        else
        {
            return queue.elementAt(index);
        }
    }

    /**
     * List jobs in the queue.  This is used by the scheduler UI.
     *
     * @return A Vector of <code>JobEntry</code> objects.
     */
    @SuppressWarnings("unchecked")
    public Vector<J> list()
    {
        if (queue != null && queue.size() > 0)
        {
            return (Vector<J>) queue.clone();
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
    public synchronized void add(J je)
    {
        queue.addElement(je);
        sortQueue();
    }

    /**
     * Batch load jobs.  Retains any already enqueued jobs.  Called on
     * <code>SchedulerService</code> start-up.
     *
     * @param jobEntries A list of the <code>JobEntry</code> objects to load.
     */
    public synchronized void batchLoad(List<J> jobEntries)
    {
        if (jobEntries != null)
        {
            queue.addAll(jobEntries);
            sortQueue();
        }

    }

    /**
     * Remove a job from the queue.
     *
     * @param je A JobEntry with the job to remove.
     */
    public synchronized void remove(J je)
    {
        queue.removeElement(je);
        sortQueue();
    }

    /**
     * Modify a job on the queue.
     *
     * @param je A JobEntry with the job to modify
     * @throws TurbineException if the runtime calculation fails
     */
    public synchronized void modify(J je) throws TurbineException
    {
        remove(je);
        je.calcRunTime();
        this.add(je);
        sortQueue();
    }

    /**
     * Update the job for its next run time.
     *
     * @param je A JobEntry to be updated.
     * @throws TurbineException a generic exception.
     */
    public synchronized void updateQueue(J je)
            throws TurbineException
    {
        je.calcRunTime();
        sortQueue();
    }

    /**
     * Re-sort the existing queue.  Consumers of this method should be
     * <code>synchronized</code>.
     */
    private void sortQueue()
    {
        Comparator<J> aComparator = (o1, o2) -> {
            Long time1 = Long.valueOf(o1.getNextRuntime());
            Long time2 = Long.valueOf(o2.getNextRuntime());
            return time1.compareTo(time2);
        };

        queue.sort(aComparator);
    }
}
