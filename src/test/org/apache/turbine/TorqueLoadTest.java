package org.apache.turbine;

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

import org.apache.torque.Torque;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Can we load and run Torque standalone, from Component and from
 * AvalonComponent Service?
 *
 * @version $Id$
 */
public class TorqueLoadTest
        extends BaseTestCase
{
    public TorqueLoadTest(String name)
            throws Exception
    {
        super(name);
    }

    public static Test suite()
    {
        return new TestSuite(TorqueLoadTest.class);
    }

    /**
     * An uninitialized Torque must not be initialized.
     */
    public void testTorqueNonInit()
            throws Exception
    {
        assertFalse("Torque should not be initialized!", Torque.isInit());
    }

    /**
     * Load Torque from a given config file.
     */
    public void testTorqueManualInit()
            throws Exception
    {
        assertFalse("Torque should not be initialized!", Torque.isInit());
        Torque.init("conf/test/TorqueTest.properties");
        assertTrue("Torque must be initialized!", Torque.isInit());
        Torque.shutdown();
        // Uncomment once we get a torque 3.1 release post alpha-2
        // Everything up to alpha-2 does not shut down Torque properly.
        // assertFalse("Torque did not shut down properly!", Torque.isInit());
    }

    /**
     * Load Torque with the ComponentService
     */
    public void testTorqueComponentServiceInit()
            throws Exception
    {
        assertFalse("Torque should not be initialized!", Torque.isInit());

        TurbineConfig tc = new TurbineConfig(".", "/conf/test/TurbineComponentService.properties");
        try
        {
            tc.initialize();
            assertTrue("Torque must be initialized!", Torque.isInit());
        }
        catch (Exception e)
        {
            throw e;
        }
        finally
        {
            tc.dispose();
        }
        // Uncomment once we get a torque 3.1 release post alpha-2
        // Everything up to alpha-2 does not shut down Torque properly.
        // assertFalse("Torque did not shut down properly!", Torque.isInit());
    }            

    private AvalonComponentService getService()
    {
        return (AvalonComponentService) TurbineServices.getInstance()
                .getService(AvalonComponentService.SERVICE_NAME);
    }

    // Uncomment once we get a torque 3.1 release post alpha-2
    // The current version of Torque doesn't run right with the AvalonComponentService
    //
    //    /**
    //     * Load Torque with the AvalonComponentService
    //     */
    //     public void testTorqueAvalonServiceInit()
    //             throws Exception
    //     {
    //         assertFalse("Torque should not be initialized!", Torque.isInit());
    
    //         TurbineConfig tc = new TurbineConfig(".", "/conf/test/TurbineAvalonService.properties");
    
    //         try
    //         {
    //             tc.initialize();
    //             assertTrue("Torque must be initialized!", Torque.isInit());
    
    //             TorqueComponent toc = 
    //                     (TorqueComponent) getService().lookup("org.apache.torque.avalon.Torque");
    //             assertTrue("TorqueComponent must be initialized!", toc.isInit());
    
    //             getService().release(toc);
    //         }
    //         catch (Exception e)
    //         {
    //             throw e;
    //         }
    //         finally
    //         {
    //             tc.dispose();
    //         }
    //         assertFalse("Torque did not shut down properly!", Torque.isInit());
    //     }            
}

