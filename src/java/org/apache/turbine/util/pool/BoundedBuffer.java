package org.apache.turbine.util.pool;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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
 * Efficient array-based bounded buffer class.
 * Adapted from CPJ, chapter 8, which describes design.
 * Originally written by Doug Lea and released into the public domain.
 * <p>[<a href="http://gee.cs.oswego.edu/dl/classes/EDU/oswego/cs/dl/util/concurrent/intro.html"> Introduction to this package. </a>] <p>
 *
 * @author <a href="mailto:ilkka.priha@simsoft.fi">Ilkka Priha</a>
 * @version $Id$
 */
public class BoundedBuffer
{
    /**
     * The default capacity.
     */
    public static final int DEFAULT_CAPACITY = 1024;

    protected final Object[] array_;      // the elements

    protected int takePtr_ = 0;            // circular indices
    protected int putPtr_ = 0;

    protected int usedSlots_ = 0;          // length
    protected int emptySlots_;             // capacity - length

    /**
     * Creates a buffer with the given capacity.
     *
     * @param capacity the capacity.
     * @throws IllegalArgumentException if capacity less or equal to zero.
     */
    public BoundedBuffer(int capacity)
            throws IllegalArgumentException
    {
        if (capacity <= 0)
        {
            throw new IllegalArgumentException();
        }

        array_ = new Object[capacity];
        emptySlots_ = capacity;
    }

    /**
     * Creates a buffer with the default capacity
     */
    public BoundedBuffer()
    {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Returns the number of elements in the buffer.
     * This is only a snapshot value, that may change
     * immediately after returning.
     *
     * @return the size.
     */
    public synchronized int size()
    {
        return usedSlots_;
    }

    /**
     * Returns the capacity of the buffer.
     *
     * @return the capacity.
     */
    public int capacity()
    {
        return array_.length;
    }

    /**
     * Peeks, but does not remove the top item from the buffer.
     *
     * @return the object or null.
     */
    public synchronized Object peek()
    {
        if (usedSlots_ > 0)
            return array_[takePtr_];
        else
            return null;
    }

    /**
     * Puts an item in the buffer only if there is capacity available.
     *
     * @param item the item to be inserted.
     * @return true if accepted, else false.
     */
    public synchronized boolean offer(Object x)
    {
        if (x == null)
            throw new IllegalArgumentException();

        if (emptySlots_ > 0)
        {
            --emptySlots_;
            array_[putPtr_] = x;
            if (++putPtr_ >= array_.length)
                putPtr_ = 0;
            usedSlots_++;
            return true;
        }
        else
            return false;
    }

    /**
     * Polls and removes the top item from the buffer if one is available.
     *
     * @return the oldest item from the buffer, or null if the buffer is empty.
     */
    public synchronized Object poll()
    {
        if (usedSlots_ > 0)
        {
            --usedSlots_;
            Object old = array_[takePtr_];
            array_[takePtr_] = null;
            if (++takePtr_ >= array_.length)
                takePtr_ = 0;
            emptySlots_++;
            return old;
        }
        else
            return null;
    }
}
