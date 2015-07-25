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
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.apache.turbine.util.TurbineException;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

/**
 * Unit testing for the quartz implementation of the scheduler service.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class QuartzSchedulerServiceTest extends BaseTestCase
{
    private TurbineConfig tc = null;

    @Before
    public void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
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
            TurbineScheduler.startScheduler();
            assertTrue(TurbineScheduler.isEnabled());

            TurbineScheduler.stopScheduler();
            assertFalse(TurbineScheduler.isEnabled());
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
            int jobCount = TurbineScheduler.listJobs().size();

            // Add a new job entry
            JobDetail jd = JobBuilder.newJob(JobEntryQuartz.class)
                    .withIdentity("SimpleJob1", JobEntryQuartz.DEFAULT_JOB_GROUP_NAME)
                    .build();
            Trigger t = TriggerBuilder.newTrigger()
                    .withIdentity("SimpleJob1", JobEntryQuartz.DEFAULT_JOB_GROUP_NAME)
                    .withSchedule(CronScheduleBuilder.cronSchedule("10 * * * * ?"))
                    .forJob(jd)
                    .build();
			JobEntryQuartz je = new JobEntryQuartz(t, jd);
            je.setJobId(jobCount + 1);

            TurbineScheduler.addJob(je);
            assertEquals(jobCount + 1, TurbineScheduler.listJobs().size());

            TurbineScheduler.removeJob(je);
            assertEquals(jobCount, TurbineScheduler.listJobs().size());

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
            JobKey jk = new JobKey("SimpleJob", JobEntryQuartz.DEFAULT_JOB_GROUP_NAME);
			JobEntry je = TurbineScheduler.getJob(jk.hashCode());
			assertThat(je, CoreMatchers.instanceOf(JobEntryQuartz.class));
			JobEntryQuartz jeq = (JobEntryQuartz)je;
            assertEquals(jeq.getJobTrigger().getJobKey(), jk);
            assertEquals(jeq.getTask(), "SimpleJob");
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
