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
