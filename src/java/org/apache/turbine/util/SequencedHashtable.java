package org.apache.turbine.util;

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

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link java.util.Hashtable} whose keys are sequenced.  The
 * sequencing of the keys allow easy access to the values in the order
 * which they were added in.  This class is thread safe.
 * <p>
 * Implementing the List interface is not possible due to a instance
 * method name clash between the Collection and the List interface:
 *
 * <table>
 * <tr><td>Collections</td><td>boolean remove(Object o)</td></tr>
 * <tr><td>Lists</td><td>Object remove(Object o)</td></tr>
 * </table>
 *
 * So one cannot implement both interfaces at the same, which is unfortunate
 * because the List interface would be very nice in conjuction with Velocity.
 * <p>
 * A slightly more complex implementation and interface could involve
 * the use of a list of <code>Map.Entry</code> objects.
 *
 * @version $Id$
 * @deprecated Use SequencedHashMap from the commons collections.
 */
public class SequencedHashtable extends Hashtable
{
    /**
     * Indicator for an unknown index.
     */
    private static final int UNKNOWN_INDEX = -1;

    /**
     * The sequence used to keep track of the hash keys.  Younger objects are
     * kept towards the end of the list.  Does not allow duplicates.
     */
    private LinkedList keySequence;

    /**
     * Creates a new instance with default storage.
     */
    public SequencedHashtable()
    {
        keySequence = new LinkedList();
    }

    /**
     * Creates a new instance with the specified storage.
     *
     * @param size The storage to allocate up front.
     */
    public SequencedHashtable(int size)
    {
        super(size);
        keySequence = new LinkedList();
    }

    /**
     * Clears all elements.
     */
    public synchronized void clear()
    {
        super.clear();
        keySequence.clear();
    }

    /**
     * Creates a shallow copy of this object, preserving the internal
     * structure by copying only references.  The keys, values, and
     * sequence are not <code>clone()</code>'d.
     *
     * @return A clone of this instance.
     */
    public synchronized Object clone()
    {
        SequencedHashtable seqHash = (SequencedHashtable) super.clone();
        seqHash.keySequence = (LinkedList) keySequence.clone();
        return seqHash;
    }

    /**
     * Returns the key at the specified index.
     */
    public Object get(int index)
    {
        return keySequence.get(index);
    }

    /**
     * Returns the value at the specified index.
     */
    public Object getValue(int index)
    {
        return get(get(index));
    }

    /**
     * Returns the index of the specified key.
     */
    public int indexOf(Object key)
    {
        return keySequence.indexOf(key);
    }

    /**
     * Returns a key iterator.
     */
    public Iterator iterator()
    {
        return keySequence.iterator();
    }

    /**
     * Returns the last index of the specified key.
     */
    public int lastIndexOf(Object key)
    {
        return keySequence.lastIndexOf(key);
    }

    /**
     * Returns the ordered sequence of keys.
     *
     * This method is meant to be used for retrieval of Key / Value pairs
     * in e.g. Velocity:
     * <PRE>
     * ## $table contains a sequenced hashtable
     * #foreach ($key in $table.sequence())
     * &lt;TR&gt;
     * &lt;TD&gt;Key: $key&lt;/TD&gt;
     * &lt;/TD&gt;Value: $table.get($key)&lt;/TD&gt;
     * &lt;/TR&gt;
     * #end
     * </PRE>
     *
     * @return The ordered list of keys.
     */
    public List sequence()
    {
        return keySequence;
    }

    /**
     * Stores the provided key/value pair.  Freshens the sequence of existing
     * elements.
     *
     * @param key   The key to the provided value.
     * @param value The value to store.
     * @return      The previous value for the specified key, or
     *              <code>null</code> if none.
     */
    public synchronized Object put(Object key, Object value)
    {
        Object prevValue = super.put(key, value);
        freshenSequence(key, prevValue);
        return prevValue;
    }

    /**
     * Freshens the sequence of the element <code>value</code> if
     * <code>value</code> is not <code>null</code>.
     *
     * @param key   The key whose sequence to freshen.
     * @param value The value whose existance to check before removing the old
     *              key sequence.
     */
    protected void freshenSequence(Object key, Object value)
    {
        if (value != null)
        {
            // Freshening existing element's sequence.
            keySequence.remove(key);
        }
        keySequence.add(key);
    }

    /**
     * Stores the provided key/value pairs.
     *
     * @param t The key/value pairs to store.
     */
    public synchronized void putAll(Map t)
    {
        Set set = t.entrySet();
        for (Iterator iter = set.iterator(); iter.hasNext();)
        {
            Map.Entry e = (Map.Entry) iter.next();
            put(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes the element at the specified index.
     *
     * @param index The index of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    public Object remove(int index)
    {
        return remove(index, null);
    }

    /**
     * Removes the element with the specified key.
     *
     * @param key   The <code>Map</code> key of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    public Object remove(Object key)
    {
        return remove(UNKNOWN_INDEX, key);
    }

    /**
     * Removes the element with the specified key or index.
     *
     * @param index The index of the object to remove, or
     *              <code>UNKNOWN_INDEX</code> if not known.
     * @param key   The <code>Map</code> key of the object to remove.
     * @return      The previous value coressponding the <code>key</code>, or
     *              <code>null</code> if none existed.
     */
    private final synchronized Object remove(int index, Object key)
    {
        if (index == UNKNOWN_INDEX) index = indexOf(key);
        if (key == null) key = get(index);
        if (index != UNKNOWN_INDEX) keySequence.remove(index);
        return super.remove(key);
    }

    /**
     * Slightly cheaper implementation of <code>values()</code> method.
     */
    public Collection values()
    {
        return keySequence;
    }
}
