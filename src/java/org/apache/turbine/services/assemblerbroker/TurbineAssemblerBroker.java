package org.apache.turbine.services.assemblerbroker;

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

import org.apache.turbine.modules.Assembler;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.apache.turbine.util.TurbineException;

/**
 * An interface the Turbine Assembler service.
 * See TurbineAssemblerBrokerService for more info.
 *
 * @version $Id$
 */
public abstract class TurbineAssemblerBroker
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return An AssemblerBroker implementation instance
     */
    public static AssemblerBrokerService getService()
    {
        return (AssemblerBrokerService) TurbineServices.getInstance()
            .getService(AssemblerBrokerService.SERVICE_NAME);
    }

    /**
     * Register a new Assembler factory with this service.
     *
     * @param type The type of Assembler Factory
     * @param factory The actual Factory Object
     */
    public static void registerFactory(String type, AssemblerFactory factory)
    {
        getService().registerFactory(type, factory);
    }

    /**
     * Return an Assembler for a given type and object name.
     *
     * @param type The Type of Assember we want
     * @param name The name of the Assembler
     *
     * @return An Assembler Object.
     *
     * @throws TurbineException If a problem locating the Assember occured.
     */
    public static Assembler getAssembler(String type, String name)
        throws TurbineException
    {
        return getService().getAssembler(type, name);
    }
}
