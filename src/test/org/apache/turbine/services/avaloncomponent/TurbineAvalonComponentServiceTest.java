package org.apache.turbine.services.avaloncomponent;


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


import static org.junit.Assert.*;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.test.TestComponent;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Simple test to make sure that the AvalonComponentService can be initialized.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineAvalonComponentServiceTest
        extends BaseTestCase
{
    private static final String PREFIX = "services." +
            AvalonComponentService.SERVICE_NAME + '.';

    /**
     * Initialize the unit test.  The AvalonComponentService will be configured
     * and initialized.*/


    /**
     * Use the service to get an instance of the TestComponent.  The test() method will be called to
     * simply write a log message.  The component will then be released.
     */
    @Test public void testGetAndUseTestComponent()
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();

        // decide here whether to start ECM or YAAFI
        //cfg.setProperty(PREFIX + "classname", TurbineAvalonComponentService.class.getName());
        cfg.setProperty(PREFIX + "classname", TurbineYaafiComponentService.class.getName());

        // we want to configure the service to load test TEST configuration files
        cfg.setProperty(PREFIX + "componentConfiguration",
                "src/test/componentConfiguration.xml");
        cfg.setProperty(PREFIX + "componentRoles",
                "src/test/componentRoles.xml");
        serviceManager.setConfiguration(cfg);

        try
        {
            serviceManager.init();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
        try
        {
            TestComponent tc = (TestComponent)TurbineServices.getInstance().getService(TestComponent.ROLE);
            assertTrue( tc != null );
            tc.test();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
