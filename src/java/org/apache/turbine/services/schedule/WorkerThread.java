package org.apache.turbine.services.schedule;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.ScheduledJobLoader;

/**
 * Wrapper for a <code>JobEntry</code> to actually perform the job's action.
 *
 * @version $Id$
 */
public class WorkerThread
        implements Runnable
{
    /**
     * The <code>JobEntry</code> to run.
     */
    private JobEntry je = null;

    /** Logging */
    private static Log log = LogFactory.getLog(ScheduleService.LOGGER_NAME);

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
            log.error("Error in WorkerThread for scheduled job #" +
                    je.getPrimaryKey() + ", task: " + je.getTask(), e);
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
        log.debug("Scheduled job #" + je.getPrimaryKey() + ' ' + state +
                ", task: " + je.getTask());
    }
}
