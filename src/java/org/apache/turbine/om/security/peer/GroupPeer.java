package org.apache.turbine.om.security.peer;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    /** The map builder for this Peer. */
    private static final TurbineMapBuilder MAP_BUILDER = (TurbineMapBuilder)
            getMapBuilder(TurbineMapBuilder.class.getName());

    /** The table name for this peer. */
    private static final String TABLE_NAME = MAP_BUILDER.getTableGroup();

    /** The column name for the Group id field. */
    public static final String GROUP_ID = MAP_BUILDER.getGroup_GroupId();

    /** The column name for the name field. */
    public static final String NAME = MAP_BUILDER.getGroup_Name();

    /** The column name for the ObjectData field */
    public static final String OBJECTDATA = MAP_BUILDER.getGroup_ObjectData();

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
