package org.apache.turbine.util.parser;


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


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
