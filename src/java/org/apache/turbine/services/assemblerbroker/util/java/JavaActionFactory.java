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


import org.apache.turbine.modules.Action;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.modules.Loader;

/**
 * An action factory that attempts to load a java class from
 * the module packages defined in the TurbineResource.properties.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class JavaActionFactory
    extends JavaBaseFactory<Action>
{
    /**
     * Get an Assembler.
     *
     * @param name name of the requested Assembler
     * @return an Assembler
     */
    @Override
    public Action getAssembler(String name)
    {
        return getAssembler(Action.PREFIX, name);
    }

    /**
     * Get the loader for this type of assembler
     *
     * @return a Loader
     */
    @Override
    public Loader<Action> getLoader()
    {
        return ActionLoader.getInstance();
    }

    /**
     * Get the class of this assembler
     *
     * @return a class
     */
    @Override
    public Class<Action> getManagedClass()
    {
        return Action.class;
    }
}
