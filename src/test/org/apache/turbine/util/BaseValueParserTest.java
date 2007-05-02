package org.apache.turbine.util;

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

import java.math.BigDecimal;

import junit.framework.TestSuite;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.parser.BaseValueParser;
import org.apache.turbine.util.parser.ParserUtils;

/**
 * Testing of the BaseValueParser class
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class BaseValueParserTest extends BaseTestCase
{
    private BaseValueParser parser;

    /**
     * Constructor for test.
     *
     * @param testName name of the test being executed
     */
    public BaseValueParserTest(String testName)
            throws Exception
    {
        super(testName);

        // Setup configuration
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");
        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(ParserUtils.URL_CASE_FOLDING_KEY,
                ParserUtils.URL_CASE_FOLDING_LOWER_VALUE );
        serviceManager.setConfiguration(cfg);

    }

    /**
     * Performs any initialization that must happen before each test is run.
     */
    protected void setUp()
    {
        parser = new BaseValueParser();
    }

    /**
     * Clean up after each test is run.
     */
    protected void tearDown()
    {
        parser = null;
    }

    /**
     * Factory method for creating a TestSuite for this class.
     *
     * @return the test suite
     */
    public static TestSuite suite()
    {
        TestSuite suite = new TestSuite(BaseValueParserTest.class);
        return suite;
    }

    public void testGetByte()
    {
        // no param
        byte result = parser.getByte("invalid");
        assertEquals(result, 0);

        // default
        result = parser.getByte("default", (byte)3);
        assertEquals(result, 3);

        // param exists
        parser.add("exists", "1");
        result = parser.getByte("exists");
        assertEquals(result, 1);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getByte("unparsable");
        assertEquals(result, 0);
    }

    public void testGetByteObject()
    {
        // no param
        Byte result = parser.getByteObject("invalid");
        assertNull(result);

        // default
        result = parser.getByteObject("default", new Byte((byte)3));
        assertEquals(result, new Byte((byte)3));

        // param exists
        parser.add("exists", "1");
        result = parser.getByteObject("exists");
        assertEquals(result, new Byte((byte)1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getByteObject("unparsable");
        assertNull(result);
    }

    public void testGetInt()
    {
        // no param
        int result = parser.getInt("invalid");
        assertEquals(result, 0);

        // default
        result = parser.getInt("default", 3);
        assertEquals(result, 3);

        // param exists
        parser.add("exists", "1");
        result = parser.getInt("exists");
        assertEquals(result, 1);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getInt("unparsable");
        assertEquals(result, 0);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        int arrayResult[] = parser.getInts("array");
        int compare[] = {1,2,3};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        int arrayResult2[] = parser.getInts("array2");
        int compare2[] = {1,0,3};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }

    public void testGetIntObject()
    {
        // no param
        Integer result = parser.getIntObject("invalid");
        assertNull(result);

        // default
        result = parser.getIntObject("default", new Integer(3));
        assertEquals(result, new Integer(3));

        // param exists
        parser.add("exists", "1");
        result = parser.getIntObject("exists");
        assertEquals(result, new Integer(1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getIntObject("unparsable");
        assertNull(result);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        Integer arrayResult[] = parser.getIntObjects("array");
        Integer compare[] = {new Integer(1), new Integer(2), new Integer(3)};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        Integer arrayResult2[] = parser.getIntObjects("array2");
        Integer compare2[] = {new Integer(1), null, new Integer(3)};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }

    public void testGetFloat()
    {
        // no param
        float result = parser.getFloat("invalid");
        assertEquals(result, 0, 0);

        // default
        result = parser.getFloat("default", 3);
        assertEquals(result, 3, 0);

        // param exists
        parser.add("exists", "1");
        result = parser.getFloat("exists");
        assertEquals(result, 1, 0);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getFloat("unparsable");
        assertEquals(result, 0, 0);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        float arrayResult[] = parser.getFloats("array");
        float compare[] = {1,2,3};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i], 0);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        float arrayResult2[] = parser.getFloats("array2");
        float compare2[] = {1,0,3};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i], 0);
        }
    }

    public void testGetFloatObject()
    {
        // no param
        Float result = parser.getFloatObject("invalid");
        assertNull(result);

        // default
        result = parser.getFloatObject("default", new Float(3));
        assertEquals(result, new Float(3));

        // param exists
        parser.add("exists", "1");
        result = parser.getFloatObject("exists");
        assertEquals(result, new Float(1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getFloatObject("unparsable");
        assertNull(result);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        Float arrayResult[] = parser.getFloatObjects("array");
        Float compare[] = {new Float(1), new Float(2), new Float(3)};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        Float arrayResult2[] = parser.getFloatObjects("array2");
        Float compare2[] = {new Float(1), null, new Float(3)};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }

    public void testGetDouble()
    {
        // no param
        double result = parser.getDouble("invalid");
        assertEquals(result, 0, 0);

        // default
        result = parser.getDouble("default", 3);
        assertEquals(result, 3, 0);

        // param exists
        parser.add("exists", "1");
        result = parser.getDouble("exists");
        assertEquals(result, 1, 0);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getDouble("unparsable");
        assertEquals(result, 0, 0);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        double arrayResult[] = parser.getDoubles("array");
        double compare[] = {1,2,3};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i], 0);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        double arrayResult2[] = parser.getDoubles("array2");
        double compare2[] = {1,0,3};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i], 0);
        }
    }

    public void testGetDoubleObject()
    {
        // no param
        Double result = parser.getDoubleObject("invalid");
        assertNull(result);

        // default
        result = parser.getDoubleObject("default", new Double(3));
        assertEquals(result, new Double(3));

        // param exists
        parser.add("exists", "1");
        result = parser.getDoubleObject("exists");
        assertEquals(result, new Double(1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getDoubleObject("unparsable");
        assertNull(result);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        Double arrayResult[] = parser.getDoubleObjects("array");
        Double compare[] = {new Double(1), new Double(2), new Double(3)};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        Double arrayResult2[] = parser.getDoubleObjects("array2");
        Double compare2[] = {new Double(1), null, new Double(3)};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }

    public void testGetLong()
    {
        // no param
        long result = parser.getLong("invalid");
        assertEquals(result, 0);

        // default
        result = parser.getLong("default", 3);
        assertEquals(result, 3);

        // param exists
        parser.add("exists", "1");
        result = parser.getLong("exists");
        assertEquals(result, 1);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getLong("unparsable");
        assertEquals(result, 0);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        long arrayResult[] = parser.getLongs("array");
        long compare[] = {1,2,3};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        long arrayResult2[] = parser.getLongs("array2");
        long compare2[] = {1,0,3};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i]);
        }
    }

    public void testGetLongObject()
    {
        // no param
        Long result = parser.getLongObject("invalid");
        assertNull(result);

        // default
        result = parser.getLongObject("default", new Long(3));
        assertEquals(result, new Long(3));

        // param exists
        parser.add("exists", "1");
        result = parser.getLongObject("exists");
        assertEquals(result, new Long(1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getLongObject("unparsable");
        assertNull(result);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        Long arrayResult[] = parser.getLongObjects("array");
        Long compare[] = {new Long(1), new Long(2), new Long(3)};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        Long arrayResult2[] = parser.getLongObjects("array2");
        Long compare2[] = {new Long(1), null, new Long(3)};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }

    public void testGetBoolean()
    {
        // no param
        boolean result = parser.getBoolean("invalid");
        assertEquals(result, false);

        // default
        result = parser.getBoolean("default", true);
        assertEquals(result, true);

        // true values - Case is intentional
        parser.add("true1", "trUe");
        result = parser.getBoolean("true1");
        assertEquals(result, true);
        parser.add("true2", "yEs");
        result = parser.getBoolean("true2");
        assertEquals(result, true);
        parser.add("true3", "1");
        result = parser.getBoolean("true3");
        assertEquals(result, true);
        parser.add("true4", "oN");
        result = parser.getBoolean("true4");
        assertEquals(result, true);

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getBoolean("unparsable");
        assertEquals(result, false);

    }

    public void testGetBooleanObject()
    {
        // no param
        Boolean result = parser.getBooleanObject("invalid");
        assertNull(result);

        // default
        result = parser.getBooleanObject("default", Boolean.TRUE);
        assertEquals(result, Boolean.TRUE);

        // true values - Case is intentional
        parser.add("true1", "trUe");
        result = parser.getBooleanObject("true1");
        assertEquals(result, Boolean.TRUE);
        parser.add("true2", "yEs");
        result = parser.getBooleanObject("true2");
        assertEquals(result, Boolean.TRUE);
        parser.add("true3", "1");
        result = parser.getBooleanObject("true3");
        assertEquals(result, Boolean.TRUE);
        parser.add("true4", "oN");
        result = parser.getBooleanObject("true4");
        assertEquals(result, Boolean.TRUE);

        // false values - Case is intentional
        parser.add("false1", "falSe");
        result = parser.getBooleanObject("false1");
        assertEquals(result, Boolean.FALSE);
        parser.add("false2", "nO");
        result = parser.getBooleanObject("false2");
        assertEquals(result, Boolean.FALSE);
        parser.add("false3", "0");
        result = parser.getBooleanObject("false3");
        assertEquals(result, Boolean.FALSE);
        parser.add("false4", "oFf");
        result = parser.getBooleanObject("false4");
        assertEquals(result, Boolean.FALSE);


        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getBooleanObject("unparsable");
        assertNull(result);
    }

    public void testGetBigDecimal()
    {
        // no param
        BigDecimal result = parser.getBigDecimal("invalid");
        assertEquals(new BigDecimal(0), result);

        // default
        result = parser.getBigDecimal("default", new BigDecimal(3));
        assertEquals(result, new BigDecimal(3));

        // param exists
        parser.add("exists", "1");
        result = parser.getBigDecimal("exists");
        assertEquals(result, new BigDecimal(1));

        // unparsable value
        parser.add("unparsable", "a");
        result = parser.getBigDecimal("unparsable");
        assertEquals(new BigDecimal(0), result);

        // array
        parser.add("array", "1");
        parser.add("array", "2");
        parser.add("array", "3");
        BigDecimal arrayResult[] = parser.getBigDecimals("array");
        BigDecimal compare[] = {new BigDecimal(1), new BigDecimal(2),
                                new BigDecimal(3)};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

        // array w/ unparsable element
        parser.add("array2", "1");
        parser.add("array2", "a");
        parser.add("array2", "3");
        BigDecimal arrayResult2[] = parser.getBigDecimals("array2");
        BigDecimal compare2[] = {new BigDecimal(1), null, new BigDecimal(3)};
        assertEquals(arrayResult2.length, compare2.length);
        for( int i=0; i<compare2.length; i++)
        {
            assertEquals(compare2[i], arrayResult2[i] );
        }
    }


    public void getString()
    {
        // no param
        String result = parser.getString("invalid");
        assertNull(result);

        // default
        result = parser.getString("default", "default");
        assertEquals(result, "default");

        // null value
        parser.add("null", "null");
        assertNull( parser.getString("null"));

        // only return the first added
        parser.add("multiple", "test");
        parser.add("multiple", "test2");
        assertEquals("test2", parser.getString("multiple"));

        // array
        parser.add("array", "line1");
        parser.add("array", "line2");
        parser.add("array", "line3");
        String arrayResult[] = parser.getStrings("array");
        String compare[] = {"line1","line2","line3"};
        assertEquals(arrayResult.length, compare.length);
        for( int i=0; i<compare.length; i++)
        {
            assertEquals(compare[i], arrayResult[i]);
        }

    }

}
