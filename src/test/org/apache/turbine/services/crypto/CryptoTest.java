package org.apache.turbine.services.crypto;

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
