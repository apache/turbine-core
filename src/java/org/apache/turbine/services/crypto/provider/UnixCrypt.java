package org.apache.turbine.services.crypto.provider;

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
     *
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
     *
     * @return The encrypted value
     *
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
