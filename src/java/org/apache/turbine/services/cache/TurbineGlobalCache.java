package org.apache.turbine.services.cache;


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


import java.io.IOException;

import org.apache.turbine.services.TurbineServices;

/**
 * This is a Facade class for GlobalCacheService.
 *
 * This class provides static methods that call related methods of the
 * implementation of the GlobalCacheService used by the System, according to
 * the settings in TurbineResources.
 *
 * @deprecated Use the Fulcrum Cache component instead.
 * @author <a href="mailto:sean@informage.net">Sean Legassick</a>
 * @version $Id$
 */
public abstract class TurbineGlobalCache
{
    /**
     * Utility method for accessing the service
     * implementation
     *
     * @return a GlobalCacheService implementation instance
     */
    protected static GlobalCacheService getService()
    {
        return (GlobalCacheService) TurbineServices
                .getInstance().getService(GlobalCacheService.SERVICE_NAME);
    }

    /**
     * Gets a cached object given its id (a String).
     *
     * @param id The String id for the object.
     * @return A CachedObject.
     * @exception ObjectExpiredException, if the object has expired in
     * the cache.
     */
    public static CachedObject getObject(String id)
            throws ObjectExpiredException
    {
        return getService().getObject(id);
    }

    /**
     * Adds an object to the cache.
     *
     * @param id The String id for the object.
     * @param o The object to add to the cache.
     */
    public static void addObject(String id,
                                 CachedObject o)
    {
        getService().addObject(id, o);
    }

    /**
     * Removes an object from the cache.
     *
     * @param id The String id for the object.
     */
    public static void removeObject(String id)
    {
        getService().removeObject(id);
    }

    /**
     * Returns the current size of the cache.
     * @return int representing current cache size in number of bytes
     */
    public static int getCacheSize()
            throws IOException
    {
        return getService().getCacheSize();
    }

    /**
     * Returns the number of objects in the cache.
     * @return int The current number of objects in the cache.
     */
    public static int getNumberOfObjects()
    {
        return getService().getNumberOfObjects();
    }
}
