package org.apache.turbine.services.assemblerbroker.util.python;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

// JDK Classes
import java.io.File;

// Turbine Classes
import org.apache.turbine.modules.Assembler;

import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.services.assemblerbroker.AssemblerBrokerService;

import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

// JPython Classes
import org.python.core.Py;

import org.python.util.PythonInterpreter;



/**
 * A screen factory that attempts to load a python class in the
 * JPython interpreter and execute it as a Turbine screen.
 * The JPython script should inherit from Turbine Screen or one
 * of its subclasses.
 */
public abstract class PythonBaseFactory implements AssemblerFactory
{

    /** Logging */
    private static Log log = LogFactory.getLog(PythonBaseFactory.class);

    public Assembler getAssembler( String subDirectory, String name ) throws Exception
    {
        Assembler assembler = null;

        // The filename of the Python script
        String fName = null;
        String confName = null;


        log.info ("Screen name for JPython " + name);

        try
        {
            String path = TurbineResources.getString (
                TurbineServices.SERVICE_PREFIX
                + AssemblerBrokerService.SERVICE_NAME
                + ".python.path") + "/";
            confName = path + "conf.py";
            fName = path + subDirectory + "/" + name.toLowerCase() + ".py";
        }
        catch (Exception e)
        {
            throw new Exception ("Python path not found - check your Properties");
        }

        File f = new File (fName);
        if (f.exists())
        {
            try
            {
                // We try to open the Py Interpreter
                PythonInterpreter interp = new PythonInterpreter();

                // Make sure the Py Interpreter use the right classloader
                // This is necissarry for servlet engines generally has
                // their own classloader implementations and servlets aren't
                // loaded in the system classloader.  The python script will
                // load java package org.apache.turbine.services.assemblerbroker.util.python;
                // the new classes to it as well.
                Py.getSystemState().setClassLoader(this.getClass().getClassLoader());

                // We import the Python SYS module.  Now we don't need to do this
                // explicitely in the scrypt.  We always use the sys module to
                // do stuff like loading java package org.apache.turbine.services.assemblerbroker.util.python;
                interp.exec("import sys");

                // Now we try to load the script file
                interp.execfile (confName);
                interp.execfile (fName);

                try
                {
                    // We create an instance of the screen class from the python script
                    interp.exec("scr = " + name + "()");
                }
                catch (Throwable e)
                {
                    throw new Exception ("\nCannot create an instance of the python class.\n"
                                         + "You probably gave your class the wrong name.\n"
                                         + "Your class should have the same name as your filename.\n"
                                         + "Filenames should be all lowercase and classnames should "
                                         + "start with a capital.\n"
                                         + "Expected class name: " + name + "\n");
                }



                // Here we convert the python sceen instance to a java instance.

                assembler = (Assembler) interp.get ("scr", Assembler.class);

            }
            catch (Exception e)
            {
                // We log the error here because this code is not widely tested yet.
                // After we tested the code on a range of platforms this won't be
                // usefull anymore.
                log.error ("PYTHON SCRIPT SCREEN LOADER ERROR:");
                log.error (e.toString());
                // Let the error fall through like the normal way.
                throw e;
            }

        }

        return assembler;
    }

}
