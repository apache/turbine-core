package org.apache.turbine.util.parser;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.cactus.ServletTestCase;
import org.apache.turbine.Turbine;
import org.apache.turbine.util.TimeSelector;

/**
 * Used to test how BaseValueParser works with TimeSelector fields.
 * @author <a href="mailto:brekke@apache.org">Jeffrey D. Brekke</a>
 * @version $Id$
 */
public class BaseValueParserTest extends ServletTestCase
{
    Turbine turbine = null;
    BaseValueParser theBaseValueParser = null;
    SimpleDateFormat stf = null;

    /**
     * Creates a new <code>BaseValueParserTest</code> instance.
     *
     * @param name a <code>String</code> value
     */
    public BaseValueParserTest (String name) 
    {
        super(name);
    }
                                             
    /**
     * This setup will be running server side.  We startup Turbine and
     * get our test port from the properties.  This gets run before
     * each testXXX test.
     * @exception Exception if an error occurs
     */
    protected void setUp()
        throws Exception
    {
        super.setUp();
        /* Note: we are using the properties file from the cache test
         *  since we don't really need any specific property at this 
         *  time.  Future tests may require a test case specific 
         *  properties file to be used.:
         */
        config.setInitParameter("properties",
                "/WEB-INF/conf/TurbineComplete.properties");
        turbine = new Turbine();
        turbine.init(config);
        theBaseValueParser = new BaseValueParser();
        stf = new SimpleDateFormat("hh:mm:ss a");
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
        return new TestSuite(BaseValueParserTest.class);
    }

    /**
     * Test that a current time 
     */
    public void testCurrentTime()
    {
        Calendar now = Calendar.getInstance();
        checkTime(now.get(Calendar.HOUR),
                  now.get(Calendar.MINUTE),
                  now.get(Calendar.SECOND),
                  now.get(Calendar.AM_PM),
                  stf.format(now.getTime()));
    }
    
    /**
     * Test a time in the morning.
     */
    public void testMorning()
    {
        checkTime(10, 5, 30, Calendar.AM, "10:05:30 AM");
    }

    /**
     * Test a time in the afternoon.
     */
    public void testAfternoon()
    {
        checkTime(5, 32, 6, Calendar.PM, "05:32:06 PM");
    }

    /**
     * Test that an invalid time returns null.
     *
     */
    public void testInvalidTime()
    {
        theBaseValueParser.add(TimeSelector.HOUR_SUFFIX, 1);
        theBaseValueParser.add(TimeSelector.MINUTE_SUFFIX, 100);
        theBaseValueParser.add(TimeSelector.SECOND_SUFFIX, 0);
        theBaseValueParser.add(TimeSelector.AMPM_SUFFIX, Calendar.AM);

        assertNull("Should not have received a date object.", 
                   theBaseValueParser.getDate(""));
    }

    /**
     * Test the midnight special case.
     */
    public void testMidnight()
    {
        checkTime(12, 0, 0, Calendar.AM, "12:00:00 AM");
    }

    /**
     * Test the noon special case.
     */
    public void testNoon()
    {
        checkTime(12, 0, 0, Calendar.PM, "12:00:00 PM");
    }

    /**
     * Helper method which sets up the parser and gets the date.
     *
     * @param hour an <code>int</code> value
     * @param min an <code>int</code> value
     * @param sec an <code>int</code> value
     * @param ampm an <code>int</code> value
     * @param results a <code>String</code> value
     */
    private void checkTime(int hour, int min, int sec, int ampm, String results)
    {
        theBaseValueParser.add(TimeSelector.HOUR_SUFFIX, hour);
        theBaseValueParser.add(TimeSelector.MINUTE_SUFFIX, min);
        theBaseValueParser.add(TimeSelector.SECOND_SUFFIX, sec);
        theBaseValueParser.add(TimeSelector.AMPM_SUFFIX, ampm);

        Date newDate = theBaseValueParser.getDate("");
        assertNotNull("Could not create date for "+results, newDate);
       
        assertEquals(results, stf.format(newDate));
    }
}
