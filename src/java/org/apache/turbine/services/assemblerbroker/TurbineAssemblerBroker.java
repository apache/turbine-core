package org.apache.turbine.services.assemblerbroker;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.turbine.modules.Assembler;

import org.apache.turbine.services.Service;
import org.apache.turbine.services.TurbineServices;

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
