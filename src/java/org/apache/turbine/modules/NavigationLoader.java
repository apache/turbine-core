package org.apache.turbine.modules;

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

import org.apache.ecs.ConcreteElement;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * The purpose of this class is to allow one to load and execute
 * Navigation modules.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class NavigationLoader
    extends GenericLoader
    implements Loader
{
    /** The single instance of this class. */
    private static NavigationLoader instance = new NavigationLoader();

    /**
     * These ctor's are private to force clients to use getInstance()
     * to access this class.
     */
    private NavigationLoader()
    {
        super();
    }

    /**
     * Attempts to load and execute the external Navigation. This is
     * used when you want to execute a Navigation which returns its
     * output via a MultiPartElement instead of out the data.getPage()
     * value.  This allows you to easily chain the execution of
     * Navigation modules together.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object that will execute the navigation.
     * @exception Exception a generic exception.
     */
    public ConcreteElement eval(RunData data, String name)
            throws Exception
    {
        // Execute Navigation
        return getInstance(name).build(data);
    }

    /**
     * Attempts to load and execute the external Navigation. This is
     * used when you want to execute a Navigation which returns its
     * output via a MultiPartElement instead of out the data.getPage()
     * value.  This allows you to easily chain the execution of
     * Navigation modules together.
     *
     * @param data Turbine information.
     * @param name Name of object that will execute the navigation.
     * @exception Exception a generic exception.
     */
    public ConcreteElement eval(PipelineData pipelineData, String name)
            throws Exception
    {
        // Execute Navigation
        return getInstance(name).build(pipelineData);
    }


    /**
     * Attempts to load and execute the external Navigation.
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @param name Name of object instance.
     * @exception Exception a generic exception.
     */
    public void exec(RunData data, String name)
            throws Exception
    {
        this.eval(data, name);
    }

    /**
     * Attempts to load and execute the external Navigation.
     *
     * @param pipelineData Turbine information.
     * @param name Name of object instance.
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
     * @return A Layout with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Assembler getAssembler(String name)
        throws Exception
    {
        return getInstance(name);
    }

    /**
     * @see org.apache.turbine.modules.Loader#getCacheSize()
     */
    public int getCacheSize()
    {
        return NavigationLoader.getConfiguredCacheSize();
    }

    /**
     * Pulls out an instance of the Navigation by name.  Name is just the
     * single name of the Navigation.
     *
     * @param name Name of requested Navigation
     * @return A Navigation with the specified name, or null.
     * @exception Exception a generic exception.
     */
    public Navigation getInstance(String name)
            throws Exception
    {
        Navigation navigation = null;

        try
        {
            if (ab != null)
            {
                // Attempt to load the navigation
                navigation = (Navigation) ab.getAssembler(Navigation.NAME, name);
            }
        }
        catch (ClassCastException cce)
        {
            // This can alternatively let this exception be thrown
            // So that the ClassCastException is shown in the
            // browser window.  Like this it shows "Screen not Found"
            navigation = null;
        }

        if (navigation == null)
        {
            // If we did not find a navigation we should try and give
            // the user a reason for that...
            // FIX ME: The AssemblerFactories should each add it's
            // own string here...
            List packages = GenericLoader.getPackages();

            throw new ClassNotFoundException(
                    "\n\n\tRequested Navigation not found: " + name +
                    "\n\tTurbine looked in the following " +
                    "modules.packages path: \n\t" + packages.toString() + "\n");
        }

        return navigation;
    }

    /**
     * The method through which this class is accessed.
     *
     * @return The single instance of this class.
     */
    public static NavigationLoader getInstance()
    {
        return instance;
    }

    /**
     * Helper method to get the configured cache size for this module
     * 
     * @return the configure cache size
     */
    private static int getConfiguredCacheSize()
    {
        return Turbine.getConfiguration().getInt(Navigation.CACHE_SIZE_KEY,
                Navigation.CACHE_SIZE_DEFAULT);
    }
}
