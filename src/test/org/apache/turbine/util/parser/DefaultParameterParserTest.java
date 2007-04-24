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

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestSuite;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.turbine.test.BaseTurbineTest;

/**
 * test whether the Default parameter parser returns its uploaded file items
 * in the keySet().
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class DefaultParameterParserTest
        extends BaseTurbineTest
{
    public DefaultParameterParserTest(String name)
            throws Exception
    {
        super(name, "conf/test/TurbineResources.properties");
    }

    public static TestSuite suite()
    {
        return new TestSuite(DefaultParameterParserTest.class);
    }

    public void testFileItemsInKeySet()
    {
        ParameterParser pp = new DefaultParameterParser();
        DiskFileItemFactory factory = new DiskFileItemFactory(10240, null);

        assertEquals("keySet() is not empty!", 0, pp.keySet().size());

        FileItem test = factory.createItem("upload-field", "application/octet-stream", false, null);
        pp.add("upload-field", test);

        assertEquals("FileItem not found in keySet()!", 1, pp.keySet().size());

        Iterator it = pp.keySet().iterator();
        assertTrue(it.hasNext());

        String name = (String) it.next();
        assertEquals("Wrong name found", "upload-field", name);

        assertFalse(it.hasNext());

        pp.add("other-field", "foo");

        assertEquals("Wrong number of fields found ", 2, pp.getKeys().length);

        assertTrue(pp.containsKey("upload-field"));
        assertTrue(pp.containsKey("other-field"));

        assertFalse(pp.containsKey("missing-field"));
    }

    public void testToString() throws IOException
    {
        ParameterParser pp = new DefaultParameterParser();
        DiskFileItemFactory factory = new DiskFileItemFactory(10240, new File("."));

        FileItem test = factory.createItem("upload-field", "application/octet-stream", false, null);
        
        // Necessary to avoid a NullPointerException in toString()
        test.getOutputStream();
        
        pp.add("upload-field", test);

        assertTrue(pp.toString().startsWith("{upload-field=[name=null,"));
    }

    /**
     * This Test method checks the DefaultParameterParser which carries two Sets inside it.
     * The suggested problem (TRB-10) was that pp.keySet() returns both Keys, but
     * pp.getStrings("key") only checks for keys which are not FileItems.  This makes no
     * sense because a FileItem is not a String and would never be returned by
     * getStrings() anyway.  The test case is retained anyway since there are
     * outstanding issues in this area with respect to 2.4.
     *
     * @throws Exception
     */
    public void testAddPathInfo() throws Exception
    {
        ParameterParser pp = new DefaultParameterParser();
        FileItemFactory factory = new DiskFileItemFactory(10240, null);

        assertEquals("keySet() is not empty!", 0, pp.keySet().size());

        FileItem test = factory.createItem("upload-field", "application/octet-stream", false, null);
        pp.add("upload-field", test);

        assertEquals("FileItem not found in keySet()!", 1, pp.keySet().size());

        Iterator it = pp.keySet().iterator();
        assertTrue(it.hasNext());

        String name = (String) it.next();
        assertEquals("Wrong name found", "upload-field", name);

        assertFalse(it.hasNext());

        pp.add("other-field", "foo");

        assertEquals("Wrong number of fields found ", 2, pp.getKeys().length);

        assertTrue(pp.containsKey("upload-field"));
        assertTrue(pp.containsKey("other-field"));

        assertNull("The returned should be null because a FileItem is not a String", pp.getStrings("upload-field"));
        assertFalse(pp.containsKey("missing-field"));        
    }
}

