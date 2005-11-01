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

import java.util.Locale;

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
 * @author <a href="mailto:sgoeschl@apache.org">Siegfried Goeschl</a>
 * @version $Id$
 */
public class LoadingComponentsTest extends BaseTestCase
{
    private static TurbineConfig tc = null;
    public LoadingComponentsTest(String name) throws Exception
    {
        super(name);
    }
    
    /**
     * Test to load a couple of Avalon services directly by the
     * AvalonComponentService.
     * 
     * @throws Exception loading failed
     */
    public void testLoadingByAvalonComponentService() throws Exception
    {
        AvalonComponentService avalonComponentService =
            (AvalonComponentService) TurbineServices.getInstance().getService(
                    AvalonComponentService.SERVICE_NAME);
        
        assertNotNull(avalonComponentService);
        
        DefaultGlobalCacheService dgcs = (DefaultGlobalCacheService)avalonComponentService.lookup(DefaultGlobalCacheService.ROLE);
        assertNotNull(dgcs);        
        CryptoService cs = (CryptoService)avalonComponentService.lookup(CryptoService.ROLE);
        assertNotNull(cs);
        LocalizationService ls = (LocalizationService)avalonComponentService.lookup(LocalizationService.ROLE);
        assertNotNull(ls);
        IntakeService intake = (IntakeService)avalonComponentService.lookup(IntakeService.ROLE);
        assertNotNull(intake);
        FactoryService fs = (FactoryService)avalonComponentService.lookup(FactoryService.ROLE);
        assertNotNull(fs);
        MimeTypeService mimetype = (MimeTypeService)avalonComponentService.lookup(MimeTypeService.ROLE);
        assertNotNull(mimetype);
    }

    /**
     * Test to load a couple of Avalon services by using the
     * TurbineServices which delegate the service retrieval to
     * the AvalonComponentService
     * 
     * @throws Exception loading failed
     */
    public void testLoadingByTurbineServices() throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();

        DefaultGlobalCacheService dgcs = (DefaultGlobalCacheService)serviceManager.getService(DefaultGlobalCacheService.ROLE);
        assertNotNull(dgcs);        
        CryptoService cs = (CryptoService)serviceManager.getService(CryptoService.ROLE);
        assertNotNull(cs);
        LocalizationService ls = (LocalizationService)serviceManager.getService(LocalizationService.ROLE);
        assertNotNull(ls);
        IntakeService intake = (IntakeService)serviceManager.getService(IntakeService.ROLE);
        assertNotNull(intake);
        FactoryService fs = (FactoryService)serviceManager.getService(FactoryService.ROLE);
        assertNotNull(fs);
        MimeTypeService mimetype = (MimeTypeService)serviceManager.getService(MimeTypeService.ROLE);
        assertNotNull(mimetype);
    }

    /**
     * Lookup up an unknown servie
     * @throws Exception
     */
    public void testLookupUnknownService() throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        
        try
        {
            serviceManager.getService("foo");
            fail("We expect an InstantiationException");
        }
        catch (InstantiationException e)
        {
            // that'w what we expect
            return;
        }
        catch (Throwable t)
        {
            fail("We expect an InstantiationException");
        }                             
    }

    /**
     * Shutdown the AvalonComponentService where the MimeTypeService
     * resides and lookup the MimeTypeService. This should trigger
     * a late initialization of AvalonComponentService and returns
     * a fully functional MimeTypeService.
     */
    public void testAvalonComponentServiceShutdown() throws Exception
    {
        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.shutdownService(AvalonComponentService.SERVICE_NAME);
        
        MimeTypeService mimeTypeService = (MimeTypeService) serviceManager.getService(MimeTypeService.class.getName());
        assertNotNull(mimeTypeService);
        
        Locale locale = new Locale("en", "US");
        String s = mimeTypeService.getCharSet(locale);
        assertEquals("ISO-8859-1", s);
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
