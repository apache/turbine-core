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
import java.math.BigDecimal;

/**
 * This class contains attributes and methods that are used by all
 * business objects within the system.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @version $Id$
 */
public abstract class BaseObject implements Persistent, Serializable
{
    public static final int NEW_ID = -1;
    
    /**
     * attribute to determine if this object has previously been saved.
     */
    private boolean is_new = true;

    /**
     * The unique id for the object which can be used for persistence.
     */
    private ObjectKey primaryKey = null;

    /**
     * A flag which can be set to indicate that an object has been
     * modified, since it was last retrieved from the persistence
     * mechanism.
     */
    private boolean modified = false;

    /**
     * getter for the object primaryKey.
     *
     * @return the object primaryKey as an Object
     */
    public ObjectKey getPrimaryKey()
    {
        return primaryKey;
    }

    /**
     * Attempts to return the object primaryKey as an int.
     *
     * @return the object primaryKey as an int; 
     * returns -1 if primaryKey was not set
     * or could not be represented as an int.
     *
     * @deprecated Use getPrimaryKey() instead.  Refer to
     * {@link org.apache.turbine.om.ObjectKey} for more information on type
     * conversion.
     */
    public int getPrimaryKeyAsInt()
    {
        int pkInt = NEW_ID;
        try
        {
            pkInt = Integer.parseInt(getPrimaryKey().toString());
        }
        catch (Exception e) {}
        return pkInt;
    }

    /**
     * Attempts to return the object primaryKey as a long.
     *
     * @return the object primaryKey as a long; 
     * returns -1 if primaryKey was not set
     * or could not be represented as a long.
     *
     * @deprecated Use getPrimaryKey() instead.  Refer to
     * {@link org.apache.turbine.om.ObjectKey} for more information on type
     * conversion.
     */
    public long getPrimaryKeyAsLong()
    {
        long pkLong = (long)NEW_ID;
        try
        {
            pkLong = Long.parseLong(getPrimaryKey().toString());
        }
        catch (Exception e) {}
        return pkLong;
    }

    /**
     * Attempts to return the object primaryKey as a BigDecimal.
     *
     * @return the object primaryKey as a BigDecimal; 
     * returns -1 if primaryKey was not
     * set or could not be represented as a BigDecimal.
     *
     * @deprecated Use getPrimaryKey() instead.  Refer to
     * {@link org.apache.turbine.om.ObjectKey} for more information on type
     * conversion.
     */
    public BigDecimal getPrimaryKeyAsBigDecimal()
    {
        BigDecimal bd = null;
        try
        {
            bd = new BigDecimal(getPrimaryKey().toString());
        }
        catch (Exception e)
        {
            bd = new BigDecimal(NEW_ID);
        }
        return bd;
    }

    /**
     * Gets the object primaryKey as a String.
     *
     * @return the object primaryKey as a String; 
     * returns null if primaryKey was not
     * set.
     *
     * @deprecated Use getPrimaryKey() instead.  Refer to
     * {@link org.apache.turbine.om.ObjectKey} for more information on type
     * conversion.
     */
    public String getPrimaryKeyAsString()
    {
        if (getPrimaryKey() == null) return null;
        return getPrimaryKey().toString();
    }


    /**
     * Returns whether the object has been modified.
     *
     * @return True if the object has been modified.
     */
    public boolean isModified()
    {
        return modified;
    }

    /**
     * Returns whether the object has ever been saved.  This will
     * be false, if the object was retrieved from storage or was created
     * and then saved.
     *
     * @return true, if the object has never been persisted.
     */
    public boolean isNew()
    {
        return is_new;
    }


    /**
     * Setter for the isNew attribute.  This method will be called
     * by Torque-generated children and Peers.
     *
     * @param b, the state of the object.
     */
    public void setNew(boolean b)
    {
        this.is_new = b;
    }

    /**
     * Sets the primaryKey for the object as an int.
     *
     * @param primaryKey The new primaryKey for the object.
     * @exception Exception, This method will not throw any exceptions
     * but this allows for children to override the method more easily 
     *
     * @deprecated
     */
    public void setPrimaryKey(int primaryKey) throws Exception
    {
        this.primaryKey = new NumberKey(BigDecimal.valueOf((long)primaryKey));
    }

    /**
     * Sets the PrimaryKey for the object as an long.
     *
     * @param PrimaryKey The new PrimaryKey for the object.
     * @exception Exception, This method will not throw any exceptions
     * but this allows for children to override the method more easily 
     *
     * @deprecated
     */
    public void setPrimaryKey(long primaryKey) throws Exception
    {
        this.primaryKey = new NumberKey(String.valueOf(primaryKey));
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param PrimaryKey The new PrimaryKey for the object.
     * @exception Exception, This method will not throw any exceptions
     * but this allows for children to override the method more easily 
     *
     */
    public void setPrimaryKey(String primaryKey) throws Exception
    {
        this.primaryKey = new StringKey(primaryKey);
    }

    /**
     * Sets the PrimaryKey for the object as an Object.
     *
     * @param PrimaryKey The new PrimaryKey for the object.
     * @exception Exception, This method will not throw any exceptions
     * but this allows for children to override the method more easily 
     *
     */
    public void setPrimaryKey(SimpleKey[] primaryKey) throws Exception
    {
        this.primaryKey = new ComboKey(primaryKey);
    }

    /**
     * Sets the PrimaryKey for the object as an Object.
     *
     * @param ObjectKey The new PrimaryKey for the object.
     */
    public void setPrimaryKey(ObjectKey primaryKey) throws Exception
    {
        this.primaryKey = primaryKey;
    }

    /**
     * Sets the modified state for the object.
     *
     * @param m The new modified state for the object.
     */
    public void setModified(boolean m)
    {
        modified = m;
    }

    /**
     * Sets the modified state for the object to be false.
     */
    public void resetModified()
    {
        modified = false;
    }

    /**
     * Saves the object. Must be overridden if called.
     */
    private static String errMsg = "method must be overridden if called";
    public void save()
        throws Exception
    {
        throw new Error("BaseObject.save: " + errMsg); 
    }

    /**
     * Retrieves a field from the object by name. Must be overridden if called.
     * BaseObject's implementation will throw an Error.
     *
     * @param field The name of the field to retrieve.
     * @return The retrieved field value
     *
     */
    public Object getByName(String field)
    {
        throw new Error("BaseObject.getByName: " + errMsg); 
    }

    /**
     * Retrieves a field from the object by name passed in
     * as a String.  Must be overridden if called.
     * BaseObject's implementation will throw an Error.
     */
    public Object getByPeerName(String name)
    {
        throw new Error("BaseObject.getByPeerName: " + errMsg); 
    }

    /**
     * Retrieves a field from the object by position as specified
     * in a database schema for example.  Must be overridden if called.
     * BaseObject's implementation will throw an Error.
     */
    public Object getByPosition(int pos)
    {
        throw new Error("BaseObject.getByPosition: " + errMsg);
    }

    /**
     * Compares this with another <code>BaseObject</code> instance.  If
     * <code>obj</code> is an instance of <code>BaseObject</code>, delegates to
     * <code>equals(BaseObject)</code>.  Otherwise, returns <code>false</code>.
     *
     * @param obj The object to compare to.
     * @return    Whether equal to the object specified.
     */
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof BaseObject)
        {
            return equals((BaseObject)obj);
        }
        else
        {
            return false;
        }
    }

    /**
     * Compares the primary key of this instance with the key of another.
     *
     * @param bo The object to compare to.
     * @return   Whether the primary keys are equal.
     */
    public boolean equals(BaseObject bo)
    {
        if (bo == null)
        {
            return false;
        }
        if (this == bo)
        {
            return true;
        }
        else if (getPrimaryKey() == null || bo.getPrimaryKey() == null)
        {
            return false;
        }
        else
        {
            return getPrimaryKey().equals(bo.getPrimaryKey());
        }
    }

    /**
     * If the primary key is not <code>null</code>, return the hashcode of the
     * primary key.  Otherwise calls <code>Object.hashCode()</code>.
     *
     * @return an <code>int</code> value
     */
    public int hashCode()
    {
        ObjectKey ok = getPrimaryKey();
        if ( ok == null) 
        {
            return super.hashCode();
        }
        
        return ok.hashCode();
    }
}








