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
import org.apache.turbine.services.schedule.JobEntry;
import org.apache.turbine.util.RunData;

/**
 * ScheduledJobs loader class.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class ScheduledJobLoader
    extends GenericLoader<ScheduledJob>
    implements Loader<ScheduledJob>
{
    /** The single instance of this class. */
    private static ScheduledJobLoader instance = new ScheduledJobLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScheduledJobLoader()
    {
        super();
    }

    /**
     * Attempts to load and execute the external ScheduledJob.
     *
     * @param job The JobEntry.
     * @param name Name of object that will execute the job.
     * @exception Exception a generic exception.
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
     * the scheduler needs the Rundata object.  The scheduler runs
     * independently of an HTTP request.  This should not extend
     * GenericLoader!  Thoughts??
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the job.
     * @exception Exception a generic exception.
     * @deprecated
     */
    @Deprecated
    @Override
    public void exec(RunData data, String name)
            throws Exception
    {
        throw new Exception("RunData objects not accepted for Scheduled jobs");
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     *
     * @param name Name of object instance.
     * @return A ScheduledJob with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public ScheduledJob getAssembler(String name)
        throws Exception
    {
        return getAssembler(ScheduledJob.class, name);
    }

    /**
     * @see org.apache.turbine.modules.Loader#getCacheSize()
     */
    public int getCacheSize()
    {
        return ScheduledJobLoader.getConfiguredCacheSize();
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

    /**
     * Helper method to get the configured cache size for this module
     *
     * @return the configure cache size
     */
    private static int getConfiguredCacheSize()
    {
        return Turbine.getConfiguration().getInt(ScheduledJob.CACHE_SIZE_KEY,
                ScheduledJob.CACHE_SIZE_DEFAULT);
    }
}
