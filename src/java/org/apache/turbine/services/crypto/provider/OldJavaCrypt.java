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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import java.security.MessageDigest;

import javax.mail.internet.MimeUtility;

import org.apache.turbine.services.crypto.CryptoAlgorithm;

/**
 * This is the Message Digest Implementation of Turbine 2.1. It does
 * not pad the Base64 encryption of the Message Digests correctly but
 * truncates after 20 chars. This leads to interoperability problems
 * if you want to use e.g. database columns between two languages.
 *
 * If you upgrade an application from Turbine 2.1 and have already used
 * the Security Service with encrypted passwords and no way to rebuild
 * your databases, use this provider. It is bug-compatible.
 *
 * DO NOT USE THIS PROVIDER FOR ANY NEW APPLICATION!
 *
 * Nevertheless it can be used as the default crypto algorithm .
 *
 * @version $Id$
 */
public class OldJavaCrypt
        implements CryptoAlgorithm
{

    /** The default cipher */
    public static final String DEFAULT_CIPHER = "SHA";

    /** The cipher to use for encryption */
    private String cipher = null;

    /**
     * C'tor
     */
    public OldJavaCrypt()
    {
        this.cipher = DEFAULT_CIPHER;
    }

    /**
     * Setting the actual cipher requested. If not
     * called, then the default cipher (SHA) is used.
     *
     * This will never throw an error even if there is no
     * provider for this cipher. The error will be thrown
     * by encrypt() (Fixme?)
     *
     * @param cipher     The cipher to use.
     */
    public void setCipher(String cipher)
    {
        this.cipher = cipher;
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
     * Encrypt the supplied string with the requested cipher
     *
     * @param value       The value to be encrypted
     * @return The encrypted value
     * @throws Exception An Exception of the underlying implementation.
     */
    public String encrypt(String value)
            throws Exception
    {
        MessageDigest md = MessageDigest.getInstance(cipher);

        // We need to use unicode here, to be independent of platform's
        // default encoding. Thanks to SGawin for spotting this.

        byte[] digest = md.digest(value.getBytes("UTF-8"));
        ByteArrayOutputStream bas =
                new ByteArrayOutputStream(digest.length + digest.length / 3 + 1);
        OutputStream encodedStream = MimeUtility.encode(bas, "base64");
        encodedStream.write(digest);
        return bas.toString();
    }
    
}
