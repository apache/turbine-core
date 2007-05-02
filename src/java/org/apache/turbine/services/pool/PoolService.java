package org.apache.turbine.services.pool;

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

import org.apache.turbine.services.Service;
import org.apache.turbine.util.TurbineException;

/**
 * The Pool Service extends the Factory Service by adding support
 * for pooling instantiated objects. When a new instance is
 * requested, the service first checks its pool if one is available.
 * If the the pool is empty, a new object will be instantiated
 * from the specified class. If only class name is given, the request
 * to create an intance will be forwarded to the Factory Service.
 *
 * <p>For objects implementing the Recyclable interface, a recycle
 * method will be called, when they are taken from the pool, and
 * a dispose method, when they are returned to the pool.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public interface PoolService
        extends Service
{
    /** The key under which this service is stored in TurbineServices. */
    String SERVICE_NAME = "PoolService";

    /** The default pool capacity. */
    int DEFAULT_POOL_CAPACITY = 128;

    /** The name of the pool capacity property */
    String POOL_CAPACITY_KEY = "pool.capacity";

    /** Are we running in debug mode? */
    String POOL_DEBUG_KEY = "pool.debug";

    /** Default Value for debug mode */
    boolean POOL_DEBUG_DEFAULT = false;

    /**
     * Gets an instance of a named class.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(String className)
            throws TurbineException;

    /**
     * Gets an instance of a named class using a specified class loader.
     *
     * <p>Class loaders are supported only if the isLoaderSupported
     * method returns true. Otherwise the loader parameter is ignored.
     *
     * @param className the name of the class.
     * @param loader the class loader.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(String className,
            ClassLoader loader)
            throws TurbineException;

    /**
     * Gets an instance of a named class.
     * Parameters for its constructor are given as an array of objects,
     * primitive types must be wrapped with a corresponding class.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(String className,
            Object[] params,
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
     * @param className the name of the class.
     * @param loader the class loader.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the instance.
     * @throws TurbineException if instantiation fails.
     */
    Object getInstance(String className,
            ClassLoader loader,
            Object[] params,
            String[] signature)
            throws TurbineException;

    /**
     * Tests if specified class loaders are supported for a named class.
     *
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise.
     * @throws TurbineException if test fails.
     * @deprecated Use TurbineFactory.isLoaderSupported(className)
     */
    boolean isLoaderSupported(String className)
            throws TurbineException;

    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    Object getInstance(Class clazz)
            throws TurbineException;

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
    Object getInstance(Class clazz,
            Object params[],
            String signature[])
            throws TurbineException;

    /**
     * Puts a used object back to the pool. Objects implementing
     * the Recyclable interface can provide a recycle method to
     * be called when they are reused and a dispose method to be
     * called when they are returned to the pool.
     *
     * @param instance the object instance to recycle.
     * @return true if the instance was accepted.
     */
    boolean putInstance(Object instance);

    /**
     * Gets the capacity of the pool for a named class.
     *
     * @param className the name of the class.
     */
    int getCapacity(String className);

    /**
     * Sets the capacity of the pool for a named class.
     * Note that the pool will be cleared after the change.
     *
     * @param className the name of the class.
     * @param capacity the new capacity.
     */
    void setCapacity(String className,
                     int capacity);

    /**
     * Gets the current size of the pool for a named class.
     *
     * @param className the name of the class.
     */
    int getSize(String className);

    /**
     * Clears instances of a named class from the pool.
     *
     * @param className the name of the class.
     */
    void clearPool(String className);

    /**
     * Clears all instances from the pool.
     */
    void clearPool();

}
