package org.apache.turbine.modules.scheduledjobs;


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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.ScheduledJob;
import org.apache.turbine.services.schedule.JobEntry;

/**
 * Simple job for use with unit testing of the scheduler service.  This
 * job merely increments a static counter variable when it is run.  You
 * can check the counter to verify the job has run.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class SimpleJob implements ScheduledJob
{
    /** Logging */
    private static Log log = LogFactory.getLog(SimpleJob.class);

    /** The test counter */
    private static int counter = 0;

    /**
     * Run the Jobentry from the scheduler queue.
     *
     * @param job The job to run.
     * @throws java.lang.Exception generic exception
     */
    @Override
    public void run(JobEntry job)
            throws Exception
    {
        counter++;
        log.info("I AM RUNNING!");
    }

    /**
     * Returns the counter value.
     *
     * @return The counter value
     */
    public static int getCounter()
    {
        return counter;
    }

    /**
     * Sets the counter.
     *
     * @param i The new counter value
     */
    public static void setCounter(int i)
    {
        counter = i;
    }
}
