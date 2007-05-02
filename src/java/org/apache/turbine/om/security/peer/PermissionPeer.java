package org.apache.turbine.om.security.peer;

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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.torque.TorqueException;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.SecurityObject;
import org.apache.turbine.om.security.TurbineRole;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.PermissionSet;

import com.workingdogs.village.Record;

/**
 * This class handles all the database access for the PERMISSION
 * table.  This table contains all the permissions that are used in
 * the system.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 *
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 *
 * @version $Id$
 */
public class PermissionPeer extends BasePeer
{
     /** Serial Version UID */
    private static final long serialVersionUID = 2762005892291909743L;

    /** The map builder for this Peer. */
    private static final TurbineMapBuilder MAP_BUILDER;

    /** The table name for this peer. */
    private static final String TABLE_NAME;

    /** The column name for the permission id field. */
    public static final String PERMISSION_ID;

    /** The column name for the ObjectData field */
    public static final String OBJECTDATA;

    /** The column name for the name field. */
    public static final String NAME;

    static
    {
        try
        {
            MAP_BUILDER = (TurbineMapBuilder)/* Torque. */getMapBuilder(TurbineMapBuilder.class.getName());
        }
        catch (TorqueException e)
        {
            log.error("Could not initialize Peer", e);
            throw new RuntimeException(e);
        }

        TABLE_NAME = MAP_BUILDER.getTablePermission();
        PERMISSION_ID = MAP_BUILDER.getPermission_PermissionId();
        NAME = MAP_BUILDER.getPermission_Name();
        OBJECTDATA = MAP_BUILDER.getPermission_ObjectData();
    }

    /**
     * Retrieves/assembles a PermissionSet
     *
     * @param criteria The criteria to use.
     * @return A PermissionSet.
     * @exception Exception a generic exception.
     */
    public static PermissionSet retrieveSet(Criteria criteria)
        throws Exception
    {
        List results = PermissionPeer.doSelect(criteria);
        PermissionSet ps = new PermissionSet();
        for (int i = 0; i < results.size(); i++)
        {
            ps.add((Permission) results.get(i));
        }
        return ps;
    }

    /**
     * Retrieves a set of Permissions associated with a particular Role.
     *
     * @param role The role to query permissions of.
     * @return A set of permissions associated with the Role.
     * @exception Exception a generic exception.
     */
    public static PermissionSet retrieveSet(Role role)
            throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(RolePermissionPeer.ROLE_ID,
                ((TurbineRole) role).getPrimaryKey());
        criteria.addJoin(RolePermissionPeer.PERMISSION_ID,
                PermissionPeer.PERMISSION_ID);
        return retrieveSet(criteria);
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     *        the SELECT statement.
     * @return Vector containing Permission objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria)
            throws TorqueException
    {
        try
        {
            criteria.addSelectColumn(PERMISSION_ID)
                    .addSelectColumn(NAME)
                    .addSelectColumn(OBJECTDATA);

            if (criteria.getOrderByColumns() == null
                    || criteria.getOrderByColumns().size() == 0)
            {
                criteria.addAscendingOrderByColumn(NAME);
            }

            // Place any checks here to intercept criteria which require
            // custom SQL.  For example:
            // if ( criteria.containsKey("SomeTable.SomeColumn") )
            // {
            //     String whereSql = "SomeTable.SomeColumn IN (Select ...";
            //     criteria.add("SomeTable.SomeColumn",
            //                  whereSQL, criteria.CUSTOM);
            // }

            // BasePeer returns a Vector of Value (Village) arrays.  The
            // array order follows the order columns were placed in the
            // Select clause.
            List rows = BasePeer.doSelect(criteria);
            List results = new ArrayList();

            // Populate the object(s).
            for (int i = 0; i < rows.size(); i++)
            {
                Permission obj = TurbineSecurity.getPermissionInstance(null);
                Record row = (Record) rows.get(i);
                ((SecurityObject) obj).setPrimaryKey(
                        new NumberKey(row.getValue(1).asInt()));
                ((SecurityObject) obj).setName(row.getValue(2).asString());
                byte[] objectData = row.getValue(3).asBytes();
                Map temp = (Map) ObjectUtils.deserialize(objectData);
                if (temp != null)
                {
                    ((SecurityObject) obj).setAttributes(temp);
                }
                results.add(obj);
            }

            return results;
        }
        catch (Exception ex)
        {
            throw new TorqueException(ex);
        }
    }

    /**
     * Builds a criteria object based upon an Permission object
     *
     * @param permission object to build the criteria
     * @return the Criteria
     */
    public static Criteria buildCriteria(Permission permission)
    {
        Criteria criteria = new Criteria();
        if (!((BaseObject) permission).isNew())
        {
            criteria.add(PERMISSION_ID,
                    ((BaseObject) permission).getPrimaryKey());
        }
        criteria.add(NAME, ((SecurityObject) permission).getName());

        /*
         * This is causing the the removal and updating of
         * a permission to crap out. This addition to the
         * criteria produces something like:
         *
         * where OBJECTDATA = {}
         *
         * Is the NAME even necessary. Wouldn't
         * criteria.add(PERMISSION_ID, N) be enough to
         * generate a where clause that would remove the
         * permission?
         *
         * criteria.add(OBJECTDATA, permission.getAttributes());
         */
        return criteria;
    }

    /**
     * Issues an update based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     *        the UPDATE statement.
     * @exception TorqueException a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put(PERMISSION_ID, criteria.remove(PERMISSION_ID));
        BasePeer.doUpdate(selectCriteria, criteria);
    }

    /**
     * Checks if a Permission is defined in the system. The name
     * is used as query criteria.
     *
     * @param permission The Permission to be checked.
     * @return <code>true</code> if given Permission exists in the system.
     * @throws DataBackendException when more than one Permission with
     *         the same name exists.
     * @throws Exception a generic exception.
     */
    public static boolean checkExists(Permission permission)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(PERMISSION_ID);
        criteria.add(NAME, ((SecurityObject) permission).getName());
        List results = BasePeer.doSelect(criteria);
        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple permissions named '"
                    + ((SecurityObject) permission).getName() + "' exist!");
        }
        return (results.size() == 1);
    }

    /**
     * Get the name of this table.
     *
     * @return A String with the name of the table.
     */
    public static String getTableName()
    {
        return TABLE_NAME;
    }

    /**
     * Returns the full name of a column.
     *
     * @param name name of a column
     * @return A String with the full name of the column.
     */
    public static String getColumnName(String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_NAME);
        sb.append(".");
        sb.append(name);
        return sb.toString();
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
        for (Enumeration e = some.elements(); e.hasMoreElements();)
        {
            Permission tmp = (Permission) e.nextElement();
            for (Enumeration f = clone.elements(); f.hasMoreElements();)
            {
                Permission tmp2 = (Permission) f.nextElement();
                if (((BaseObject) tmp).getPrimaryKey()
                        == ((BaseObject) tmp2).getPrimaryKey())
                {
                    clone.removeElement(tmp2);
                    break;
                }
            }
        }
        return clone;
    }
}
