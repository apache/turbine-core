package org.apache.turbine.services.assemblerbroker.util.java;


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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.GenericLoader;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;

/**
 * A screen factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @param <T> the specialized assembler type
 */
public abstract class JavaBaseFactory<T extends Assembler>
    implements AssemblerFactory<T>
{
    /** A vector of packages. */
    private static List<String> packages = GenericLoader.getPackages();

    /** Logging */
    protected Logger log = LogManager.getLogger(this.getClass());

    /**
     * A cache for previously obtained Class instances, which we keep in order
     * to reduce the Class.forName() overhead (which can be sizable).
     */
    private final ConcurrentHashMap<String, Class<T>> classCache = new ConcurrentHashMap<String, Class<T>>();

    /**
     * Get an Assembler.
     *
     * @param packageName java package name
     * @param name name of the requested Assembler
     * @return an Assembler
     */
    @SuppressWarnings("unchecked")
    public T getAssembler(String packageName, String name)
    {
        T assembler = null;

        log.debug("Class Fragment is {}", name);

        if (StringUtils.isNotEmpty(name))
        {
            for (String p : packages)
            {
                StringBuilder sb = new StringBuilder();

                sb.append(p).append('.').append(packageName).append('.').append(name);
                String className = sb.toString();

                log.debug("Trying {}", className);

                try
                {
                    Class<T> servClass = classCache.get(className);
                    if (servClass == null)
                    {
                        servClass = (Class<T>) Class.forName(className);
                        Class<T> _servClass = classCache.putIfAbsent(className, servClass);
                        if (_servClass != null)
                        {
                            servClass = _servClass;
                        }
                    }
                    assembler = servClass.newInstance();
                    break; // for()
                }
                catch (ClassNotFoundException cnfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug("{}: Not found", className);
                }
                catch (NoClassDefFoundError ncdfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug("{}: No Class Definition found", className);
                }
                // With ClassCastException, InstantiationException we hit big problems
                catch (ClassCastException | InstantiationException | IllegalAccessException e)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error("Could not load {}", className, e);
                    break; // for()
                }
            }
        }

        log.debug("Returning: {}", assembler);

        return assembler;
    }

    /**
     * Get the loader for this type of assembler
     *
     * @return a Loader
     */
    @Override
    public abstract Loader<T> getLoader();

    /**
     * Get the size of a possibly configured cache
     *
     * @return the size of the cache in bytes
     */
    @Override
    public int getCacheSize()
    {
        return getLoader().getCacheSize();
    }
}
