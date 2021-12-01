package org.apache.turbine.services.security;


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


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.Configuration;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.model.turbine.TurbineUserManager;
import org.apache.fulcrum.security.model.turbine.entity.TurbineUser;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.fulcrum.security.util.UserSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.om.security.TurbineUserDelegate;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.ObjectUtils;

/**
 * Default user manager.
 *
 * The user manager wraps Fulcrum security user objects into
 * Turbine-specific ones.
 *
 *
 * <ol>
 * <li>either in a method with the same name (and very similar signature)</li>
 * <li>or mapped to method names as listed below:
 *
 * <ul>
 * <li>method(s) in this manager -&gt; Fulcrum manager method(s)
 * <li>{@link #createAccount(User, String)}createAccount -&gt; addUser(User, String)
 * <li>{@link #removeAccount(User)} -&gt; removeUser(User)
 * <li>{@link #store(User)} -&gt; saveUser(User)
 * <li>{@link #retrieve(String)} and {@link #retrieve(String, String)} -&gt; getUser(String), getUser(String, String)
 * <li>{@link #retrieveList(Object)} -&gt; getAllUsers()
 * <li>{@link #accountExists(String)}, {@link #accountExists(User)} -&gt; checkExists(String), checkExists(User)
 * </ul>
 *
 * </li>
 * </ol>
 *
 * In this way all public methods of Fulcrum {@link TurbineUserManager} interface are used by reference of the Fulcrum delegate {@link #umDelegate}
 * and wrapped by this manager.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @version $Id: PassiveUserManager.java 1096130 2011-04-23 10:37:19Z ludwig $
 */
public class DefaultUserManager implements UserManager
{
    /** Fulcrum user manager instance to delegate to */
    private TurbineUserManager umDelegate = null;

    private FactoryService factoryService = null;

    /** The user class, which the UserManager uses as wrapper for Fulcrum {@link TurbineUser} */
    private String userWrapperClass;


    /** Logging */
    private static final Logger log = LogManager.getLogger(DefaultUserManager.class);

    /**
     * Wrap a Fulcrum user object into a Turbine user object
     *
     * @param <U> user class
     * @param user the user object to delegate to
     *
     * @return the wrapped object
     */
    protected <U extends User> U wrap(TurbineUser user)
    {
        @SuppressWarnings("unchecked")
        U u = (U) getUserWrapper(user);
        return u;
    }

    /**
     * Exception could be ignored, as it is tested before in {@link #init(Configuration)}.
     *
     * @param <U> user class
     * @param user the user object to wrap
     * @return instance extending {@link User}
     */
    @SuppressWarnings("unchecked")
	public <U extends User> U getUserWrapper(TurbineUser user)
    {
		try
		{
            Object params[] = new Object[] { user };
            String signature[] = new String[] { TurbineUser.class.getName() };
            return (U) factoryService.getInstance(getUserWrapperClass(), params, signature);
		}
		catch (Exception e)
		{
			log.error("after init/late instantiation exception", e);
			return null; // (U)new DefaultUserImpl(user);
		}
	}

    /**
     * Get the wrapper class for user objects
     *
     * @return the wrapper class name
     */
    public String getUserWrapperClass()
    {
		return userWrapperClass;
	}

    /**
     * Set the wrapper class for user objects
     *
     * @param userWrapperClass2 the wrapper class name
     */
    public void setUserWrapperClass(String userWrapperClass2)
    {
		userWrapperClass = userWrapperClass2;
	}

	/**
     * Initializes the UserManager
     *
     * @param conf A Configuration object to init this Manager
     */
    @Override
    public void init(Configuration conf) throws InitializationException
    {
        ServiceManager manager = TurbineServices.getInstance();
        this.umDelegate = (TurbineUserManager)manager.getService(TurbineUserManager.ROLE);

        String userWrapperClass = conf.getString(
                SecurityService.USER_WRAPPER_KEY,
                SecurityService.USER_WRAPPER_DEFAULT);

        try
        {
        	factoryService = (FactoryService)manager.getService(FactoryService.ROLE);

            //  check instantiation
        	// should provide default constructor
        	TurbineUser turbineUser = umDelegate.getUserInstance();
        			//(TurbineUser) factoryService.getInstance(userClass);
            Object params[] = new Object[] { turbineUser };
            String signature[] = new String[] { TurbineUser.class.getName() };

            // Just check if exceptions would occur
            factoryService.getInstance(userWrapperClass, params, signature);

            this.setUserWrapperClass(userWrapperClass);
        }
        catch (Exception e)
	    {
	       throw new InitializationException("Failed to instantiate user wrapper class", e);
	    }
    }


	/**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    @Override
    public boolean accountExists(User user)
            throws DataBackendException
    {
        if (user == null) {
            return false;
        }
        return umDelegate.checkExists(user.getUserDelegate());
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    @Override
    public boolean accountExists(String userName)
            throws DataBackendException
    {
        return umDelegate.checkExists(userName);
    }

    /**
     * Retrieve a user from persistent storage using username as the
     * key.
     *
     * @param username the name of the user.
     * @return an User object.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public <U extends User> U retrieve(String username)
            throws UnknownEntityException, DataBackendException
    {
        TurbineUser u = umDelegate.getUser(username);
        return wrap(u);
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
    @Override
    public List<? extends User> retrieveList(Object criteria)
            throws DataBackendException
    {
        UserSet<org.apache.fulcrum.security.entity.User> uset = umDelegate.getAllUsers();

        List<User> userList = uset.stream()
                .map(u -> (TurbineUser) u)
                .map(this::wrap)
                .map(u -> (User)u)
                .collect(Collectors.toList());

        return userList;
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
     * @throws PasswordMismatchException if the supplied password was
     *            incorrect.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public <U extends User> U retrieve(String username, String password)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        TurbineUser u = umDelegate.getUser(username, password);
        return wrap(u);
    }

    /**
     * Save an User object to persistent storage. User's record is
     * required to exist in the storage.
     *
     * @param user an User object to store.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public void store(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        try
        {
            user.setObjectdata(ObjectUtils.serializeMap(user.getPermStorage()));
        }
        catch (Exception e)
        {
            throw new DataBackendException("Could not serialize permanent storage", e);
        }

        umDelegate.saveUser(((TurbineUserDelegate)user).getUserDelegate());
    }

    /**
     * Saves User data when the session is unbound. The user account is required
     * to exist in the storage.
     *
     * LastLogin, AccessCounter, persistent pull tools, and any data stored
     * in the permData hashtable that is not mapped to a column will be saved.
     *
     * @throws UnknownEntityException if the user's account does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public void saveOnSessionUnbind(User user)
            throws UnknownEntityException, DataBackendException
    {
        store(user);
    }

    /**
     * Authenticate an User with the specified password. If authentication
     * is successful the method returns nothing. If there are any problems,
     * exception was thrown.
     *
     * @param user an User object to authenticate.
     * @param password the user supplied password.
     * @throws PasswordMismatchException if the supplied password was
     *            incorrect.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public void authenticate(User user, String password)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        umDelegate.authenticate(user, password);
    }

    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @param initialPassword The password to use for the object creation
     *
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    @Override
    public void createAccount(User user, String initialPassword)
            throws UnknownEntityException, EntityExistsException, DataBackendException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        umDelegate.addUser(user.getUserDelegate(), initialPassword);
    }

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    @Override
    public void removeAccount(User user)
            throws UnknownEntityException, DataBackendException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        umDelegate.removeUser(user.getUserDelegate());
    }

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @throws PasswordMismatchException if the supplied password was
     *            incorrect.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public void changePassword(User user, String oldPassword,
                               String newPassword)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        umDelegate.changePassword(
                ((TurbineUserDelegate)user).getUserDelegate(),
                oldPassword, newPassword);
    }

    /**
     * Forcibly sets new password for an User.
     *
     * This is supposed by the administrator to change the forgotten or
     * compromised passwords. Certain implementations of this feature
     * would require administrative level access to the authenticating
     * server / program.
     *
     * @param user an User to change password for.
     * @param password the new password.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    @Override
    public void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        umDelegate.forcePassword(user.getUserDelegate(), password);
    }

    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException
     *             if the anonymous User object couldn't be constructed.
     */
    @Override
    public <U extends User> U getAnonymousUser() throws UnknownEntityException
    {
        TurbineUser u = umDelegate.getAnonymousUser();
        return wrap(u);
    }

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param u a user object
     *
     * @return True if this is an anonymous user
     *
     */
    @Override
    public boolean isAnonymousUser(User u)
    {
        return umDelegate.isAnonymousUser(u);
    }

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using the
     * default constructor.
     *
     * @return an object implementing User interface.
     * @throws DataBackendException
     *             if the object could not be instantiated.
     */
    @Override
    public <U extends User> U getUserInstance() throws DataBackendException
    {
        TurbineUser u = umDelegate.getUserInstance();
        return wrap(u);
    }

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using the
     * default constructor.
     *
     * @param userName
     *            The name of the user.
     *
     * @return an object implementing User interface.
     * @throws DataBackendException
     *             if the object could not be instantiated.
     */
    @Override
    public <U extends User> U getUserInstance(String userName) throws DataBackendException
    {
        TurbineUser u = umDelegate.getUserInstance(userName);
        return wrap(u);
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of ACL interface.
     *
     * @return systems's chosen implementation of ACL interface.
     * @throws UnknownEntityException
     *             if the implementation of ACL interface could not be
     *             determined, or does not exist.
     */
    @Override
    public <A extends AccessControlList> A getACL(User user) throws UnknownEntityException
    {
        if (user == null) {
            throw new UnknownEntityException("user is null");
        }
        return umDelegate.getACL(user.getUserDelegate());
    }
}
