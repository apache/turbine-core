package org.apache.turbine.services.xmlrpc.util;

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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test class for FileHandler.
 *
 * @author <a href="mailto:jvanzyl@periapt.com">Jason van Zyl</a>
 * @version $Id$
 */
public class FileHandlerTest extends TestCase
{
    /**
     * Creates a new instance.
     */
    public FileHandlerTest(String name)
    {
        super(name);
    }

    /**
     * Creates a test suite for this class.
     *
     * @return A test suite for this class.
     */
    public static Test suite()
    {
        return new TestSuite(FileHandlerTest.class);
    }

    /**
     * Test to make sure that the FileHandler is working
     * correctly for files moving from the client to
     * the server.
     */
    public void testSend() throws Exception
    {
        FileTransfer.send("http://localhost:9000/RPC2",
                          "test.location",
                          "test.txt",
                          "test.location",
                          "test.send");
    }

    /**
     * Test to make sure that the FileHandler is working
     * correctly for files moving from the server to
     * the client.
     */
    public void testGet() throws Exception
    {
        FileTransfer.get("http://localhost:9000/RPC2",
                         "test.location",
                         "test.txt",
                         "test.location",
                         "test.get");
    }

    /**
     * Test to make sure that the FileHandler is working
     * correctly to remove files from the server.
     */
    public void testRemove() throws Exception
    {
        FileTransfer.remove("http://localhost:9000/RPC2",
                            "test.location",
                            "test.txt");
    }


}
