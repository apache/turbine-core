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

import java.util.Date;
import org.apache.turbine.util.TurbineException;

/**
 * This class can be used as an ObjectKey to uniquely identify an 
 * object within an application where the id is a Date.
 */
public class DateKey extends SimpleKey 
{

    /**
     * Creates an DateKey whose internal representation will be
     * set later, through a set method
     */
    public DateKey()
    {
    }

    /**
     * Creates a DateKey whose internal representation is a Date
     * given by the long number given by the String
     */
    public DateKey(String key) throws NumberFormatException 
    {
        this.key = new Date(Long.parseLong(key));
    }

    /**
     * Creates a DateKey
     */
    public DateKey(Date key)
    {
        this.key = key;
    }

    /**
     * Creates a DateKey that is equivalent to key.
     */
    public DateKey(DateKey key)
    {
        this.key = key.getValue();
    }

    /**
     * Sets the internal representation to a String
     */
    public void setValue(String key)
    {
        this.key = new Date(Long.parseLong(key));
    }

    /**
     * Sets the internal representation to the same object used
     * by key.
     */
    public void setValue(DateKey key)
    {
        if (key != null)
            this.key = key.getValue();
        else
            this.key = null;
    }

    /**
     * Access the underlying Date object.
     *
     * @return a <code>Date</code> value
     */
    public Date getDate()
    {
        return (Date)key;
    }

    /**
     * keyObj is equal to this DateKey if keyObj is a DateKey or String 
     * that contains the same information this key contains.  Two ObjectKeys
     * that both contain null values are not considered equal.
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
            // check against a DateKey. Two keys are equal, if their 
            // internal keys equivalent.
            else if ( keyObj instanceof DateKey) 
            {
                Object obj = ((DateKey)keyObj).getValue();
                isEqual =  key.equals(obj);                
            }        
        }        
        return isEqual;
    }

    public String toString()
    {
        if ( key != null ) 
        {
            return key.toString();
        }
        return "";
    }
}
