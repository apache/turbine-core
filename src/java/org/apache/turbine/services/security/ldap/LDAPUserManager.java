package org.apache.turbine.services.security.ldap;

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

import java.util.List;
import java.util.Hashtable;
import java.util.Vector;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.services.security.UserManager;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * A UserManager performs {@link org.apache.turbine.om.security.User}
 * object related tasks on behalf of the
 * {@link org.apache.turbine.services.security.SecurityService}.
 *
 * This implementation uses ldap for retrieving user data. It
 * expects that the User interface implementation will be castable to
 * {@link org.apache.turbine.om.BaseObject}.
 *
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:tadewunmi@gluecode.com">Tracy M. Adewunmi</a>
 * @author <a href="mailto:lflournoy@gluecode.com">Leonard J. Flournoy</a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:hhernandez@itweb.com.mx">Humberto Hernandez</a>
 * @version $Id$
 */
public class LDAPUserManager implements UserManager
{
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
     * Check wether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException Error accessing the data backend.
     */
    public boolean accountExists(User user) throws DataBackendException
    {
        return accountExists(user.getName());
    }

    /**
     *
     * Check wether a specified user's account exists.
     * The login name is used for looking up the account.
     *
     * @param username The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException Error accessing the data backend.
     */
    public boolean accountExists(String username)
            throws DataBackendException
    {
        try
        {
            User ldapUser = retrieve(username);
        }
        catch (UnknownEntityException ex)
        {
            return false;
        }

        return true;
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key.
     *
     * @param username the name of the user.
     * @return an User object.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException Error accessing the data backend.
     */
    public User retrieve(String username)
            throws UnknownEntityException, DataBackendException
    {
        try
        {
            DirContext ctx = bindAsAdmin();

            /*
             * Define the search.
             */
            String userBaseSearch = LDAPSecurityConstants.getBaseSearch();
            String filter = LDAPSecurityConstants.getNameAttribute();

            filter = "(" + filter + "=" + username + ")";

            /*
             * Create the default search controls.
             */
            SearchControls ctls = new SearchControls();

            NamingEnumeration answer =
                    ctx.search(userBaseSearch, filter, ctls);

            if (answer.hasMore())
            {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attribs = sr.getAttributes();
                LDAPUser ldapUser = createLDAPUser();

                ldapUser.setLDAPAttributes(attribs);
                ldapUser.setTemp("turbine.user", ldapUser);

                return ldapUser;
            }
            else
            {
                throw new UnknownEntityException("The given user: "
                        + username + "\n does not exist.");
            }
        }
        catch (NamingException ex)
        {
            throw new DataBackendException(
                    "The LDAP server specified is unavailable", ex);
        }
    }

    /**
     * This is currently not implemented to behave as expected.  It
     * ignores the Criteria argument and returns all the users.
     *
     * Retrieve a set of users that meet the specified criteria.
     *
     * As the keys for the criteria, you should use the constants that
     * are defined in {@link User} interface, plus the the names
     * of the custom attributes you added to your user representation
     * in the data storage. Use verbatim names of the attributes -
     * without table name prefix in case of DB implementation.
     *
     * @param criteria The criteria of selection.
     * @return a List of users meeting the criteria.
     * @throws DataBackendException Error accessing the data backend.
     * @deprecated Use <a href="#retrieveList">retrieveList</a> instead.
     */
    public User[] retrieve(Object criteria)
            throws DataBackendException
    {
        return (User []) retrieveList(criteria).toArray(new User[0]);
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
    public List retrieveList(Object criteria)
            throws DataBackendException
    {
        List users = new Vector(0);

        try
        {
            DirContext ctx = bindAsAdmin();

            String userBaseSearch = LDAPSecurityConstants.getBaseSearch();
            String filter = LDAPSecurityConstants.getNameAttribute();

            filter = "(" + filter + "=*)";

            /*
             * Create the default search controls.
             */
            SearchControls ctls = new SearchControls();

            NamingEnumeration answer =
                    ctx.search(userBaseSearch, filter, ctls);

            while (answer.hasMore())
            {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attribs = sr.getAttributes();
                LDAPUser ldapUser = createLDAPUser();

                ldapUser.setLDAPAttributes(attribs);
                ldapUser.setTemp("turbine.user", ldapUser);
                users.add(ldapUser);
            }
        }
        catch (NamingException ex)
        {
            throw new DataBackendException(
                    "The LDAP server specified is unavailable", ex);
        }
        return users;
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
     * @exception DataBackendException Error accessing the data backend.
     */
    public User retrieve(String username, String password)
            throws PasswordMismatchException,
            UnknownEntityException, DataBackendException
    {
        User user = retrieve(username);

        authenticate(user, password);
        return user;
    }

    /**
     * Save a User object to persistent storage. User's account is
     * required to exist in the storage.
     *
     * @param user an User object to store.
     * @throws UnknownEntityException if the user's account does not
     *            exist in the database.
     * @throws DataBackendException if there is an LDAP error
     *
     */
    public void store(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '"
                    + user.getName() + "' does not exist");
        }

        try
        {
            LDAPUser ldapUser = (LDAPUser) user;
            Attributes attrs = ldapUser.getLDAPAttributes();
            String name = ldapUser.getDN();

            DirContext ctx = bindAsAdmin();

            ctx.modifyAttributes(name, DirContext.REPLACE_ATTRIBUTE, attrs);
        }
        catch (NamingException ex)
        {
            throw new DataBackendException("NamingException caught", ex);
        }
    }

    /**
     * This method is not yet implemented.
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
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '" +
                    user.getName() + "' does not exist");
        }
    }

    /**
     * Authenticate a User with the specified password. If authentication
     * is successful the method returns nothing. If there are any problems,
     * exception was thrown.
     *
     * @param user a User object to authenticate.
     * @param password the user supplied password.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException Error accessing the data backend.
     */
    public void authenticate(User user, String password)
            throws PasswordMismatchException,
            UnknownEntityException,
            DataBackendException
    {
        LDAPUser ldapUser = (LDAPUser) user;

        try
        {
            bind(ldapUser.getDN(), password);
        }
        catch (AuthenticationException ex)
        {
            throw new PasswordMismatchException(
                    "The given password for: "
                    + ldapUser.getDN() + " is invalid\n");
        }
        catch (NamingException ex)
        {
            throw new DataBackendException(
                    "NamingException caught:", ex);
        }
    }

    /**
     * This method is not yet implemented
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param newPass the new password.
     * @param oldPass the old password.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException Error accessing the data backend.
     */
    public void changePassword(User user, String oldPass, String newPass)
            throws PasswordMismatchException,
            UnknownEntityException, DataBackendException
    {
        throw new DataBackendException(
                "The method changePassword has no implementation.");
    }

    /**
     * This method is not yet implemented
     * Forcibly sets new password for an User.
     *
     * This is supposed to be used by the administrator to change the forgotten
     * or compromised passwords. Certain implementatations of this feature
     * would require adminstrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException Error accessing the data backend.
     */
    public void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException
    {
        throw new DataBackendException(
                "The method forcePassword has no implementation.");
    }

    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @param initialPassword Not used yet.
     * @throws DataBackendException Error accessing the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void createAccount(User user, String initialPassword)
            throws EntityExistsException, DataBackendException
    {
        if (accountExists(user))
        {
            throw new EntityExistsException("The account '"
                    + user.getName() + "' already exist");
        }

        try
        {
            LDAPUser ldapUser = (LDAPUser) user;
            Attributes attrs = ldapUser.getLDAPAttributes();
            String name = ldapUser.getDN();

            DirContext ctx = bindAsAdmin();

            ctx.bind(name, null, attrs);
        }
        catch (NamingException ex)
        {
            throw new DataBackendException("NamingException caught", ex);
        }
    }

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException Error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeAccount(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '"
                    + user.getName() + "' does not exist");
        }

        try
        {
            LDAPUser ldapUser = (LDAPUser) user;
            String name = ldapUser.getDN();

            DirContext ctx = bindAsAdmin();

            ctx.unbind(name);
        }
        catch (NamingException ex)
        {
            throw new DataBackendException("NamingException caught", ex);
        }
    }

    /**
     * Bind as the admin user.
     *
     * @throws NamingException when an error occurs with the named server.
     * @return a new DirContext.
     */
    public static DirContext bindAsAdmin()
            throws NamingException
    {
        String adminUser = LDAPSecurityConstants.getAdminUsername();
        String adminPassword = LDAPSecurityConstants.getAdminPassword();

        return bind(adminUser, adminPassword);
    }

    /**
     * Creates an initial context.
     *
     * @param username admin username supplied in TRP.
     * @param password admin password supplied in TRP
     * @throws NamingException when an error occurs with the named server.
     * @return a new DirContext.
     */
    public static DirContext bind(String username, String password)
            throws NamingException
    {
        String host = LDAPSecurityConstants.getLDAPHost();
        String port = LDAPSecurityConstants.getLDAPPort();
        String providerURL = "ldap://" + host + ":" + port;
        String ldapProvider = LDAPSecurityConstants.getLDAPProvider();
        String authentication = LDAPSecurityConstants.getLDAPAuthentication();

        /*
         * creating an initial context using Sun's client
         * LDAP Provider.
         */
        Hashtable env = new Hashtable();

        env.put(Context.INITIAL_CONTEXT_FACTORY, ldapProvider);
        env.put(Context.PROVIDER_URL, providerURL);
        env.put(Context.SECURITY_AUTHENTICATION, authentication);
        env.put(Context.SECURITY_PRINCIPAL, username);
        env.put(Context.SECURITY_CREDENTIALS, password);

        DirContext ctx = new javax.naming.directory.InitialDirContext(env);

        return ctx;
    }

    /**
     * Create a new instance of the LDAP User according to the value
     * configured in TurbineResources.properties.
     * @return a new instance of the LDAP User.
     * @throws DataBackendException if there is an error creating the
     */
    private LDAPUser createLDAPUser()
            throws DataBackendException
    {
        try
        {
            return (LDAPUser) TurbineSecurity.getUserInstance();
        }
        catch (ClassCastException ex)
        {
            throw new DataBackendException("ClassCastException:", ex);
        }
        catch (UnknownEntityException ex)
        {
            throw new DataBackendException("UnknownEntityException:", ex);
        }
    }

}
