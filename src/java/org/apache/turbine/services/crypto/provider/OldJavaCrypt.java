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
 * @deprecated Use the Fulcrum Crypto component instead.
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
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
     *
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
        ByteArrayOutputStream bas =
                new ByteArrayOutputStream(digest.length + digest.length / 3 + 1);
        OutputStream encodedStream = MimeUtility.encode(bas, "base64");
        encodedStream.write(digest);
        return bas.toString();
    }
}
