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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class implements a Stack for String objects.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 * @deprecated This class will be removed after the 2.3 release. It is
 *             not part of the Web Framework scope. If you need this class,
 *             please lobby for inclusion in the <a href="http://jakarta.apache.org/commons/">commons-collections</a> component repository.
 */
public class StringStackBuffer implements Serializable
{
    /** The stack. */
    private Stack stk = null;

    /**
     * Constructor.
     */
    public StringStackBuffer()
    {
        stk = new Stack();
    }

    /**
     * Adds the String to the collection if it does not already
     * contain it.
     *
     * @param s A String.
     * @return A StringStackBuffer.
     */
    public StringStackBuffer add(String s)
    {
        if (s != null && !contains(s))
            stk.push(s);
        return this;
    }

    /**
     * Adds all Strings in the given StringStackBuffer to the collection
     * (skipping those it already contains)
     *
     * @param s A StringStackBuffer.
     * @return A StringStackBuffer.
     */
    public StringStackBuffer addAll(StringStackBuffer s)
    {
        for (Iterator it = s.stk.iterator(); it.hasNext();)
        {
            add((String) it.next());
        }
        return this;
    }

    /**
     * Clears the Stack.
     *
     */
    public void clear()
    {
        stk.clear();
    }

    /**
     * Does the Stack contain this String?
     *
     * @param s A String.
     * @return True if the Stack contains this String.
     */
    public boolean contains(String s)
    {
        return (stk.search(s) != -1);
    }

    /**
     * Is the Stack empty?
     * @return True if the Stack is empty.
     */
    public boolean empty()
    {
        return stk.empty();
    }

    /**
     * Get a String off the Stack at a certain position.
     *
     * @param i An int with the position.
     * @return A String.
     */
    public String get(int i)
    {
        return (String) stk.elementAt(i);
    }

    /**
     * What is the size of the Stack?
     *
     * @return An int, the size of the Stack.
     */
    public int size()
    {
        return stk.size();
    }

    /**
     * Converts the stack to a single {@link java.lang.String} with no
     * separator.
     *
     * @return The stack elements as a single block of text.
     */
    public String toString()
    {
        return toString("");
    }

    /**
     * Converts the stack to a single {@link java.lang.String}.
     *
     * @param separator The text to use as glue between elements in
     * the stack.
     * @return The stack elements--glued together by
     * <code>separator</code>--as a single block of text.
     */
    public String toString(String separator)
    {
        String s;
        if (size() > 0)
        {
            if (separator == null)
            {
                separator = "";
            }

            // Determine what size to pre-allocate for the buffer.
            int totalSize = 0;
            for (int i = 0; i < stk.size(); i++)
            {
                totalSize += get(i).length();
            }
            totalSize += (stk.size() - 1) * separator.length();

            StringBuffer sb = new StringBuffer(totalSize).append(get(0));
            for (int i = 1; i < stk.size(); i++)
            {
                sb.append(separator).append(get(i));
            }
            s = sb.toString();
        }
        else
        {
            s = "";
        }
        return s;
    }

    /**
     * Compares two StringStackBuffers.  Considered equal if the toString()
     * methods are equal.
     *
     */
    public boolean equals(Object ssbuf)
    {
        boolean isEquiv = false;
        if (ssbuf == null || !(ssbuf instanceof StringStackBuffer))
        {
            isEquiv = false;
        }

        else if (ssbuf == this)
        {
            isEquiv = true;
        }

        else if (this.toString().equals(ssbuf.toString()))
        {
            isEquiv = true;
        }

        return isEquiv;
    }

    public String[] toStringArray()
    {
        String[] ss = new String[size()];
        for (int i = 0; i < size(); i++)
        {
            ss[i] = get(i);
        }
        return ss;
    }
}





