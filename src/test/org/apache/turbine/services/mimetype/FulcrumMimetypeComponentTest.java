package org.apache.turbine.services.mimetype;


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


import java.util.Locale;

import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
/**
 * Unit test for Accessing the Fulcrum Mimetype component within Turbine.
 * 
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class FulcrumMimetypeComponentTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public FulcrumMimetypeComponentTest(String name) throws Exception
    {
        super(name);
    }
    public void testComponent() throws Exception
    {
        AvalonComponentService acs =
            (AvalonComponentService) TurbineServices.getInstance().getService(
                    AvalonComponentService.SERVICE_NAME);
        MimeTypeService mimeTypeService = (MimeTypeService) acs.lookup(MimeTypeService.class.getName());

        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
    }

    public void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
    }
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
