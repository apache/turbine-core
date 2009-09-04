package org.apache.turbine.services.osgi;


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


import java.lang.reflect.Method;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.BaseTestCase;
import org.osgi.framework.Bundle;


/**
 * Simple test to make sure that the OSGiComponentService can be initialized.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: TurbineOSGiComponentServiceTest.java 731294 2009-01-04 16:39:38Z tv $
 */
public class TurbineOSGiComponentServiceTest
        extends BaseTestCase
{
    private static final String PREFIX = "services." +
            OSGiComponentService.SERVICE_NAME + '.';

    /**
     * Initialize the unit test.  The OSGiComponentService will be configured
     * and initialized.

     *
     * @param name
     */
    public TurbineOSGiComponentServiceTest(String name)
            throws Exception
    {
        super(name);
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();

        cfg.setProperty(PREFIX + "classname", TurbineOSGiComponentService.class.getName());
        cfg.setProperty(PREFIX + "earlyInit", "true");

        // install a bundle
        cfg.setProperty(PREFIX + "installBundle.1",
                "file:src/test/org.apache.turbine.osgi.test.HelloWorldBundle_1.0.0.dev.jar");

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
    }

    /**
     * Use the service to get an instance of the TestComponent.  The test() method will be called to
     * simply write a log message.  The component will then be released.
     */
    public void testGetAndUseTestComponent()
    {
        try
        {
            OSGiComponentService osgi = 
                (OSGiComponentService)TurbineServices.getInstance().getService(OSGiComponentService.SERVICE_NAME);
            
            assertNotNull("Service should be present", osgi);
            
            Bundle[] bundles = osgi.getBundles();
            
            String myBundleName = "org.apache.turbine.osgi.test.HelloWorldBundle";
            boolean bundleFound = false;
            int bundleState = 0;
            
            for (int i = 0; i < bundles.length; i++)
            {
                System.out.println("Found bundle " + bundles[i].getSymbolicName() + " state " + bundles[i].getState());
                
                if (myBundleName.equals(bundles[i].getSymbolicName()))
                {
                    bundleFound = true;
                    bundleState = bundles[i].getState();
                }
            }
            
            assertTrue("Bundle should be installed", bundleFound);
            assertEquals("Bundle should have been activated", bundleState, Bundle.ACTIVE);
            
            Object service = TurbineServices.getInstance().getService("org.apache.turbine.osgi.test.helloworldbundle.HelloWorldService");
            
            assertNotNull("OSGI-implemented HelloWorldService should be present", service);
            
            Method sayHello = service.getClass().getMethod("sayHello", String.class);
            String response = (String)sayHello.invoke(service, "Joe");
            
            assertEquals("Service should return 'Hello Joe'", response, "Hello Joe");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
