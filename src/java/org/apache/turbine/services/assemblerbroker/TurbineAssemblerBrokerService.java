package org.apache.turbine.services.assemblerbroker;


/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.configuration2.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.AnnotationProcessor;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.apache.turbine.util.TurbineException;

/**
 * TurbineAssemblerBrokerService allows assemblers (like screens,
 * actions and layouts) to be loaded from one or more AssemblerFactory
 * classes.  AssemblerFactory classes are registered with this broker
 * by adding them to the TurbineResources.properties file.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbineAssemblerBrokerService
        extends TurbineBaseService
        implements AssemblerBrokerService
{
    /** Logging */
    private static Logger log
            = LogManager.getLogger(TurbineAssemblerBrokerService.class);

    /** A structure that holds the registered AssemblerFactories */
    private Map<Class<?>, List<?>> factories = null;

    /** A cache that holds the generated Assemblers */
    private ConcurrentMap<String, Assembler> assemblerCache = null;

    /** A cache that holds the Loaders */
    private ConcurrentMap<Class<?>, Loader<? extends Assembler>> loaderCache = null;

    /** Caching on/off */
    private boolean isCaching;

    /**
     * Get a list of AssemblerFactories of a certain type
     *
     * @param type type of Assembler
     *
     * @param <T> the type of the assembler
     *
     * @return list of AssemblerFactories
     */
    @SuppressWarnings("unchecked")
    private <T extends Assembler> List<AssemblerFactory<T>> getFactoryGroup(Class<T> type)
    {
        if (!factories.containsKey(type))
        {
            factories.put(type, new ArrayList<AssemblerFactory<T>>());
        }
        return (List<AssemblerFactory<T>>) factories.get(type);
    }

    /**
     * Utility method to register all factories for a given type.
     *
     * @param type type of Assembler
     * @throws TurbineException if the factory for the given type could not be registered
     */
    private void registerFactories(String type)
        throws TurbineException
    {
        List<Object> names = getConfiguration().getList(type);

        log.info("Registering {} {} factories.", Integer.valueOf(names.size()), type);

        for (Object name2 : names)
        {
            String factory = (String) name2;
            try
            {
                @SuppressWarnings("unchecked")
                AssemblerFactory<? extends Assembler> af =
                    (AssemblerFactory<? extends Assembler>) Class.forName(factory).getDeclaredConstructor().newInstance();
                registerFactory(af);
            }
            // these must be passed to the VM
            catch (ThreadDeath | OutOfMemoryError e)
            {
                throw e;
            }
            // when using Class.forName(), NoClassDefFoundErrors are likely
            // to happen (missing jar files)
            catch (Throwable t)
            {
                throw new TurbineException("Failed registering " + type
                        + " factory: " + factory, t);
            }
        }
    }

    /**
     * Initializes the AssemblerBroker and loads the AssemblerFactory
     * classes registered in TurbineResources.Properties.
     *
     * @throws InitializationException if problems occur while registering the factories
     */
    @Override
    public void init()
        throws InitializationException
    {
        factories = new HashMap<>();

        try
        {
            Configuration conf = getConfiguration();

            for (Iterator<String> i = conf.getKeys(); i.hasNext();)
            {
                String type = i.next();

                if (!"classname".equalsIgnoreCase(type))
                {
                    registerFactories(type);
                }
            }
        }
        catch (TurbineException e)
        {
            throw new InitializationException(
                    "AssemblerBrokerService failed to initialize", e);
        }

        isCaching = Turbine.getConfiguration()
            .getBoolean(TurbineConstants.MODULE_CACHE_KEY,
                        TurbineConstants.MODULE_CACHE_DEFAULT);

        if (isCaching)
        {
            int cacheSize = Turbine.getConfiguration()
                .getInt(TurbineConstants.MODULE_CACHE_SIZE_KEY,
                        TurbineConstants.MODULE_CACHE_SIZE_DEFAULT);

            assemblerCache = new ConcurrentHashMap<>(cacheSize);
            loaderCache = new ConcurrentHashMap<>(cacheSize);
        }

        setInit(true);
    }

    /**
     * Register a new AssemblerFactory
     *
     * @param factory factory to register
     *
     * @param <T> the type of the assembler
     *
     */
    @Override
    public <T extends Assembler> void registerFactory(AssemblerFactory<T> factory)
    {
        getFactoryGroup(factory.getManagedClass()).add(factory);
    }

    /**
     * Attempt to retrieve an Assembler of a given type with
     * a name.  Cycle through all the registered AssemblerFactory
     * classes of type and return the first non-null assembly
     * found.  If an assembly was not found return null.
     *
     * @param type type of Assembler
     * @param name name of the requested Assembler
     *
     * @param <T> the type of the assembler
     *
     * @return an Assembler or null
     * @throws TurbineException if the assembler could not be loaded
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Assembler> T getAssembler(Class<T> type, String name)
        throws TurbineException
    {
        String key = type + ":" + name;
        T assembler = null;

        if (isCaching && assemblerCache.containsKey(key))
        {
            assembler = (T) assemblerCache.get(key);
            log.debug("Found {} in the cache!", key);
        }
        else
        {
            log.debug("Loading {}", key);
            List<AssemblerFactory<T>> facs = getFactoryGroup(type);

            for (Iterator<AssemblerFactory<T>> it = facs.iterator(); (assembler == null) && it.hasNext();)
            {
                AssemblerFactory<T> fac = it.next();

                try
                {
                    assembler = fac.getAssembler(name);
                }
                catch (Exception e)
                {
                    throw new TurbineException("Failed to load an assembler for "
                                               + name + " from the "
                                               + type + " factory "
                                               + fac.getClass().getName(), e);
                }

                if (assembler != null)
                {
                    AnnotationProcessor.process(assembler);

                    if (isCaching)
                    {
                        T oldAssembler = (T) assemblerCache.putIfAbsent(key, assembler);
                        if (oldAssembler != null)
                        {
                            assembler = oldAssembler;
                        }
                    }
                }
            }
        }

        return assembler;
    }

    /**
     * Get a Loader for the given assembler type
     *
     * @param type The Type of the Assembler
     *
     * @param <T> the type of the assembler
     *
     * @return A Loader instance for the requested type
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Assembler> Loader<T> getLoader(Class<T> type)
    {
        Loader<T> loader = null;

        if (isCaching && loaderCache.containsKey(type))
        {
            loader = (Loader<T>) loaderCache.get(type);
            log.debug("Found {} loader in the cache!", type);
        }
        else
        {
            log.debug("Getting Loader for {}", type);
            List<AssemblerFactory<T>> facs = getFactoryGroup(type);

            for (Iterator<AssemblerFactory<T>> it = facs.iterator(); (loader == null) && it.hasNext();)
            {
                AssemblerFactory<T> fac = it.next();
                loader = fac.getLoader();
            }

            if (isCaching && loader != null)
            {
                loaderCache.put(type, loader);
            }
        }

        if (loader == null)
        {
            log.warn("Loader for {} is null.", type);
        }

        return loader;
    }
}
