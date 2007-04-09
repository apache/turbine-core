package org.apache.turbine.util;

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

import org.apache.commons.collections.map.LRUMap;

/**
 * A fixed length object cache implementing the LRU algorithm.  Convenient for
 * buffering recently used objects.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 * 
 * @deprecated use LRUMap from commons-collections directly
 */
public class BufferCache
    extends LRUMap
{
    /** Serial Version UID */
    private static final long serialVersionUID = 5206274963401520445L;

    /**
     * The default maximum cache size.
     */
    private static final int DEFAULT_MAX_SIZE = 35;

    /**
     * Creates a new instance with default storage buffer pre-allocated.
     */
    public BufferCache()
    {
        this(DEFAULT_MAX_SIZE);
    }

    /**
     * Creates a new instance with the specified storage buffer pre-allocated.
     *
     * @param maxSize The maximum size of the cache.
     */
    public BufferCache(int maxSize)
    {
        super(maxSize);
    }

    /**
     * Stores the provided key/value pair, freshening its list index if the
     * specified key already exists.
     *
     * @param key   The key to the provided value.
     * @param value The value to store.
     * @return      The previous value for the specified key, or
     *              <code>null</code> if none.
     */
    public synchronized Object put(Object key, Object value)
    {
        return super.put(key, value);
    }

    /**
     * Retrieves the value associated with the provided key, freshening the
     * sequence of the key as well.
     *
     * @param key The key whose value to retrieve.
     * @return    The keyed value.
     */
    public synchronized Object get(Object key)
    {
        return super.get(key);
    }
}
