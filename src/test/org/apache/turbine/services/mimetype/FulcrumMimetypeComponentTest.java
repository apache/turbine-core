package org.apache.turbine.services.mimetype;

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

import java.util.Locale;

import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for Accessing the Fulcrum Mimetype component within Turbine.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:sgoeschl@apache.org">Siegfried Goeschl</a>
 * @version $Id$
 */
public class FulcrumMimetypeComponentTest extends BaseTestCase
{
    private static TurbineConfig tc = null;

    @Test public void testComponent() throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        MimeTypeService mimeTypeService = (MimeTypeService) serviceManager.getService(MimeTypeService.class.getName());

        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
    }

    @BeforeClass
    public static void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
    }
    @AfterClass
    public static void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
