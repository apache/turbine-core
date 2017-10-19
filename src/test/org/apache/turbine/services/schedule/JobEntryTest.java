package org.apache.turbine.services.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

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

/**
 * Unit testing for Job Entries.  Ensure that removing NumberKey from TurbineNonPersistentScheduler
 * still works.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id: JobEntryTest.java 615328 2008-01-25 20:25:05Z tv $
 */
public class JobEntryTest
{

    private JobEntry je1;
    private JobEntry je2;

    @Before
    public void setUpBefore() throws Exception
    {

        // Add a new job entry
        je1 = new JobEntryNonPersistent();
        je1.setJobId(1);

        je2 = new JobEntryNonPersistent();
        je2.setJobId(2);
    }


    /**
     * Tests if the job entries are comparable
     */
    @Test public void testCompareTo()
    {
        assertNotEquals(je1.compareTo(je2), 0);
        je2.setJobId(je1.getJobId());
        assertEquals(je1.compareTo(je2), 0);

    }

}
