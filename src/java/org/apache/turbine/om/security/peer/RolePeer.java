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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.torque.TorqueException;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.TurbineRole;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.security.db.DBSecurityService;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.RoleSet;


/**
 * This class handles all the database access for the ROLE table.
 * This table contains all the roles that a given member can play.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class RolePeer extends BasePeer
{
    private static final TurbineMapBuilder mapBuilder =
        (TurbineMapBuilder) getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");

    /** The table name for this peer. */
    private static final String TABLE_NAME = mapBuilder.getTableRole();

    /** The column name for the role id field. */
    public static final String ROLE_ID = mapBuilder.getRole_RoleId();

    /** The column name for the name field. */
    public static final String NAME = mapBuilder.getRole_Name();

    /** The column name for the ObjectData field */
    public static final String OBJECTDATA = mapBuilder.getRole_ObjectData();

    /** The Oracle sequence name for this peer. */
    private static final String SEQUENCE_NAME = mapBuilder.getSequenceRole();

    /**
    * Retrieves/assembles a RoleSet based on the Criteria passed in
    */
    public static RoleSet retrieveSet(Criteria criteria) throws Exception
    {
        List results = RolePeer.doSelect(criteria);
        RoleSet rs = new RoleSet();
        for (int i = 0; i < results.size(); i++)
        {
            rs.add((Role) results.get(i));
        }
        return rs;
    }

    /**
     * Retrieves a set of Roles that an User was assigned in a Group
     *
     * @param user An user.
     * @param group A group
     * @return A Set of Roles of this User in the Group
     * @exception Exception, a generic exception.
     */
    public static RoleSet retrieveSet( User user, Group group )
        throws Exception
    {
        Criteria criteria = new Criteria();

        /*
         * Peer specific methods should absolutely NOT be part
         * of any of the generic interfaces in the security system.
         * this is not good.
         *
         * UserPeer up = TurbineSecurity.getUserPeerInstance();
         */

        UserPeer up = ((DBSecurityService)TurbineSecurity.getService())
            .getUserPeerInstance();

        criteria.add(up.getFullColumnName(UserPeer.USERNAME),
                     user.getUserName());
        criteria.add(UserGroupRolePeer.GROUP_ID,
                     ((Persistent)group).getPrimaryKey());

        criteria.addJoin(up.getFullColumnName(UserPeer.USER_ID),
                         UserGroupRolePeer.USER_ID);
        criteria.addJoin(UserGroupRolePeer.ROLE_ID, RolePeer.ROLE_ID);
        return retrieveSet(criteria);
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param Criteria object containing data that is used to create
     * the SELECT statement.
     * @return Vector containing Role objects.
     * @exception Exception, a generic exception.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        try
        {
            criteria.addSelectColumn(ROLE_ID)
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
            for (int i = 0; i < rows.size(); i++ )
            {
                //Role obj = new Role();
                Role obj = new TurbineRole();
                Record row = (Record) rows.get(i);
                ((TurbineRole) obj).setPrimaryKey(row.getValue(1).asInt());
                ((TurbineRole) obj).setName(row.getValue(2).asString());
                byte[] objectData = (byte[]) row.getValue(3).asBytes();
                Map temp = (Map) ObjectUtils.deserialize(objectData);
                if (temp != null)
                {
                    ((TurbineRole) obj).setAttributes(temp);
                }
                results.add(obj);
            }

            return results;
        }
        catch (Exception ex)
        {
            throw new TorqueException (ex);
        }
    }

    /**
     * Builds a criteria object based upon an Role object
     */
    public static Criteria buildCriteria( Role role )
    {
        Criteria criteria = new Criteria();
        if ( !((BaseObject)role).isNew() )
        {
            criteria.add(ROLE_ID, ((BaseObject)role).getPrimaryKey());
        }
        criteria.add(NAME, role.getName());
        // causing the removal and updating of roles to
        // crap out because of the generated SQL.
        //criteria.add(OBJECTDATA, role.getAttributes());
        return criteria;
    }

    /**
     * Issues an update based on a criteria.
     *
     * @param Criteria object containing data that is used to create
     * the UPDATE statement.
     * @exception Exception, a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put( ROLE_ID, criteria.remove(ROLE_ID) );
        BasePeer.doUpdate( selectCriteria, criteria );
    }

    /**
     * Checks if a Role is defined in the system. The name
     * is used as query criteria.
     *
     * @param permission The Role to be checked.
     * @return <code>true</code> if given Role exists in the system.
     * @throws DataBackendException when more than one Role with
     *         the same name exists.
     * @throws Exception, a generic exception.
     */
    public static boolean checkExists( Role role )
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(ROLE_ID);
        criteria.add(NAME, role.getName());
        List results = BasePeer.doSelect(criteria);
        if(results.size() > 1)
        {
            throw new DataBackendException("Multiple roles named '" +
                role.getName() + "' exist!");
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
}
