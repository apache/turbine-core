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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.resources.TurbineResources;

/**
 * This Service functions as a Global Cache.  A global cache is a good
 * place to store items that you may need to access often but don't
 * necessarily need (or want) to fetch from the database everytime.  A
 * good example would be a look up table of States that you store in a
 * database and use throughout your application.  Since information
 * about States doesn't change very often, you could store this
 * information in the Global Cache and decrease the overhead of
 * hitting the database everytime you need State information.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:jon@clearink.com">Jon S. Stevens</a>
 * @author <a href="mailto:john@zenplex.com">John Thorhauer</a>
 * @version $Id$
 */
public class TurbineGlobalCacheService
    extends TurbineBaseService
    implements GlobalCacheService,
               Runnable
{
    /** The cache. **/
    private Hashtable cache = null;

    /** cacheCheckFrequency (default - 5 seconds) */
    private long cacheCheckFrequency =
        TurbineResources.getLong("cache.check.frequency", 5000);

    /**
     * Constructor.
     */
    public TurbineGlobalCacheService()
    {
    }

    /**
     * Called the first time the Service is used.
     */
    public void init()
        throws InitializationException
    {
        try
        {
            cache = new Hashtable(20);

            // Start housekeeping thread.
            Thread housekeeping = new Thread(this);
            // Indicate that this is a system thread. JVM will quit only when there
            // are no more active user threads. Settings threads spawned internally
            // by Turbine as daemons allows commandline applications using Turbine
            // to terminate in an orderly manner.
            housekeeping.setDaemon(true);
            housekeeping.start();

            setInit(true);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                "TurbineGlobalCacheService failed to initialize", e);
        }
    }

    /**
     * Returns an item from the cache.
     *
     * @param id The key of the stored object.
     * @return The object from the cache.
     * @exception ObjectExpiredException, when either the object is
     * not in the cache or it has expired.
     */
    public CachedObject getObject(String id)
        throws ObjectExpiredException
    {
        CachedObject obj = null;
        boolean stale = false;

        obj = (CachedObject) cache.get(id);

        if (obj == null)
        {
            // Not in the cache.
            throw new ObjectExpiredException();
        }

        if (obj.isStale())
        {
            // Expired.
            throw new ObjectExpiredException();
        }

        if (obj instanceof RefreshableCachedObject)
        {
            // notify it that it's being accessed.
            RefreshableCachedObject rco = (RefreshableCachedObject) obj;
            rco.touch();
        }

        return obj;
    }

    /**
     * Adds an object to the cache.
     *
     * @param id The key to store the object by.
     * @param o The object to cache.
     */
    public void addObject(String id,
                          CachedObject o)
    {
        // If the cache already contains the key, remove it and add
        // the fresh one.
        if ( cache.containsKey(id) )
        {
            cache.remove(id);
        }
        cache.put(id,o);
    }

    /**
     * Removes an object from the cache.
     *
     * @param id The String id for the object.
     */
    public void removeObject(String id)
    {
        cache.remove(id);
    }

    /**
     * Circle through the cache and remove stale objects.  Frequency
     * is determined by the cacheCheckFrequency property.
     */
    public void run()
    {
        while(true)
        {
            // Sleep for amount of time set in cacheCheckFrequency -
            // default = 5 seconds.
            try
            {
                Thread.sleep(cacheCheckFrequency);
            }
            catch(InterruptedException exc)
            {
            }

            clearCache();
        }
    }

    /**
     * Iterate through the cache and remove or refresh stale objects.
     */
    public void clearCache()
    {
        Vector refreshThese = new Vector(20);
        // Sync on this object so that other threads do not
        // change the Hashtable while enumerating over it.
        synchronized (this)
        {
            for ( Enumeration e = cache.keys(); e.hasMoreElements(); )
            {
                String key = (String) e.nextElement();
                CachedObject co = (CachedObject) cache.get(key);
                if (co instanceof RefreshableCachedObject)
                {
                    RefreshableCachedObject rco = (RefreshableCachedObject)co;
                    if (rco.isUntouched())
                        cache.remove(key);
                    else if (rco.isStale())
                        // Do refreshing outside of sync block so as not
                        // to prolong holding the lock on this object
                        refreshThese.addElement(key);
                }
                else if ( co.isStale() )
                {
                    cache.remove(key);
                }
            }
        }

        for ( Enumeration e = refreshThese.elements(); e.hasMoreElements(); )
        {
            String key = (String)e.nextElement();
            CachedObject co = (CachedObject)cache.get(key);
            RefreshableCachedObject rco = (RefreshableCachedObject)co;
            rco.refresh();
        }
    }

    /**
     * Returns the number of objects currently stored in the cache
     *
     * @return int number of object in the cache
     */
    public int getNumberOfObjects()
    {
        return cache.size();
    }

    /**
     * Returns the current size of the cache.
     *
     * @return int representing current cache size in number of bytes
     */
    public int getCacheSize()
        throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(cache);
        out.flush();
        //
        // Subtract 4 bytes from the length, because the serialization
        // magic number (2 bytes) and version number (2 bytes) are
        // both written to the stream before the object
        //
        int objectsize = baos.toByteArray().length - 4;
        return objectsize;
    }
}
