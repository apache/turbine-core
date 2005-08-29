package org.apache.turbine.services.pool;

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

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.factory.FactoryService;
import org.apache.turbine.services.factory.TurbineFactory;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.pool.ArrayCtorRecyclable;
import org.apache.turbine.util.pool.BoundedBuffer;
import org.apache.turbine.util.pool.Recyclable;

/**
 * The Pool Service extends the Factory Service by adding support
 * for pooling instantiated objects. When a new instance is
 * requested, the service first checks its pool if one is available.
 * If the the pool is empty, a new instance will be requested
 * from the FactoryService.
 *
 * <p>For objects implementing the Recyclable interface, a recycle
 * method will be called, when they taken from the pool, and
 * a dispose method, when they are returned to the pool.
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class TurbinePoolService
        extends TurbineBaseService
        implements PoolService
{
    /** Are we currently debugging the pool recycling? */
    private boolean debugPool = false;

    /** Internal Reference to the Factory */
    private FactoryService factoryService;

    /** Logging */
    private static Log log = LogFactory.getLog(TurbinePoolService.class);

    /**
     * An inner class for class specific pools.
     */
    private class PoolBuffer
    {
        /**
         * An inner class for cached recycle methods.
         */
        private class Recycler
        {
            /** The recycle method. */
            private final Method recycle;

            /** The signature. */
            private final String[] signature;

            /**
             * Constructs a new recycler.
             *
             * @param rec the recycle method.
             * @param sign the signature.
             */
            public Recycler(Method rec, String[] sign)
            {
                recycle = rec;
                signature = ((sign != null) && (sign.length > 0))
                        ? sign : null;
            }

            /**
             * Matches the given signature against
             * that of the recycle method of this recycler.
             *
             * @param sign the signature.
             * @return the matching recycle method or null.
             */
            public Method match(String[] sign)
            {
                if ((sign != null) && (sign.length > 0))
                {
                    if ((signature != null)
                            && (sign.length == signature.length))
                    {
                        for (int i = 0; i < signature.length; i++)
                        {
                            if (!signature[i].equals(sign[i]))
                            {
                                return null;
                            }
                        }
                        return recycle;
                    }
                    else
                    {
                        return null;
                    }
                }
                else if (signature == null)
                {
                    return recycle;
                }
                else
                {
                    return null;
                }
            }
        }

        /** A buffer for class instances. */
        private BoundedBuffer pool;

        /** A flag to determine if a more efficient recycler is implemented. */
        private boolean arrayCtorRecyclable;

        /** A cache for recycling methods. */
        private ArrayList recyclers;

        /**
         * Constructs a new pool buffer with a specific capacity.
         *
         * @param capacity a capacity.
         */
        public PoolBuffer(int capacity)
        {
            pool = new BoundedBuffer(capacity);
        }

        /**
         * Tells pool that it contains objects which can be
         * initialized using an Object array.
         *
         * @param isArrayCtor a <code>boolean</code> value
         */
        public void setArrayCtorRecyclable(boolean isArrayCtor)
        {
            arrayCtorRecyclable = isArrayCtor;
        }

        /**
         * Polls for an instance from the pool.
         *
         * @return an instance or null.
         */
        public Object poll(Object[] params, String[] signature)
                throws TurbineException
        {
            // If we're debugging the recycling code, we want different
            // objects to be used when pulling from the pool. Ensure that
            // each pool fills up at least to half its capacity.
            if (debugPool && (pool.size() < (pool.capacity() / 2)))
            {
                log.debug("Size: " + pool.size()
                        + ", capacity: " + pool.capacity());
                return null;
            }

            Object instance = pool.poll();
            if (instance != null)
            {
                if (arrayCtorRecyclable)
                {
                    ((ArrayCtorRecyclable) instance).recycle(params);
                }
                else if (instance instanceof Recyclable)
                {
                    try
                    {
                        if ((signature != null) && (signature.length > 0))
                        {
                            /* Get the recycle method from the cache. */
                            Method recycle = getRecycle(signature);
                            if (recycle == null)
                            {
                                synchronized (this)
                                {
                                    /* Make a synchronized recheck. */
                                    recycle = getRecycle(signature);
                                    if (recycle == null)
                                    {
                                        Class clazz = instance.getClass();
                                        recycle = clazz.getMethod("recycle",
                                                factoryService.getSignature(
                                                        clazz, params, signature));
                                        ArrayList cache = recyclers != null
                                                ? (ArrayList) recyclers.clone()
                                                : new ArrayList();
                                        cache.add(
                                                new Recycler(recycle, signature));
                                        recyclers = cache;
                                    }
                                }
                            }
                            recycle.invoke(instance, params);
                        }
                        else
                        {
                            ((Recyclable) instance).recycle();
                        }
                    }
                    catch (Exception x)
                    {
                        throw new TurbineException(
                                "Recycling failed for " + instance.getClass().getName(), x);
                    }
                }
            }
            return instance;
        }

        /**
         * Offers an instance to the pool.
         *
         * @param instance an instance.
         */
        public boolean offer(Object instance)
        {
            if (instance instanceof Recyclable)
            {
                try
                {
                    ((Recyclable) instance).dispose();
                }
                catch (Exception x)
                {
                    return false;
                }
            }
            return pool.offer(instance);
        }

        /**
         * Returns the capacity of the pool.
         *
         * @return the capacity.
         */
        public int capacity()
        {
            return pool.capacity();
        }

        /**
         * Returns the size of the pool.
         *
         * @return the size.
         */
        public int size()
        {
            return pool.size();
        }

        /**
         * Returns a cached recycle method
         * corresponding to the given signature.
         *
         * @param signature the signature.
         * @return the recycle method or null.
         */
        private Method getRecycle(String[] signature)
        {
            ArrayList cache = recyclers;
            if (cache != null)
            {
                Method recycle;
                for (Iterator i = cache.iterator(); i.hasNext();)
                {
                    recycle = ((Recycler) i.next()).match(signature);
                    if (recycle != null)
                    {
                        return recycle;
                    }
                }
            }
            return null;
        }
    }

    /**
     * The default capacity of pools.
     */
    private int poolCapacity = DEFAULT_POOL_CAPACITY;

    /**
     * The pool repository, one pool for each class.
     */
    private HashMap poolRepository = new HashMap();

    /**
     * Constructs a Pool Service.
     */
    public TurbinePoolService()
    {
    }

    /**
     * Initializes the service by setting the pool capacity.
     *
     * @param config initialization configuration.
     * @throws InitializationException if initialization fails.
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = getConfiguration();

        int capacity = conf.getInt(POOL_CAPACITY_KEY,
                DEFAULT_POOL_CAPACITY);

        if (capacity <= 0)
        {
            throw new IllegalArgumentException("Capacity must be >0");
        }
        poolCapacity = capacity;

        debugPool = conf.getBoolean(POOL_DEBUG_KEY,
                POOL_DEBUG_DEFAULT);

        if (debugPool)
        {
            log.info("Activated Pool Debugging!");
        }

        factoryService = TurbineFactory.getService();

        if (factoryService == null)
        {
            throw new InitializationException("Factory Service is not configured"
                    + " but required for the Pool Service!");
        }

        setInit(true);
    }

    /**
     * Gets an instance of a named class either from the pool
     * or by calling the Factory Service if the pool is empty.
     *
     * @param className the name of the class.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public Object getInstance(String className)
            throws TurbineException
    {
        Object instance = pollInstance(className, null, null);
        return (instance == null)
                ? factoryService.getInstance(className)
                : instance;
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
    public Object getInstance(String className,
                              ClassLoader loader)
            throws TurbineException
    {
        Object instance = pollInstance(className, null, null);
        return (instance == null)
                ? factoryService.getInstance(className, loader)
                : instance;
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
    public Object getInstance(String className,
                              Object[] params,
                              String[] signature)
            throws TurbineException
    {
        Object instance = pollInstance(className, params, signature);
        return (instance == null)
                ? factoryService.getInstance(className, params, signature)
                : instance;
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
    public Object getInstance(String className,
                              ClassLoader loader,
                              Object[] params,
                              String[] signature)
            throws TurbineException
    {
        Object instance = pollInstance(className, params, signature);
        return (instance == null)
                ? factoryService.getInstance(className, loader, params, signature)
                : instance;
    }

    /**
     * Tests if specified class loaders are supported for a named class.
     *
     * @param className the name of the class.
     * @return true if class loaders are supported, false otherwise.
     * @throws TurbineException if test fails.
     * @deprecated Use TurbineFactory.isLoaderSupported(className);
     */
    public boolean isLoaderSupported(String className)
            throws TurbineException
    {
        return factoryService.isLoaderSupported(className);
    }

    /**
     * Gets an instance of a specified class either from the pool
     * or by instatiating from the class if the pool is empty.
     *
     * @param clazz the class.
     * @return the instance.
     * @throws TurbineException if recycling fails.
     */
    public Object getInstance(Class clazz)
            throws TurbineException
    {
        Object instance = pollInstance(clazz.getName(), null, null);
        return (instance == null)
                ? factoryService.getInstance(clazz.getName())
                : instance;
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
    public Object getInstance(Class clazz,
                              Object params[],
                              String signature[])
            throws TurbineException
    {
        Object instance = pollInstance(clazz.getName(), params, signature);
        return (instance == null)
                ? factoryService.getInstance(clazz.getName(), params, signature)
                : instance;
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
    public boolean putInstance(Object instance)
    {
        if (instance != null)
        {
            HashMap repository = poolRepository;
            String className = instance.getClass().getName();
            PoolBuffer pool = (PoolBuffer) repository.get(className);
            if (pool == null)
            {
                pool = new PoolBuffer(getCapacity(className));
                repository = (HashMap) repository.clone();
                repository.put(className, pool);
                poolRepository = repository;

                if (instance instanceof ArrayCtorRecyclable)
                {
                    pool.setArrayCtorRecyclable(true);
                }
            }
            return pool.offer(instance);
        }
        else
        {
            return false;
        }
    }

    /**
     * Gets the capacity of the pool for a named class.
     *
     * @param className the name of the class.
     */
    public int getCapacity(String className)
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        if (pool == null)
        {
            /* Check class specific capacity. */
            int capacity;

            Configuration conf = getConfiguration();
            capacity = conf.getInt(
                    POOL_CAPACITY_KEY + '.' + className,
                    poolCapacity);
            capacity = (capacity <= 0) ? poolCapacity : capacity;
            return capacity;
        }
        else
        {
            return pool.capacity();
        }
    }

    /**
     * Sets the capacity of the pool for a named class.
     * Note that the pool will be cleared after the change.
     *
     * @param className the name of the class.
     * @param capacity the new capacity.
     */
    public void setCapacity(String className,
                            int capacity)
    {
        HashMap repository = poolRepository;
        repository = (repository != null)
                ? (HashMap) repository.clone() : new HashMap();

        capacity = (capacity <= 0) ? poolCapacity : capacity;

        repository.put(className, new PoolBuffer(capacity));
        poolRepository = repository;
    }

    /**
     * Gets the current size of the pool for a named class.
     *
     * @param className the name of the class.
     */
    public int getSize(String className)
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        return (pool != null) ? pool.size() : 0;
    }

    /**
     * Clears instances of a named class from the pool.
     *
     * @param className the name of the class.
     */
    public void clearPool(String className)
    {
        HashMap repository = poolRepository;
        if (repository.get(className) != null)
        {
            repository = (HashMap) repository.clone();
            repository.remove(className);
            poolRepository = repository;
        }
    }

    /**
     * Clears all instances from the pool.
     */
    public void clearPool()
    {
        poolRepository = new HashMap();
    }

    /**
     * Polls and recycles an object of the named class from the pool.
     *
     * @param className the name of the class.
     * @param params an array containing the parameters of the constructor.
     * @param signature an array containing the signature of the constructor.
     * @return the object or null.
     * @throws TurbineException if recycling fails.
     */
    private Object pollInstance(String className,
                                Object[] params,
                                String[] signature)
            throws TurbineException
    {
        PoolBuffer pool = (PoolBuffer) poolRepository.get(className);
        return (pool != null) ? pool.poll(params, signature) : null;
    }
}
