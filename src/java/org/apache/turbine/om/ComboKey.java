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

import java.util.ArrayList;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.ObjectUtils;

/**
 * This class can be used as an ObjectKey to uniquely identify an 
 * object within an application where the id  consists of multiple 
 * entities such a String[] representing a multi-column primary key.
 */
public class ComboKey extends ObjectKey  
{
    // might want to shift these to TR.props

    /**
     * The single character used to separate key values in a string.
     */
    public static final char SEPARATOR = ':';

    /**
     * The single character used to separate key values in a string.
     */
    public static final String SEPARATOR_STRING = ":";

    ArrayList tmpKeys;
    StringBuffer sbuf;
    SimpleKey[] key;

    /**
     * Creates an ComboKey whose internal representation will be
     * set later, through a set method
     */
    public ComboKey()
    {
        tmpKeys = new ArrayList(4);
        sbuf = new StringBuffer();
    }

    /**
     * Creates a ComboKey whose internal representation is an
     * array of SimpleKeys.  
     */
    public ComboKey(SimpleKey[] keys)
    {
        this();
        setValue(keys);        
    }
 
    /**
     * Creates a compound ComboKey whose internal representation is a
     * String array.  
     */
    public ComboKey(String[] keys) throws TurbineException
    {
        this();
        setValue(keys);
    }

    /**
     * Sets the internal representation to a String array.  
     */
    public ComboKey(String keys) throws TurbineException
    {   
        this();
        setValue(keys);
    }        

    /**
     * Sets the internal representation using an array of SimpleKeys.  
     */
    public void setValue(SimpleKey[] keys)
    {
        if ( this.key == null ) 
        {
            this.key = keys;            
        }
        else
        {
            for ( int i=0; i<this.key.length; i++ ) 
            {
                if ( this.key[i] == null ) 
                {
                    this.key[i] = keys[i];
                }
            }
        }        
    }

    /**
     * Sets the internal representation using a String array.  
     */
    private static String errMsg = 
        "This method cannot be used with an uninitialized ComboKey";
    public void setValue(String[] keys) throws TurbineException
    {
        if ( this.key == null ) 
        {
            throw new TurbineException(errMsg);
            /*
            this.key = new SimpleKey[keys.length];            
            for ( int i=0; i<keys.length; i++ ) 
            {
                this.key[i] = new SimpleKey(keys[i]);
            }
            */
        }
        else
        {
            for ( int i=0; i<this.key.length; i++ ) 
            {
                if ( this.key[i] == null && keys[i] != null ) 
                {
                    throw new TurbineException(errMsg);
                    // this.key[i] = new SimpleKey( keys[i] );
                }
                else 
                {
                    this.key[i].setValue( keys[i] );
                }
            }
        }        
    }

    /**
     * Sets the internal representation using a String of the 
     * form produced by the toString method.  
     */
    public void setValue(String keys) throws TurbineException
    {
        int previousIndex = -1;
        int indexOfSep = keys.indexOf(SEPARATOR);
        while ( indexOfSep != -1 ) 
        {
            if ( indexOfSep == 0) 
            {
                tmpKeys.add(null);
            }
            else if ( indexOfSep > 0 && indexOfSep < keys.length()-1 ) 
            {
                tmpKeys.add( keys.substring(previousIndex+1, indexOfSep) );
            }
        
            else if ( indexOfSep == keys.length()-1 ) 
            {
                tmpKeys.add(null);
            }        
            indexOfSep = keys.indexOf(SEPARATOR);
        }

        if ( this.key == null ) 
        {
            throw new TurbineException(errMsg);
            /*
            this.key = new SimpleKey[tmpKeys.size()];            
            for ( int i=0; i<this.key.length; i++ ) 
            {
                this.key[i] = new SimpleKey( (String)tmpKeys.get(i) );
            }
            */
        }
        else
        {
            for ( int i=0; i<this.key.length; i++ ) 
            {
                if ( this.key[i] == null && tmpKeys.get(i) != null ) 
                {
                    throw new TurbineException(errMsg);
                    // this.key[i] = new SimpleKey( (String)tmpKeys.get(i) );
                }
                else 
                {
                    this.key[i].setValue( (String)tmpKeys.get(i) );
                }
            }
        }        

        tmpKeys.clear();
    }


    public void setValue(ComboKey keys)
    {
        setValue((SimpleKey[])keys.getValue());
    }


    /**
     * Get the underlying object.
     */
    public Object getValue()
    {
        return key;
    }

    /**
     * This method will return true if the conditions for a looseEquals
     * are met and in addition no parts of the keys are null.
     */
    public boolean equals(Object keyObj)
    {
        boolean isEqual = false;

        if ( key != null ) 
        {
            // check that all keys are not null
            isEqual = true;
            SimpleKey[] keys = (SimpleKey[])key;
            for ( int i=0; i<keys.length && isEqual; i++ ) 
            {
                isEqual &= keys[i] != null && keys[i].getValue() != null;
            }

            isEqual &= looseEquals(keyObj);
        }

        return isEqual;
    }


    /**
     * keyObj is equal to this ComboKey if keyObj is a ComboKey, String, 
     * ObjectKey[], or String[] that contains the same information this key
     * contains. 
     * For example A String[] might be equal to this key, if this key was 
     * instantiated with a String[] and the arrays contain equal Strings.
     * Another example, would be if keyObj is an ComboKey that was 
     * instantiated with a ObjectKey[] and this ComboKey was instantiated with
     * a String[], but the ObjectKeys in the ObjectKey[] were instantiated 
     * with Strings that equal the Strings in this KeyObject's String[]
     * This method is not as strict as the equals method which does not
     * allow any null keys parts, while the internal key may not be null
     * portions may be, and the two object will be considered equal if
     * their null portions match. 
     */
    public boolean looseEquals(Object keyObj)
    {
        boolean isEqual = false;

        if ( key != null ) 
        {
            // Checks  a compound key (ObjectKey[] or String[]
            // based) with the delimited String created by the
            // toString() method.  Slightly expensive, but should be less
            // than parsing the String into its constituents.
            if (keyObj instanceof String)
            {
                isEqual =  toString().equals(keyObj);                
            }
            // check against a ObjectKey. Two keys are equal, if their 
            // internal keys equivalent.
            else if ( keyObj instanceof ComboKey) 
            {
                SimpleKey[] obj = (SimpleKey[])
                    ((ComboKey)keyObj).getValue();
                
                SimpleKey[] keys1 = (SimpleKey[])key;
                SimpleKey[] keys2 = (SimpleKey[])obj;
                isEqual = keys1.length == keys2.length;
                for ( int i=0; i<keys1.length && isEqual; i++) 
                {
                    isEqual &= ObjectUtils.equals(keys1[i], keys2[i]);
                }
            }        
            else if ( keyObj instanceof SimpleKey[] 
                      && key instanceof SimpleKey[] )
            {
                SimpleKey[] keys1 = (SimpleKey[])key;
                SimpleKey[] keys2 = (SimpleKey[])keyObj;
                isEqual = keys1.length == keys2.length;
                for ( int i=0; i<keys1.length && isEqual; i++) 
                {
                    isEqual &= ObjectUtils.equals(keys1[i], keys2[i]);
                }
            }
        }
        return isEqual;
    }

    public void appendTo(StringBuffer sb)
    {
      if ( key != null ) 
      {
        SimpleKey[] keys = (SimpleKey[])key;
        for ( int i=0; i<keys.length; i++) 
        {
            if ( i != 0 ) 
            {
                sb.append(SEPARATOR);    
            }
            if ( keys[i] != null ) 
            {
                keys[i].appendTo(sb);                
            }
        }
      }
    }

    /**
     * if the underlying key array is not null and the first element is
     * not null this method returns the hashcode of the first element
     * in the key.  Otherwise calls ObjectKey.hashCode()
     *
     * @return an <code>int</code> value
     */
    public int hashCode()
    {
        if ( key == null ) 
        {
            return super.hashCode();
        }
        
        SimpleKey sk = ((SimpleKey[])key)[0];
        if ( sk == null ) 
        {
            return super.hashCode();            
        }
        
        return sk.hashCode();
    }

    /**
     * A String that may consist of one section or multiple sections 
     * separated by a colon.
     */
    public String toString()
    {        
        if ( sbuf.length() > 0 ) 
        {
            sbuf.delete(0, sbuf.length());            
        }
        appendTo(sbuf);
        return sbuf.toString();
    }
}
