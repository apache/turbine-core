package org.apache.turbine.om.security.peer;

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

import com.workingdogs.village.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.torque.TorqueException;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.SecurityObject;
import org.apache.turbine.om.security.TurbineGroup;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.GroupSet;

/**
 * This class handles all the database access for the Group table.
 * This table contains all the Groups that a given member can play.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class GroupPeer extends BasePeer
{
    private static final TurbineMapBuilder mapBuilder =
        (TurbineMapBuilder) getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");

    /** The table name for this peer. */
    private static final String TABLE_NAME = mapBuilder.getTableGroup();

    /** The column name for the Group id field. */
    public static final String GROUP_ID = mapBuilder.getGroup_GroupId();

    /** The column name for the name field. */
    public static final String NAME = mapBuilder.getGroup_Name();

    /** The column name for the ObjectData field */
    public static final String OBJECTDATA = mapBuilder.getGroup_ObjectData();

    /** The Oracle sequence name for this peer. */
    private static final String SEQUENCE_NAME = mapBuilder.getSequenceGroup();

    /**
     * Retrieves/assembles a GroupSet of all of the Groups.
     *
     * @return A GroupSet.
     * @exception Exception a generic exception.
     */
    public static GroupSet retrieveSet() throws Exception
    {
        return retrieveSet(new Criteria());
    }

    /**
     * Retrieves/assembles a GroupSet based on the Criteria passed in
     *
     * @param criteria The criteria to use.
     * @throws Exception a generic exception.
     * @return a GroupSet
     */
    public static GroupSet retrieveSet(Criteria criteria) throws Exception
    {
        List results = GroupPeer.doSelect(criteria);
        GroupSet rs = new GroupSet();
        for (int i = 0; i < results.size(); i++)
        {
            rs.add((Group) results.get(i));
        }
        return rs;
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria object containing data that is used to create
     *        the SELECT statement.
     * @return Vector containing Group objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria) throws TorqueException
    {
        try
        {
            criteria.addSelectColumn(GROUP_ID)
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
                Group obj = TurbineSecurity.getGroupInstance(null);
                Record row = (Record) rows.get(i);
                ((SecurityObject) obj).setPrimaryKey(
                        new NumberKey(row.getValue(1).asInt()));
                ((SecurityObject) obj).setName(row.getValue(2).asString());
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
     * Issues an update based on a criteria.
     *
     * @param criteria object containing data that is used to create
     *        the UPDATE statement.
     * @exception TorqueException a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put(GROUP_ID, criteria.remove(GROUP_ID));
        BasePeer.doUpdate(selectCriteria, criteria);
    }

    /**
     * Checks if a Group is defined in the system. The name
     * is used as query criteria.
     *
     * @param group The Group to be checked.
     * @return <code>true</code> if given Group exists in the system.
     * @throws DataBackendException when more than one Group with
     *         the same name exists.
     * @throws Exception a generic exception.
     */
    public static boolean checkExists(Group group)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(GROUP_ID);
        criteria.add(NAME, ((SecurityObject) group).getName());
        List results = BasePeer.doSelect(criteria);
        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple groups named '"
                    + ((TurbineGroup) group).getName() + "' exist!");
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
     * @param name name of the column
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
     * Builds a criteria object based upon an Group object
     *
     * @param group object to build the Criteria
     * @return the Criteria
     */
    public static Criteria buildCriteria(Group group)
    {
        Criteria criteria = new Criteria();
        criteria.add(NAME, ((SecurityObject) group).getName());
        if (!((BaseObject) group).isNew())
        {
            criteria.add(GROUP_ID, ((BaseObject) group).getPrimaryKey());
        }
        // Causing the removal and updating of a group to
        // crap out.
        //criteria.add(OBJECTDATA, group.getAttributes());
        return criteria;
    }
}
