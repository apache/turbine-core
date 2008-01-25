package org.apache.turbine.om.security;


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


import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.ServletTestCase;
import org.apache.turbine.Turbine;
import java.util.*;

/**
 * Test the TurbineUser
 *
 * This tests that we can use the TurbineUser classes.  Note, this can be very dependent
 * on various configuration values.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineUserTest extends ServletTestCase
{

    Turbine turbine = null;

    /**
     * Constructor for TurbineUserTest.
     * @param arg0
     */
    public TurbineUserTest(String name)
    {
        super(name);
    }

    /**
     * This will setup an instance of turbine to use when testing
     * @exception if an exception occurs.
     */

    protected void setUp() throws Exception
    {
        super.setUp();

        config.setInitParameter("properties", "/WEB-INF/conf/TurbineComplete.properties");
        turbine = new Turbine();
        turbine.init(config);
    }

    /**
     * Shut down our turbine servlet and let our parents clean up also.
     *
     * @exception Exception if an error occurs
     */
    protected void tearDown() throws Exception
    {
        turbine.destroy();
        super.tearDown();
    }

    /**
     * Return a test suite of all our tests.
     *
     * @return a <code>Test</code> value
     */
    public static Test suite()
    {
        return new TestSuite(TurbineUserTest.class);
    }

    /**
     * Tests if a TurbineUser can be created.  This seemed to cause
     * errors at one point.
     */
    public void testCreatingTurbineUser() throws Exception
    {
        TurbineUser user = null;

        user = new TurbineUser();

        assertNotNull(user);
    }

    public void testSavingAndStoringTemporaryValues() throws Exception
    {
        TurbineUser user = new TurbineUser();
        user.setTemp("test", "value");
        assertEquals("value", user.getTemp("test"));

        assertNull(user.getTemp("nonexistentvalue"));
        assertEquals("defaultvalue", user.getTemp("nonexistentvalues", "defaultvalue"));
        Hashtable htTemp = new Hashtable();
        htTemp.put("test1", "value1");
        htTemp.put("test2", new Integer(5));

        user.setTempStorage(htTemp);
        assertEquals("value1", user.getTemp("test1"));
        Integer retVal = (Integer) user.getTemp("test2");
        assertTrue(retVal.intValue() == 5);
        assertNull(user.getTemp("test"));
    }

    public void testSavingAndStoringPermValues() throws Exception
    {
        TurbineUser user = new TurbineUser();
        user.setPerm("test", "value");
        assertEquals("value", user.getPerm("test"));

        assertNull(user.getPerm("nonexistentvalue"));
        assertEquals("defaultvalue", user.getPerm("nonexistentvalues", "defaultvalue"));

        Hashtable htPerm = new Hashtable();
        htPerm.put("test1", "value1");
        htPerm.put("test2", new Integer(5));

        user.setPermStorage(htPerm);
        assertEquals("value1", user.getPerm("test1"));
        Integer retVal = (Integer) user.getPerm("test2");
        assertTrue(retVal.intValue() == 5);
        assertNull(user.getPerm("test"));
    }
}
