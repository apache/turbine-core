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

import org.apache.fulcrum.crypto.CryptoAlgorithm;
import org.apache.fulcrum.crypto.CryptoService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.avaloncomponent.AvalonComponentService;
import org.apache.turbine.test.BaseTestCase;
import org.apache.turbine.util.TurbineConfig;

/**
 * Verifies the Fulcrum Crypto Service works properly in Turbine.
 * <br>
 *
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class CryptoRunningInECMTest extends BaseTestCase
{
    private static final String preDefinedInput = "Oeltanks";
    private static TurbineConfig tc = null;
    private CryptoService cryptoService;

    public CryptoRunningInECMTest(String name) throws Exception
    {
        super(name);

      
    }

    public void testMd5()
    {
        String preDefinedResult = "XSop0mncK19Ii2r2CUe29w==";

        try
        {
            CryptoAlgorithm ca =cryptoService.getCryptoAlgorithm("default");

            ca.setCipher("MD5");

            String output = ca.encrypt(preDefinedInput);

            assertEquals("MD5 Encryption failed ", preDefinedResult, output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }

    public void testSha1()
    {
        String preDefinedResult = "uVDiJHaavRYX8oWt5ctkaa7j1cw=";

        try
        {
            CryptoAlgorithm ca = cryptoService.getCryptoAlgorithm("default");

            ca.setCipher("SHA1");

            String output = ca.encrypt(preDefinedInput);

            assertEquals("SHA1 Encryption failed ", preDefinedResult, output);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail();
        }
    }
    public void setUp() throws Exception
    {
        tc =
            new TurbineConfig(
                ".",
                "/conf/test/TestFulcrumComponents.properties");
        tc.initialize();
        AvalonComponentService acs =
            (AvalonComponentService) TurbineServices.getInstance().getService(
                    AvalonComponentService.SERVICE_NAME);
        cryptoService = (CryptoService) acs.lookup(CryptoService.ROLE);
    }
    public void tearDown() throws Exception
    {
        if (tc != null)
        {
            tc.dispose();
        }
    }
}
