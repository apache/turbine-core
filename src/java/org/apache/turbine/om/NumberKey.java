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

import java.math.BigDecimal;
import org.apache.turbine.util.TurbineException;

/**
 * This class can be used as an ObjectKey to uniquely identify an
 * object within an application where the id  consists
 * of a single entity such a GUID or the value of a db row's primary key.
 */
public class NumberKey extends SimpleKey
{
    /**
     * Creates a NumberKey whose internal representation will be
     * set later, through a set method
     */
    public NumberKey()
    {
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     */
    public NumberKey(String key)
    {
        this.key = new BigDecimal(key);
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     */
    public NumberKey(BigDecimal key)
    {
        this.key = key;
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     */
    public NumberKey(NumberKey key)
    {
        this.key = key.getValue();
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     */
    public NumberKey(long key)
    {
        this.key = new BigDecimal(key);
    }

    /**
     * Creates a NumberKey equivalent to <code>key</code>.
     * Convenience only. Not very efficient at all.
     */
    public NumberKey(int key)
    {
        this.key = new BigDecimal(new Integer(key).toString());
    }

    /**
     * Sets the internal representation using a String representation
     * of a number
     */
    public void setValue(String key) throws NumberFormatException
    {
        this.key = new BigDecimal(key);
    }

    /**
     * Sets the underlying object
     */
    public void setValue(BigDecimal key)
    {
        this.key = key;
    }

    /**
     * Sets the internal representation to the same object used
     * by key.
     */
    public void setValue(NumberKey key)
    {
        this.key = (key == null ? null : key.getValue());
    }

    /**
     * Access the underlying BigDecimal object.
     *
     * @return a <code>BigDecimal</code> value
     */
    public BigDecimal getBigDecimal()
    {
        return (BigDecimal)key;
    }

    /**
     * keyObj is equal to this NumberKey if keyObj is a NumberKey or String
     * that contains the same information this key contains.
     */
    public boolean equals(Object keyObj)
    {
        boolean isEqual = false;

        if ( key != null )
        {
            if (keyObj instanceof String)
            {
                isEqual =  toString().equals(keyObj);
            }
            // check against a NumberKey. Two keys are equal, if their
            // internal keys equivalent.
            else if ( keyObj instanceof NumberKey  )
            {
                Object obj = ((NumberKey)keyObj).getValue();
                isEqual =  key.equals(obj);
            }
        }
        return isEqual;
    }

    /**
     * Invokes the toString() method on the object.
     */
    public String toString()
    {
        if ( key != null )
        {
            return key.toString();
        }
        return "";
    }
}
