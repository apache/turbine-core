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

import java.io.CharArrayReader;
import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.cactus.ServletTestCase;
import org.apache.turbine.Turbine;

/**
 * Test the CSVParser.
 *
 * NOTE : I am assuming (as is in the code of DataStreamParser.java
 * that the values are reusing the same object for the values.
 * If this shouldn't be, we need to fix that in the code!.
 *
 * @author <a href="mailto:martin@mvdb.net">Martin van den Bemt</a>
 * @version $Id$
 */
public class CSVParserTest
            extends ServletTestCase
{

    Turbine turbine = null;

    /**
     * Constructor for CSVParserTest.
     * @param arg0
     */
    public CSVParserTest(String name)
    {
        super(name);
    }

    /**
     * This will setup an instance of turbine to use when testing
     * @exception if an exception occurs.
     */

    protected void setUp()
    throws Exception
    {
        super.setUp();

        config.setInitParameter("properties",
                                "/WEB-INF/conf/TurbineComplete.properties");
        turbine = new Turbine();
        turbine.init(config);
    }

    /**
     * Shut down our turbine servlet and let our parents clean up also.
     *
     * @exception Exception if an error occurs
     */
    protected void tearDown()
    throws Exception
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
        return new TestSuite(CSVParserTest.class);
    }

    /**
     * Tests if you can leave field values empty
     */
    public void testEmptyFieldValues()
    {
        String values = "field1,field2,field3,field4\nvalue11,,value13,\nvalue21,,value23,";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        CSVParser parser = new CSVParser(reader);
        StringBuffer sb = new StringBuffer();
        try
        {
            parser.readColumnNames();
            int currentRecord = 1;
            while (parser.hasNextRow())
            {
                ValueParser vp = parser.nextRow();
                int currentField = 1;
                while (currentField <= 4)
                {
                    if (currentField == 2 || currentField == 4)
                    {
                        assertNull(vp.getString("field" + currentField));
                    }
                    else
                    {
                        assertEquals("value" + currentRecord + currentField, vp.getString("field" + currentField));
                    }
                    currentField += 1;
                }
                currentRecord += 1;
            }
        }
        catch (IOException ioe)
        {
            fail("Unexpected exception in testcase occured : " + ioe.toString());
        }
    }

    /**
     * Tests if normal operation is still working
     */
    public void testNormalFieldValues()
    {
        String values = "field1,field2,field3,field4\nvalue11,value12,value13,value14\nvalue21,value22,value23,value24";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        CSVParser parser = new CSVParser(reader);
        StringBuffer sb = new StringBuffer();
        try
        {
            parser.readColumnNames();
            int currentRecord = 1;
            while (parser.hasNextRow())
            {
                ValueParser vp = parser.nextRow();
                int currentField = 1;
                while (currentField <= 4)
                {
                    assertEquals("value" + currentRecord + currentField, vp.getString("field" + currentField));
                    currentField += 1;
                }
                currentRecord += 1;
            }
        }
        catch (IOException ioe)
        {
            fail("Unexpected exception in testcase occured : " + ioe.toString());
        }
    }

    /**
     * Tests if some fields are empty, but the values exists..
     */
    public void testEmptyFieldNames()
    {
        String values = "field1,,field3,\nvalue11,value12,value13,value14\nvalue21,value22,value23,value24";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        CSVParser parser = new CSVParser(reader);
        StringBuffer sb = new StringBuffer();
        try
        {
            parser.readColumnNames();
            int currentRecord = 1;

            while (parser.hasNextRow())
            {
                ValueParser vp = parser.nextRow();
                int currentField = 1;
                while (currentField <= 4)
                {
                    if (currentField == 2 || currentField == 4)
                    {
                        assertEquals("value" + currentRecord + currentField,
                                     vp.getString(DataStreamParser.EMPTYFIELDNAME + currentField));
                    }
                    else
                    {
                        assertEquals("value" + currentRecord + currentField,
                                     vp.getString("field" + currentField));
                    }
                    currentField += 1;
                }
                currentRecord += 1;
            }
        }
        catch (IOException ioe)
        {
            fail("Unexpected exception in testcase occured : " + ioe.toString());
        }
    }
}
