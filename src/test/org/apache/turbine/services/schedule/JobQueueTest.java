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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.turbine.util.TurbineException;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing for JobQueue.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class JobQueueTest
{
    private JobQueue<JobEntryNonPersistent> queue;
    private JobEntryNonPersistent je1;
    private JobEntryNonPersistent je2;

    @Before
    public void setUpBefore() throws Exception
    {
        queue = new JobQueue<>();

        // Add a new job entry
        je1 = new JobEntryNonPersistent(1,2,3,4,5,"je1");
        je1.setJobId(1);

        je2 = new JobEntryNonPersistent(0,2,3,4,5,"je2");
        je2.setJobId(2);
    }

    /**
     * Test job queue functions
     * @throws TurbineException if the queue update fails
     */
    @Test
    public void testJobQueue() throws TurbineException
    {
        assertNull(queue.getNext());

        queue.add(je2);
        queue.add(je1);
        assertEquals(2, queue.list().size());

        JobEntryNonPersistent je_a = queue.getNext();
        assertNotNull(je_a);
        assertEquals(je2, je_a);
        assertEquals(1, queue.list().size());

        JobEntryNonPersistent je_b = queue.getJob(je2);
        assertNull(je_b);
        JobEntryNonPersistent je_c = queue.getJob(je1);
        assertNotNull(je_c);
        assertEquals(je1, je_c);

        je_c.setSecond(2);
        queue.updateQueue(je_c);
        assertEquals(1, queue.list().size());

        je2.setSecond(3);
        queue.updateQueue(je2);
        JobEntryNonPersistent je_d = queue.getNext();
        assertNotNull(je_d);
        assertEquals(je1, je_d);
        JobEntryNonPersistent je_e = queue.getNext();
        assertNotNull(je_e);
        assertEquals(je2, je_e);

        // queue should now be empty
        assertNull(queue.list());
    }

}
