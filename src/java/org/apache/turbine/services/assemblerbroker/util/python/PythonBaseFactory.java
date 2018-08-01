package org.apache.turbine.services.assemblerbroker.util.python;


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


import java.io.File;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.modules.Assembler;
import org.apache.turbine.modules.Loader;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;
import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;
import org.python.core.Py;
import org.python.util.PythonInterpreter;

/**
 * A factory that attempts to load a python class in the
 * JPython interpreter and execute it as a Turbine screen.
 * The JPython script should inherit from Turbine Screen or one
 * of its subclasses.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @param <T> the specialized assembler type
 */
public abstract class PythonBaseFactory<T extends Assembler>
        implements AssemblerFactory<T>
{
    /** Key for the python path */
    public static final String PYTHON_PATH = "python.path";

    /** Global config file. This is executed before every screen */
    public static final String PYTHON_CONFIG_FILE = "conf.py";

    /** Logging */
    private static Log log = LogFactory.getLog(PythonBaseFactory.class);

    /** Our configuration */
    private final Configuration conf = TurbineServices.getInstance().getConfiguration(AssemblerBrokerService.SERVICE_NAME);

    /**
     * Get an Assembler.
     *
     * @param subDirectory subdirectory within python.path
     * @param name name of the requested Assembler
     * @return an Assembler
     * @throws Exception generic exception
     */
    public T getAssembler(String subDirectory, String name)
            throws Exception
    {
        String path = conf.getString(PYTHON_PATH);

        if (StringUtils.isEmpty(path))
        {
            throw new Exception(
                "Python path not found - check your Properties");
        }

        log.debug("Screen name for JPython: " + name);

        T assembler = null;

        String confName = path + "/" + PYTHON_CONFIG_FILE;

        // The filename of the Python script
        StringBuilder fName = new StringBuilder();

        fName.append(path);
        fName.append("/");
        fName.append(subDirectory);
        fName.append("/");
        fName.append(name.toLowerCase());
        fName.append(".py");

        File f = new File(fName.toString());

        if (f.exists())
        {
            PythonInterpreter interp = null;

            try
            {
                // We try to open the Py Interpreter
                interp = new PythonInterpreter();

                // Make sure the Py Interpreter use the right classloader
                // This is necessary for servlet engines generally has
                // their own classloader implementations and servlets aren't
                // loaded in the system classloader.  The python script will
                // load java package
                // org.apache.turbine.services.assemblerbroker.util.python;
                // the new classes to it as well.
                Py.getSystemState().setClassLoader(this.getClass().getClassLoader());

                // We import the Python SYS module. Now we don't need to do this
                // explicitly in the script.  We always use the sys module to
                // do stuff like loading java package
                // org.apache.turbine.services.assemblerbroker.util.python;
                interp.exec("import sys");

                // Now we try to load the script file
                interp.execfile(confName);
                interp.execfile(fName.toString());

                try
                {
                    // We create an instance of the screen class from the
                    // python script
                    interp.exec("scr = " + name + "()");
                }
                catch (Throwable e)
                {
                    throw new Exception(
                        "\nCannot create an instance of the python class.\n"
                        + "You probably gave your class the wrong name.\n"
                        + "Your class should have the same name as your "
                        + "filename.\nFilenames should be all lowercase and "
                        + "classnames should start with a capital.\n"
                        + "Expected class name: " + name + "\n");
                }

                // Here we convert the python screen instance to a java instance.
                @SuppressWarnings("unchecked") // Cast from Object necessary
				T t = (T) interp.get("scr", Assembler.class);
				assembler = t;
            }
            catch (Exception e)
            {
                // We log the error here because this code is not widely tested
                // yet. After we tested the code on a range of platforms this
                // won't be useful anymore.
                log.error("PYTHON SCRIPT SCREEN LOADER ERROR:", e);
                throw e;
            }
            finally
            {
                if (interp != null)
                {
                    interp.close();
                }
            }
        }
        return assembler;
    }

    /**
     * Get the loader for this type of assembler
     *
     * @return a Loader
     */
    @Override
    public abstract Loader<T> getLoader();

    /**
     * Get the size of a possibly configured cache
     *
     * @return the size of the cache in bytes
     */
    @Override
    public int getCacheSize()

    {
        return getLoader().getCacheSize();
    }
}
