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

import java.io.Serializable;

import org.apache.turbine.Turbine;

/**
 * Wrapper for an object you want to store in a cache for a period of
 * time.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class CachedObject
        implements Serializable
{

    /** Cache the object with the Default TTL */
    public static final int DEFAULT = 0;

    /** Do not expire the object */
    public static final int FOREVER = -1;

    /** The object to be cached. */
    private Object contents = null;

    /** Default age (30 minutes). */
    private long defaultage =
            Turbine.getConfiguration()
            .getLong("cachedobject.defaultage", 1800000);

    /** When created. **/
    protected long created = 0;

    /** When it expires. **/
    private long expires = 0;

    /** Is this object stale/expired? */
    private boolean stale = false;

    /**
     * Constructor; sets the object to expire in the default time (30
     * minutes).
     *
     * @param o The object you want to cache.
     */
    public CachedObject(Object o)
    {
        this.contents = o;
        this.expires = defaultage;
        this.created = System.currentTimeMillis();
    }

    /**
     * Constructor.
     *
     * @param o The object to cache.
     * @param expires How long before the object expires, in ms,
     * e.g. 1000 = 1 second.
     */
    public CachedObject(Object o, long expires)
    {
        if (expires == DEFAULT)
        {
            this.expires = defaultage;
        }

        this.contents = o;
        this.expires = expires;
        this.created = System.currentTimeMillis();
    }

    /**
     * Returns the cached object.
     *
     * @return The cached object.
     */
    public Object getContents()
    {
        return contents;
    }

    /**
     * Returns the creation time for the object.
     *
     * @return When the object was created.
     */
    public long getCreated()
    {
        return created;
    }

    /**
     * Returns the expiration time for the object.
     *
     * @return When the object expires.
     */
    public long getExpires()
    {
        return expires;
    }

    /**
     * Set the expiration interval for the object.
     *
     * @param expires Expiration interval in millis ( 1 second = 1000 millis)
     */
    public void setExpires(long expires)
    {
        if (expires == DEFAULT)
        {
            this.expires = defaultage;
        }
        else
        {
            this.expires = expires;
        }
        if (expires == FOREVER)
        {
            setStale(false);
        }
        else
        {
            setStale((System.currentTimeMillis() - created) > expires);
        }
    }

    /**
     * Set the stale status for the object.
     *
     * @param stale Whether the object is stale or not.
     */
    public synchronized void setStale(boolean stale)
    {
        this.stale = stale;
    }

    /**
     * Get the stale status for the object.
     *
     * @return Whether the object is stale or not.
     */
    public synchronized boolean getStale()
    {
        return stale;
    }

    /**
     * Is the object stale?
     *
     * @return True if the object is stale.
     */
    public synchronized boolean isStale()
    {
        if (expires == FOREVER)
        {
            return false;
        }

        setStale((System.currentTimeMillis() - created) > expires);
        return getStale();
    }
}
