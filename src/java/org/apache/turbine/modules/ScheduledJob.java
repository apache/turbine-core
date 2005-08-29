package org.apache.turbine.modules;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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
    /**
     * Run the Jobentry from the scheduler queue.
     *
     * @param job The job to run.
     */
    public abstract void run(JobEntry job)
            throws Exception;
}
