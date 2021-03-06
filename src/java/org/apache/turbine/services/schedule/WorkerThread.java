package org.apache.turbine.services.schedule;

import org.apache.logging.log4j.LogManager;

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

import org.apache.logging.log4j.Logger;
import org.apache.turbine.modules.ScheduledJobLoader;

/**
 * Wrapper for a <code>JobEntry</code> to actually perform the job's action.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id: WorkerThread.java 534527 2007-05-02 16:10:59Z tv $
 */
public class WorkerThread
        implements Runnable
{
    /**
     * The <code>JobEntry</code> to run.
     */
    private JobEntry je = null;

    /** Logging */
    private static final Logger log = LogManager.getLogger(ScheduleService.LOGGER_NAME);

    /**
     * Creates a new worker to run the specified <code>JobEntry</code>.
     *
     * @param je The <code>JobEntry</code> to create a worker for.
     */
    public WorkerThread(JobEntry je)
    {
        this.je = je;
    }

    /**
     * Run the job.
     */
    @Override
    public void run()
    {
        if (je == null || je.isActive())
        {
            return;
        }

        try
        {
            if (!je.isActive())
            {
                je.setActive(true);
                logStateChange("started");
                ScheduledJobLoader.getInstance().exec(je, je.getTask());
            }
        }
        catch (Exception e)
        {
            log.error("Error in WorkerThread for scheduled job #{}, task: {}",
                    Integer.valueOf(je.getJobId()), je.getTask(), e);
        }
        finally
        {
            if (je.isActive())
            {
                je.setActive(false);
                logStateChange("completed");
            }
        }
    }

    /**
     * Macro to log <code>JobEntry</code> status information.
     *
     * @param state The new state of the <code>JobEntry</code>.
     */
    private final void logStateChange(String state)
    {
        log.error("Scheduled job #{} {}, task: {}",
                Integer.valueOf(je.getJobId()), state, je.getTask());
    }
}
