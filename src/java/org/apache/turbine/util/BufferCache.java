package org.apache.turbine.util;

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


/**
 * A fixed length object cache implementing the LRU algorithm.  Convenient for
 * buffering recently used objects.
 *
 * @author <a href="mailto:dlr@collab.net">Daniel Rall</a>
 * @version $Id$
 */
public class BufferCache extends SequencedHashtable
{
    /**
     * The default maximum cache size.
     */
    private static final int DEFAULT_MAX_SIZE = 35;

    /**
     * The size of the cache.  The newest elements in the sequence are kept
     * toward the end.
     */
    private int maxSize;

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
        this.maxSize = maxSize;
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
        int size = size();
        if (size > 0 && size + 1 >= maxSize)
        {
            // Stay within constraints of allocated buffer by releasing the
            // eldest buffered object.
            remove(0);
        }
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
        Object value = super.get(key);
        if (value != null)
        {
            freshenSequence(key, value);
        }
        return value;
    }
}
