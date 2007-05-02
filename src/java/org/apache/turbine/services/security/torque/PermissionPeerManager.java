package org.apache.turbine.services.security.torque;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.beans.PropertyDescriptor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.security.torque.om.TurbineRolePermissionPeer;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.PermissionSet;

import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

/**
 * This class capsulates all direct Peer access for the Permission entities.
 * It allows the exchange of the default Turbine supplied TurbinePermissionPeer
 * class against a custom class.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 *
 */

public class PermissionPeerManager
    implements PermissionPeerManagerConstants
{
    /** The class of the Peer the TorqueSecurityService uses */
    private static Class persistentPeerClass = null;

    /** The class name of the objects returned by the configured peer. */
    private static Class permissionObject = null;

    /** The name of the Table used for Permission Object queries  */
    private static String tableName = null;

    /** The name of the column used as "Name" Column */
    private static String nameColumn = null;

    /** The name of the column used as "Id" Column */
    private static String idColumn = null;

    /** The "Name" property descriptor */
    private static PropertyDescriptor namePropDesc = null;

    /** The "Id" property descriptor */
    private static PropertyDescriptor idPropDesc = null;

    /** Logging */
    static Log log = LogFactory.getLog(PermissionPeerManager.class);

    /**
     * Initializes the PermissionPeerManager, loading the class object for the
     * Peer used to retrieve Permission objects
     *
     * @param conf The configuration object used to configure the Manager
     *
     * @exception InitializationException A problem occured during initialization
     */

    public static void init(Configuration conf)
        throws InitializationException
    {
        String persistentPeerClassName =
            conf.getString(PERMISSION_PEER_CLASS_KEY,
                           PERMISSION_PEER_CLASS_DEFAULT);

        String permissionObjectName = null;

        try
        {
            persistentPeerClass = Class.forName(persistentPeerClassName);

            tableName  =
                (String) persistentPeerClass.getField("TABLE_NAME").get(null);

            //
            // We have either an user configured Object class or we use the
            // default as supplied by the Peer class
            //
            // Default from Peer, can be overridden

            permissionObject = getPersistenceClass();

            permissionObjectName = conf.getString(PERMISSION_CLASS_KEY,
                                        permissionObject.getName());

            // Maybe the user set a new value...
            permissionObject = Class.forName(permissionObjectName);

            /* If any of the following Field queries fails, the permission
             * subsystem is unusable. So check this right here at init time,
             * which saves us much time and hassle if it fails...
             */

            nameColumn = (String) persistentPeerClass.getField(
                    conf.getString(PERMISSION_NAME_COLUMN_KEY,
                                   PERMISSION_NAME_COLUMN_DEFAULT)
                    ).get(null);

            idColumn = (String) persistentPeerClass.getField(
                    conf.getString(PERMISSION_ID_COLUMN_KEY,
                                   PERMISSION_ID_COLUMN_DEFAULT)
                    ).get(null);

            namePropDesc = new PropertyDescriptor(
                    conf.getString(PERMISSION_NAME_PROPERTY_KEY,
                                   PERMISSION_NAME_PROPERTY_DEFAULT),
                    permissionObject);

            idPropDesc = new PropertyDescriptor(
                    conf.getString(PERMISSION_ID_PROPERTY_KEY,
                                   PERMISSION_ID_PROPERTY_DEFAULT),
                    permissionObject);
        }
        catch (Exception e)
        {
            if (persistentPeerClassName == null || persistentPeerClass == null)
            {
                throw new InitializationException(
                    "Could not find PermissionPeer class ("
                    + persistentPeerClassName + ")", e);
            }
            if (tableName == null)
            {
                throw new InitializationException(
                    "Failed to get the table name from the Peer object", e);
            }

            if (permissionObject == null || permissionObjectName == null)
            {
                throw new InitializationException(
                    "Failed to get the object type from the Peer object", e);
            }


            if (nameColumn == null || namePropDesc == null)
            {
                throw new InitializationException(
                    "PermissionPeer " + persistentPeerClassName +
                    " has no name column information!", e);
            }
            if (idColumn == null || idPropDesc == null)
            {
                throw new InitializationException(
                    "PermissionPeer " + persistentPeerClassName +
                    " has no id column information!", e);
            }
        }
    }

    /**
     * Get the name of this table.
     *
     * @return A String with the name of the table.
     */
    public static String getTableName()
    {
        return tableName;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Name Column for a permission
     *
     * @return A String containing the column name
     */
    public static String getNameColumn()
    {
        return nameColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Id Column for a permission
     *
     * @return A String containing the column id
     *
     */
    public static String getIdColumn()
    {
        return idColumn;
    }

    /**
     * Returns the full name of a column.
     *
     * @param name The column to fully qualify
     *
     * @return A String with the full name of the column.
     */
    public static String getColumnName(String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(getTableName());
        sb.append(".");
        sb.append(name);
        return sb.toString();
    }

    /**
     * Returns a new, empty object for the underlying peer.
     * Used to create a new underlying object
     *
     * @return A new object which is compatible to the Peer
     *         and can be used as a User object
     *
     */

    public static Persistent newPersistentInstance()
    {
        Persistent obj = null;

        if(permissionObject == null)
        {
            // This can happen if the Turbine wants to determine the
            // name of the anonymous user before the security service
            // has been initialized. In this case, the Peer Manager
            // has not yet been inited and the permissionObject is still
            // null. Return null in this case.
            //
            return obj;
        }

        try
        {
            obj = (Persistent) permissionObject.newInstance();
        }
        catch (Exception e)
        {
            log.error("Could not instantiate a permission object", e);
            obj = null;
        }
        return obj;
    }

    /**
     * Checks if a Permission is defined in the system. The name
     * is used as query criteria.
     *
     * @param permission The Permission to be checked.
     * @return <code>true</code> if given Permission exists in the system.
     * @throws DataBackendException when more than one Permission with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    public static boolean checkExists(Permission permission)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();

        criteria.addSelectColumn(getIdColumn());

        criteria.add(getNameColumn(), permission.getName());

        List results = BasePeer.doSelect(criteria);

        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple permissions named '" +
                                           permission.getName() + "' exist!");
        }
        return (results.size() == 1);
    }

    /**
     * Retrieves/assembles a PermissionSet
     *
     * @param criteria The criteria to use.
     * @return A PermissionSet.
     * @exception Exception A generic Exception.
     */
    public static PermissionSet retrieveSet(Criteria criteria)
        throws Exception
    {
        List results = doSelect(criteria);
        PermissionSet ps = new PermissionSet();

        for(Iterator it = results.iterator(); it.hasNext(); )
        {
            ps.add((Permission) it.next());
        }
        return ps;
    }

    /**
     * Retrieves a set of Permissions associated with a particular Role.
     *
     * @param role The role to query permissions of.
     * @return A set of permissions associated with the Role.
     * @exception Exception A generic Exception.
     */
    public static PermissionSet retrieveSet(Role role)
        throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineRolePermissionPeer.ROLE_ID,
                     ((Persistent) role).getPrimaryKey());

        criteria.addJoin(TurbineRolePermissionPeer.PERMISSION_ID,
                         getIdColumn());

        return retrieveSet(criteria);
    }

    /**
     * Pass in two Vector's of Permission Objects.  It will return a
     * new Vector with the difference of the two Vectors: C = (A - B).
     *
     * @param some Vector B in C = (A - B).
     * @param all Vector A in C = (A - B).
     * @return Vector C in C = (A - B).
     */
    public static final Vector getDifference(Vector some, Vector all)
    {
        Vector clone = (Vector) all.clone();
        for (Enumeration e = some.elements() ; e.hasMoreElements() ;)
        {
            Permission tmp = (Permission) e.nextElement();
            for (Enumeration f = clone.elements() ; f.hasMoreElements() ;)
            {
                Permission tmp2 = (Permission) f.nextElement();
                if (((Persistent) tmp).getPrimaryKey() ==
                    ((Persistent) tmp2).getPrimaryKey())
                {
                    clone.removeElement(tmp2);
                    break;
                }
            }
        }
        return clone;
    }

    /*
     * ========================================================================
     *
     * WARNING! Do not read on if you have a weak stomach. What follows here
     * are some abominations thanks to the braindead static peers of Torque
     * and the rigidity of Java....
     *
     * ========================================================================
     *
     */

    /**
     * Calls buildCriteria(Permission permission) in
     * the configured PermissionPeer. If you get a
     * ClassCastException in this routine, you put a
     * Permission object into this method which
     * can't be cast into an object for the
     * TorqueSecurityService. This is a configuration error most of
     * the time.
     *
     * @param permission An object which implements
     *                 the Permission interface
     *
     * @return A criteria for the supplied permission object
     */

    public static Criteria buildCriteria(Permission permission)
    {
        Criteria crit;

        try
        {
            Class[] clazz = new Class[] { permissionObject };
            Object[] params =
              new Object[] { ((TorquePermission) permission).getPersistentObj() };

            crit =  (Criteria) persistentPeerClass
                .getMethod("buildCriteria", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            crit = null;
        }

        return crit;
    }

    /**
     * Invokes doUpdate(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */

    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Criteria.class };
            Object[] params = new Object[] { criteria };

            persistentPeerClass
                .getMethod("doUpdate", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            throw new TorqueException("doUpdate failed", e);
        }
    }

    /**
     * Invokes doInsert(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */

    public static void doInsert(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Criteria.class };
            Object[] params = new Object[] { criteria };

            persistentPeerClass
                .getMethod("doInsert", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            throw new TorqueException("doInsert failed", e);
        }
    }

    /**
     * Invokes doSelect(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @return A List of Permission Objects selected by the Criteria
     *
     * @exception TorqueException A problem occured.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        List list;

        try
        {
            Class[] clazz =
              new Class[] { Criteria.class };
            Object[] params = new Object[] { criteria };

            list = (List) persistentPeerClass
                .getMethod("doSelect", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            throw new TorqueException("doSelect failed", e);
        }

        List newList = new ArrayList(list.size());

        //
        // Wrap the returned Objects into TorquePermissions.
        //
        for (Iterator it = list.iterator(); it.hasNext(); )
        {
            Permission p = getNewPermission((Persistent) it.next());
            newList.add(p);
        }

        return newList;
    }

    /**
     * Invokes doDelete(Criteria c) on the configured Peer Object
     *
     * @param criteria  A Criteria Object
     *
     * @exception TorqueException A problem occured.
     */
    public static void doDelete(Criteria criteria)
        throws TorqueException
    {
        try
        {
            Class[] clazz = new Class[] { Criteria.class };
            Object[] params = new Object[] { criteria };

            persistentPeerClass
                .getMethod("doDelete", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            throw new TorqueException("doDelete failed", e);
        }
    }

    /**
     * Invokes setName(String s) on the supplied base object
     *
     * @param obj The object to use for setting the name
     * @param name The Name to set
     */
    public static void setPermissionName(Persistent obj, String name)
    {
        if(obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { name };
            namePropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be a Permission Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getName() on the supplied base object
     *
     * @param obj The object to use for getting the name
     *
     * @return A string containing the name
     */
    public static String getPermissionName(Persistent obj)
    {
        String name = null;

        if(obj == null)
        {
            return null;
        }

        try
        {
            name = (String) namePropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be a Permission Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return name;
    }

    /**
     * Invokes setId(int n) on the supplied base object
     *
     * @param obj The object to use for setting the name
     * @param id The new Id
     */
    public static void setId(Persistent obj, int id)
    {
        if(obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { Integer.TYPE };
            idPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be a Permission Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getId() on the supplied base object
     *
     * @param obj The object to use for getting the id
     *
     * @return The Id of this object
     */
    public static Integer getIdAsObj(Persistent obj)
    {
        Integer id = null;

        if(obj == null)
        {
            return new Integer(0);
        }

        try
        {
            id = (Integer) idPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be a Permission Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return id;
    }

    /**
     * Returns the Class of the configured Object class
     * from the peer
     *
     * @return The class of the objects returned by the configured peer
     *
     */

    private static Class getPersistenceClass()
    {
        Class persistenceClass = null;

        try
        {
            Object[] params = new Object[0];

            persistenceClass =  (Class) persistentPeerClass
                .getMethod("getOMClass", (Class[])null)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            persistenceClass = null;
        }

        return persistenceClass;
    }

    /**
     * Returns a new, configured Permission Object with
     * a supplied Persistent object at its core
     *
     * @param p The persistent object
     *
     * @return a new, configured Permission Object
     *
     * @exception Exception Could not create a new Object
     *
     */

    public static Permission getNewPermission(Persistent p)
    {
        Permission perm = null;
        try
        {
            Class permissionWrapperClass = TurbineSecurity.getPermissionClass();

            Class [] clazz = new Class [] { Persistent.class };
            Object [] params = new Object [] { p };

            perm = (Permission) permissionWrapperClass
              .getConstructor(clazz)
              .newInstance(params);
        }
        catch (Exception e)
        {
            log.error("Could not instantiate a new permission from supplied persistent: ", e);
        }

        return perm;
    }
}

