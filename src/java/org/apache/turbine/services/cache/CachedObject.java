package org.apache.turbine.services.cache;

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

import java.io.Serializable;
import org.apache.turbine.services.resources.TurbineResources;

/**
 * Wrapper for an object you want to store in a cache for a period of
 * time.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class CachedObject
    implements java.io.Serializable
{

    /** Cache the object with the Default TTL */
    public static final int DEFAULT = 0;

    /** Do not expire the object */
    public static final int FOREVER = -1;

    /** The object to be cached. */
    private Object contents = null;

    /** Default age (30 minutes). */
    private long defaultage =
        TurbineResources.getLong("cachedobject.defaultage", 1800000);

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
    public CachedObject(Object o,
                        long expires)
    {
        if ( expires == DEFAULT )
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
     * Set the stale status for the object.
     *
     * @param stale Whether the object is stale or not.
     */
    public synchronized void setStale ( boolean stale )
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
        if(expires == FOREVER)
        {
            return false;
        }

        setStale( (System.currentTimeMillis() - created) > expires );
        return getStale();
    }
}
