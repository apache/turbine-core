package org.apache.turbine.modules;

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

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.services.TurbineServices;

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
                Vector packages = Turbine.getConfiguration()
                    .getVector(TurbineConstants.MODULE_PACKAGES);

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
