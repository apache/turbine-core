package org.apache.turbine.services.crypto.provider;

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

import org.apache.turbine.services.crypto.CryptoAlgorithm;

/**
 * This is a dummy for "cleartext" encryption. It goes through
 * the notions of the CryptoAlgorithm interface but actually does
 * nothing. It can be used as a replacement for the "encrypt = no"
 * setting in the TR.props.
 *
 * Can be used as the default crypto algorithm
 *
 * @version $Id$
 */
public class ClearCrypt
        implements CryptoAlgorithm
{
    /**
     * C'tor
     */
    public ClearCrypt()
    {
    }

    /**
     * This class never uses an algorithm, so this is
     * just a dummy.
     *
     * @param cipher    Cipher (ignored)
     */
    public void setCipher(String cipher)
    {
        /* dummy */
    }

    /**
     * This class never uses a seed, so this is
     * just a dummy.
     *
     * @param seed        Seed (ignored)
     */
    public void setSeed(String seed)
    {
        /* dummy */
    }

    /**
     * encrypt the supplied string with the requested cipher
     *
     * @param value       The value to be encrypted
     * @return The encrypted value
     * @throws Exception An Exception of the underlying implementation.
     */
    public String encrypt(String value)
            throws Exception
    {
        /*
         * Ultra-clever implementation. ;-)
         */

        return value;
    }
    
}
