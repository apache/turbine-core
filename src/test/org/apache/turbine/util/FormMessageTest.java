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


import org.apache.turbine.test.BaseTestCase;

public class FormMessageTest
    extends BaseTestCase
{
    public FormMessageTest(String name)
            throws Exception
    {
        super(name);
    }

    public void testCreateFormMessage()
    {
        FormMessage fm = new FormMessage("mainForm","someField","A message!");
        String fmString = fm.toString();
        assertTrue("Make sure toString works",fmString.indexOf("someField")>-1);
        assertTrue("Make sure toString works",fmString.indexOf("A message")>-1);
        assertTrue("Make sure toString works",fmString.indexOf("mainForm")>-1);
    }

    public void testCreateFormMessageMultipleFields()
    {
        FormMessage fm = new FormMessage("mainForm","someField","A message!");
        fm.setFieldName("someOtherField");
        String fmString = fm.toString();
        assertTrue("Make sure toString works",fmString.indexOf("someField")>-1);
        assertTrue("Make sure toString works",fmString.indexOf("someOtherField")>-1);
        assertTrue("Make sure toString works",fmString.indexOf("A message")>-1);
        assertTrue("Make sure toString works",fmString.indexOf("mainForm")>-1);
    }
}
