package org.apache.turbine.om.security;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
