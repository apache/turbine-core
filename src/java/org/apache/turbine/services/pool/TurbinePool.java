package org.apache.turbine.services.pool;

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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;

/**
 * This is a static accessor to common pooling tasks.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public abstract class TurbinePool
{
    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(String className)
            throws TurbineException
    {
        return getService().getInstance(className);
    }

    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * The specified class loader will be passed to the Factory Service.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(String className,
                                     ClassLoader loader)
            throws TurbineException
    {
        return getService().getInstance(className, loader);
    }

    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(String className,
                                     Object[] params,
                                     String[] signature)
            throws TurbineException
    {
        return getService().getInstance(className, params, signature);
    }

    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     * The specified class loader will be passed to the Factory Service.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(String className,
                                     ClassLoader loader,
                                     Object[] params,
                                     String[] signature)
            throws TurbineException
    {
        return getService().getInstance(className, loader, params, signature);
    }

    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(Class clazz)
            throws TurbineException
    {
        return getService().getInstance(clazz);
    }

    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @param clazz the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public static Object getInstance(Class clazz,
                                     Object params[],
                                     String signature[])
            throws TurbineException
    {
        return getService().getInstance(clazz, params, signature);
    }

    /**
     * Puts a used object back to the pool. Objects implementing
     * the Recyclable interface can provide a recycle method to
     * be called when they are reused and a dispose method to be
     * called when they are returned to the pool.
     *
     * @param instance the object instance to recycle.
     * @return true if the instance was accepted.
     */
    public static boolean putInstance(Object instance)
    {
        return getService().putInstance(instance);
    }

    /**
     * Gets the pool service implementation.
     *
     * @return the pool service implementation.
     */
    protected static PoolService getService()
    {
        return (PoolService) TurbineServices.
                getInstance().getService(PoolService.SERVICE_NAME);
    }
}
