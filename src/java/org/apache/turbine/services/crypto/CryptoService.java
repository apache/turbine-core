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


import java.security.NoSuchAlgorithmException;

import org.apache.turbine.services.Service;

/**
 * The Crypto Service manages the availability of various crypto
 * sources. It provides a consistent interface to things like the
 * various java.security Message Digest stuff or the Unix Crypt
 * algorithm.
 *
 * It contains no actual crypto code so it should be fine to import/export
 * everywhere.
 *
 * @deprecated Use the Fulcrum Crypto component instead.
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 */

public interface CryptoService
        extends Service
{
    /** The name of the service */
    String SERVICE_NAME = "CryptoService";

    /**
     * Returns a CryptoAlgorithm Object which represents the requested
     * crypto algorithm.
     *
     * @param algorithm      Name of the requested algorithm
     *
     * @return An Object representing the algorithm
     *
     * @throws NoSuchAlgorithmException  Requested algorithm is not available
     *
     */

    CryptoAlgorithm getCryptoAlgorithm(String algorithm)
            throws NoSuchAlgorithmException;
}
