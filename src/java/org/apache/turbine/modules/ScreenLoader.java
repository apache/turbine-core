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
import org.apache.ecs.ConcreteElement;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.assemblerbroker.TurbineAssemblerBroker;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.RunData;

/**
 * The purpose of this class is to allow one to load and execute
 * Screen modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class ScreenLoader
    extends GenericLoader
    implements Loader
{
    /** Logging */
    private static Log log = LogFactory.getLog(ScreenLoader.class);

    /** The single instance of this class. */
    private static ScreenLoader instance =
        new ScreenLoader(Turbine.getConfiguration()
                         .getInt(TurbineConstants.SCREEN_CACHE_SIZE_KEY,
                                 TurbineConstants.SCREEN_CACHE_SIZE_DEFAULT));

    /** The Assembler Broker Service */
    private static AssemblerBrokerService ab = TurbineAssemblerBroker.getService();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScreenLoader()
    {
        super();
    }

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private ScreenLoader(int i)
    {
        super(i);
    }

    /**
     * Adds an instance of an object into the hashtable.
     *
     * @param name Name of object.
     * @param screen Screen to be associated with name.
     */
    private void addInstance(String name, Screen screen)
    {
        if (cache())
        {
            this.put(name, screen);
        }
    }

    /**
     * Attempts to load and execute the external Screen. This is used
     * when you want to execute a Screen which returns its output via
     * a MultiPartElement instead of out the data.getPage() value.
     * This allows you to easily chain the execution of Screen modules
     * together.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    public ConcreteElement eval(RunData data, String name)
            throws Exception
    {
        // Execute screen
        return getInstance(name).build(data);
    }

    /**
     * Attempts to load and execute the external Screen. This is used
     * when you want to execute a Screen which returns its output via
     * a MultiPartElement instead of out the data.getPage() value.
     * This allows you to easily chain the execution of Screen modules
     * together.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    public ConcreteElement eval(PipelineData pipelineData, String name)
            throws Exception
    {
        // Execute screen
        return getInstance(name).build(pipelineData);
    }
    /**
     * Attempts to load and execute the Screen. This is used when you
     * want to execute a Screen which returns its output via the
     * data.getPage() object.
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    public void exec(RunData data, String name)
            throws Exception
    {
        this.eval(data, name);
    }

    /**
     * Attempts to load and execute the Screen. This is used when you
     * want to execute a Screen which returns its output via the
     * data.getPage() object.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the screen.
     * @exception Exception a generic exception.
     */
    public void exec(PipelineData pipelineData, String name)
	throws Exception
	{
        this.eval(pipelineData, name);
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
     * Pulls out an instance of the Screen by name.  Name is just the
     * single name of the Screen.
     *
     * @param name Name of requested Screen.
     * @return A Screen with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Screen getInstance(String name)
            throws Exception
    {
        Screen screen = null;

        // Check if the screen is already in the cache
        if (cache() && this.containsKey(name))
        {
            screen = (Screen) this.get(name);
            log.debug("Found Screen " + name + " in the cache!");
        }
        else
        {
            log.debug("Loading Screen " + name + " from the Assembler Broker");

            try
            {
                if (ab != null)
                {
                    // Attempt to load the screen
                    screen = (Screen) ab.getAssembler(
                        AssemblerBrokerService.SCREEN_TYPE, name);
                }
            }
            catch (ClassCastException cce)
            {
                // This can alternatively let this exception be thrown
                // So that the ClassCastException is shown in the
                // browser window.  Like this it shows "Screen not Found"
                screen = null;
            }

            if (screen == null)
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
                        "\n\n\tRequested Screen not found: " + name +
                        "\n\tTurbine looked in the following " +
                        "modules.packages path: \n\t" + packages.toString() + "\n");
            }
            else if (cache())
            {
                // The new instance is added to the cache
                addInstance(name, screen);
            }
        }
        return screen;
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static ScreenLoader getInstance()
    {
        return instance;
    }
}
