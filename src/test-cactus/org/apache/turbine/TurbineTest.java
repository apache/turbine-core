/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
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
 *     "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache" or
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
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

package org.apache.turbine;

// Cactus and Junit imports
import junit.awtui.TestRunner;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.*;

import org.apache.turbine.Turbine;
import org.apache.commons.configuration.Configuration;
/**
 * Test starting up Turbine with various parameters.  Because I can't get
 * shutdown to work, I made two tests.
 *
 * @author <a href="epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class TurbineTest extends ServletTestCase
{
    private Configuration configuration = null;
    private Turbine turbine = null;

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TurbineTest(String name)
    {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        TestRunner.main(new String[] { TurbineTest.class.getName()});
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TurbineTest.class);
    }

    /**
     * This setup will be running server side.  We startup Turbine and
     * get our test port from the properties.  This gets run before
     * each testXXX test.
     */
    protected void setUp() throws Exception
    {
        super.setUp();
        configuration = null;
        turbine = new Turbine();

    }

    /**
     * After each testXXX test runs, shut down the Turbine servlet.
     */
    protected void tearDown() throws Exception
    {
        if (turbine != null)
        {
            turbine.destroy();
        }
        super.tearDown();
    }

    /**
     * Simple test that verify that turbine throws an exception 
     * when all parameters are missing, and the default TR.props
     * doesn't exists
     * @throws Exception
     */
    public void offtestCreateTurbineNoParameters() throws Exception
    {
//System.out.println("Config:" + config.getInitParameter("properties"))
        try
        {

            
            turbine.init(config);
            fail(
                "Should have thrown a javax.servlet.ServletException "
                    + "because the default TR.props doesn't exist!");
        }
        catch (javax.servlet.ServletException se)
        {
            turbine = null;
        }
        assertTrue("Make sure turbine is null", turbine == null);
    }

    /**
     * Simple test that verify that turbine can start up with a
     * TR.props
     * @throws Exception
     */
    public void testCreateTurbineWithTRProps() throws Exception
    {
        config.setInitParameter(
            "properties",
            "WEB-INF/conf/TurbineComplete.properties");

        turbine.init(config);
        assertNotNull("Make sure turbine loaded", turbine);
        configuration = turbine.getConfiguration();
        assertTrue("Make sure we have values", !configuration.isEmpty());
        assertEquals(
            "Read a config value",
            "lower",
            configuration.getString("url.case.folding"));
    }

  
}
