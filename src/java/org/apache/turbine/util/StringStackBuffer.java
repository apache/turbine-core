package org.apache.turbine.util;

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

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

/**
 * This class implements a Stack for String objects.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
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





