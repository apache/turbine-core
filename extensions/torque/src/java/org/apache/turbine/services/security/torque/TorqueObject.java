package org.apache.turbine.services.security.torque;

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

import java.sql.Connection;

import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.Persistent;

import org.apache.turbine.om.security.SecurityEntity;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * All the Torque Security objects (User, Group, Role, Permission) are
 * derived from this class which contains the base compare and management
 * methods for all security objects.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public abstract class TorqueObject
    implements SecurityEntity,
               Comparable,
               Persistent
{
    /** The underlying database Object which is proxied */
    protected Persistent obj = null;

    /**
     * Constructs a new TorqueObject
     *
     */
    public TorqueObject()
    {
    }

    /**
     * Constructs a new Object with the specified name.
     *
     * @param name The name of the new object.
     */
    public TorqueObject(String name)
    {
        this.setName(name);
    }

    /**
     * This Constructor is used when a Manager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into TorqueObjects.
     *
     * @param obj An Object from the peer
     */
   public  TorqueObject(Persistent obj)
    {
        this.obj = obj;
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * @return The underlying persistent object
     *
     */
    public abstract Persistent getPersistentObj();

    /**
     * Returns the name of this object
     *
     * @return The name of the object
     */
    public abstract String getName();

    /**
     * Sets the name of this object
     *
     * @param name The name of the object
     */
    public abstract void setName(String name);

    /**
     * getter for the object primaryKey.
     *
     * @return the object primaryKey as an Object
     */
    public ObjectKey getPrimaryKey()
    {
        Persistent p = getPersistentObj();
        if(p != null)
        {
            return p.getPrimaryKey();
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey The new PrimaryKey for the object.
     *
     * @exception Exception This method might throw an exceptions
     */
    public void setPrimaryKey(ObjectKey primaryKey)
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Sets the PrimaryKey for the object.
     *
     * @param primaryKey the String should be of the form produced by
     *        ObjectKey.toString().
     *
     * @exception Exception This method might throw an exceptions
     */
    public void setPrimaryKey(String primaryKey)
        throws Exception
    {
        getPersistentObj().setPrimaryKey(primaryKey);
    }

    /**
     * Returns whether the object has been modified, since it was
     * last retrieved from storage.
     *
     * @return True if the object has been modified.
     */
    public boolean isModified()
    {
        return getPersistentObj().isModified();
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
        return getPersistentObj().isNew();
    }

    /**
     * Setter for the isNew attribute.  This method will be called
     * by Torque-generated children and Peers.
     *
     * @param b the state of the object.
     */
    public void setNew(boolean b)
    {
        getPersistentObj().setNew(b);
    }

    /**
     * Sets the modified state for the object.
     *
     * @param m The new modified state for the object.
     */
    public void setModified(boolean m)
    {
        getPersistentObj().setModified(m);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.
     *
     * @param torqueName The name under which the object should be stored.
     *
     * @exception Exception This method might throw an exceptions
     */
    public void save(String torqueName)
        throws Exception
    {
        getPersistentObj().save(torqueName);
    }

    /**
     * Stores the object in the database.  If the object is new,
     * it inserts it; otherwise an update is performed.  This method
     * is meant to be used as part of a transaction, otherwise use
     * the save() method and the connection details will be handled
     * internally
     *
     * @param con A Connection object to save the object
     *
     * @exception Exception This method might throw an exceptions
     */
    public void save(Connection con)
        throws Exception
    {
        getPersistentObj().save(con);
    }

    /**
     * Makes changes made to the TorqueObject permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public abstract void save()
        throws TurbineSecurityException;

    /**
     * Used for ordering TorqueObjects.
     *
     * @param obj The Object to compare to.
     * @return -1 if the name of the other object is lexically greater than this
     *         group, 1 if it is lexically lesser, 0 if they are equal.
     */
    public int compareTo(Object obj)
    {
        if (this.getClass() != obj.getClass())
        {
            throw new ClassCastException();
        }
        String name1 = ((SecurityEntity) obj).getName();
        String name2 = this.getName();

        return name2.compareTo(name1);
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
        if (obj != null && obj instanceof TorqueObject)
        {
            return equals((TorqueObject) obj);
        }
        else
        {
            return false;
        }
    }

    /**
     * Compares the primary key of this instance with the key of another.
     *
     * @param torqueObject The TorqueObject to compare to.
     * @return   Whether the primary keys are equal.
     */
    public boolean equals(TorqueObject torqueObject)
    {
        if (torqueObject == null)
        {
            return false;
        }
        if (this == torqueObject)
        {
            return true;
        }
        else if (getPrimaryKey() == null || torqueObject.getPrimaryKey() == null)
        {
            return false;
        }
        else
        {
            return getPrimaryKey().equals(torqueObject.getPrimaryKey());
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
        if (ok == null)
        {
            return super.hashCode();
        }

        return ok.hashCode();
    }
}
