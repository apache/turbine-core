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


/**
 * This interface describes the various Crypto Algorithms that are
 * handed out by the Crypto Service.
 *
 * @deprecated Use the Fulcrum Crypto component instead.
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface CryptoAlgorithm
{
    /**
     * Allows the user to set a salt value whenever the
     * algorithm is used. Setting a new salt should invalidate
     * all internal state of this object.
     * <p>
     * Algorithms that do not use a salt are allowed to ignore
     * this parameter.
     * <p>
     * Algorithms must be able to deal with the null value as salt.
     * They should treat it as "use a random salt".
     *
     * @param salt      The salt value
     *
     */

    void setSeed(String salt);

    /**
     * Performs the actual encryption.
     *
     * @param value       The value to be encrypted
     *
     * @return The encrypted value
     *
     * @throws Exception various errors from the underlying ciphers.
     *                   The caller should catch them and report accordingly.
     *
     */

    String encrypt(String value)
            throws Exception;

    /**
     * Algorithms that perform multiple ciphers get told
     * with setCipher, which cipher to use. This should be
     * called before any other method call.
     *
     * If called after any call to encrypt or setSeed, the
     * CryptoAlgorithm may choose to ignore this or to reset
     * and use the new cipher.
     *
     * If any other call is used before this, the algorithm
     * should use a default cipher and not throw an error.
     *
     * @param cipher    The cipher to use.
     *
     */

    void setCipher(String cipher);

}
