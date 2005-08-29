package org.apache.turbine.om.security.peer;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
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

import java.sql.Connection;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.workingdogs.village.Column;
import com.workingdogs.village.Record;
import com.workingdogs.village.Schema;
import com.workingdogs.village.Value;

import org.apache.torque.TorqueException;
import org.apache.torque.map.TableMap;
import org.apache.torque.om.NumberKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.ObjectUtils;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;

/**
 * This class handles all the database access for the User/User
 * table.  This table contains all the information for a given user.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @version $Id$
 */
public class TurbineUserPeer extends BasePeer implements UserPeer
{
    /** Serial Version UID */
    private static final long serialVersionUID = -5981268145973167352L;

    /** The mapBuilder for this Peer. */
    private static final TurbineMapBuilder MAP_BUILDER = (TurbineMapBuilder)
            getMapBuilder(TurbineMapBuilder.class.getName());

    // column names
    /** The column name for the visitor id field. */
    private static final String USER_ID_COLUMN = MAP_BUILDER.getUserId();

    /** This is the value that is stored in the database for confirmed users. */
    public static final String CONFIRM_DATA
            = org.apache.turbine.om.security.User.CONFIRM_DATA;

    /** The column name for the visitor id field. */
    private static final String OBJECT_DATA_COLUMN = MAP_BUILDER.getObjectData();

    /** The table name for this peer. */
    private static final String TABLE_NAME = MAP_BUILDER.getTableUser();

    // Criteria Keys
    /** The key name for the visitor id field. */
    public static final String USER_ID = MAP_BUILDER.getUser_UserId();

    /** The key name for the username field. */
    public static final String USERNAME = MAP_BUILDER.getUser_Username();

    /** The key name for the password field. */
    public static final String PASSWORD = MAP_BUILDER.getUser_Password();

    /** The key name for the first name field. */
    public static final String FIRST_NAME = MAP_BUILDER.getUser_FirstName();

    /** The key name for the last name field. */
    public static final String LAST_NAME = MAP_BUILDER.getUser_LastName();

    /** The key name for the modified field. */
    public static final String MODIFIED = MAP_BUILDER.getUser_Modified();

    /** The key name for the created field. */
    public static final String CREATED = MAP_BUILDER.getUser_Created();

    /** The key name for the email field. */
    public static final String EMAIL = MAP_BUILDER.getUser_Email();

    /** The key name for the last_login field. */
    public static final String LAST_LOGIN = MAP_BUILDER.getUser_LastLogin();

    /** The key name for the confirm_value field. */
    public static final String CONFIRM_VALUE
            = MAP_BUILDER.getUser_ConfirmValue();

    /** The key name for the object_data field. */
    public static final String OBJECT_DATA = MAP_BUILDER.getUser_ObjectData();

    /** The schema. */
    private static Schema schema = initTableSchema(TABLE_NAME);

    /** The columns. */
    private static Column[] columns
            = initTableColumns(schema);

    /** The names of the columns. */
    public static String[] columnNames = initColumnNames(columns);

    /** The keys for the criteria. */
    public static String[] criteriaKeys
            = initCriteriaKeys(TABLE_NAME, columnNames);


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
     *
     * Returns the full name of a column.
     *
     * @param name name of a column
     * @return A String with the full name of the column.
     */
    public String getFullColumnName(String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_NAME);
        sb.append(".");
        sb.append(name);
        return sb.toString();
    }

    /**
     * Builds a criteria object based upon an User object.  Data
     * stored in the permData table which a key matching a column
     * name is removed from the permData table and added as a criterion.
     * All remaining data in the permData table is serialized and
     * added as a criterion for the OBJECT_DATA column.
     *
     * @param user object to build the criteria
     * @return the Criteria
     */
    public static Criteria buildCriteria(User user)
    {
        Hashtable permData = (Hashtable) user.getPermStorage().clone();
        Criteria criteria = new Criteria();
        if (!((Persistent) user).isNew())
        {
            criteria.add(USER_ID, ((Persistent) user).getPrimaryKey());
        }

        for (int i = 1; i < TurbineUserPeer.columnNames.length; i++)
        {
            if (permData.containsKey(TurbineUserPeer.columnNames[i]))
            {
                criteria.add(TurbineUserPeer.criteriaKeys[i],
                        permData.remove(TurbineUserPeer.columnNames[i]));
            }
        }
        criteria.add(TurbineUserPeer.OBJECT_DATA, permData);
        return criteria;
    }

    /**
     * Add all the columns needed to create a new object
     *
     * @param criteria The criteria to use.
     * @exception TorqueException a generic exception.
     */
    public static void addSelectColumns(Criteria criteria)
            throws TorqueException
    {
        for (int i = 0; i < columnNames.length; i++)
        {
            criteria.addSelectColumn(new StringBuffer()
                .append(TABLE_NAME)
                .append(".")
                .append(columnNames[i]).toString());
        }
    }

    /**
     *
     * @param row
     * @param offset
     * @param obj
     * @throws TorqueException
     */
    public static void populateObject(Record row, int offset, User obj)
        throws TorqueException
    {
        try
        {
            // Set values are where columns are expected.  They are not
            // required to be in these positions, as we set the positions
            // immediately following.
            int idPosition = 1;
            int objectDataPosition = columnNames.length;
            for (int i = 0; i < columnNames.length; i++)
            {
                if (columnNames[i].equals(USER_ID_COLUMN))
                {
                    idPosition = i + 1;
                }
                if (columnNames[i].equals(OBJECT_DATA_COLUMN))
                {
                    objectDataPosition = i + 1;
                }
            }

            ((Persistent) obj).setPrimaryKey(
                new NumberKey(row.getValue(idPosition).asBigDecimal()));

            // Restore the Permanent Storage Hashtable.  First the
            // Hashtable is restored, then any explicit table columns
            // which should be included in the Hashtable are added.
            byte[] objectData = row.getValue(objectDataPosition).asBytes();
            Hashtable tempHash = (Hashtable)
                    ObjectUtils.deserialize(objectData);
            if (tempHash == null)
            {
                tempHash = new Hashtable(10);
            }

            for (int j = 0; j < columnNames.length; j++)
            {
                if (!(columnNames[j].equalsIgnoreCase(USER_ID_COLUMN)
                        || columnNames[j].equalsIgnoreCase(OBJECT_DATA_COLUMN)))
                {
                    Object obj2 = null;
                    Value value = row.getValue(j + 1);
                    if (value.isByte())
                    {
                        obj2 = new Byte(value.asByte());
                    }
                    if (value.isBigDecimal())
                    {
                        obj2 = value.asBigDecimal();
                    }
                    if (value.isBytes())
                    {
                        obj2 = value.asBytes();
                    }
                    if (value.isDate())
                    {
                        obj2 = value.asDate();
                    }
                    if (value.isShort())
                    {
                        obj2 = new Short(value.asShort());
                    }
                    if (value.isInt())
                    {
                        obj2 = new Integer(value.asInt());
                    }
                    if (value.isLong())
                    {
                        obj2 = new Long(value.asLong());
                    }
                    if (value.isDouble())
                    {
                        obj2 = new Double(value.asDouble());
                    }
                    if (value.isFloat())
                    {
                        obj2 = new Float(value.asFloat());
                    }
                    if (value.isBoolean())
                    {
                        obj2 = Boolean.valueOf(value.asBoolean());
                    }
                    if (value.isString())
                    {
                        obj2 = value.asString();
                    }
                    if (value.isTime())
                    {
                        obj2 = value.asTime();
                    }
                    if (value.isTimestamp())
                    {
                        obj2 = value.asTimestamp();
                    }
                    if (value.isUtilDate())
                    {
                        obj2 = value.asUtilDate();
                    }
                    if (obj2 != null)
                    {
                        tempHash.put(columnNames[j], obj2);
                    }
                }
            }
            obj.setPermStorage(tempHash);
        }
        catch (Exception ex)
        {
            throw new TorqueException(ex);
        }
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     *        the SELECT statement.
     * @return Vector containing TurbineUser objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria)
        throws TorqueException
    {
        return doSelect(criteria, (User) null);
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     *        the SELECT statement.
     * @param current User object that is to be used as part of the
     *        results - if not passed, then a new one is created.
     * @return Vector containing TurbineUser objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria, User current)
        throws TorqueException
    {
        // add User table columns
        addSelectColumns(criteria);

        if (criteria.getOrderByColumns() == null)
        {
            criteria.addAscendingOrderByColumn(LAST_NAME);
        }

        // Place any checks here to intercept criteria which require
        // custom SQL.  For example:
        // if ( criteria.containsKey("SomeTable.SomeColumn") )
        // {
        //     String whereSql = "SomeTable.SomeColumn IN (Select ...";
        //     criteria.add("SomeTable.SomeColumn",
        //                  whereSQL, criteria.CUSTOM);
        // }

        // BasePeer returns a Vector of Record (Village) objects.  The
        // array order follows the order columns were placed in the
        // Select clause.
        List rows = BasePeer.doSelect(criteria);
        List results = new ArrayList();

        // Populate the object(s).
        for (int i = 0; i < rows.size(); i++)
        {
            Record row = (Record) rows.get(i);
            // Add User to the return Vector.
            if (current == null)
            {
                results.add(row2Object(row, 1, null));
            }
            else
            {
                populateObject(row, 1, current);
                ((Persistent) current).setNew(false);
            }
        }
        return results;
    }

    /**
     * Issues a select based on a criteria.
     *
     * @param criteria Object containing data that is used to create
     *        the SELECT statement.
     * @param dbConn
     * @return List containing TurbineUser objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria, Connection dbConn)
        throws TorqueException
    {
        // add User table columns
        addSelectColumns(criteria);

        if (criteria.getOrderByColumns() == null)
        {
            criteria.addAscendingOrderByColumn(LAST_NAME);
        }

        // BasePeer returns a List of Record (Village) objects.  The
        // array order follows the order columns were placed in the
        // Select clause.
        List rows = BasePeer.doSelect(criteria, dbConn);
        List results = new ArrayList();

        // Populate the object(s).
        for (int i = 0; i < rows.size(); i++)
        {
            Record row = (Record) rows.get(i);
            // Add User to the return Vector.
            results.add(row2Object(row, 1, null));
        }
        return results;
    }

    /**
     * Implementss torque peers' method.  Does not use the Class argument
     * as Users need to go through TurbineSecurity
     *
     * @exception TorqueException a generic exception.
     */
    public static User row2Object(Record row, int offset, Class cls)
        throws TorqueException
    {
        try
        {
            User obj = TurbineSecurity.getUserInstance();
            populateObject(row, offset, obj);
            ((Persistent) obj).setNew(false);
            ((Persistent) obj).setModified(false);
            return obj;
        }
        catch (Exception ex)
        {
            throw new TorqueException (ex);
        }
    }

    /**
     * The type of User this peer will instantiate.
     *
     * @exception Exception a generic exception.
     */
    public static Class getOMClass() throws Exception
    {
        return TurbineSecurity.getUserClass();
    }

    /**
     * Issues an update based on a criteria.
     * The criteria only uses USER_ID.
     *
     * @param criteria Object containing data that is used to create
     *        the UPDATE statement.
     * @exception TorqueException a generic exception.
     */
    public static void doUpdate(Criteria criteria)
        throws TorqueException
    {
        Criteria selectCriteria = new Criteria(2);
        selectCriteria.put(USER_ID, criteria.remove(USER_ID));
        BasePeer.doUpdate(selectCriteria, criteria);
    }

    /**
     * Checks if a User is defined in the system. The name
     * is used as query criteria.
     *
     * @param user The User to be checked.
     * @return <code>true</code> if given User exists in the system.
     * @throws DataBackendException when more than one User with
     *         the same name exists.
     * @throws Exception a generic exception.
     */
    public static boolean checkExists(User user)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();
        criteria.addSelectColumn(USER_ID);
        criteria.add(USERNAME, user.getName());
        List results = BasePeer.doSelect(criteria);
        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple users named '"
                    + user.getName() + "' exist!");
        }
        return (results.size() == 1);
    }

    /**
     * Returns a vector of all User objects.
     *
     * @return A Vector with all users in the system.
     * @exception Exception a generic exception.
     */
    public static List selectAllUsers()
        throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(TurbineUserPeer.LAST_NAME);
        criteria.addAscendingOrderByColumn(TurbineUserPeer.FIRST_NAME);
        criteria.setIgnoreCase(true);
        return TurbineUserPeer.doSelect(criteria);
    }

    /**
     * Returns a vector of all confirmed User objects.
     *
     * @return A Vector with all confirmed users in the system.
     * @exception Exception a generic exception.
     */
    public static List selectAllConfirmedUsers()
        throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(User.CONFIRM_VALUE, User.CONFIRM_DATA);
        criteria.addAscendingOrderByColumn(TurbineUserPeer.LAST_NAME);
        criteria.addAscendingOrderByColumn(TurbineUserPeer.FIRST_NAME);
        criteria.setIgnoreCase(true);
        return TurbineUserPeer.doSelect(criteria);
    }

    /**
     * Returns the TableMap related to this peer.  This method is not
     * needed for general use but a specific application could have a
     * need.
     */
    protected static TableMap getTableMap()
    {
        return MAP_BUILDER.getDatabaseMap().getTable(TABLE_NAME);
    }
}
