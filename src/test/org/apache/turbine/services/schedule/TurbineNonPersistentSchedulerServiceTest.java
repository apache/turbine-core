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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.turbine.modules.scheduledjobs.SimpleJob;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit testing for the non-persistent implementation of the scheduler service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TurbineNonPersistentSchedulerServiceTest.java 615328 2008-01-25 20:25:05Z tv $
 */
public class TurbineNonPersistentSchedulerServiceTest
{
    private TurbineConfig tc = null;

    private ScheduleService scheduler = null;

    @Before
    public void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TurbineNonPersistentSchedulerServiceTest.properties");
        tc.initialize();

        scheduler = (ScheduleService)TurbineServices.getInstance().getService(ScheduleService.SERVICE_NAME);
    }

    @After
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }

    /**
     * Tests the ability to enable and disable the service.
     */
    @Test public void testEnableDisable()
    {
        try
        {
            scheduler.startScheduler();
            assertTrue(scheduler.isEnabled());

            scheduler.stopScheduler();
            assertFalse(scheduler.isEnabled());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests the ability to add and remove a job.  A list of jobs will be obtained from
     * the service to determine if the operation were successful.
     */
    @Test public void testAddRemoveJob()
    {
        try
        {
            // get the current job count for later comparison
            int jobCount = scheduler.listJobs().size();

            // Add a new job entry
            JobEntry je = scheduler.newJob(0, 1, -1, -1, -1, "SimpleJob");

            scheduler.addJob(je);
            assertEquals(jobCount + 1, scheduler.listJobs().size());

            assertTrue(scheduler.listJobs().contains( je ));
            scheduler.removeJob(je);
            assertTrue(!scheduler.listJobs().contains( je ));
            assertEquals(jobCount, scheduler.listJobs().size());

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Tests the ability to retrieve the job added during initialization.
     */
    @Test public void testGetJob()
    {
        try
        {
			JobEntry je = scheduler.getJob(1);
			assertThat(je, CoreMatchers.instanceOf(JobEntryNonPersistent.class));
			JobEntryNonPersistent jenp = (JobEntryNonPersistent)je;
            assertEquals(1, jenp.getJobId());
            assertEquals(1, jenp.getSecond());
            assertEquals(-1, jenp.getMinute());
            assertEquals(-1, jenp.getHour());
            assertEquals(-1, jenp.getDayOfMonth());
            assertEquals(-1, jenp.getWeekDay());
            assertEquals("SimpleJob", jenp.getTask());
        }
        catch (TurbineException e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Test to make sure a job actually runs.
     */
    @Test public void testRunningJob()
    {
        try
        {
           int beforeCount = SimpleJob.getCounter();
           Thread.sleep(1200);
           int afterCount = SimpleJob.getCounter();
           assertTrue(beforeCount < afterCount);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

}
