package org.apache.turbine.services.factory;

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

import org.apache.turbine.util.TurbineException;

/**
 * Factory is an interface for object factories. Object factories
 * can be registered to the Factory Service to support customized
 * functionality during instantiation of specific classes that 
 * the service itself cannot provide. Examples include
 * instantiation of XML parsers and secure sockets requiring
 * provider specific initializations before instantiation. 
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface Factory
{
    /**
     * Initializes the factory. This method is called by
     * the Factory Service before the factory is taken into use.
     *
     * @param className the name of the production class
     * @throws TurbineException if initialization fails.
     */
    void init(String className)
        throws TurbineException;

    /**
     * Gets an instance of a class.
     *
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance()
        throws TurbineException;

    /**
     * Gets an instance of a class using a specified class loader.
     * 
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored. 
     *
     * @param loader the class loader.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(ClassLoader loader)
        throws TurbineException;

    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(Object[] params,
                       String[] signature)
        throws TurbineException;

    /**
     * Gets an instance of a named class using a specified class loader.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     * 
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored. 
     *
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(ClassLoader loader,
                       Object[] params,
                       String[] signature)
        throws TurbineException;

    /**
     * Tests if this object factory supports specified class loaders.
     * 
     * @return true if class loaders are supported, false otherwise. 
     */
    boolean isLoaderSupported();
}
