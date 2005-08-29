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

/**
 * The idea of the RefreshableCachedObject is that, rather than
 * removing items from the cache when they become stale, we'll tell them to
 * refresh themselves instead.  That way they'll always be in the
 * cache, and the code to refresh them will be run by the background
 * thread rather than by a user request thread.  You can also set a TTL (Time
 * To Live) for the object.  This way, if the object hasn't been touched
 * for the TTL period, then it will be removed from the cache.
 *
 * This extends CachedObject and provides a method for refreshing the
 * cached object, and resetting its expire time.
 *
 * @author <a href="mailto:nissim@nksystems.com">Nissim Karpenstein</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class RefreshableCachedObject
        extends CachedObject
{

    /**
     * How long to wait before removing an untouched object from the cache.
     * Negative numbers mean never remove (the default).
     */
    private long timeToLive = -1;

    /**
     * The last time the Object was accessed from the cache.
     */
    private long lastAccess;

    /**
     * Constructor; sets the object to expire in the default time (30
     * minutes).
     *
     * @param o The object you want to cache.
     */
    public RefreshableCachedObject(Refreshable o)
    {
        super(o);
        lastAccess = System.currentTimeMillis();
    }

    /**
     * Constructor.
     *
     * @param o The object to cache.
     * @param expires How long before the object expires, in ms,
     * e.g. 1000 = 1 second.
     */
    public RefreshableCachedObject(Refreshable o,
                                   long expires)
    {
        super(o, expires);
        lastAccess = System.currentTimeMillis();
    }

    /**
     * Sets the timeToLive value
     *
     * @param timeToLive the new Value in milliseconds
     */
    public synchronized void setTTL(long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    /**
     * Gets the timeToLive value.
     *
     * @return The current timeToLive value (in milliseconds)
     */
    public synchronized long getTTL()
    {
        return timeToLive;
    }

    /**
     * Sets the last acccess time to the current time.
     */
    public synchronized void touch()
    {
        lastAccess = System.currentTimeMillis();
    }

    /**
     * Returns true if the object hasn't been touched
     * in the previous TTL period.
     */
    public synchronized boolean isUntouched()
    {
        if (timeToLive < 0)
            return false;

        if (lastAccess + timeToLive < System.currentTimeMillis())
            return true;
        else
            return false;
    }

    /**
     * Refresh the object and the created time.
     */
    public void refresh()
    {
        Refreshable r = (Refreshable) getContents();
        synchronized (this)
        {
            created = System.currentTimeMillis();
            r.refresh();
        }
    }
}
