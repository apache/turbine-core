package org.apache.turbine.services.template;

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
import junit.framework.TestSuite;

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.template.TemplateEngineService;
import org.apache.turbine.services.template.TemplateService;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Tests startup of the Template Service and registration of the
 * Velocity Service.
 *
 * @author <a href="hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class InitTest
    extends BaseTestCase
{
    private static TurbineConfig tc = null;
    private static TemplateService ts = null;

    public InitTest(String name)
            throws Exception
    {
        super(name);
        tc = new TurbineConfig(".", "/conf/test/TemplateService.properties");
        tc.initialize();

        ts = (TemplateService) TurbineServices.getInstance().getService(TemplateService.SERVICE_NAME);
    }

    public static Test suite()
    {
        return new TestSuite(InitTest.class);
    }

    public void testService()
        throws Exception
    {

        // Can we start the service?
        assertNotNull("Could not load Service!", ts);

        // Did we register the Velocity Service correctly for "vm" templates?
        VelocityService vs = (VelocityService) TurbineServices
            .getInstance().getService(VelocityService.SERVICE_NAME);

        TemplateEngineService tes = ts.getTemplateEngineService("foo.vm");

        assertEquals("Template Service did not return Velocity Service for .vm Templates", vs, tes);
    }
}
