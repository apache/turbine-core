package org.apache.turbine.util.parser;

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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestSuite;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.test.BaseTurbineTest;
import org.apache.turbine.util.DateSelector;
import org.apache.turbine.util.TimeSelector;

/**
 * test whether the Default parameter parser returns its uploaded file items
 * in the keySet().
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class BaseValueParserTest
        extends BaseTurbineTest
{
    public BaseValueParserTest(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static TestSuite suite()
    {
        return new TestSuite(BaseValueParserTest.class);
    }

    public void testSetup()
    {
        BaseValueParser vp = new BaseValueParser();
        assertFalse(vp.isDisposed());

        assertEquals("Wrong Character Encoding", TurbineConstants.PARAMETER_ENCODING_DEFAULT, vp.getCharacterEncoding());
    }

    public void testSetupWithEncoding()
    {
        String encoding = "ISO-8859-2";

        BaseValueParser vp = new BaseValueParser(encoding);
        assertFalse(vp.isDisposed());

        assertEquals("Wrong Character Encoding", encoding, vp.getCharacterEncoding());
    }

    public void testChangeEncoding()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong Character Encoding", TurbineConstants.PARAMETER_ENCODING_DEFAULT, vp.getCharacterEncoding());

        String encoding = "ISO-8859-2";
        vp.setCharacterEncoding(encoding);

        assertEquals("Wrong Character Encoding", encoding, vp.getCharacterEncoding());
    }

    public void testClear()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.clear();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());
    }

    public void testDispose()
    {
        BaseValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.dispose();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());
        assertTrue(vp.isDisposed());
    }

    public void testKeyArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        vp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());
    }

    public void testDoubleAdd()
    {
        ValueParser vp = new BaseValueParser();
        vp.setLocale(Locale.US);

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        double testValue = 2.2;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "2.2", vp.getString("foo"));
        assertEquals("Wrong double value", testValue, vp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", testValue, vp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = vp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = vp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
        
        vp.clear();
        vp.setLocale(Locale.GERMANY);
        
        String testDouble = "2,3";
        vp.add("foo", testDouble);
        assertEquals("Wrong double value", 2.3, vp.getDouble("foo"), 0.001);
    }

    public void testIntAdd()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        int testValue = 123;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "123", vp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue, vp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue, vp.getIntObject("foo").intValue());

        int [] ints = vp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue, ints[0]);

        Integer [] intObjs = vp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue, intObjs[0].intValue());
    }

    public void testIntegerAdd()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        Integer testValue = new Integer(123);

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "123", vp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue.intValue(), vp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue.intValue(), vp.getIntObject("foo").intValue());

        int [] ints = vp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue.intValue(), ints[0]);

        Integer [] intObjs = vp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue.intValue(), intObjs[0].intValue());
    }

    public void testLongAdd()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        long testValue = 9223372036854775807l;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "9223372036854775807", vp.getString("foo"));
        assertEquals("Wrong long value", (long) testValue, vp.getLong("foo"));
        assertEquals("Wrong Long value", (long) testValue, vp.getLongObject("foo").longValue());

        long [] longs = vp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", testValue, longs[0]);

        Long [] longObjs = vp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", testValue, longObjs[0].longValue());
    }

    public void testLongToInt()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        long testValue = 1234l;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "1234", vp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue, vp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue, vp.getIntObject("foo").intValue());

        int [] ints = vp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue, ints[0]);

        Integer [] intObjs = vp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue, intObjs[0].intValue());
    }

    public void testIntToLong()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        int testValue = 123;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "123", vp.getString("foo"));
        assertEquals("Wrong long value", (long) testValue, vp.getLong("foo"));
        assertEquals("Wrong Long value", (long) testValue, vp.getLongObject("foo").longValue());

        long [] longs = vp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", testValue, longs[0]);

        Long [] longObjs = vp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", testValue, longObjs[0].longValue());
    }

    public void testIntToDouble()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        int testValue = 123;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "123", vp.getString("foo"));
        assertEquals("Wrong double value", (double) testValue, vp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", (double) testValue, vp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = vp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = vp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
    }

    public void testLongToDouble()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        long testValue = 9223372036854775807l;

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", "9223372036854775807", vp.getString("foo"));
        assertEquals("Wrong double value", (double) testValue, vp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", (double) testValue, vp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = vp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = vp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
    }

    public void testStringAdd()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String testValue = "the quick brown fox";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", testValue, vp.getString("foo"));

        String [] Strings = vp.getStrings("foo");
        assertEquals("Wrong Array Size", 1, Strings.length);

        assertEquals("Wrong String array value", testValue, Strings[0]);
    }

    public void testStringToInt()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String testValue = "123456";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", testValue, vp.getString("foo"));

        assertEquals("Wrong int value", Integer.parseInt(testValue), vp.getInt("foo"));
        assertEquals("Wrong Int value", Integer.valueOf(testValue).intValue(), vp.getIntObject("foo").intValue());

        int [] ints = vp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", Integer.parseInt(testValue), ints[0]);

        Integer [] intObjs = vp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", Integer.valueOf(testValue).intValue(), intObjs[0].intValue());
    }

    public void testStringToLong()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String testValue = "123456";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", testValue, vp.getString("foo"));

        assertEquals("Wrong long value", Long.parseLong(testValue), vp.getLong("foo"));
        assertEquals("Wrong Long value", Long.valueOf(testValue).longValue(), vp.getLongObject("foo").longValue());

        long [] longs = vp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", Long.parseLong(testValue), longs[0]);

        Long [] longObjs = vp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", Long.valueOf(testValue).longValue(), longObjs[0].longValue());
    }

    public void testStringArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String [] testValue = new String [] {
            "foo", "bar", "baz"
        };

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        String [] res = vp.getStrings("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getString("foo"));

        vp.add("foo", "xxx");

        res = vp.getStrings("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], "xxx");

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getString("foo"));
    }

    public void testRemove()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String testValue = "the quick brown fox";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", testValue, vp.getString("foo"));

        assertNotNull(vp.remove("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertNull(vp.getString("foo"));

        // Test non-existing key
        assertNull(vp.remove("baz"));

        // Test removing null value
        assertNull(vp.remove(null));
    }

    public void testRemoveArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String testValue = "the quick brown fox";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        assertEquals("Wrong string value", testValue, vp.getString("foo"));

        String [] res = vp.getStrings("foo");

        assertEquals("Wrong number of elements", 2, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue);
        }

        vp.remove("foo");

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertNull(vp.getString("foo"));
    }

    public void testContainsKey()
    {
        ValueParser vp = new BaseValueParser();

        vp.add("foo", "bar");
        vp.add("bar", new String [] { "foo", "bar" });

        assertTrue(vp.containsKey("foo"));
        assertTrue(vp.containsKey("bar"));
        assertFalse(vp.containsKey("baz"));
    }

    public void testDateSelector()
    {
        BaseValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());
        assertFalse(vp.containsDateSelectorKeys("foo"));

        vp.add("foo" + DateSelector.DAY_SUFFIX, "1");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertFalse(vp.containsDateSelectorKeys("foo"));

        vp.add("foo" + DateSelector.MONTH_SUFFIX, "1");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());
        assertFalse(vp.containsDateSelectorKeys("foo"));

        vp.add("foo" + DateSelector.YEAR_SUFFIX, "2005");

        assertEquals("Wrong number of keys", 3, vp.keySet().size());
        assertTrue(vp.containsDateSelectorKeys("foo"));
    }

    public void testTimeSelector()
    {
        BaseValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());
        assertFalse(vp.containsTimeSelectorKeys("foo"));

        vp.add("foo" + TimeSelector.HOUR_SUFFIX, "22");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertFalse(vp.containsTimeSelectorKeys("foo"));

        vp.add("foo" + TimeSelector.MINUTE_SUFFIX, "58");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());
        assertFalse(vp.containsTimeSelectorKeys("foo"));

        vp.add("foo" + TimeSelector.SECOND_SUFFIX, "0");

        assertEquals("Wrong number of keys", 3, vp.keySet().size());
        assertTrue(vp.containsTimeSelectorKeys("foo"));
    }

    public void testDate()
    {
        BaseValueParser vp = new BaseValueParser();
        vp.setLocale(Locale.US);

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "03/21/2008");
        
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.clear();
        cal.set(2008, 2, 21, 0, 0, 0);

        assertEquals("Wrong Date value (US)", cal.getTime(), vp.getDate("foo"));

        vp.clear();
        vp.setLocale(Locale.GERMANY);

        vp.add("foo", "21.03.2008");
        
        cal = Calendar.getInstance(Locale.GERMANY);
        cal.clear();
        cal.set(2008, 2, 21, 0, 0, 0);

        assertEquals("Wrong Date value (German)", cal.getTime(), vp.getDate("foo"));
    }

    public void testBooleanObject()
    {
        ValueParser vp = new BaseValueParser();

        vp.add("t1", "true");
        vp.add("t2", "yes");
        vp.add("t3", "on");
        vp.add("t4", "1");
        vp.add("t5", 1);

        vp.add("f1", "false");
        vp.add("f2", "no");
        vp.add("f3", "off");
        vp.add("f4", "0");
        vp.add("f5", 0);

        vp.add("e1", "nix");
        vp.add("e2", "weg");
        vp.add("e3", 200);
        vp.add("e4", -2.5);

        assertEquals("Value is not true", Boolean.TRUE, vp.getBooleanObject("t1"));
        assertEquals("Value is not true", Boolean.TRUE, vp.getBooleanObject("t2"));
        assertEquals("Value is not true", Boolean.TRUE, vp.getBooleanObject("t3"));
        assertEquals("Value is not true", Boolean.TRUE, vp.getBooleanObject("t4"));
        assertEquals("Value is not true", Boolean.TRUE, vp.getBooleanObject("t5"));

        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f1"));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f2"));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f3"));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f4"));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f5"));

        assertNull(vp.getBooleanObject("e1"));
        assertNull(vp.getBooleanObject("e2"));
        assertNull(vp.getBooleanObject("e3"));
        assertNull(vp.getBooleanObject("e4"));

        assertNull(vp.getBooleanObject("does-not-exist"));
    }

    public void testBoolDefault()
    {
        ValueParser vp = new BaseValueParser();

        vp.add("t1", "true");
        vp.add("f1", "false");

        assertTrue(vp.getBoolean("t1"));
        assertFalse(vp.getBoolean("f1"));

        assertFalse(vp.getBoolean("does not exist"));

        assertTrue(vp.getBoolean("t1", false));
        assertFalse(vp.getBoolean("f1", true));

        assertFalse(vp.getBoolean("does not exist", false));
        assertTrue(vp.getBoolean("does not exist", true));
    }

    public void testBooleanDefault()
    {
        ValueParser vp = new BaseValueParser();

        vp.add("t1", "true");
        vp.add("f1", "false");

        assertEquals("Value is not true",  Boolean.TRUE, vp.getBooleanObject("t1"));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f1"));

        assertNull(vp.getBooleanObject("does not exist"));

        assertEquals("Value is not true",  Boolean.TRUE, vp.getBooleanObject("t1", Boolean.FALSE));
        assertEquals("Value is not true",  Boolean.TRUE, vp.getBooleanObject("t1", null));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f1", Boolean.TRUE));
        assertEquals("Value is not false", Boolean.FALSE, vp.getBooleanObject("f1", null));

        assertNull(vp.getBooleanObject("does not exist", null));
    }

    public void testDoubleArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        double [] testValue = {
            1.0, 2.0, 3.0
        };

        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo", testValue[i]);

            String [] res = vp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        double [] res = vp.getDoubles("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001);
        }

        Double [] resObj = vp.getDoubleObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].doubleValue(), testValue[i], 0.001);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getDoubleObject("foo").doubleValue(), 0.001);

        vp.add("foo", 4.0);

        res = vp.getDoubles("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001);
        }

        assertEquals(res[3], 4.0, 0.001);

        resObj = vp.getDoubleObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].doubleValue(), testValue[i], 0.001);
        }

        assertEquals(resObj[3].doubleValue(), 4.0, 0.001);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getDouble("foo"), 0.001);
    }

    public void testFloatArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        float [] testValue = {
            1.0f, 2.0f, 3.0f
        };

        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo", testValue[i]);

            String [] res = vp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        float [] res = vp.getFloats("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001f);
        }

        Float [] resObj = vp.getFloatObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].floatValue(), testValue[i], 0.001f);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getFloatObject("foo").floatValue(), 0.001f);

        vp.add("foo", 4.0f);

        res = vp.getFloats("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001f);
        }

        assertEquals(res[3], 4.0f, 0.001f);

        resObj = vp.getFloatObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].floatValue(), testValue[i], 0.001f);
        }

        assertEquals(resObj[3].floatValue(), 4.0f, 0.001f);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getFloat("foo"), 0.001f);
    }

    public void testBigDecimalArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        long [] testValue = {
            12345678,87654321,1092837465,
        };

        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo", testValue[i]);

            String [] res = vp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        BigDecimal [] res = vp.getBigDecimals("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i].longValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getBigDecimal("foo").longValue());

        vp.add("foo", 77777777);

        res = vp.getBigDecimals("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i].longValue(), testValue[i], 0.001);
        }

        assertEquals(res[3].longValue(), 77777777);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getBigDecimal("foo").longValue());
    }

    public void testIntegerArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        int [] testValue = {
            1, 2, 3
        };

        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo", testValue[i]);

            String [] res = vp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        int [] res = vp.getInts("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        Integer [] resObj = vp.getIntObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].intValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getIntObject("foo").intValue());

        vp.add("foo", 4);

        res = vp.getInts("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], 4);

        resObj = vp.getIntObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].intValue(), testValue[i]);
        }

        assertEquals(resObj[3].intValue(), 4);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getInt("foo"));
    }

    public void testLongArray()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        long [] testValue = {
            1l, 2l, 3l
        };

        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo", testValue[i]);

            String [] res = vp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        long [] res = vp.getLongs("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        Long [] resObj = vp.getLongObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].longValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], vp.getLongObject("foo").longValue());

        vp.add("foo", 4);

        res = vp.getLongs("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], 4);

        resObj = vp.getLongObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].longValue(), testValue[i]);
        }

        assertEquals(resObj[3].longValue(), 4);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], vp.getLong("foo"));
    }

    public void testByteArray()
            throws Exception
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String  testValue = "abcdefg";

        vp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        byte [] res = vp.getBytes("foo");

        assertEquals("Wrong number of elements", 7, res.length);

        for (int i = 0; i < res.length; i++)
        {
            byte [] testByte = testValue.substring(i, i + 1).getBytes(vp.getCharacterEncoding());
            assertEquals("More than one byte for a char!", 1, testByte.length);
            assertEquals("Wrong value", res[i], testByte[0]);
        }
    }

    public void testByte()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        String [] testValue = {
            "0", "127", "-1",
            "0", "-127", "100"
        };


        for (int i = 0; i < testValue.length; i++)
        {
            vp.add("foo" + i, testValue[i]);
        }

        assertEquals("Wrong number of keys", 6, vp.keySet().size());

        assertEquals("Wrong value", (byte) 0,    vp.getByte("foo0"));
        assertEquals("Wrong value", (byte) 127,  vp.getByte("foo1"));
        assertEquals("Wrong value", (byte) -1,   vp.getByte("foo2"));
        assertEquals("Wrong value", (byte) 0,    vp.getByte("foo3"));
        assertEquals("Wrong value", (byte) -127, vp.getByte("foo4"));
        assertEquals("Wrong value", (byte) 100,  vp.getByte("foo5"));

        assertEquals("Wrong value", new Byte((byte) 0),    vp.getByteObject("foo0"));
        assertEquals("Wrong value", new Byte((byte) 127),  vp.getByteObject("foo1"));
        assertEquals("Wrong value", new Byte((byte) -1),   vp.getByteObject("foo2"));
        assertEquals("Wrong value", new Byte((byte) 0),    vp.getByteObject("foo3"));
        assertEquals("Wrong value", new Byte((byte) -127), vp.getByteObject("foo4"));
        assertEquals("Wrong value", new Byte((byte) 100),  vp.getByteObject("foo5"));

    }

    public void testStringDefault()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong value found", "bar", vp.getString("foo", "xxx"));
        assertEquals("Wrong value found", "bar", vp.getString("foo", null));

        assertEquals("Wrong value found", "baz", vp.getString("does-not-exist", "baz"));
        assertNull(vp.getString("does-not-exist", null));
    }

    public void testSetString()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        vp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        String [] res = vp.getStrings("bar");
        assertEquals("Wrong number of values", 2, res.length);
        assertEquals("Wrong value found", "foo", res[0]);
        assertEquals("Wrong value found", "baz", res[1]);

        vp.setString("bar", "xxx");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        res = vp.getStrings("bar");
        assertEquals("Wrong number of values", 1, res.length);
        assertEquals("Wrong value found", "xxx", res[0]);
    }

    public void testSetStrings()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

        vp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        vp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        String [] res = vp.getStrings("bar");
        assertEquals("Wrong number of values", 2, res.length);
        assertEquals("Wrong value found", "foo", res[0]);
        assertEquals("Wrong value found", "baz", res[1]);

        String [] newValues = new String [] { "aaa", "bbb", "ccc", "ddd" };

        vp.setStrings("bar", newValues);

        assertEquals("Wrong number of keys", 2, vp.keySet().size());

        res = vp.getStrings("bar");
        assertEquals("Wrong number of values", newValues.length, res.length);

        for (int i = 0 ; i < newValues.length; i++)
        {
            assertEquals("Wrong value found", newValues[i], res[i]);
        }
    }

    public void testSetProperties()
            throws Exception
    {
        ValueParser vp = new BaseValueParser();

        vp.add("longvalue", 12345l);
        vp.add("doublevalue", 2.0);
        vp.add("intValue", 200);
        vp.add("stringvalue", "foobar");
        vp.add("booleanvalue", "true");

        PropertyBean bp = new PropertyBean();
        bp.setDoNotTouchValue("abcdef");

        vp.setProperties(bp);

        assertEquals("Wrong value in bean", "abcdef", bp.getDoNotTouchValue());
        assertEquals("Wrong value in bean", "foobar", bp.getStringValue());
        assertEquals("Wrong value in bean", 200,      bp.getIntValue());
        assertEquals("Wrong value in bean", 2.0,      bp.getDoubleValue(), 0.001);
        assertEquals("Wrong value in bean", 12345l,   bp.getLongValue());
        assertEquals("Wrong value in bean", Boolean.TRUE, bp.getBooleanValue());
    }

    public void testAddNulls()
    {
        ValueParser vp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", (Integer) null);

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", (String) null);

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("bar", "null");

        assertEquals("Wrong number of keys", 1, vp.keySet().size());

    }

    public void testAddNullArrays()
    {
        String [] res = null;

        ValueParser vp = new BaseValueParser();
        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        vp.add("foo", new String [] { "foo", "bar" });
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 2, res.length);

        // null value should not change contents
        vp.add("foo", (String) null);
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 2, res.length);

        // null value should not change contents
        vp.add("foo", (String []) null);
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 2, res.length);

        // empty String array should not change contents
        vp.add("foo", new String [0]);
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 2, res.length);

        // String array with null value should not change contents
        vp.add("foo", new String [] { null });
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 2, res.length);

        // String array with null value should only add non-null values
        vp.add("foo", new String [] { "bla", null, "foo" });
        res = vp.getStrings("foo");
        assertEquals("Wrong number of keys", 1, vp.keySet().size());
        assertEquals("Wrong number of values", 4, res.length);
    }

    public void testNonExistingResults()
    {
        ValueParser vp = new BaseValueParser();
        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertEquals("Wrong value for non existing key", 0.0, vp.getDouble("foo"), 0.001);
        assertNull(vp.getDoubles("foo"));
        assertNull(vp.getDoubleObject("foo"));
        assertNull(vp.getDoubleObjects("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertNull(vp.getString("foo"));
        assertNull(vp.getStrings("foo"));

        assertEquals("Wrong value for non existing key", 0.0f, vp.getFloat("foo"), 0.001);
        assertNull(vp.getFloats("foo"));
        assertNull(vp.getFloatObject("foo"));
        assertNull(vp.getFloatObjects("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertEquals("Wrong value for non existing key", 0.0, vp.getBigDecimal("foo").doubleValue(), 0.001);
        assertNull(vp.getBigDecimals("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertEquals("Wrong value for non existing key", 0, vp.getInt("foo"));
        assertNull(vp.getInts("foo"));
        assertNull(vp.getIntObject("foo"));
        assertNull(vp.getIntObjects("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertEquals("Wrong value for non existing key", 0, vp.getLong("foo"));
        assertNull(vp.getLongs("foo"));
        assertNull(vp.getLongObject("foo"));
        assertNull(vp.getLongObjects("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());

        assertEquals("Wrong value for non existing key", 0, vp.getByte("foo"));
        assertNull(vp.getByteObject("foo"));

        assertEquals("Wrong number of keys", 0, vp.keySet().size());
    }
}

