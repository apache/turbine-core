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
import org.apache.turbine.services.crypto.provider.ClearCrypt;
import org.apache.turbine.services.crypto.provider.JavaCrypt;
import org.apache.turbine.services.crypto.provider.OldJavaCrypt;
import org.apache.turbine.services.crypto.provider.UnixCrypt;
import org.apache.turbine.services.factory.FactoryService;
import org.apache.turbine.services.factory.TurbineFactoryService;
import org.apache.turbine.test.BaseTestCase;

public class CryptoTest
    extends BaseTestCase
{
    private static final String PREFIX = "services." +
        CryptoService.SERVICE_NAME + '.';

    private static final String preDefinedInput = "Oeltanks";

    public CryptoTest( String name )
            throws Exception
    {
        super(name);

        ServiceManager serviceManager = TurbineServices.getInstance();
        serviceManager.setApplicationRoot(".");

        Configuration cfg = new BaseConfiguration();
        cfg.setProperty(PREFIX + "classname",
                        TurbineCryptoService.class.getName());

        cfg.setProperty(PREFIX + "algorithm.unix",
                        UnixCrypt.class.getName());
        cfg.setProperty(PREFIX + "algorithm.clear",
                        ClearCrypt.class.getName());
        cfg.setProperty(PREFIX + "algorithm.java",
                        JavaCrypt.class.getName());
        cfg.setProperty(PREFIX + "algorithm.oldjava",
                        OldJavaCrypt.class.getName());

        /* Do _not_ configure a default! We want to test explicitly */

        cfg.setProperty(PREFIX + "algorithm.default",
                        "none");

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
        return new TestSuite(CryptoTest.class);
    }

    public void testUnixCrypt()
    {
        String preDefinedSeed    = "z5";
        String preDefinedResult  = "z5EQaXpuu059c";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("unix");

            /*
             * Test predefined Seed
             */

            ca.setSeed(preDefinedSeed);

            String output = ca.encrypt(preDefinedInput);

            assertEquals("Encryption failed ",
                         preDefinedResult,
                         output);

            /*
             * Test random Seed
             *
             */

            ca.setSeed(null);

            String result = ca.encrypt(preDefinedInput);

            ca.setSeed(result);

            output = ca.encrypt(preDefinedInput);

            assertEquals("Encryption failed ",
                         output,
                         result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testClearCrypt()
    {
        String preDefinedResult  = "Oeltanks";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("clear");
            String output = ca.encrypt(preDefinedInput);

            assertEquals("Encryption failed ",
                         preDefinedResult,
                         output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testOldJavaCryptMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe2";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("oldjava");

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

    public void testOldJavaCryptSha1()
    {
        String preDefinedResult  = "uVDiJHaavRYX8oWt5ctkaa7j";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("oldjava");

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

    public void testJavaCryptMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("java");

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

    public void testJavaCryptSha1()
    {
        String preDefinedResult  = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";

        try
        {
            CryptoAlgorithm ca = TurbineCrypto.getCryptoAlgorithm("java");

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
