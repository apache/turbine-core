package org.apache.turbine.services.avaloncomponent;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.test.TestComponent;

import junit.framework.TestCase;

/**
 * Simple test to make sure that the AvalonComponentService can be initialized.
 *
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public class TurbineAvalonComponentServiceTest extends TestCase
{
    private static final String PREFIX = "services." +
            AvalonComponentService.SERVICE_NAME + '.';

    /**
     * Initialize the unit test.  The AvalonComponentService will be configured
     * and initialized.
     *
     * @param name
     */
    public TurbineAvalonComponentServiceTest(String name)
    {
        super(name);
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname",
                TurbineAvalonComponentService.class.getName());

        // we want to configure the service to load test TEST configuration files
        cfg.setProperty(PREFIX + "componentConfiguration",
                "src/test/componentConfiguration.xml");
        cfg.setProperty(PREFIX + "componentRoles",
                "src/test/ComponentRoles.xml");
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
            AvalonComponentService cs = (AvalonComponentService)
                    TurbineServices.getInstance().getService(AvalonComponentService.SERVICE_NAME);

            TestComponent tc = (TestComponent) cs.lookup(TestComponent.ROLE);
            tc.test();
            cs.release(tc);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
