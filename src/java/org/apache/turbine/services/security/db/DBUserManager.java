package org.apache.turbine.services.security.db;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.torque.om.BaseObject;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.peer.TurbineUserPeer;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.security.UserManager;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * An UserManager performs {@link org.apache.turbine.om.security.User} objects
 * related tasks on behalf of the
 * {@link org.apache.turbine.services.security.BaseSecurityService}.
 *
 * This implementation uses a relational database for storing user data. It
 * expects that the User interface implementation will be castable to
 * {@link org.apache.turbine.om.BaseObject}.
 *
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @version $Id$
 */
public class DBUserManager implements UserManager
{
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
    public boolean accountExists( User user )
        throws DataBackendException
    {
        return accountExists(user.getUserName());
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param usename The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing
     *         the data backend.
     */
    public boolean accountExists( String username )
        throws DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USERNAME, username);
        List users;
        try
        {
            users = TurbineUserPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new DataBackendException(
                "Failed to check account's presence", e);
        }
        if ( users.size() > 1 )
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + username + "'");
        }
        return(users.size() == 1);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key.
     *
     * @param username the name of the user.
     * @return an User object.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User retrieve( String username )
        throws UnknownEntityException, DataBackendException
    {
        Criteria criteria = new Criteria();
        criteria.add( TurbineUserPeer.USERNAME, username );
        List users;
        try
        {
            users = TurbineUserPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new DataBackendException("Failed to retrieve user '" +
                username + "'", e);
        }
        if ( users.size() > 1 )
        {
            throw new DataBackendException(
                "Multiple Users with same username '" + username + "'");
        }
        if ( users.size() == 1 )
        {
            return (User)users.get(0);
        }
        throw new UnknownEntityException("Unknown user '" + username + "'");
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
     */
    public User[] retrieve( Criteria criteria )
        throws DataBackendException
    {
        Iterator keys = criteria.keySet().iterator();
        while(keys.hasNext())
        {
            String key = (String)keys.next();
            // set the table name for all attached criterion
            Criteria.Criterion[] criterion = criteria
                .getCriterion(key).getAttachedCriterion();
            for (int i=0;i<criterion.length;i++)
            {
                String table = criterion[i].getTable();
                if ( table == null || "".equals(table) )
                {
                    criterion[i].setTable(TurbineUserPeer.getTableName());
                }
            }
        }
        List users = new ArrayList(0);
        try
        {
            users = TurbineUserPeer.doSelect(criteria);
        }
        catch(Exception e)
        {
            throw new DataBackendException("Failed to retrieve users", e);
        }
        return (User[])users.toArray(new User[0]);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key, and authenticate the user. The implementation may chose
     * to authenticate to the server as the user whose data is being
     * retrieved.
     *
     * @param username the name of the user.
     * @param password the user supplied password.
     * @return an User object.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public User retrieve( String username, String password )
         throws PasswordMismatchException, UnknownEntityException,
                DataBackendException
    {
        User user = retrieve(username);
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
        if(!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                user.getUserName() + "' does not exist");
        }
        Criteria criteria = TurbineUserPeer.buildCriteria(user);
        try
        {
            TurbineUserPeer.doUpdate(criteria);
        }
        catch(Exception e)
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
    public void authenticate( User user, String password )
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        if(!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                user.getUserName() + "' does not exist");
        }
        String encrypted = TurbineSecurity.encryptPassword(password);
        if(!user.getPassword().equals(encrypted))
        {
            throw new PasswordMismatchException("The passwords do not match");
        }
    }

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void changePassword( User user, String oldPassword,
                                String newPassword )
        throws PasswordMismatchException, UnknownEntityException,
               DataBackendException
    {
        String encrypted = TurbineSecurity.encryptPassword(oldPassword);
        if(!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                user.getUserName() + "' does not exist");
        }
        if(!user.getPassword().equals(encrypted))
        {
            throw new PasswordMismatchException(
                "The supplied old password for '" + user.getUserName() +
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
    public void forcePassword( User user, String password )
        throws UnknownEntityException, DataBackendException
    {
        if(!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                user.getUserName() + "' does not exist");
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
     * @param user the object describing account to be created.
     * @throws DataBackendException if there was an error accessing
               the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void createAccount( User user, String initialPassword )
        throws EntityExistsException, DataBackendException
    {
        if(accountExists(user))
        {
            throw new EntityExistsException("The account '" +
                user.getUserName() + "' already exists");
        }
        String encrypted = TurbineSecurity.encryptPassword(initialPassword);
        user.setPassword(encrypted);
        Criteria criteria = TurbineUserPeer.buildCriteria(user);
        try
        {
            // we can safely assume that BaseObject derivate is used as User
            // implementation with this UserManager
            ((BaseObject)user).setPrimaryKey(
                TurbineUserPeer.doInsert(criteria));
        }
        catch(Exception e)
        {
            throw new DataBackendException("Failed to create account '" +
                user.getUserName() + "'", e);
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
    public void removeAccount( User user )
        throws UnknownEntityException, DataBackendException
    {
        if(!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                user.getUserName() + "' does not exist");
        }
        Criteria criteria = new Criteria();
        criteria.add(TurbineUserPeer.USERNAME, user.getUserName());
        try
        {
            TurbineUserPeer.doDelete(criteria);
        }
        catch(Exception e)
        {
            throw new DataBackendException("Failed to remove account '" +
                user.getUserName() + "'", e);
        }
    }
}
