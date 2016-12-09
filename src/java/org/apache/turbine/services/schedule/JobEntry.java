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

import java.util.Date;

import org.apache.turbine.util.TurbineException;

/**
 * This is a interface for a scheduled job. It does not specify how to configure
 * when to run, that is left to subclasses. See the JobEntryTorque for an
 * example of a JobEntry backed by Torque objects.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public interface JobEntry extends Comparable<JobEntry>
{
    /**
     * Sets whether the job is running.
     *
     * @param isActive
     *            Whether the job is running.
     */
    void setActive(boolean isActive);

    /**
     * Check to see if job is currently active/running
     *
     * @return true if job is currently being run by the worker thread, otherwise
     *         false
     */
    boolean isActive();

    /**
     * Check to see if job is new
     *
     * @return true if job is not yet saved, otherwise
     *         false
     */
    boolean isNew();

    /**
     * Get the value of jobId.
     *
     * @return int
     */
    int getJobId();

    /**
     * Set the value of jobId.
     *
     * @param v new value
     */
    void setJobId(int v);

    /**
     * Get the Task
     *
     * @return String
     */
    String getTask();

    /**
     * Set the value of Task
     *
     * @param v new value
     */
    void setTask(String v);

    /**
     * Get the next runtime for this job as a long.
     *
     * @return The next run time as a long.
     */
    long getNextRuntime();

    /**
     * Gets the next runtime as a date
     *
     * @return Next run date
     */
    Date getNextRunDate();

    /**
     * Get the next runtime for this job as a String.
     *
     * @return The next run time as a String.
     */
    String getNextRunAsString();

    /**
     * Calculate how long before the next runtime. <br>
     *
     * The runtime determines it's position in the job queue. Here's the logic: <br>
     * 1. Create a date the represents when this job is to run. <br>
     * 2. If this date has expired, them "roll" appropriate date fields forward
     * to the next date. <br>
     * 3. Calculate the diff in time between the current time and the next run
     * time. <br>
     *
     * @throws TurbineException
     *                a generic exception.
     */
    void calcRunTime() throws TurbineException;

}
