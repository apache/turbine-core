package org.apache.turbine.modules;


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


// Turbine Scheduler Classes

import org.apache.turbine.services.schedule.JobEntry;

/**
 * All Scheduled jobs should extend this.  The class that extends
 * ScheduledJobs should contain the code that you actually want to
 * execute at a specific time.  The name of this class is what you
 * register in the JobEntry.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public abstract class ScheduledJob extends Assembler
{
    /** Prefix for scheduler job related classes */
    public static final String PREFIX = "scheduledjobs";

    /** The key for the scheduler job cache size if module caching is on. */
    public static final String CACHE_SIZE_KEY = "scheduledjob.cache.size";

    /** The default size of the scheduler job cache if module caching is on. */
    public static final int CACHE_SIZE_DEFAULT = 10;

    /** Represents Scheduled Job Objects */
    public static final String NAME = "scheduledjob";

    /**
     * @see org.apache.turbine.modules.Assembler#getPrefix()
     */
    @Override
    public String getPrefix()
    {
        return PREFIX;
    }

    /**
     * Run the Jobentry from the scheduler queue.
     *
     * @param job The job to run.
     * @throws Exception if something goes wrong
     */
    public abstract void run(JobEntry job)
            throws Exception;
}
