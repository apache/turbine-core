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


import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.Service;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.apache.turbine.util.TurbineException;

/**
 * An interface the Turbine Assembler service.
 * See TurbineAssemblerBrokerService for more info.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface AssemblerBrokerService
        extends Service
{
    /** Name of the Service */
    String SERVICE_NAME = "AssemblerBrokerService";

    /**
     * Register an AssemblerFactory class
     *
     * @param factory The factory object
     */
    <T extends Assembler> void registerFactory(AssemblerFactory<T> factory);

    /**
     * Attempts to load an Assembler of a type with a given name
     *
     * @param type The Type of the Assembler
     * @param name The Name of the Assembler
     * @return An Assembler object for the requested name and type
     *
     * @throws TurbineException Something went wrong while looking for the Assembler
     */
    <T extends Assembler> T getAssembler(Class<T> type, String name) throws TurbineException;

    /**
     * Get a Loader for the given assembler type
     *
     * @param type The Type of the Assembler
     * @return A Loader instance for the requested type
     */
    <T extends Assembler> Loader<T> getLoader(Class<T> type);
}
