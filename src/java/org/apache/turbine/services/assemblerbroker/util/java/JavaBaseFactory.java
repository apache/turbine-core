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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.GenericLoader;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;

/**
 * A screen factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class JavaBaseFactory
    implements AssemblerFactory
{
    /** A vector of packages. */
    private static List packages = GenericLoader.getPackages();

    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());

    /**
     * A cache for previously obtained Class instances, which we keep in order
     * to reduce the Class.forName() overhead (which can be sizable).
     */
    private Map classCache = Collections.synchronizedMap(new HashMap());

    /**
     * Get an Assembler.
     *
     * @param packageName java package name
     * @param name name of the requested Assembler
     * @return an Assembler
     */
    public Assembler getAssembler(String packageName, String name)
    {
        Assembler assembler = null;

        log.debug("Class Fragment is " + name);

        if (StringUtils.isNotEmpty(name))
        {
            for (Iterator it = packages.iterator(); it.hasNext();)
            {
                StringBuffer sb = new StringBuffer();

                sb.append(it.next()).append('.').append(packageName).append('.').append(name);
                
                String className = sb.toString();

                log.debug("Trying " + className);

                try
                {
                    Class servClass = (Class) classCache.get(className);
                    if(servClass == null)
                    {
                        servClass = Class.forName(className.toString());
                        classCache.put(className, servClass);
                    }
                    assembler = (Assembler) servClass.newInstance();
                    break; // for()
                }
                catch (ClassNotFoundException cnfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug(className + ": Not found");
                }
                catch (NoClassDefFoundError ncdfe)
                {
                    // Do this so we loop through all the packages.
                    log.debug(className + ": No Class Definition found");
                }
                catch (ClassCastException cce)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error("Could not load "+className, cce);
                    break; // for()
                }
                catch (InstantiationException ine)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error("Could not load "+className, ine);
                    break; // for()
                }
                catch (IllegalAccessException ilae)
                {
                    // This means trouble!
                    // Alternatively we can throw this exception so
                    // that it will appear on the client browser
                    log.error("Could not load "+className, ilae);
                    break; // for()
                }
                // With ClassCastException, InstantiationException we hit big problems
            }
        }
        log.debug("Returning: " + assembler);

        return assembler;
    }
}
