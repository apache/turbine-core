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
import java.util.stream.Collectors;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;

/**
 * This is the base class for the loaders. It contains code that is
 * used across all of the loaders. It also specifies the interface
 * that is required to be called a Loader.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @param <T> the specialized assembler type
 */
public abstract class GenericLoader<T extends Assembler>
{
    /** The Assembler Broker Service */
    protected AssemblerBrokerService ab;

    /** @serial This can be serialized */
    private boolean reload = false;

    /** Base packages path for Turbine */
    private static final String TURBINE_PACKAGE = "org.apache.turbine.modules";

    /** Packages paths for Turbine */
    private static List<String> TURBINE_PACKAGES = null;

    /**
     * Basic constructor for creating a loader.
     */
    public GenericLoader()
    {
        super();
        ab = (AssemblerBrokerService)TurbineServices.getInstance().getService(AssemblerBrokerService.SERVICE_NAME);
    }

    /**
     * Attempts to load and execute the external action that has been
     * set.
     * @param pipelineData the Turbine request
     * @param name the name of the assembler module
     * @throws Exception a generic exception.
     */
    public abstract void exec(PipelineData pipelineData, String name)
            throws Exception;

    /**
     * Returns whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @return True if the action is reload.
     */
    public boolean reload()
    {
        return this.reload;
    }

    /**
     * Sets whether or not this external action is reload itself.
     * This is in cases where the Next button would be clicked, but
     * since we are checking for that, we would go into an endless
     * loop.
     *
     * @param reload True if the action must be marked as reload.
     * @return Itself.
     */
    public GenericLoader<T> setReload(boolean reload)
    {
        this.reload = reload;
        return this;
    }

    /**
     * Gets the base package where Turbine should find its default
     * modules.
     *
     * @return A String with the base package name.
     */
    public static String getBasePackage()
    {
        return TURBINE_PACKAGE;
    }

    /**
     * Gets the package list where Turbine should find its
     * modules.
     *
     * @return A List with the package names (including the base package).
     */
    public static List<String> getPackages()
    {
        if (TURBINE_PACKAGES == null)
        {  
            List<String> turbinePackages = Turbine.getConfiguration()
            .getList(TurbineConstants.MODULE_PACKAGES).stream().map( o -> (String) o ).collect( Collectors.toList() );

            TURBINE_PACKAGES = turbinePackages;
        }

        List<String> packages = TURBINE_PACKAGES;

        if (!packages.contains(TURBINE_PACKAGE))
        {
            packages.add(TURBINE_PACKAGE);
        }

        return packages;
    }

    /**
     * Pulls out an instance of the object by name.  Name is just the
     * single name of the object.
     *
     * @param type Type of the assembler.
     * @param name Name of object instance.
     * @return A Screen with the specified name, or null.
     * @throws Exception a generic exception.
     */
    protected T getAssembler(Class<T> type, String name)
        throws Exception
    {
        T asm = null;

        try
        {
            if (ab != null)
            {
                // Attempt to load the assembler
                asm = ab.getAssembler(type, name);
            }
        }
        catch (ClassCastException cce)
        {
            // This can alternatively let this exception be thrown
            // So that the ClassCastException is shown in the
            // browser window.  Like this it shows "Screen not Found"
            asm = null;
        }

        if (asm == null)
        {
            // If we did not find a screen we should try and give
            // the user a reason for that...
            // FIX ME: The AssemblerFactories should each add it's
            // own string here...
            List<String> packages = GenericLoader.getPackages();

            throw new ClassNotFoundException(
                    "\n\n\tRequested " + type + " not found: " + name +
                    "\n\tTurbine looked in the following " +
                    "modules.packages path: \n\t" + packages.toString() + "\n");
        }

        return asm;
    }
}
