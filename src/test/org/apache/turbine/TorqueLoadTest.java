package org.apache.turbine;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
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
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
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

