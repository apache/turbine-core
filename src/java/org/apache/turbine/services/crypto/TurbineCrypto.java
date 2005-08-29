package org.apache.turbine.services.crypto;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.security.NoSuchAlgorithmException;

import org.apache.turbine.services.TurbineServices;

/**
 * This is a facade class for the CryptoService.
 *
 * Here are the static methods that call related methods of the
 * various implementations of the Crypto Security Service, according
 * to the settings in TurbineResources.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class TurbineCrypto
{
    /**
     * Retrieves an implementation of the CryptoService, based on
     * the settings in TurbineResources.
     *
     * @return an implementation of the CryptoService
     */

    public static CryptoService getService()
    {
        return (CryptoService) TurbineServices.getInstance()
                .getService(CryptoService.SERVICE_NAME);
    }

    /**
     * Returns a CryptoAlgorithm Object which represents the requested
     * crypto algorithm.
     *
     * @param algo      Name of the requested algorithm
     * @return An Object representing the algorithm
     * @throws NoSuchAlgorithmException  Requested algorithm is not available
     */
    public static CryptoAlgorithm getCryptoAlgorithm(String algo)
            throws NoSuchAlgorithmException
    {
        return getService().getCryptoAlgorithm(algo);
    }

}
