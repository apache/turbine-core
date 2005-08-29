package org.apache.turbine.services.crypto.provider;

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

import org.apache.turbine.services.crypto.CryptoAlgorithm;

/**
 * Implements Standard Unix crypt(3) for use with the Crypto Service.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class UnixCrypt
        implements CryptoAlgorithm
{

    /** The seed to use */
    private String seed = null;

    /** standard Unix crypt chars (64) */
    private static final char[] SALT_CHARS =
            (("abcdefghijklmnopqrstuvwxyz"
            + "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./").toCharArray());

    /**
     * C'tor
     */
    public UnixCrypt()
    {
    }

    /**
     * This class never uses anything but
     * UnixCrypt, so it is just a dummy
     * (Fixme: Should we throw an exception if
     * something is requested that we don't support?
     *
     * @param cipher    Cipher (ignored)
     */
    public void setCipher(String cipher)
    {
        /* dummy */
    }

    /**
     * Setting the seed for the UnixCrypt
     * algorithm. If a null value is supplied,
     * or no seed is set, then a random seed is used.
     *
     * @param seed     The seed value to use.
     */
    public void setSeed(String seed)
    {
        this.seed = seed;
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
        if (seed == null)
        {
            java.util.Random randomGenerator = new java.util.Random();
            int numSaltChars = SALT_CHARS.length;

            seed = (new StringBuffer())
                    .append(SALT_CHARS[Math.abs(randomGenerator.nextInt())
                    % numSaltChars])
                    .append(SALT_CHARS[Math.abs(randomGenerator.nextInt())
                    % numSaltChars])
                    .toString();
        }

        /* UnixCrypt seems to be a really widespread name... */
        return new cryptix.tools.UnixCrypt(seed).crypt(value);
    }

}
