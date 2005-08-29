package org.apache.turbine.util;

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
public class TSVParserTest
            extends ServletTestCase
{
    Turbine turbine = null;

    /**
     * Constructor for CSVParserTest.
     * @param arg0
     */
    public TSVParserTest(String name)
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
        /* Note: we are using the properties file from the cache test
         *  since we don't really need any specific property at this
         *  time.  Future tests may require a test case specific
         *  properties file to be used.:
         */
        config.setInitParameter("properties",
                                "/WEB-INF/conf/TurbineDefault.properties");
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
        return new TestSuite(TSVParserTest.class);
    }

    /**
     * Tests if you can leave field values empty
     */
    public void testEmptyFieldValues()
    {
        String values = "field1\tfield2\tfield3\tfield4\nvalue11\t\tvalue13\t\nvalue21\t\tvalue23\t";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        TSVParser parser = new TSVParser(reader);
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
        String values = "field1\tfield2\tfield3\tfield4\nvalue11\tvalue12\tvalue13\tvalue14\nvalue21\tvalue22\tvalue23\tvalue24";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        TSVParser parser = new TSVParser(reader);
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
        String values = "field1\t\tfield3\t\nvalue11\tvalue12\tvalue13\tvalue14\tvalue21\tvalue22\tvalue23\tvalue24";
        CharArrayReader reader = new CharArrayReader(values.toCharArray());
        TSVParser parser = new TSVParser(reader);
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
                        assertEquals("value" + currentRecord + currentField, vp.getString(DataStreamParser.EMPTYFIELDNAME + currentField));
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
}
