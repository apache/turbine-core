package org.apache.turbine.services;

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

import org.apache.fulcrum.cache.DefaultGlobalCacheService;
import org.apache.fulcrum.crypto.CryptoService;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.intake.IntakeService;
import org.apache.fulcrum.localization.LocalizationService;
import org.apache.fulcrum.mimetype.MimeTypeService;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;
/**
 * Unit test for verifing that we can load all the appropriate components from the
 * appropriate Container.  For now that is just ECM (AvalonComponentService)
 * but in the future with mixed containers there could be multiple.
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @version $Id$
 */
public class LoadingComponentsTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public LoadingComponentsTest(String name) throws Exception
    {
        super(name);
    }
    public void testLoading() throws Exception
    {
        AvalonComponentService ecm =
            (AvalonComponentService) TurbineServices.getInstance().getService(
                    AvalonComponentService.SERVICE_NAME);
        DefaultGlobalCacheService dgcs = (DefaultGlobalCacheService)ecm.lookup(DefaultGlobalCacheService.ROLE);
        assertNotNull(dgcs);
        
        CryptoService cs = (CryptoService)ecm.lookup(CryptoService.ROLE);
        assertNotNull(cs);
        LocalizationService ls = (LocalizationService)ecm.lookup(LocalizationService.ROLE);
        assertNotNull(ls);
        IntakeService intake = (IntakeService)ecm.lookup(IntakeService.ROLE);
        assertNotNull(intake);
        FactoryService fs = (FactoryService)ecm.lookup(FactoryService.ROLE);
        assertNotNull(fs);
        MimeTypeService mimetype = (MimeTypeService)ecm.lookup(MimeTypeService.ROLE);
        assertNotNull(mimetype);
    }
    public void setUp() throws Exception
    {
        tc = new TurbineConfig(".", "/conf/test/TestFulcrumComponents.properties");
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
