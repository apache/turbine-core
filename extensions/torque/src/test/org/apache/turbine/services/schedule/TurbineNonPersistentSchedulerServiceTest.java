package org.apache.turbine.services.schedule;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * @version $Id$
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
			JobEntryTorque je = new JobEntryTorque();
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
			JobEntryTorque je = (JobEntryTorque)TurbineScheduler.getJob(1);
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
