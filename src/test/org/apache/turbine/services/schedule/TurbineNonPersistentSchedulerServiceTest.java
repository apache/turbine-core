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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.apache.turbine.modules.scheduledjob.SimpleJob;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
//import org.apache.turbine.test.BaseTestCase;

/**
 * Unit testing for the non-persistent implementation of the scheduler service.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: TurbineNonPersistentSchedulerServiceTest.java 615328 2008-01-25 20:25:05Z tv $
 */
public class TurbineNonPersistentSchedulerServiceTest extends TestCase
{
    private static final String PREFIX = "services." + ScheduleService.SERVICE_NAME + '.';

    public TurbineNonPersistentSchedulerServiceTest(String name)
            throws Exception
    {
        super(name);

        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname", TurbineNonPersistentSchedulerService.class.getName());

        cfg.setProperty(PREFIX + "scheduler.jobs", "SimpleJob");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.ID", "1");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.SECOND", "10");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.MINUTE", "-1");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.HOUR", "-1");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.WEEK_DAY", "-1");
        cfg.setProperty(PREFIX + "scheduler.job.SimpleJob.DAY_OF_MONTH", "-1");
        cfg.setProperty(PREFIX + "enabled", "true");

        serviceManager.setConfiguration(cfg);

        try
        {
            serviceManager.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public static Test suite()
    {
        return new TestSuite(TurbineNonPersistentSchedulerServiceTest.class);
    }

    /**
     * Tests the ability to enable and disable the service.
     */
    public void testEnableDisable()
    {
        try
        {
            TurbineScheduler.startScheduler();
            assertEquals(true, TurbineScheduler.isEnabled());

            TurbineScheduler.stopScheduler();
            assertEquals(false, TurbineScheduler.isEnabled());
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
    public void testAddRemoveJob()
    {
        try
        {
            // get the current job count for later comparison
            int jobCount = TurbineScheduler.listJobs().size();

            // Add a new job entry
			JobEntry je = new JobEntry();
            je.setJobId(jobCount + 1);
            je.setSecond(0);
            je.setMinute(1);
            je.setHour(-1);
            je.setDayOfMonth(-1);
            je.setWeekDay(-1);
            je.setTask("SimpleJob");

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
    public void testGetJob()
    {
        try
        {
			JobEntry je = (JobEntry)TurbineScheduler.getJob(1);
            assertEquals(je.getJobId(), 1);
            assertEquals(je.getSecond(), 10);
            assertEquals(je.getMinute(), -1);
            assertEquals(je.getHour(), -1);
            assertEquals(je.getDayOfMonth(), -1);
            assertEquals(je.getWeekDay(), -1);
            assertEquals(je.getTask(), "SimpleJob");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    /** Test to make sure a job actually runs.  Currently not work.
     * @TODO Must get testRunningJob to work.
     *
     */
    public void OFFtestRunningJob()
    {
        try
        {
           int beforeCount = SimpleJob.getCounter();
           Thread.sleep(120000);
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
