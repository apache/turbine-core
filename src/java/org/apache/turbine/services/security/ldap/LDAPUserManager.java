package org.apache.turbine.services.security.ldap;

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

import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.security.UserManager;
import org.apache.turbine.services.security.ldap.util.ParseExceptionMessage;
import org.apache.turbine.util.Log;
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
 * @version $Id$
 */
public class LDAPUserManager implements UserManager, LDAPSecurityConstants
{
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
        return accountExists(user.getUserName());
    }

    /**
      *
      * Check wether a specified user's account exists.
      * The login name is used for looking up the account.
      *
      * @param usename The name of the user to be checked.
      * @return true if the specified account exists
      * @throws DataBackendException Error accessing the data backend.
      */
    public boolean accountExists(String username)
            throws DataBackendException
    {
        /*!
         * Is it possible to retrieve more then one user?
         * Possibly the check for multiple users that is
         * employed in the DBUserManager can be employed
         * here.
         */

        try
        {
            User ldapUser = retrieve(username);
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                "Failed to check account's presence", e);
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
        String dNAttribute = TurbineResources.getString(LDAP_DN_ATTR);
        String filter = TurbineResources.getString(LDAP_SEARCH_FLTR);
        String userBaseSearch = TurbineResources.getString(LDAP_BASE_SERACH);
        String dN = null;
        String adminUser = TurbineResources.getString(LDAP_ADMIN_USRNAME);
        String adminPassword = TurbineResources.getString(LDAP_ADMIN_PASSWRD);

        adminUser = adminUser.replace('/', '=');
        adminUser = adminUser.replace('%', ',');

        User ldapUser = null;

        /*
         * The userBaseSearch string contains some
         * characters that need to be transformed.
         */
        userBaseSearch = userBaseSearch.replace('/', '=');
        userBaseSearch = userBaseSearch.replace('%', ',');

        try
        {
            DirContext ctx = bind(adminUser, adminPassword);

            /*
             * Create the default search controls.
             */
            SearchControls ctls = new SearchControls();

            /*
             * Create filter.
             */
            filter = "("+filter + "="+username + ")";

            NamingEnumeration answer =
                    ctx.search(userBaseSearch, filter, ctls);

            while (answer.hasMore())
            {
                SearchResult sr = (SearchResult) answer.next();
                Attributes attribs = sr.getAttributes();
                Log.debug("attribs:  " + attribs.get(dNAttribute));
                dN = attribs.get(dNAttribute).toString();
                Log.debug("dN:  " + dN);
            }

            if (dN == null)
            {
                throw new UnknownEntityException("The given user: " +
                        username + "\n does not exist.");
            }

            StringTokenizer sT = new StringTokenizer(dN, ":");

            while (sT.hasMoreElements())
            {
                dN = sT.nextToken();
            }

            dN = dN.trim();

            ldapUser = new LDAPUser();
            ldapUser.setUserName(dN);

            ldapUser.setTemp("turbine.user", ldapUser);

        }
        catch (NamingException nameEx)
        {
            throw new DataBackendException(
                "The LDAP server specified is unavailable");
        }
        return ldapUser;
    }

    /**
      * This is currently not implemented to behave as expected.  It is
      * just here to support the interface requirement.
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
      */
    public User[] retrieve(Criteria criteria) throws DataBackendException
    {
        Vector users = new Vector(0);
        return (User[]) users.toArray(new User[0]);
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
            UnknownEntityException,DataBackendException
    {

        User user = retrieve(username);
        authenticate(user, password);
        return user;
    }

    /**
      * This method is not yet implemented
      *
      * Save a User object to persistent storage. User's account is
      * required to exist in the storage.
      *
      * @param user an User object to store.
      * @exception UnknownEntityException if the user's account does not
      *            exist in the database.
      *
      */
    public void store(User user)
        throws UnknownEntityException,DataBackendException
    {
        if (!accountExists(user))
        {
            throw new UnknownEntityException("The account '"+
                    user.getUserName() + "' does not exist");
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
            UnknownEntityException, DataBackendException
    {
        try
        {
            bind(user.getUserName(), password);
        }
        catch (NamingException authEx)
        {
            throw new PasswordMismatchException(
                "The given password for: " +
                    user.getUserName() + " is invalid\n");
        }
    }

    /**
      * This method is not yet implemented
      * Change the password for an User.
      *
      * @param user an User to change password for.
      * @param password the new password.
      * @exception PasswordMismatchException if the supplied password was
      *            incorrect.
      * @exception UnknownEntityException if the user's account does not
      *            exist in the database.
      * @exception DataBackendException Error accessing the data backend.
      */
    public void changePassword(User user, String oldPassword, String newPassword)
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
      * This is supposed to be used by the administrator to change the forgotten or
      * compromised passwords. Certain implementatations of this feature
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
        throws UnknownEntityException,DataBackendException
    {
        throw new DataBackendException(
            "The method forcePassword has no implementation.");
    }

    /**
      * This method is not yet implemented
      * Creates new user account with specified attributes.
      *
      * @param user the object describing account to be created.
      * @throws DataBackendException Error accessing the data backend.
      * @throws EntityExistsException if the user account already exists.
      */
    public void createAccount(User user, String initialPassword)
        throws EntityExistsException,DataBackendException
    {
        throw new DataBackendException(
            "The method createAccount has no implementation.");
    }

    /**
      * This method is not yet implemented
      * Removes an user account from the system.
      *
      * @param user the object describing the account to be removed.
      * @throws DataBackendException Error accessing the data backend.
      * @throws UnknownEntityException if the user account is not present.
      */
    public void removeAccount(User user)
        throws UnknownEntityException,DataBackendException
    {
        throw new DataBackendException(
            "The method removeAccount has no implementation.");
    }


    /**
     * Creats an initial context.
     *
     * @param ldap admin username supplied in TRP.
     * @param ldap admin password supplied in TRP
     * @throws DataBackendException Error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     * @throws NamingException when an error occurs with the named server.
     */
    public DirContext bind(String username, String password)
        throws NamingException, DataBackendException, UnknownEntityException
    {
        DirContext ctx = null;

        try
        {
            String host = TurbineResources.getString(LDAP_HOST);
            String port = TurbineResources.getString(LDAP_PORT);
            String ldapProvider = TurbineResources.getString(LDAP_PROVIDER);
            String ldapAuthentication = TurbineResources.getString(LDAP_AUTHENTICATION);

            String providerURL = new String("ldap://" + host + ":" + port);

            /*
             * creating an initial context using Sun's client
             * LDAP Provider.
             */
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, ldapProvider);

            env.put(Context.PROVIDER_URL, providerURL);
            env.put(Context.SECURITY_AUTHENTICATION, ldapAuthentication);
            env.put(Context.SECURITY_PRINCIPAL, username);
            env.put(Context.SECURITY_CREDENTIALS, password);

            ctx = new javax.naming.directory.InitialDirContext(env);
            Log.debug("CTX: " + ctx.toString());
        }
        catch (NamingException ne)
        {
            String errno = ParseExceptionMessage.findErrno(ne.getExplanation());

            if (errno.equals("49"))
            {
                throw new UnknownEntityException(
                    "The given credentials for the administrator are invalid");
            }
            else if (errno.equals("22"))
            {
                throw new DataBackendException(
                    "The LDAP server specified is unavailable");
            }
            else
            {
                throw ne;
            }
        }
        catch (Exception e)
        {
            Log.error(e);
        }

        return ctx;
    }
}
