package org.apache.turbine.services.assemblerbroker.util.python;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Assembler;

/**
 * A screen factory that attempts to load a python class in the
 * JPython interpreter and execute it as a Turbine screen.
 * The JPython script should inherit from Turbine Screen or one
 * of its subclasses.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class PythonActionFactory
        extends PythonBaseFactory
{
    /**
     * Get an Assembler.
     *
     * @param name name of the requested Assembler
     * @return an Assembler
     * @throws Exception generic exception
     */
    public Assembler getAssembler(String name)
        throws Exception
    {
        return getAssembler(TurbineConstants.ACTION_PREFIX, name);
    }
}
