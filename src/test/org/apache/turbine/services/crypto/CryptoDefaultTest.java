package org.apache.turbine.services.crypto;

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

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.factory.FactoryService;
import org.apache.turbine.services.factory.TurbineFactoryService;
import org.apache.turbine.test.BaseTestCase;

public class CryptoDefaultTest
    extends BaseTestCase
{
    private static final String PREFIX = "services." +
        CryptoService.SERVICE_NAME + '.';

    private static final String preDefinedInput = "Oeltanks";

    public CryptoDefaultTest(String name)
            throws Exception
    {
        super(name);

        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname",
                        TurbineCryptoService.class.getName());

        /* No providers configured. Should be "java" then */

        /* Ugh */

        cfg.setProperty("services." + FactoryService.SERVICE_NAME + ".classname",
                        TurbineFactoryService.class.getName());

        serviceManager.setConfiguration(cfg);

        try
        {
            serviceManager.init();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public static Test suite()
    {
        return new TestSuite(CryptoDefaultTest.class);
    }

    public void testMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("default");

            ca.setCipher("MD5");

            String output = ca.encrypt(preDefinedInput);

            assertEquals("MD5 Encryption failed ",
                         preDefinedResult,
                         output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testSha1()
    {
        String preDefinedResult  = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("default");

            ca.setCipher("SHA1");

            String output = ca.encrypt(preDefinedInput);

            assertEquals("SHA1 Encryption failed ",
                         preDefinedResult,
                         output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
}
