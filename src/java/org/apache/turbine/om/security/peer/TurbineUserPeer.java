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
import com.workingdogs.village.Schema;
import com.workingdogs.village.Value;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
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
    /** The mapBuilder for this Peer. */
    private static final TurbineMapBuilder mapBuilder = (TurbineMapBuilder)
            getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");

    // column names
    /** The column name for the visitor id field. */
    private static final String USER_ID_COLUMN = mapBuilder.getUserId();

    /** The column name for the login name field */
    private static final String USERNAME_COLUMN = mapBuilder.getUsername();

    /** The key name for the first name field. */
    private static final String FIRST_NAME_COLUMN = mapBuilder.getFirstName();

    /** The key name for the last name field. */
    private static final String LAST_NAME_COLUMN = mapBuilder.getLastName();

    /** The column name for the modified field. */
    private static final String MODIFIED_COLUMN = mapBuilder.getModified();

    /** The column name for the created field. */
    private static final String CREATED_COLUMN = mapBuilder.getCreated();

    /** The column name for the last_login field. */
    private static final String LAST_LOGIN_COLUMN = mapBuilder.getLastLogin();

    /** The column name for the email field. */
    private static final String EMAIL_COLUMN = mapBuilder.getEmail();

    /** The column name for the confirm_value field. */
    private static final String CONFIRM_VALUE_COLUMN
            = mapBuilder.getConfirmValue();

    /** This is the value that is stored in the database for confirmed users. */
    public static final String CONFIRM_DATA
            = org.apache.turbine.om.security.User.CONFIRM_DATA;

    /** The column name for the visitor id field. */
    private static final String OBJECT_DATA_COLUMN = mapBuilder.getObjectData();

    /** The table name for this peer. */
    private static final String TABLE_NAME = mapBuilder.getTableUser();

    // Criteria Keys
    /** The key name for the visitor id field. */
    public static final String USER_ID = mapBuilder.getUser_UserId();

    /** The key name for the username field. */
    public static final String USERNAME = mapBuilder.getUser_Username();

    /** The key name for the password field. */
    public static final String PASSWORD = mapBuilder.getUser_Password();

    /** The key name for the first name field. */
    public static final String FIRST_NAME = mapBuilder.getUser_FirstName();

    /** The key name for the last name field. */
    public static final String LAST_NAME = mapBuilder.getUser_LastName();

    /** The key name for the modified field. */
    public static final String MODIFIED = mapBuilder.getUser_Modified();

    /** The key name for the created field. */
    public static final String CREATED = mapBuilder.getUser_Created();

    /** The key name for the email field. */
    public static final String EMAIL = mapBuilder.getUser_Email();

    /** The key name for the last_login field. */
    public static final String LAST_LOGIN = mapBuilder.getUser_LastLogin();

    /** The key name for the confirm_value field. */
    public static final String CONFIRM_VALUE
            = mapBuilder.getUser_ConfirmValue();

    /** The key name for the object_data field. */
    public static final String OBJECT_DATA = mapBuilder.getUser_ObjectData();

    /** The Oracle sequence name for this peer. */
    private static final String SEQUENCE_NAME = mapBuilder.getSequenceUser();

    /** The schema. */
    private static Schema schema = initTableSchema(TABLE_NAME);

    /** The columns. */
    private static com.workingdogs.village.Column[] columns
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
            byte[] objectData = (byte[])
                    row.getValue(objectDataPosition).asBytes();
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
                        obj2 = new Boolean(value.asBoolean());
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
        // stuff for postgresql problem.....
        criteria.setBlobFlag(true);

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
     * @return Vector containing TurbineUser objects.
     * @exception TorqueException a generic exception.
     */
    public static List doSelect(Criteria criteria, Connection dbConn)
        throws TorqueException
    {
        // stuff for postgresql problem.....
        criteria.setBlobFlag(true);

        // add User table columns
        addSelectColumns(criteria);

        if (criteria.getOrderByColumns() == null)
        {
            criteria.addAscendingOrderByColumn(LAST_NAME);
        }

        // BasePeer returns a Vector of Record (Village) objects.  The
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
        criteria.add(USERNAME, user.getUserName());
        List results = BasePeer.doSelect(criteria);
        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple users named '"
                    + user.getUserName() + "' exist!");
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
        return mapBuilder.getDatabaseMap().getTable(TABLE_NAME);
    }
}
