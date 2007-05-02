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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.services.schedule.JobEntry;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.RunData;

/**
 * ScheduledJobs loader class.
 *
 * @author <a href="mailto:mbryson@mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class ScheduledJobLoader
    extends GenericLoader
{
    /** Serial Version UID */
    private static final long serialVersionUID = 7207944483452185019L;

    /** Logging */
    private static Log log = LogFactory.getLog(ScheduledJobLoader.class);

    /** The single instance of this class. */
    private static ScheduledJobLoader instance =
        new ScheduledJobLoader(Turbine.getConfiguration()
            .getInt(TurbineConstants.SCHEDULED_JOB_CACHE_SIZE_KEY,
                TurbineConstants.SCHEDULED_JOB_CACHE_SIZE_DEFAULT));

    /** The Assembler Broker Service */
    private static AssemblerBrokerService ab = TurbineAssemblerBroker.getService();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScheduledJobLoader()
    {
        super();
    }

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScheduledJobLoader(int i)
    {
        super(i);
    }

    /**
     * Adds an instance of an object into the hashtable.
     *
     * @param name Name of object.
     * @param job Job to be associated with name.
     */
    private void addInstance(String name, ScheduledJob job)
    {
        if (cache())
        {
            this.put(name, (ScheduledJob) job);
        }
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
        getInstance(name).run(job);
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
     */
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
     * @return An ScheduledJob with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public ScheduledJob getInstance(String name)
            throws Exception
    {
        ScheduledJob job = null;

        // Check if the screen is already in the cache
        if (cache() && this.containsKey(name))
        {
            job = (ScheduledJob) this.get(name);
            log.debug("Found Job " + name + " in the cache!");
        }
        else
        {
            log.debug("Loading Job " + name + " from the Assembler Broker");

            try
            {
                if (ab != null)
                {
                    // Attempt to load the job
                    job = (ScheduledJob) ab.getAssembler(
                        AssemblerBrokerService.SCHEDULEDJOB_TYPE, name);
                }
            }
            catch (ClassCastException cce)
            {
                // This can alternatively let this exception be thrown
                // So that the ClassCastException is shown in the
                // browser window.  Like this it shows "Screen not Found"
                job = null;
            }

            if (job == null)
            {
                // If we did not find a screen we should try and give
                // the user a reason for that...
                // FIX ME: The AssemblerFactories should each add it's
                // own string here...
                List packages = Turbine.getConfiguration()
                    .getList(TurbineConstants.MODULE_PACKAGES);

                ObjectUtils.addOnce(packages, GenericLoader.getBasePackage());

                throw new ClassNotFoundException(
                        "\n\n\tRequested ScheduledJob not found: " + name +
                        "\n\tTurbine looked in the following " +
                        "modules.packages path: \n\t" + packages.toString() + "\n");
            }
            else if (cache())
            {
                // The new instance is added to the cache
                addInstance(name, job);
            }
        }
        return job;
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
