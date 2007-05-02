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
import java.util.List;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.torque.TorqueException;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.DataBackendException;

/**
 * This class capsulates all direct Peer access for the User entities.
 * It allows the exchange of the default Turbine supplied TurbineUserPeer
 * class against a custom class.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class UserPeerManager
    implements UserPeerManagerConstants
{
    /** Serial UID */
    private static final long serialVersionUID = 6943046259921811593L;

    /** The class of the Peer the TorqueSecurityService uses */
    private static Class userPeerClass = null;

    /** The class name of the objects returned by the configured peer. */
    private static Class userObject = null;

    /** The name of the Table used for Group Object queries  */
    private static String tableName = null;

    /** The name of the column used as "Name" Column */
    private static String nameColumn = null;

    /** The name of the column used as "Id" Column */
    private static String idColumn = null;

    /** The name of the column used as "Password" Column */
    private static String passwordColumn = null;

    /** The name of the column used as "First name" Column */
    private static String firstNameColumn = null;

    /** The name of the column used as "Last name" Column */
    private static String lastNameColumn = null;

    /** The name of the column used as "Email" Column */
    private static String emailColumn = null;

    /** The name of the column used as "Confirm" Column */
    private static String confirmColumn = null;

    /** The name of the column used as "create date" Column */
    private static String createDateColumn = null;

    /** The name of the column used as "last login" Column */
    private static String lastLoginColumn = null;

    /** The name of the column used as "objectdata" Column */
    private static String objectdataColumn = null;

    /** The "Name" property descriptor */
    private static PropertyDescriptor namePropDesc = null;

    /** The "Id" property descriptor */
    private static PropertyDescriptor idPropDesc = null;

    /** The "Password" property descriptor */
    private static PropertyDescriptor passwordPropDesc = null;

    /** The "First name" property descriptor */
    private static PropertyDescriptor firstNamePropDesc = null;

    /** The "Last name" property descriptor */
    private static PropertyDescriptor lastNamePropDesc = null;

    /** The "Email" property descriptor */
    private static PropertyDescriptor emailPropDesc = null;

    /** The "Confirm" property descriptor */
    private static PropertyDescriptor confirmPropDesc = null;

    /** The "create date" property descriptor */
    private static PropertyDescriptor createDatePropDesc = null;

    /** The "last login" property descriptor */
    private static PropertyDescriptor lastLoginPropDesc = null;

    /** The "objectdata" property descriptor */
    private static PropertyDescriptor objectdataPropDesc = null;

    /** Logging */
    static Log log = LogFactory.getLog(UserPeerManager.class);

    /**
     * Initializes the UserPeerManager, loading the class object for the
     * Peer used to retrieve User objects
     *
     * @param conf The configuration object used to configure the Manager
     *
     * @exception InitializationException A problem occured during
     *            initialization
     */

    public static void init(Configuration conf)
        throws InitializationException
    {
        String userPeerClassName = conf.getString(USER_PEER_CLASS_KEY,
                                                  USER_PEER_CLASS_DEFAULT);
        String userObjectName = null;

        try
        {
            userPeerClass = Class.forName(userPeerClassName);

            tableName  =
              (String) userPeerClass.getField("TABLE_NAME").get(null);

            //
            // We have either an user configured Object class or we use the
            // default as supplied by the Peer class
            //

            // Default from Peer, can be overridden

            userObject = getPersistenceClass();

            userObjectName = conf.getString(USER_CLASS_KEY,
                    userObject.getName());

            // Maybe the user set a new value...
            userObject = Class.forName(userObjectName);

            /* If any of the following Field queries fails, the user
             * subsystem is unusable. So check this right here at init time,
             * which saves us much time and hassle if it fails...
             */

            nameColumn = (String) userPeerClass.getField(
                conf.getString(USER_NAME_COLUMN_KEY,
                               USER_NAME_COLUMN_DEFAULT)
                ).get(null);

            idColumn = (String) userPeerClass.getField(
                conf.getString(USER_ID_COLUMN_KEY,
                               USER_ID_COLUMN_DEFAULT)
                ).get(null);

            passwordColumn = (String) userPeerClass.getField(
                conf.getString(USER_PASSWORD_COLUMN_KEY,
                               USER_PASSWORD_COLUMN_DEFAULT)
                ).get(null);

            firstNameColumn  = (String) userPeerClass.getField(
                conf.getString(USER_FIRST_NAME_COLUMN_KEY,
                               USER_FIRST_NAME_COLUMN_DEFAULT)
                ).get(null);

            lastNameColumn = (String) userPeerClass.getField(
                conf.getString(USER_LAST_NAME_COLUMN_KEY,
                               USER_LAST_NAME_COLUMN_DEFAULT)
                ).get(null);

            emailColumn = (String) userPeerClass.getField(
                conf.getString(USER_EMAIL_COLUMN_KEY,
                               USER_EMAIL_COLUMN_DEFAULT)
                ).get(null);

            confirmColumn    = (String) userPeerClass.getField(
                conf.getString(USER_CONFIRM_COLUMN_KEY,
                               USER_CONFIRM_COLUMN_DEFAULT)
                ).get(null);

            createDateColumn = (String) userPeerClass.getField(
                conf.getString(USER_CREATE_COLUMN_KEY,
                               USER_CREATE_COLUMN_DEFAULT)
                ).get(null);

            lastLoginColumn = (String) userPeerClass.getField(
                conf.getString(USER_LAST_LOGIN_COLUMN_KEY,
                               USER_LAST_LOGIN_COLUMN_DEFAULT)
                ).get(null);

            objectdataColumn = (String) userPeerClass.getField(
                conf.getString(USER_OBJECTDATA_COLUMN_KEY,
                               USER_OBJECTDATA_COLUMN_DEFAULT)
                ).get(null);

            namePropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_NAME_PROPERTY_KEY,
                                           USER_NAME_PROPERTY_DEFAULT),
                                       userObject);

            idPropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_ID_PROPERTY_KEY,
                                           USER_ID_PROPERTY_DEFAULT),
                                       userObject);

            passwordPropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_PASSWORD_PROPERTY_KEY,
                                           USER_PASSWORD_PROPERTY_DEFAULT),
                                       userObject);

            firstNamePropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_FIRST_NAME_PROPERTY_KEY,
                                           USER_FIRST_NAME_PROPERTY_DEFAULT),
                                       userObject);

            lastNamePropDesc   =
                new PropertyDescriptor(conf.getString(
                                           USER_LAST_NAME_PROPERTY_KEY,
                                           USER_LAST_NAME_PROPERTY_DEFAULT),
                                       userObject);

            emailPropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_EMAIL_PROPERTY_KEY,
                                           USER_EMAIL_PROPERTY_DEFAULT),
                                       userObject);

            confirmPropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_CONFIRM_PROPERTY_KEY,
                                           USER_CONFIRM_PROPERTY_DEFAULT),
                                       userObject);

            createDatePropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_CREATE_PROPERTY_KEY,
                                           USER_CREATE_PROPERTY_DEFAULT),
                                       userObject);

            lastLoginPropDesc  =
                new PropertyDescriptor(conf.getString(
                                           USER_LAST_LOGIN_PROPERTY_KEY,
                                           USER_LAST_LOGIN_PROPERTY_DEFAULT),
                                       userObject);

            objectdataPropDesc =
                new PropertyDescriptor(conf.getString(
                                           USER_OBJECTDATA_PROPERTY_KEY,
                                           USER_OBJECTDATA_PROPERTY_DEFAULT),
                                       userObject);
        }
        catch (Exception e)
        {
            if (userPeerClassName == null || userPeerClass == null)
            {
                throw new InitializationException(
                    "Could not find UserPeer class ("
                    + userPeerClassName + ")", e);
            }
            if (tableName == null)
            {
                throw new InitializationException(
                    "Failed to get the table name from the Peer object", e);
            }

            if (userObject == null || userObjectName == null)
            {
                throw new InitializationException(
                    "Failed to get the object type from the Peer object", e);
            }


            if (nameColumn == null || namePropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no name column information!", e);
            }
            if (idColumn == null || idPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no id column information!", e);
            }
            if (passwordColumn == null || passwordPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no password column information!", e);
            }
            if (firstNameColumn == null || firstNamePropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no firstName column information!", e);
            }
            if (lastNameColumn == null || lastNamePropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no lastName column information!", e);
            }
            if (emailColumn == null || emailPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no email column information!", e);
            }
            if (confirmColumn == null || confirmPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no confirm column information!", e);
            }
            if (createDateColumn == null || createDatePropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no createDate column information!", e);
            }
            if (lastLoginColumn == null || lastLoginPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no lastLogin column information!", e);
            }
            if (objectdataColumn == null || objectdataPropDesc == null)
            {
                throw new InitializationException(
                    "UserPeer " + userPeerClassName
                    + " has no objectdata column information!", e);
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
     * use as the Name Column for a group
     *
     * @return A String containing the column name
     */
    public static String getNameColumn()
    {
        return nameColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Id Column for a group
     *
     * @return A String containing the column id
     */
    public static String getIdColumn()
    {
        return idColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Password Column for a role
     *
     * @return A String containing the column name
     */
    public static String getPasswordColumn()
    {
        return passwordColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the FirstName Column for a role
     *
     * @return A String containing the column name
     */
    public static String getFirstNameColumn()
    {
        return firstNameColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the LastName Column for a role
     *
     * @return A String containing the column name
     */
    public static String getLastNameColumn()
    {
        return lastNameColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Email Column for a role
     *
     * @return A String containing the column name
     */
    public static String getEmailColumn()
    {
        return emailColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the Confirm Column for a role
     *
     * @return A String containing the column name
     */
    public static String getConfirmColumn()
    {
        return confirmColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the CreateDate Column for a role
     *
     * @return A String containing the column name
     */
    public static String getCreateDateColumn()
    {
        return createDateColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the LastLogin Column for a role
     *
     * @return A String containing the column name
     */
    public static String getLastLoginColumn()
    {
        return lastLoginColumn;
    }

    /**
     * Returns the fully qualified name of the Column to
     * use as the objectdata Column for a role
     *
     * @return A String containing the column name
     */
    public static String getObjectdataColumn()
    {
        return objectdataColumn;
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
     * Returns the full name of a column.
     *
     * @param name The column to fully qualify
     *
     * @return A String with the full name of the column.
     * @deprecated use getColumnName(String name)
     */
    public String getFullColumnName(String name)
    {
        return getColumnName(name);
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

        if (userObject == null)
        {
            // This can happen if the Turbine wants to determine the
            // name of the anonymous user before the security service
            // has been initialized. In this case, the Peer Manager
            // has not yet been inited and the userObject is still
            // null. Return null in this case.
            //
            return obj;
        }

        try
        {
            obj = (Persistent) userObject.newInstance();
        }
        catch (Exception e)
        {
            log.error("Could not instantiate a user object", e);
            obj = null;
        }
        return obj;
    }

    /**
     * Checks if a User is defined in the system. The name
     * is used as query criteria.
     *
     * @param user The User to be checked.
     * @return <code>true</code> if given User exists in the system.
     * @throws DataBackendException when more than one User with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    public static boolean checkExists(User user)
        throws DataBackendException, Exception
    {
        Criteria criteria = new Criteria();

        criteria.addSelectColumn(getIdColumn());

        criteria.add(getNameColumn(), user.getName());

        List results = BasePeer.doSelect(criteria);

        if (results.size() > 1)
        {
            throw new DataBackendException("Multiple users named '" +
                                           user.getName() + "' exist!");
        }
        return (results.size() == 1);
    }

    /**
     * Returns a List of all User objects.
     *
     * @return A List with all users in the system.
     * @exception Exception A generic exception.
     */
    public static List selectAllUsers()
        throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.addAscendingOrderByColumn(getLastNameColumn());
        criteria.addAscendingOrderByColumn(getFirstNameColumn());
        criteria.setIgnoreCase(true);
        return doSelect(criteria);
    }

    /**
     * Returns a List of all confirmed User objects.
     *
     * @return A List with all confirmed users in the system.
     * @exception Exception A generic exception.
     */
    public static List selectAllConfirmedUsers()
        throws Exception
    {
        Criteria criteria = new Criteria();

        criteria.add (getConfirmColumn(), User.CONFIRM_DATA);
        criteria.addAscendingOrderByColumn(getLastNameColumn());
        criteria.addAscendingOrderByColumn(getFirstNameColumn());
        criteria.setIgnoreCase(true);
        return doSelect(criteria);
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
     * Calls buildCriteria(User user) in the configured UserPeer. If you get
     * a ClassCastException in this routine, you put a User object into this
     * method which can't be cast into an object for the TorqueSecurityService. This is a
     * configuration error most of the time.
     *
     * @param user An object which implements the User interface
     *
     * @return A criteria for the supplied user object
     */

    public static Criteria buildCriteria(User user)
    {
        Criteria crit;

        try
        {
            Class[] clazz = new Class[] { userObject };
            Object[] params =
                new Object[] { ((TorqueUser) user).getPersistentObj() };

            crit =  (Criteria) userPeerClass
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

            userPeerClass
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

            userPeerClass
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
     * @return A List of User Objects selected by the Criteria
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

            list = (List) userPeerClass
                .getMethod("doSelect", clazz)
                .invoke(null, params);
        }
        catch (Exception e)
        {
            throw new TorqueException("doSelect failed", e);
        }
        List newList = new ArrayList(list.size());

        //
        // Wrap the returned Objects into TorqueUsers.
        //
        for (Iterator it = list.iterator(); it.hasNext(); )
        {
            User u = getNewUser((Persistent) it.next());
            newList.add(u);
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

            userPeerClass
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
    public static void setUserName(Persistent obj, String name)
    {
        if (obj == null)
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
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
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
     *
     * @deprecated use getName(obj)
     */
    public static String getUserName(Persistent obj)
    {
        return getName(obj);
    }

    /**
     * Invokes getName() on the supplied base object
     *
     * @param obj The object to use for getting the name
     *
     * @return A string containing the name
     */
    public static String getName(Persistent obj)
    {
        String name = null;

        if (obj == null)
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
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
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
     * Invokes setPassword(String s) on the supplied base object
     *
     * @param obj The object to use for setting the password
     * @param password The Password to set
     */
    public static void setUserPassword(Persistent obj, String password)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { password };
            passwordPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getPassword() on the supplied base object
     *
     * @param obj The object to use for getting the password
     *
     * @return A string containing the password
     */
    public static String getUserPassword(Persistent obj)
    {
        String password = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            password = (String) passwordPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return password;
    }

    /**
     * Invokes setFirstName(String s) on the supplied base object
     *
     * @param obj The object to use for setting the first name
     * @param firstName The first name to set
     */
    public static void setUserFirstName(Persistent obj, String firstName)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { firstName };
            firstNamePropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getFirstName() on the supplied base object
     *
     * @param obj The object to use for getting the first name
     *
     * @return A string containing the first name
     */
    public static String getUserFirstName(Persistent obj)
    {
        String firstName = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            firstName = (String) firstNamePropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return firstName;
    }

    /**
     * Invokes setLastName(String s) on the supplied base object
     *
     * @param obj The object to use for setting the last name
     * @param lastName The Last Name to set
     */
    public static void setUserLastName(Persistent obj, String lastName)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { lastName };
            lastNamePropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getLastName() on the supplied base object
     *
     * @param obj The object to use for getting the last name
     *
     * @return A string containing the last name
     */
    public static String getUserLastName(Persistent obj)
    {
        String lastName = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            lastName = (String) lastNamePropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return lastName;
    }

    /**
     * Invokes setEmail(String s) on the supplied base object
     *
     * @param obj The object to use for setting the email
     * @param email The Email to set
     */
    public static void setUserEmail(Persistent obj, String email)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { email };
            emailPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getEmail() on the supplied base object
     *
     * @param obj The object to use for getting the email
     *
     * @return A string containing the email
     */
    public static String getUserEmail(Persistent obj)
    {
        String email = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            email = (String) emailPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return email;
    }

    /**
     * Invokes setConfirmed(String s) on the supplied base object
     *
     * @param obj The object to use for setting the confirm value
     * @param confirm The confirm value to set
     */
    public static void setUserConfirmed(Persistent obj, String confirm)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { confirm };
            confirmPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getConfirmed() on the supplied base object
     *
     * @param obj The object to use for getting the confirm value
     *
     * @return A string containing the confirm value
     */
    public static String getUserConfirmed(Persistent obj)
    {
        String confirm = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            confirm = (String) confirmPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return confirm;
    }

    /**
     * Invokes setCreateDate(java.util.Date date) on the supplied base object
     *
     * @param obj The object to use for setting the create date
     * @param createDate The create date to set
     */
    public static void setUserCreateDate(Persistent obj, java.util.Date createDate)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { createDate };
            createDatePropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getCreateDate() on the supplied base object
     *
     * @param obj The object to use for getting the create date
     *
     * @return A string containing the create date
     */
    public static java.util.Date getUserCreateDate(Persistent obj)
    {
        java.util.Date createDate = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            createDate = (java.util.Date) createDatePropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return createDate;
    }

    /**
     * Invokes setLastLogin(java.util.Date date) on the supplied base object
     *
     * @param obj The object to use for setting the last login daet
     * @param lastLogin The last login date to set
     */
    public static void setUserLastLogin(Persistent obj, java.util.Date lastLogin)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { lastLogin };
            lastLoginPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getLastLogin() on the supplied base object
     *
     * @param obj The object to use for getting the last login date
     *
     * @return A string containing the last login date
     */
    public static java.util.Date getUserLastLogin(Persistent obj)
    {
        java.util.Date lastLogin = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            lastLogin = (java.util.Date) lastLoginPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return lastLogin;
    }

    /**
     * Invokes setObjectdata(byte [] date) on the supplied base object
     *
     * @param obj The object to use for setting the last login daet
     * @param objectdata The objectdata to use
     */
    public static void setUserObjectdata(Persistent obj, byte [] objectdata)
    {
        if (obj == null)
        {
            return;
        }

        try
        {
            Object[] params = new Object[] { objectdata };
            objectdataPropDesc.getWriteMethod().invoke(obj, params);
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
    }

    /**
     * Invokes getObjectdata() on the supplied base object
     *
     * @param obj The object to use for getting the last login date
     *
     * @return A string containing the last login date
     */
    public static byte [] getUserObjectdata(Persistent obj)
    {
        byte [] objectdata = null;

        if (obj == null)
        {
            return null;
        }

        try
        {
            objectdata = (byte []) objectdataPropDesc
                .getReadMethod()
                .invoke(obj, new Object[] {});
        }
        catch (ClassCastException cce)
        {
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
            log.error(msg);
            throw new RuntimeException(msg);
        }
        catch (Exception e)
        {
            log.error(e, e);
        }
        return objectdata;
    }

    /**
     * Invokes setId(int n) on the supplied base object
     *
     * @param obj The object to use for setting the name
     * @param id The new Id
     */
    public static void setId(Persistent obj, int id)
    {
        if (obj == null)
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
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
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

        if (obj == null)
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
            String msg = obj.getClass().getName() + " does not seem to be an User Object!";
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

            persistenceClass =  (Class) userPeerClass
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
     * Returns a new, configured User Object with
     * a supplied Persistent object at its core
     *
     * @param p The persistent object
     *
     * @return a new, configured User Object
     *
     * @exception Exception Could not create a new Object
     *
     */

    public static User getNewUser(Persistent p)
    {
        User u = null;
        try
        {
            Class userWrapperClass = TurbineSecurity.getUserClass();

            Class [] clazz = new Class [] { Persistent.class };
            Object [] params = new Object [] { p };

            u = (User) userWrapperClass
                .getConstructor(clazz)
                .newInstance(params);
        }
        catch (Exception e)
        {
            log.error("Could not instantiate a new user from supplied persistent: ", e);
        }

        return u;
    }
}


