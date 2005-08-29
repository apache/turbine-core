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

    /** Predefined types for Turbine built-in assemblers: Actions  */
    String ACTION_TYPE = "action";

    /** Predefined types for Turbine built-in assemblers: Screens  */
    String SCREEN_TYPE = "screen";

    /** Predefined types for Turbine built-in assemblers: Navigations  */
    String NAVIGATION_TYPE = "navigation";

    /** Predefined types for Turbine built-in assemblers: Layouts  */
    String LAYOUT_TYPE = "layout";

    /** Predefined types for Turbine built-in assemblers: Pages  */
    String PAGE_TYPE = "page";

    /** Predefined types for Turbine built-in assemblers: Scheduler Jobs  */
    String SCHEDULEDJOB_TYPE = "scheduledjob";

    /**
     * Register an AssemblerFactory class for a given type
     *
     * @param type Type of the Factory
     * @param factory The factory object
     */
    void registerFactory(String type, AssemblerFactory factory);

    /**
     * Attempts to load an Assembler of a type with a given name
     *
     * @param type The Type of the Assembler
     * @param name The Name of the Assembler
     * @return An Assembler object for the requested name and type
     *
     * @throws TurbineException Something went wrong while looking for the Assembler
     */
    Assembler getAssembler(String type, String name) throws TurbineException;
}
