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

import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.services.BaseService;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.crypto.provider.JavaCrypt;
import org.apache.turbine.services.factory.FactoryService;

/**
 * An implementation of CryptoService that uses either supplied crypto
 * Algorithms (provided in Turbine.Services.properties) or tries to get them via
 * the normal java mechanisms if this fails.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineCryptoService
        extends BaseService
        implements CryptoService
{
    /** Key Prefix for our algorithms */
    private static final String ALGORITHM = "algorithm";

    /** Default Key */
    private static final String DEFAULT_KEY = "default";

    /** Default Encryption Class */
    private static final String DEFAULT_CLASS =
            JavaCrypt.class.getName();

    /** Names of the registered algorithms and the wanted classes */
    private Hashtable algos = null;

    /** A factory to construct CryptoAlgorithm objects  */
    private FactoryService factoryService = null;

    /**
     * There is not much to initialize here. This runs
     * as early init method.
     *
     * @throws InitializationException Something went wrong in the init
     *         stage
     */
    public void init()
            throws InitializationException
    {
        this.algos = new Hashtable();

        /*
         * Set up default (Can be overridden by default key
         * from the properties
         */

        algos.put(DEFAULT_KEY, DEFAULT_CLASS);

        /* get the parts of the configuration relevant to us. */

        Configuration conf = getConfiguration().subset(ALGORITHM);

        if (conf != null)
        {
            for (Iterator it = conf.getKeys(); it.hasNext();)
            {
                String key = (String) it.next();
                String val = conf.getString(key);
                // Log.debug("Registered " + val
                //            + " for Crypto Algorithm " + key);
                algos.put(key, val);
            }
        }

        try
        {
            factoryService = (FactoryService) TurbineServices.getInstance().
                    getService(FactoryService.SERVICE_NAME);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                    "Failed to get a Factory object: ", e);
        }

        setInit(true);
    }

    /**
     * Returns a CryptoAlgorithm Object which represents the requested
     * crypto algorithm.
     *
     * @param algo      Name of the requested algorithm
     * @return An Object representing the algorithm
     * @throws NoSuchAlgorithmException  Requested algorithm is not available
     */
    public CryptoAlgorithm getCryptoAlgorithm(String algo)
            throws NoSuchAlgorithmException
    {
        String cryptoClass = (String) algos.get(algo);
        CryptoAlgorithm ca = null;

        if (cryptoClass == null)
        {
            cryptoClass = (String) algos.get(DEFAULT_KEY);
        }

        if (cryptoClass == null || cryptoClass.equalsIgnoreCase("none"))
        {
            throw new NoSuchAlgorithmException(
                    "TurbineCryptoService: No Algorithm for "
                    + algo + " found");
        }

        try
        {
            ca = (CryptoAlgorithm) factoryService.getInstance(cryptoClass);
        }
        catch (Exception e)
        {
            throw new NoSuchAlgorithmException(
                    "TurbineCryptoService: Error instantiating "
                    + cryptoClass + " for " + algo);
        }

        ca.setCipher(algo);

        return ca;
    }
    
}
