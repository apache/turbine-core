package org.apache.turbine.services.pool;

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

import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.TurbineException;

/**
 * This is a static accessor to common pooling tasks.
 *
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
    public static PoolService getService()
    {
        return (PoolService) TurbineServices.
                getInstance().getService(PoolService.SERVICE_NAME);
    }
}
