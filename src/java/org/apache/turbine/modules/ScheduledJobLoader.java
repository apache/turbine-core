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


import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.schedule.JobEntry;

/**
 * ScheduledJobs loader class.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public final class ScheduledJobLoader
    extends GenericLoader<ScheduledJob>
{
    /** The single instance of this class. */
    private static ScheduledJobLoader instance = new ScheduledJobLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScheduledJobLoader()
    {
        super(ScheduledJob.class,
                () -> Turbine.getConfiguration().getInt(ScheduledJob.CACHE_SIZE_KEY,
                        ScheduledJob.CACHE_SIZE_DEFAULT));
    }

    /**
     * Attempts to load and execute the external ScheduledJob.
     *
     * @param job The JobEntry.
     * @param name Name of object that will execute the job.
     * @throws Exception a generic exception.
     */
    public void exec(JobEntry job, String name)
            throws Exception
    {
        // Execute job
        getAssembler(name).run(job);
    }

    /**
     * Attempts to load and execute the external ScheduledJob.
     *
     * HELP! - THIS IS UGLY!
     *
     * I want the cache stuff from GenericLoader, BUT, I don't think
     * the scheduler needs the PipelineData object.  The scheduler runs
     * independently of an HTTP request.  This should not extend
     * GenericLoader!  Thoughts??
     *
     * @param pipelineData Turbine information.
     * @param name Name of object that will execute the job.
     * @throws Exception a generic exception.
     * @deprecated
     */
    @Deprecated
    @Override
    public void exec(PipelineData pipelineData, String name)
            throws Exception
    {
        throw new Exception("PipelineData objects not accepted for Scheduled jobs");
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static ScheduledJobLoader getInstance()
    {
        return instance;
    }
}
