package org.apache.turbine.modules;

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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.RunData;

/**
 * The purpose of this class is to allow one to load and execute Page
 * modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class PageLoader
    extends GenericLoader
    implements Loader
{
    /** Logging */
    private static Log log = LogFactory.getLog(PageLoader.class);

    /** The single instance of this class. */
    private static PageLoader instance =
        new PageLoader(Turbine.getConfiguration()
                       .getInt(TurbineConstants.PAGE_CACHE_SIZE_KEY,
                               TurbineConstants.PAGE_CACHE_SIZE_DEFAULT));

    /** The Assembler Broker Service */
    private static AssemblerBrokerService ab = TurbineAssemblerBroker.getService();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private PageLoader()
    {
        super();
    }

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private PageLoader(int i)
    {
        super(i);
    }

    /**
     * Adds an instance of an object into the hashtable.
     *
     * @param name Name of object.
     * @param page Page to be associated with name.
     */
    private void addInstance(String name, Page page)
    {
        if (cache())
        {
            this.put(name, (Page) page);
        }
    }

    /**
     * Attempts to load and execute the external page.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the page.
     * @exception Exception a generic exception.
     */
    public void exec(RunData data, String name)
            throws Exception
    {
        // Execute page
        getInstance(name).build(data);
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object. This is equal to getInstance but
     * returns an Assembler object and is needed to fulfil the Loader
     * interface.
     *
     * @param name Name of object instance.
     * @return A Screen with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Assembler getAssembler(String name)
        throws Exception
    {
        return getInstance(name);
    }

    /**
     * Pulls out an instance of the page by name.  Name is just the
     * single name of the page.
     *
     * @param name Name of object instance.
     * @return A Page with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Page getInstance(String name)
            throws Exception
    {
        Page page = null;

        // Check if the screen is already in the cache
        if (cache() && this.containsKey(name))
        {
            page = (Page) this.get(name);
            log.debug("Found Page " + name + " in the cache!");
        }
        else
        {
            log.debug("Loading Page " + name + " from the Assembler Broker");

            try
            {
                if (ab != null)
                {
                    // Attempt to load the screen
                    page = (Page) ab.getAssembler(
                        AssemblerBrokerService.PAGE_TYPE, name);
                }
            }
            catch (ClassCastException cce)
            {
                // This can alternatively let this exception be thrown
                // So that the ClassCastException is shown in the
                // browser window.  Like this it shows "Screen not Found"
                page = null;
            }

            if (page == null)
            {
                // If we did not find a screen we should try and give
                // the user a reason for that...
                // FIX ME: The AssemblerFactories should each add it's
                // own string here...
                List packages = Turbine.getConfiguration()
                    .getList(TurbineConstants.MODULE_PACKAGES);

                ObjectUtils.addOnce(packages,
                        GenericLoader.getBasePackage());

                throw new ClassNotFoundException(
                        "\n\n\tRequested Page not found: " + name +
                        "\n\tTurbine looked in the following " +
                        "modules.packages path: \n\t" + packages.toString() + "\n");
            }
            else if (cache())
            {
                // The new instance is added to the cache
                addInstance(name, page);
            }
        }
        return page;
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static PageLoader getInstance()
    {
        return instance;
    }
}
