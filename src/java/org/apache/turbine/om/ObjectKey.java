package org.apache.turbine.om;

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
import org.apache.turbine.util.TurbineException;

/**
 * This class can be used to uniquely identify an object within 
 * an application.  There are four subclasses: StringKey, NumberKey,
 * and DateKey, and ComboKey which is a Key made up of a combination 
 * ofthe first three.
 */
public abstract class ObjectKey implements Serializable, Comparable
{
    /**
     * The underlying key value.
     */
    protected Object key;

    /**
     * Initializes the internal key value to <code>null</code>.
     */
    protected ObjectKey()
    {
        key = null;
    }

    /**
     * Returns the hashcode of the underlying value (key), if key is
     * not null.  Otherwise calls Object.hashCode()
     *
     * @return an <code>int</code> value
     */
    public int hashCode()
    {
        if ( key == null ) 
        {
            return super.hashCode();
        }
        return key.hashCode();
    }

    /**
     * Get the underlying object.
     */
    public Object getValue()
    {
        return key;
    }

    /**
     * Appends a String representation of the key to a buffer.
     *
     * @param sb a <code>StringBuffer</code>
     */
    public void appendTo(StringBuffer sb)
    {
        sb.append(key.toString());
    }

    /**
     * Implements the compareTo method.
     *
     * @param obj the object to compare to this object
     */
    public int compareTo(Object obj)
    {
        return toString().compareTo(obj.toString());
    }

    /**
     * Reset the underlying object using a String.
     *
     * @param s a <code>String</code> value
     * @exception TurbineException if an error occurs
     */
    public abstract void setValue(String s) throws TurbineException;
}
