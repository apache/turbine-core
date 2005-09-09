package org.apache.turbine.util.pool;

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

import junit.framework.TestSuite;

import org.apache.turbine.test.BaseTestCase;

/**
 * test whether the Default parameter parser returns its uploaded file items
 * in the keySet().
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class RecyclableSupportTest
        extends BaseTestCase
{
    public RecyclableSupportTest(String name)
            throws Exception
    {
        super(name);
    }

    public static TestSuite suite()
    {
        return new TestSuite(RecyclableSupportTest.class);
    }

    public void testRecyclableSupport()
    {
        RecyclableSupport rs = new RecyclableSupport();

        assertFalse(rs.isDisposed());

        rs.dispose();

        assertTrue(rs.isDisposed());

        rs.recycle();

        assertFalse(rs.isDisposed());
    }
}
