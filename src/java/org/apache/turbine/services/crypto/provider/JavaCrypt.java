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


import java.security.MessageDigest;


import org.apache.commons.codec.binary.Base64;
import org.apache.turbine.services.crypto.CryptoAlgorithm;

/**
 * Implements the normal java.security.MessageDigest stream cipers.
 * Base64 strings returned by this provider are correctly padded to
 * multiples of four bytes. If you run into interoperability problems
 * with other languages, especially perl and the Digest::MD5 module,
 * note that the md5_base64 function from this package incorrectly drops
 * the pad bytes. Use the MIME::Base64 package instead.
 *
 * If you upgrade from Turbine 2.1 and suddently your old stored passwords
 * no longer work, please take a look at the OldJavaCrypt provider for
 * bug-to-bug compatibility.
 *
 * This provider can be used as the default crypto algorithm provider.
 *
 * @deprecated Use the Fulcrum Crypto component instead.
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class JavaCrypt
        implements CryptoAlgorithm
{

    /** The default cipher */
    public static final String DEFAULT_CIPHER = "SHA";

    /** The cipher to use for encryption */
    private String cipher = null;

    /**
     * C'tor
     *
     */

    public JavaCrypt()
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
     *
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
     *
     */

    public void setSeed(String seed)
    {
        /* dummy */
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
        MessageDigest md = MessageDigest.getInstance(cipher);

        // We need to use unicode here, to be independent of platform's
        // default encoding. Thanks to SGawin for spotting this.
        byte[] digest = md.digest(value.getBytes("UTF-8"));

        // Base64-encode the digest.
        byte[] encodedDigest = Base64.encodeBase64(digest);
        return (encodedDigest == null ? null : new String(encodedDigest));
    }
}
