package org.apache.turbine.om.security.peer;

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

import com.workingdogs.village.Record;
import com.workingdogs.village.Value;
import java.util.Enumeration;
import java.util.*;
import java.util.Vector;
import org.apache.turbine.om.BaseObject;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.util.BasePeer;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.SecurityObject;
import org.apache.turbine.om.security.TurbineRole;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.StringStackBuffer;
import org.apache.torque.util.Criteria;
import org.apache.torque.map.MapBuilder;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.torque.TorqueException;

/**
 * This class handles all the database access for the PERMISSION
 * table.  This table contains all the permissions that are used in
 * the system.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class PermissionPeer extends BasePeer
{
    private static final TurbineMapBuilder mapBuilder =
        (TurbineMapBuilder) getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");

    /** The table name for this peer. */
    private static final String TABLE_NAME = mapBuilder.getTablePermission();

    /** The column name for the permission id field. */
    public static final String PERMISSION_ID =
        mapBuilder.getPermission_PermissionId();

    /** The column name for the name field. */
    public static final String NAME = mapBuilder.getPermission_Name();

    /** The column name for the ObjectData field */
    public static final String OBJECTDATA =
        mapBuilder.getPermission_ObjectData();

    /** The Oracle sequence name for this peer. */
    private static final String SEQUENCE_NAME =
        mapBuilder.getSequencePermission();


    /**
     * Retrieves/assembles a PermissionSet
     *
     * @param criteria The criteria to use.
     * @return A PermissionSet.
     * @exception Exception, a generic exception.
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
     * @exception Exception, a generic exception.
     */
    public static PermissionSet retrieveSet( Role role )
        throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(RolePermissionPeer.ROLE_ID,
                     ((TurbineRole)role).getPrimaryKey());
        criteria.addJoin(RolePermissionPeer.PERMISSION_ID,
                         PermissionPeer.PERMISSION_ID);
        return retrieveSet(criteria);
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     * the SELECT statement.
     * @return Vector containing Permission objects.
     * @exception Exception, a generic exception.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        try
        {
            criteria.addSelectColumn(PERMISSION_ID)
                    .addSelectColumn(NAME)
                    .addSelectColumn(OBJECTDATA);

            if (criteria.getOrderByColumns() == null ||
                criteria.getOrderByColumns().size() == 0)
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
            for ( int i=0; i<rows.size(); i++ )
            {
                Permission obj = TurbineSecurity.getNewPermission(null);
                Record row = (Record) rows.get(i);
                ((SecurityObject) obj).setPrimaryKey( row.getValue(1).asInt() );
                ((SecurityObject) obj).setName( row.getValue(2).asString() );
                byte[] objectData = (byte[]) row.getValue(3).asBytes();
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
     */
    public static Criteria buildCriteria( Permission permission )
    {
        Criteria criteria = new Criteria();
        if ( !((BaseObject)permission).isNew() )
        {
            criteria.add(PERMISSION_ID,
                         ((BaseObject)permission).getPrimaryKey());
        }
        criteria.add(NAME, ((SecurityObject)permission).getName());

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
     * the UPDATE statement.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put( PERMISSION_ID,
                            criteria.remove(PERMISSION_ID) );
        BasePeer.doUpdate( selectCriteria, criteria );
    }

    /**
     * Checks if a Permission is defined in the system. The name
     * is used as query criteria.
     *
     * @param permission The Permission to be checked.
     * @return <code>true</code> if given Permission exists in the system.
     * @throws DataBackendException when more than one Permission with
     *         the same name exists.
     * @throws Exception, a generic exception.
     */
    public static boolean checkExists( Permission permission )
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(PERMISSION_ID);
        criteria.add(NAME, ((SecurityObject)permission).getName());
        List results = BasePeer.doSelect(criteria);
        if(results.size() > 1)
        {
            throw new DataBackendException("Multiple permissions named '" +
                ((SecurityObject)permission).getName() + "' exist!");
        }
        return (results.size()==1);
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
     * @return A String with the full name of the column.
     */
    public static String getColumnName (String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append (TABLE_NAME);
        sb.append (".");
        sb.append (name);
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
    public static final Vector getDifference(Vector some,
                                             Vector all)
    {
        Vector clone = (Vector)all.clone();
        for (Enumeration e = some.elements() ; e.hasMoreElements() ;)
        {
            Permission tmp = (Permission) e.nextElement();
            for (Enumeration f = clone.elements() ; f.hasMoreElements() ;)
            {
                Permission tmp2 = (Permission) f.nextElement();
                if (((BaseObject)tmp).getPrimaryKey() ==
                    ((BaseObject)tmp2).getPrimaryKey())
                {
                    clone.removeElement(tmp2);
                    break;
                }
            }
        }
        return clone;
    }
}
