package org.apache.turbine.services.assemblerbroker;

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

import java.util.Hashtable;
import java.util.Vector;

import org.apache.turbine.modules.Assembler;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

import org.apache.turbine.services.assemblerbroker.util.AssemblerFactory;

import org.apache.turbine.services.resources.TurbineResources;

import org.apache.turbine.util.TurbineException;

/**
 * TurbineAssemblerBrokerService allows assemblers (like screens,
 * actions and layouts) to be loaded from one or more AssemblerFactory
 * classes.  AssemblerFactory classes are registered with this broker
 * by adding them to the TurbineResources.properties file.
 *
 * @author <a href="mailto:leon@opticode.co.za">Leon Messerschmidt</a>
 */
public class TurbineAssemblerBrokerService
    extends TurbineBaseService
    implements AssemblerBrokerService

{
    /** A structure that holds the registered AssemblerFactories*/
    private Hashtable factories = null;

    /**
     * Get a list of AssemblerFactories of a certain type
     */
    private Vector getFactoryGroup (String type)
    {
        if (!factories.containsKey (type))
        {
            factories.put (type, new Vector());
        }
        return (Vector)factories.get(type);
    }

    /**
     * Utiltiy method to register all factories for a given
     * type.
     */
    private void registerFactories (String type)
        throws TurbineException
    {
        String key = TurbineServices.SERVICE_PREFIX+
                     AssemblerBrokerService.SERVICE_NAME+
                     "."+
                     type;

        String[] names = TurbineResources.getStringArray(key);

//        Log.info ("Registering " + names.length + " " + type + " factories.");

        for (int i=0; i<names.length; i++)
        {
            try
            {
                Object o = Class.forName (names[i]).newInstance();
                registerFactory (type, (AssemblerFactory)o);
            }
            // these must be passed to the VM
            catch(ThreadDeath e)
            {
                throw e;
            }
            catch(OutOfMemoryError e)
            {
                throw e;
            }
            // when using Class.forName(), NoClassDefFoundErrors are likely 
            // to happen (missing jar files)
            catch (Throwable t)
            {
                throw new TurbineException("Failed registering " + type + " factories", t);
            }
        }
    }


    /**
     * Initializes the AssemblerBroker and loads the AssemblerFactory
     * classes registerd in TurbineResources.Properties.
     */
    public void init()
        throws InitializationException
    {
        factories = new Hashtable();
        try 
        {
            registerFactories (AssemblerBrokerService.ACTION_TYPE);
            registerFactories (AssemblerBrokerService.SCREEN_TYPE);
            registerFactories (AssemblerBrokerService.NAVIGATION_TYPE);
            registerFactories (AssemblerBrokerService.LAYOUT_TYPE);
            registerFactories (AssemblerBrokerService.PAGE_TYPE);
            registerFactories (AssemblerBrokerService.SCHEDULEDJOB_TYPE);
        }
        catch(TurbineException e) 
        {
            throw new InitializationException("AssemblerBrokerService failed to initialize", e);
        }
        setInit(true);
    }

    /**
     * Register a new AssemblerFactory under a certain type
     */
    public void registerFactory(String type, AssemblerFactory factory)
    {
        getFactoryGroup(type).add (factory);
    }

    /**
     * Attempt to retrieve an Assembler of a given type with
     * a name.  Cycle through all the registered AssemblerFactory
     * classes of type and retrun the first non-null assembly
     * found.  If an assembly was not found return null.
     */
    public Assembler getAssembler(String type, String name) 
        throws TurbineException
    {
        Vector facs = getFactoryGroup(type);

        for (int i=0; i<facs.size(); i++)
        {
            AssemblerFactory fac = (AssemblerFactory)facs.get(i);
            Assembler assembler = null;
            try
            {
                assembler = fac.getAssembler (name);
            }
            catch (Exception e)
            {
                throw new TurbineException("Failed to find the " 
                                           + type 
                                           +" named "
                                           + name, e);
            }

            if (assembler != null) 
            {
                return assembler;
            }
        }
        return null;
    }
}
