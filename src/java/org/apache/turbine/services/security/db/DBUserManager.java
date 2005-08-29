package org.apache.turbine.services.security.db;

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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.torque.om.BaseObject;
import org.apache.torque.om.ObjectKey;
import org.apache.torque.om.Persistent;
import org.apache.torque.util.Criteria;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.peer.TurbineUserPeer;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.security.UserManager;
import org.apache.turbine.util.db.map.TurbineMapBuilder;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * An UserManager performs {@link org.apache.turbine.om.security.User}
 * objects related tasks on behalf of the
 * {@link org.apache.turbine.services.security.BaseSecurityService}.
 *
 * This implementation uses a relational database for storing user data. It
 * expects that the User interface implementation will be castable to
 * {@link org.apache.torque.om.BaseObject}.
 *
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DBUserManager
        implements UserManager
{
    /** Logging */
    private static Log log = LogFactory.getLog(DBUserManager.class);

    /**
     * Initializes the UserManager
     *
     * @param conf A Configuration object to init this Manager
     */
    public void init(Configuration conf)
    {
        // GNDN
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean accountExists(User user)
            throws DataBackendException
    {
        return accountExists(user.getName());
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean accountExists(String userName)
            throws DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USERNAME, userName);
        List users;
        try
        {
            users = TurbineUserPeer.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                    "Failed to check account's presence", e);
        }
        if (users.size() > 1)
        {
            throw new DataBackendException(
                    "Multiple Users with same username '" + userName + "'");
        }
        return (users.size() == 1);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key.
     *
     * @param userName the name of the user.
     * @return an User object.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User retrieve(String userName)
            throws UnknownEntityException, DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USERNAME, userName);

        List users = retrieveList(criteria);

        if (users.size() > 1)
        {
            throw new DataBackendException(
                    "Multiple Users with same username '" + userName + "'");
        }
        if (users.size() == 1)
        {
            return (User) users.get(0);
        }
        throw new UnknownEntityException("Unknown user '" + userName + "'");
    }

    /**
     * Retrieve a user from persistent storage using the primary key
     *
     * @param key The primary key object
     * @return an User object.
     * @throws UnknownEntityException if the user's record does not
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public User retrieveById(Object key)
            throws UnknownEntityException, DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USER_ID, key);

        List users = retrieveList(criteria);

        if (users.size() > 1)
        {
            throw new DataBackendException(
                "Multiple Users with same unique Key '" + String.valueOf(key) + "'");
        }
        if (users.size() == 1)
        {
            return (User) users.get(0);
        }
        throw new UnknownEntityException("Unknown user with key '" + String.valueOf(key) + "'");
    }

    /**
     * Retrieve a list of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of Torque implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public List retrieveList(Criteria criteria)
        throws DataBackendException
    {
        for (Iterator keys = criteria.keySet().iterator(); keys.hasNext(); )
        {
            String key = (String) keys.next();

            // set the table name for all attached criterion
            Criteria.Criterion[] criterion = criteria
                    .getCriterion(key).getAttachedCriterion();

            for (int i = 0; i < criterion.length; i++)
            {
                if (StringUtils.isEmpty(criterion[i].getTable()))
                {
                    criterion[i].setTable(TurbineUserPeer.getTableName());
                }
            }
        }
        List users = null;
        try
        {
            users = TurbineUserPeer.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to retrieve users", e);
        }
        return users;
    }

    /**
     * Retrieve a set of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     * @deprecated Use <a href="#retrieveList">retrieveList</a> instead.
     */
    public User[] retrieve(Criteria criteria)
            throws DataBackendException
    {
        return (User []) retrieveList(criteria).toArray(new User[0]);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key, and authenticate the user. The implementation may chose
     * to authenticate to the server as the user whose data is being
     * retrieved.
     *
     * @param userName the name of the user.
     * @param password the user supplied password.
     * @return an User object.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User retrieve(String userName, String password)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        User user = retrieve(userName);
        authenticate(user, password);
        return user;
    }

    /**
     * Save an User object to persistent storage. User's account is
     * required to exist in the storage.
     *
     * @param user an User object to store.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void store(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }

        Criteria criteria = TurbineUserPeer.buildCriteria(user);
        try
        {
            TurbineUserPeer.doUpdate(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to save user object", e);
        }
    }

    /**
     * Saves User data when the session is unbound. The user account is required
     * to exist in the storage.
     *
     * LastLogin, AccessCounter, persistent pull tools, and any data stored
     * in the permData hashtable that is not mapped to a column will be saved.
     *
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void saveOnSessionUnbind(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (!user.hasLoggedIn())
        {
            return;
        }

        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }
        Criteria crit = new Criteria();
        if (!((Persistent) user).isNew())
        {
            crit.add(TurbineUserPeer.USER_ID, ((Persistent) user).getPrimaryKey());
        }

        Hashtable permStorage = (Hashtable) user.getPermStorage().clone();
        crit.add(TurbineUserPeer.LAST_LOGIN, permStorage.remove(TurbineUserPeer.LAST_LOGIN));

        // The OBJECT_DATA column only stores data not mapped to a column.  We must
        // remove all of the extra data and serialize the rest.  Access Counter
        // is not mapped to a column so it will be serialized into OBJECT_DATA.
        for (int i = 1; i < TurbineUserPeer.columnNames.length; i++)
        {
            if (permStorage.containsKey(TurbineUserPeer.columnNames[i]))
            {
                permStorage.remove(TurbineUserPeer.columnNames[i]);
            }
        }
        crit.add(TurbineUserPeer.OBJECT_DATA, permStorage);

        try
        {
            TurbineUserPeer.doUpdate(crit);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to save user object", e);
        }

    }

    /**
     * Authenticate an User with the specified password. If authentication
     * is successful the method returns nothing. If there are any problems,
     * exception was thrown.
     *
     * @param user an User object to authenticate.
     * @param password the user supplied password.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void authenticate(User user, String password)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }

        // log.debug("Supplied Pass: " + password);
        // log.debug("User Pass: " + user.getPassword());

        /*
         * Unix crypt needs the existing, encrypted password text as
         * salt for checking the supplied password. So we supply it
         * into the checkPassword routine
         */

        if (!TurbineSecurity.checkPassword(password, user.getPassword()))
        {
            throw new PasswordMismatchException("The passwords do not match");
        }
    }

    /**
     * Change the password for an User. The user must have supplied the
     * old password to allow the change.
     *
     * @param user an User to change password for.
     * @param oldPassword The old password to verify
     * @param newPassword The new password to set
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void changePassword(User user, String oldPassword,
                               String newPassword)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }

        if (!TurbineSecurity.checkPassword(oldPassword, user.getPassword()))
        {
            throw new PasswordMismatchException(
                    "The supplied old password for '" + user.getName() +
                    "' was incorrect");
        }
        user.setPassword(TurbineSecurity.encryptPassword(newPassword));
        // save the changes in the database imediately, to prevent the password
        // being 'reverted' to the old value if the user data is lost somehow
        // before it is saved at session's expiry.
        store(user);
    }

    /**
     * Forcibly sets new password for an User.
     *
     * This is supposed by the administrator to change the forgotten or
     * compromised passwords. Certain implementatations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }
        user.setPassword(TurbineSecurity.encryptPassword(password));
        // save the changes in the database immediately, to prevent the
        // password being 'reverted' to the old value if the user data
        // is lost somehow before it is saved at session's expiry.
        store(user);
    }

    /**
     * Creates new user account with specified attributes.
     *
     * @param user The object describing account to be created.
     * @param initialPassword the password for the new account
     * @throws DataBackendException if there was an error accessing
     the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void createAccount(User user, String initialPassword)
            throws EntityExistsException, DataBackendException
    {
        if (StringUtils.isEmpty(user.getName()))
        {
            throw new DataBackendException("Could not create "
                    + "an user with empty name!");
        }

        if (accountExists(user))
        {
            throw new EntityExistsException("The account '" +
                    user.getName() + "' already exists");
        }
        user.setPassword(TurbineSecurity.encryptPassword(initialPassword));

        Criteria criteria = TurbineUserPeer.buildCriteria(user);
        try
        {
            // perform the insert to the database
            ObjectKey pk = TurbineUserPeer.doInsert(criteria);

            // update the user object with the primary key
            TurbineMapBuilder mapbuilder = (TurbineMapBuilder)
                    TurbineUserPeer.getMapBuilder("org.apache.turbine.util.db.map.TurbineMapBuilder");
            user.setPerm(mapbuilder.getUserId(), pk);
            ((BaseObject) user).setPrimaryKey(pk);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to create account '" +
                    user.getName() + "'", e);
        }
    }

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing
     the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeAccount(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USERNAME, user.getName());
        try
        {
            TurbineUserPeer.doDelete(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to remove account '" +
                    user.getName() + "'", e);
        }
    }
}
