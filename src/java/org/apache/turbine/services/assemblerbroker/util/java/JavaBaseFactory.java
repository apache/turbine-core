package org.apache.turbine.services.assemblerbroker.util.java;

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

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.GenericLoader;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.apache.turbine.util.ObjectUtils;

/**
 * A screen factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 *
 * @version $Id$
 */
public abstract class JavaBaseFactory
    implements AssemblerFactory
{
    /** A vector of packages. */
    private static Vector packages =
        Turbine.getConfiguration().getVector(TurbineConstants.MODULE_PACKAGES);

    /** Logging */
    protected Log log = LogFactory.getLog(this.getClass());

    static
    {
        ObjectUtils.addOnce(packages, GenericLoader.getBasePackage());
    }

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
                StringBuffer className = new StringBuffer();

                className.append(it.next());
                className.append('.');
                className.append(packageName);
                className.append('.');
                className.append(name);

                log.debug("Trying " + className);

                try
                {
                    Class servClass = Class.forName(className.toString());
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
