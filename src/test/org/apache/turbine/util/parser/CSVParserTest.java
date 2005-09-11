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

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestSuite;

import org.apache.commons.lang.StringUtils;
import org.apache.turbine.test.BaseTurbineTest;

/**
 * test whether the Default parameter parser returns its uploaded file items
 * in the keySet().
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class CSVParserTest
        extends BaseTurbineTest
{
    public CSVParserTest(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static TestSuite suite()
    {
        return new TestSuite(CSVParserTest.class);
    }

    public void testSimpleReader()
    {
        String readLine = "0,1,2,3,4,5,6,7,8,9\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testMultipleLines()
    {
        String readLine =  "0,1,2,3,4,5,6,7,8,9\n"
                + "10,11,12,13,14,15,16,17,18,19\n"
                + "20,21,22,23,24,25,26,27,28,29\n"
                + "30,31,32,33,34,35,36,37,38,39\n"
                + "40,41,42,43,44,45,46,47,48,49\n"
                + "50,51,52,53,54,55,56,57,58,59\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        int cnt = 0;

        while(p.hasNext())
        {
            ValueParser vp = (ValueParser) p.next();

            for (int i = 0; i < fields.length; i ++)
            {
                assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
                assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
                values[i] += 10;
            }
            cnt++;
        }

        assertEquals("Wrong number of lines found", 6, cnt);
    }

    public void testPadding()
    {
        String [] strValues = {"   0"," 1 ","2      "," 3 "," 4"," 5 "," 6"," 7 ","8","  9            "};

        String readLine = StringUtils.join(strValues, ',') + "\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] intValues = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", strValues[i], vp.getString(fields[i]));
            assertEquals("Value does not match", intValues[i], vp.getInt(fields[i]));
        }
    }

    public void testMissing()
    {
        String [] strValues = { "100", "", "300", "", "500", "", "700", "", "900", ""};

        String readLine = StringUtils.join(strValues, ',') + "\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] intValues = { 100, 200, 300, 400, 500, 600, 700, 800, 900, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", strValues[i], vp.getString(fields[i]));

            if ((i % 2) == 0)
            {
                assertEquals("Value does not match", intValues[i], vp.getInt(fields[i]));
            }
        }
    }


    public void testEmpty()
    {
        String readLine = "\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", "", vp.getString(fields[i]));
        }
    }

    public void testEOF()
    {
        String readLine = "0,1,2,3,4,5,6,7,8,9";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testLessFields()
    {
        String readLine = "0,1,2,3,4,5,6,7,8,9";

        String [] fields = { "eins", "zwei", "drei" };
        int [] values = { 0, 1, 2, };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testLessFieldsMultipleLines()
    {
        String readLine =  "0,1,2,3,4,5,6,7,8,9\n"
                + "10,11,12,13,14,15,16,17,18,19\n"
                + "20,21,22,23,24,25,26,27,28,29\n"
                + "30,31,32,33,34,35,36,37,38,39\n"
                + "40,41,42,43,44,45,46,47,48,49\n"
                + "50,51,52,53,54,55,56,57,58,59\n";

        String [] fields = { "eins", "zwei", "drei",  };
        int [] values = { 0, 1, 2,  };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        int cnt = 0;

        while(p.hasNext())
        {
            ValueParser vp = (ValueParser) p.next();

            for (int i = 0; i < fields.length; i ++)
            {
                assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
                assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
                values[i] += 10;
            }
            cnt++;
        }

        assertEquals("Wrong number of lines found", 6, cnt);
    }

    public void testMoreFields()
    {
        String readLine = "0,1,2,3\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < values.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }

        for (int i = values.length; i < fields.length; i ++)
        {
            assertEquals("Value does not match", "", vp.getString(fields[i]));
        }
    }

    public void testMoreFieldsMultipleLines()
    {
        String readLine =  "0,1,2,3\n"
                + "10,11,12,13\n"
                + "20,21,22,23\n"
                + "30,31,32,33\n"
                + "40,41,42,43\n"
                + "50,51,52,53\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        int cnt = 0;

        while(p.hasNext())
        {
            ValueParser vp = (ValueParser) p.next();

            for (int i = 0; i < values.length; i ++)
            {
                assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
                assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
                values[i] += 10;
            }
            for (int i = values.length; i < fields.length; i ++)
            {
                assertEquals("Value does not match", "", vp.getString(fields[i]));
            }
            cnt++;
        }

        assertEquals("Wrong number of lines found", 6, cnt);
    }

    public void testQuotedReader()
    {
        String readLine = "\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\"\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testMissingTrailingQuoteEOL()
    {
        String readLine = "\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\n";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testMissingTrailingQuoteEOF()
    {
        String readLine = "\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9";

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };
        int [] values = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0 };

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", Integer.toString(values[i]), vp.getString(fields[i]));
            assertEquals("Value does not match", values[i], vp.getInt(fields[i]));
        }
    }

    public void testQuotedSeparator()
    {
        String [] strValues = { "0,1,2,3,4,5,6,7,8,9",
                                "1","2","3","4","5","6","7","8","9"};

        String [] fields = { "eins", "zwei", "drei", "vier", "fuenf", "sechs", "sieben", "acht", "neun", "null", };

        String readLine = "\"" + StringUtils.join(strValues, "\",\"") + "\"\n";

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());

        for (int i = 0; i < fields.length; i ++)
        {
            assertEquals("Value does not match", strValues[i], vp.getString(fields[i]));
        }
    }

    public void testQuotedQuote()
    {
        String [] strValues = { "\\\"", };

        String [] fields = { "eins",  };

        String readLine = "\"" + StringUtils.join(strValues, "\",\"") + "\"\n";

        Reader myReader = new StringReader(readLine);

        List fieldNames = Arrays.asList(fields);

        DataStreamParser p = new CSVParser(myReader, fieldNames, "UTF-8");

        assertTrue(p.hasNext());

        ValueParser vp = (ValueParser) p.next();

        assertFalse(p.hasNext());
        assertEquals("Value does not match", "\"", vp.getString(fields[0]));
    }

}

