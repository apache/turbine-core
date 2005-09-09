package org.apache.turbine.util.parser;

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

import java.math.BigDecimal;

import java.util.Iterator;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.turbine.TurbineConstants;

import org.apache.turbine.util.DateSelector;
import org.apache.turbine.util.TimeSelector;

import org.apache.turbine.test.BaseTurbineTest;

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
        BaseValueParser bvp = new BaseValueParser();
        assertFalse(bvp.isDisposed());

        assertEquals("Wrong Character Encoding", TurbineConstants.PARAMETER_ENCODING_DEFAULT, bvp.getCharacterEncoding());
    }

    public void testSetupWithEncoding()
    {
        String encoding = "ISO-8859-2";

        BaseValueParser bvp = new BaseValueParser(encoding);
        assertFalse(bvp.isDisposed());

        assertEquals("Wrong Character Encoding", encoding, bvp.getCharacterEncoding());
    }

    public void testChangeEncoding()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong Character Encoding", TurbineConstants.PARAMETER_ENCODING_DEFAULT, bvp.getCharacterEncoding());

        String encoding = "ISO-8859-2";
        bvp.setCharacterEncoding(encoding);

        assertEquals("Wrong Character Encoding", encoding, bvp.getCharacterEncoding());
    }

    public void testClear()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.clear();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
    }

    public void testDispose()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.dispose();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        assertTrue(bvp.isDisposed());
    }

    public void testKeyArray()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
        
        bvp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
    }

    public void testDoubleAdd()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        double testValue = 2.0;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "2.0", bvp.getString("foo"));
        assertEquals("Wrong double value", (double) testValue, bvp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", (double) testValue, bvp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = bvp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = bvp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
    }

    public void testIntAdd()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        int testValue = 123;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "123", bvp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue, bvp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue, bvp.getIntObject("foo").intValue());

        int [] ints = bvp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue, ints[0]);

        Integer [] intObjs = bvp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue, intObjs[0].intValue());
    }

    public void testIntegerAdd()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        Integer testValue = new Integer(123);

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "123", bvp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue.intValue(), bvp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue.intValue(), bvp.getIntObject("foo").intValue());

        int [] ints = bvp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue.intValue(), ints[0]);

        Integer [] intObjs = bvp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue.intValue(), intObjs[0].intValue());
    }

    public void testLongAdd()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        long testValue = 9223372036854775807l;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "9223372036854775807", bvp.getString("foo"));
        assertEquals("Wrong long value", (long) testValue, bvp.getLong("foo"));
        assertEquals("Wrong Long value", (long) testValue, bvp.getLongObject("foo").longValue());

        long [] longs = bvp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", testValue, longs[0]);

        Long [] longObjs = bvp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", testValue, longObjs[0].longValue());
    }

    public void testLongToInt()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        long testValue = 1234l;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "1234", bvp.getString("foo"));
        assertEquals("Wrong int value", (int) testValue, bvp.getInt("foo"));
        assertEquals("Wrong Int value", (int) testValue, bvp.getIntObject("foo").intValue());

        int [] ints = bvp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", testValue, ints[0]);

        Integer [] intObjs = bvp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", testValue, intObjs[0].intValue());
    }

    public void testIntToLong()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        int testValue = 123;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "123", bvp.getString("foo"));
        assertEquals("Wrong long value", (long) testValue, bvp.getLong("foo"));
        assertEquals("Wrong Long value", (long) testValue, bvp.getLongObject("foo").longValue());

        long [] longs = bvp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", testValue, longs[0]);

        Long [] longObjs = bvp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", testValue, longObjs[0].longValue());
    }

    public void testIntToDouble()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        int testValue = 123;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "123", bvp.getString("foo"));
        assertEquals("Wrong double value", (double) testValue, bvp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", (double) testValue, bvp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = bvp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = bvp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
    }

    public void testLongToDouble()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        long testValue = 9223372036854775807l;

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", "9223372036854775807", bvp.getString("foo"));
        assertEquals("Wrong double value", (double) testValue, bvp.getDouble("foo"), 0.001);
        assertEquals("Wrong Double value", (double) testValue, bvp.getDoubleObject("foo").doubleValue(), 0.001);

        double [] doubles = bvp.getDoubles("foo");
        assertEquals("Wrong Array Size", 1, doubles.length);

        assertEquals("Wrong double array value", testValue, doubles[0], 0.001);

        Double [] doubleObjs = bvp.getDoubleObjects("foo");
        assertEquals("Wrong Array Size", 1, doubleObjs.length);

        assertEquals("Wrong Double array value", testValue, doubleObjs[0].doubleValue(), 0.001);
    }

    public void testStringAdd()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String testValue = "the quick brown fox";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", testValue, bvp.getString("foo"));

        String [] Strings = bvp.getStrings("foo");
        assertEquals("Wrong Array Size", 1, Strings.length);

        assertEquals("Wrong String array value", testValue, Strings[0]);
    }

    public void testStringToInt()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String testValue = "123456";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", testValue, bvp.getString("foo"));

        assertEquals("Wrong int value", Integer.parseInt(testValue), bvp.getInt("foo"));
        assertEquals("Wrong Int value", Integer.valueOf(testValue).intValue(), bvp.getIntObject("foo").intValue());

        int [] ints = bvp.getInts("foo");
        assertEquals("Wrong Array Size", 1, ints.length);

        assertEquals("Wrong int array value", Integer.parseInt(testValue), ints[0]);

        Integer [] intObjs = bvp.getIntObjects("foo");
        assertEquals("Wrong Array Size", 1, intObjs.length);

        assertEquals("Wrong Int array value", Integer.valueOf(testValue).intValue(), intObjs[0].intValue());
    }

    public void testStringToLong()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String testValue = "123456";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", testValue, bvp.getString("foo"));

        assertEquals("Wrong long value", Long.parseLong(testValue), bvp.getLong("foo"));
        assertEquals("Wrong Long value", Long.valueOf(testValue).longValue(), bvp.getLongObject("foo").longValue());

        long [] longs = bvp.getLongs("foo");
        assertEquals("Wrong Array Size", 1, longs.length);

        assertEquals("Wrong long array value", Long.parseLong(testValue), longs[0]);

        Long [] longObjs = bvp.getLongObjects("foo");
        assertEquals("Wrong Array Size", 1, longObjs.length);

        assertEquals("Wrong Long array value", Long.valueOf(testValue).longValue(), longObjs[0].longValue());
    }

    public void testStringArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        String [] testValue = new String [] {
            "foo", "bar", "baz"
        };

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        String [] res = bvp.getStrings("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getString("foo"));

        bvp.add("foo", "xxx");

        res = bvp.getStrings("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], "xxx");

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getString("foo"));
    }

    public void testRemove()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String testValue = "the quick brown fox";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", testValue, bvp.getString("foo"));

        bvp.remove("foo");

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        assertNull(bvp.getString("foo"));
    }
        
    public void testRemoveArray()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String testValue = "the quick brown fox";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        assertEquals("Wrong string value", testValue, bvp.getString("foo"));

        String [] res = bvp.getStrings("foo");

        assertEquals("Wrong number of elements", 2, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue);
        }

        bvp.remove("foo");

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        assertNull(bvp.getString("foo"));
    }

    public void testContainsKey()
    {
        BaseValueParser bvp = new BaseValueParser();

        bvp.add("foo", "bar");
        bvp.add("bar", new String [] { "foo", "bar" });

        assertTrue(bvp.containsKey("foo"));
        assertTrue(bvp.containsKey("bar"));
        assertFalse(bvp.containsKey("baz"));
    }

    public void testDateSelector()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        assertFalse(bvp.containsDateSelectorKeys("foo"));

        bvp.add("foo" + DateSelector.DAY_SUFFIX, "1");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());
        assertFalse(bvp.containsDateSelectorKeys("foo"));

        bvp.add("foo" + DateSelector.MONTH_SUFFIX, "1");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
        assertFalse(bvp.containsDateSelectorKeys("foo"));

        bvp.add("foo" + DateSelector.YEAR_SUFFIX, "2005");

        assertEquals("Wrong number of keys", 3, bvp.keySet().size());
        assertTrue(bvp.containsDateSelectorKeys("foo"));
    }

    public void testTimeSelector()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        assertFalse(bvp.containsTimeSelectorKeys("foo"));

        bvp.add("foo" + TimeSelector.HOUR_SUFFIX, "22");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());
        assertFalse(bvp.containsTimeSelectorKeys("foo"));

        bvp.add("foo" + TimeSelector.MINUTE_SUFFIX, "58");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
        assertFalse(bvp.containsTimeSelectorKeys("foo"));

        bvp.add("foo" + TimeSelector.SECOND_SUFFIX, "0");

        assertEquals("Wrong number of keys", 3, bvp.keySet().size());
        assertTrue(bvp.containsTimeSelectorKeys("foo"));
    }

    public void testBooleanObject()
    {
        BaseValueParser bvp = new BaseValueParser();

        bvp.add("t1", "true");
        bvp.add("t2", "yes");
        bvp.add("t3", "on");
        bvp.add("t4", "1");
        bvp.add("t5", 1);

        bvp.add("f1", "false");
        bvp.add("f2", "no");
        bvp.add("f3", "off");
        bvp.add("f4", "0");
        bvp.add("f5", 0);

        bvp.add("e1", "nix");
        bvp.add("e2", "weg");
        bvp.add("e3", 200);
        bvp.add("e4", -2.5);

        assertEquals("Value is not true", Boolean.TRUE, bvp.getBooleanObject("t1"));
        assertEquals("Value is not true", Boolean.TRUE, bvp.getBooleanObject("t2"));
        assertEquals("Value is not true", Boolean.TRUE, bvp.getBooleanObject("t3"));
        assertEquals("Value is not true", Boolean.TRUE, bvp.getBooleanObject("t4"));
        assertEquals("Value is not true", Boolean.TRUE, bvp.getBooleanObject("t5"));

        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f1"));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f2"));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f3"));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f4"));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f5"));

        assertNull(bvp.getBooleanObject("e1"));
        assertNull(bvp.getBooleanObject("e2"));
        assertNull(bvp.getBooleanObject("e3"));
        assertNull(bvp.getBooleanObject("e4"));

        assertNull(bvp.getBooleanObject("does-not-exist"));
    }

    public void testBoolDefault()
    {
        BaseValueParser bvp = new BaseValueParser();

        bvp.add("t1", "true");
        bvp.add("f1", "false");

        assertTrue(bvp.getBoolean("t1"));
        assertFalse(bvp.getBoolean("f1"));

        assertFalse(bvp.getBoolean("does not exist"));

        assertTrue(bvp.getBoolean("t1", false));
        assertFalse(bvp.getBoolean("f1", true));

        assertFalse(bvp.getBoolean("does not exist", false));
        assertTrue(bvp.getBoolean("does not exist", true));
    }

    public void testBooleanDefault()
    {
        BaseValueParser bvp = new BaseValueParser();

        bvp.add("t1", "true");
        bvp.add("f1", "false");

        assertEquals("Value is not true",  Boolean.TRUE, bvp.getBooleanObject("t1"));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f1"));

        assertNull(bvp.getBooleanObject("does not exist"));

        assertEquals("Value is not true",  Boolean.TRUE, bvp.getBooleanObject("t1", Boolean.FALSE));
        assertEquals("Value is not true",  Boolean.TRUE, bvp.getBooleanObject("t1", null));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f1", Boolean.TRUE));
        assertEquals("Value is not false", Boolean.FALSE, bvp.getBooleanObject("f1", null));

        assertNull(bvp.getBooleanObject("does not exist", null));
    }

    public void testDoubleArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        double [] testValue = {
            1.0, 2.0, 3.0
        };

        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo", testValue[i]);

            String [] res = bvp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        double [] res = bvp.getDoubles("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001);
        }

        Double [] resObj = bvp.getDoubleObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].doubleValue(), testValue[i], 0.001);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getDoubleObject("foo").doubleValue(), 0.001);

        bvp.add("foo", 4.0);

        res = bvp.getDoubles("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001);
        }

        assertEquals(res[3], 4.0, 0.001);

        resObj = bvp.getDoubleObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].doubleValue(), testValue[i], 0.001);
        }

        assertEquals(resObj[3].doubleValue(), 4.0, 0.001);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getDouble("foo"), 0.001);
    }

    public void testFloatArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        float [] testValue = {
            1.0f, 2.0f, 3.0f
        };

        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo", testValue[i]);

            String [] res = bvp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        float [] res = bvp.getFloats("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001f);
        }

        Float [] resObj = bvp.getFloatObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].floatValue(), testValue[i], 0.001f);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getFloatObject("foo").floatValue(), 0.001f);

        bvp.add("foo", 4.0f);

        res = bvp.getFloats("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i], 0.001f);
        }

        assertEquals(res[3], 4.0f, 0.001f);

        resObj = bvp.getFloatObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].floatValue(), testValue[i], 0.001f);
        }

        assertEquals(resObj[3].floatValue(), 4.0f, 0.001f);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getFloat("foo"), 0.001f);
    }

    public void testBigDecimalArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        long [] testValue = {
            12345678,87654321,1092837465,
        };

        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo", testValue[i]);

            String [] res = bvp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        BigDecimal [] res = bvp.getBigDecimals("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i].longValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getBigDecimal("foo").longValue());

        bvp.add("foo", 77777777);

        res = bvp.getBigDecimals("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i].longValue(), testValue[i], 0.001);
        }

        assertEquals(res[3].longValue(), 77777777);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getBigDecimal("foo").longValue());
    }

    public void testIntegerArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        int [] testValue = {
            1, 2, 3
        };

        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo", testValue[i]);

            String [] res = bvp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        int [] res = bvp.getInts("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        Integer [] resObj = bvp.getIntObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].intValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getIntObject("foo").intValue());

        bvp.add("foo", 4);

        res = bvp.getInts("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], 4);

        resObj = bvp.getIntObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].intValue(), testValue[i]);
        }

        assertEquals(resObj[3].intValue(), 4);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getInt("foo"));
    }

    public void testLongArray()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        long [] testValue = {
            1l, 2l, 3l
        };

        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo", testValue[i]);

            String [] res = bvp.getStrings("foo");
            assertEquals("Wrong number of elements", res.length, i + 1);
        }

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        long [] res = bvp.getLongs("foo");

        assertEquals("Wrong number of elements", 3, res.length);

        for (int i = 0; i < res.length; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        Long [] resObj = bvp.getLongObjects("foo");

        assertEquals("Wrong number of elements", 3, resObj.length);

        for (int i = 0; i < resObj.length; i++)
        {
            assertEquals("Wrong value", resObj[i].longValue(), testValue[i]);
        }

        assertEquals("Wrong element returned", testValue[0], bvp.getLongObject("foo").longValue());

        bvp.add("foo", 4);

        res = bvp.getLongs("foo");

        assertEquals("Wrong number of elements", 4, res.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", res[i], testValue[i]);
        }

        assertEquals(res[3], 4);

        resObj = bvp.getLongObjects("foo");

        assertEquals("Wrong number of elements", 4, resObj.length);

        for (int i = 0; i < 3; i++)
        {
            assertEquals("Wrong value", resObj[i].longValue(), testValue[i]);
        }

        assertEquals(resObj[3].longValue(), 4);

        // should append at the end.
        assertEquals("Wrong element returned", testValue[0], bvp.getLong("foo"));
    }

    public void testByteArray()
            throws Exception
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());
        
        String  testValue = "abcdefg";

        bvp.add("foo", testValue);

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        byte [] res = bvp.getBytes("foo");

        assertEquals("Wrong number of elements", 7, res.length);

        for (int i = 0; i < res.length; i++)
        {
            byte [] testByte = testValue.substring(i, i + 1).getBytes(bvp.getCharacterEncoding());
            assertEquals("More than one byte for a char!", 1, testByte.length);
            assertEquals("Wrong value", res[i], testByte[0]);
        }
    }

    public void testByte()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        String [] testValue = {
            "0", "127", "-1",
            "0", "-127", "100"
        };

        
        for (int i = 0; i < testValue.length; i++)
        {
            bvp.add("foo" + i, testValue[i]);
        }

        assertEquals("Wrong number of keys", 6, bvp.keySet().size());

        assertEquals("Wrong value", (byte) 0,    bvp.getByte("foo0"));
        assertEquals("Wrong value", (byte) 127,  bvp.getByte("foo1"));
        assertEquals("Wrong value", (byte) -1,   bvp.getByte("foo2"));
        assertEquals("Wrong value", (byte) 0,    bvp.getByte("foo3"));
        assertEquals("Wrong value", (byte) -127, bvp.getByte("foo4"));
        assertEquals("Wrong value", (byte) 100,  bvp.getByte("foo5"));

        assertEquals("Wrong value", new Byte((byte) 0),    bvp.getByteObject("foo0"));
        assertEquals("Wrong value", new Byte((byte) 127),  bvp.getByteObject("foo1"));
        assertEquals("Wrong value", new Byte((byte) -1),   bvp.getByteObject("foo2"));
        assertEquals("Wrong value", new Byte((byte) 0),    bvp.getByteObject("foo3"));
        assertEquals("Wrong value", new Byte((byte) -127), bvp.getByteObject("foo4"));
        assertEquals("Wrong value", new Byte((byte) 100),  bvp.getByteObject("foo5"));

    }

    public void testStringDefault()
    {
        BaseValueParser bvp = new BaseValueParser();
        
        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong value found", "bar", bvp.getString("foo", "xxx"));
        assertEquals("Wrong value found", "bar", bvp.getString("foo", null));

        assertEquals("Wrong value found", "baz", bvp.getString("does-not-exist", "baz"));
        assertNull(bvp.getString("does-not-exist", null));
    }

    public void testSetString()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
        
        bvp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());

        String [] res = bvp.getStrings("bar");
        assertEquals("Wrong number of values", 2, res.length);
        assertEquals("Wrong value found", "foo", res[0]);
        assertEquals("Wrong value found", "baz", res[1]);

        bvp.setString("bar", "xxx");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());

        res = bvp.getStrings("bar");
        assertEquals("Wrong number of values", 1, res.length);
        assertEquals("Wrong value found", "xxx", res[0]);
    }

    public void testSetStrings()
    {
        BaseValueParser bvp = new BaseValueParser();

        assertEquals("Wrong number of keys", 0, bvp.keySet().size());

        bvp.add("foo", "bar");

        assertEquals("Wrong number of keys", 1, bvp.keySet().size());

        bvp.add("bar", "foo");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());
        
        bvp.add("bar", "baz");

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());

        String [] res = bvp.getStrings("bar");
        assertEquals("Wrong number of values", 2, res.length);
        assertEquals("Wrong value found", "foo", res[0]);
        assertEquals("Wrong value found", "baz", res[1]);

        String [] newValues = new String [] { "aaa", "bbb", "ccc", "ddd" };

        bvp.setStrings("bar", newValues);

        assertEquals("Wrong number of keys", 2, bvp.keySet().size());

        res = bvp.getStrings("bar");
        assertEquals("Wrong number of values", newValues.length, res.length);

        for (int i = 0 ; i < newValues.length; i++)
        {
            assertEquals("Wrong value found", newValues[i], res[i]);
        }
    }

    public void testSetProperties()
            throws Exception
    {
        BaseValueParser bvp = new BaseValueParser();

        bvp.add("longvalue", 12345l);
        bvp.add("doublevalue", 2.0);
        bvp.add("intValue", 200);
        bvp.add("stringvalue", "foobar");
        bvp.add("booleanvalue", "true");

        PropertyBean bp = new PropertyBean();
        bp.setDoNotTouchValue("abcdef");

        bvp.setProperties(bp);

        assertEquals("Wrong value in bean", "abcdef", bp.getDoNotTouchValue());
        assertEquals("Wrong value in bean", "foobar", bp.getStringValue());
        assertEquals("Wrong value in bean", 200,      bp.getIntValue());
        assertEquals("Wrong value in bean", 2.0,      bp.getDoubleValue(), 0.001);
        assertEquals("Wrong value in bean", 12345l,   bp.getLongValue());
        assertEquals("Wrong value in bean", Boolean.TRUE, bp.getBooleanValue());
    }
}

