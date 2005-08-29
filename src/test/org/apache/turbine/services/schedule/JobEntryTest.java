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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.test.BaseTestCase;

/**
 * Unit testing for Job Entries.  Ensure that removing NumberKey from TurbineNonPersistentScheduler
 * still works.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class JobEntryTest extends BaseTestCase
{
    private JobEntry je1;
    private JobEntry je2;

    public JobEntryTest(String name)
            throws Exception
    {
        super(name);

        // Add a new job entry
        je1 = new JobEntry();
        je1.setJobId(1);
        je1.setSecond(0);
        je1.setMinute(1);
        je1.setHour(-1);
        je1.setDayOfMonth(-1);
        je1.setWeekDay(-1);
        je1.setTask("SimpleJob");

        je2 = new JobEntry();
        je2.setJobId(2);
        je2.setSecond(0);
        je2.setMinute(1);
        je2.setHour(-1);
        je2.setDayOfMonth(-1);
        je2.setWeekDay(-1);
        je2.setTask("SimpleJob");
    }

    public static Test suite()
    {
        return new TestSuite(JobEntryTest.class);
    }

    /**
     * Tests the ability to enable and disable the service.
     */
    public void testCompareTo()
    {
        assertFalse(je1.equals(je2));
        je2.setJobId(je1.getJobId());
        assertTrue(je1.equals(je2));

    }

}
