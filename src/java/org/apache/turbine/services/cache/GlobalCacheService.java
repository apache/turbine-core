package org.apache.turbine.services.cache;

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

import java.io.IOException;

import org.apache.turbine.services.Service;

/**
 * GlobalCacheService interface.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public interface GlobalCacheService
        extends Service
{
    String SERVICE_NAME = "GlobalCacheService";

    /**
     * Gets a cached object given its id (a String).
     *
     * @param id The String id for the object.
     * @return A CachedObject.
     * @exception ObjectExpiredException, if the object has expired in
     * the cache.
     */
    CachedObject getObject(String id)
            throws ObjectExpiredException;

    /**
     * Adds an object to the cache.
     *
     * @param id The String id for the object.
     * @param o The object to add to the cache.
     */
    void addObject(String id, CachedObject o);

    /**
     * Removes an object from the cache.
     *
     * @param id The String id for the object.
     */
    void removeObject(String id);

    /**
     * Returns the current size of the cache.
     * @return int representing current cache size in number of bytes
     */
    int getCacheSize()
            throws IOException;

    /**
     * Returns the number of objects in the cache.
     * @return int The current number of objects in the cache.
     */
    int getNumberOfObjects();

    /**
     * Flush the cache of all objects.
     */
    void flushCache();
}
