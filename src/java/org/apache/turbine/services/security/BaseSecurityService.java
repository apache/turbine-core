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


import java.util.Map;

import javax.servlet.ServletConfig;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.crypto.CryptoAlgorithm;
import org.apache.fulcrum.crypto.CryptoService;
import org.apache.fulcrum.factory.FactoryService;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * This is a common subset of SecurityService implementation.
 *
 * Provided functionality includes:
 * <ul>
 * <li> methods for retrieving User objects, that delegates functionality
 *      to the pluggable implementations of the User interface.
 * <li> synchronization mechanism for methods reading/modifying the security
 *      information, that guarantees that multiple threads may read the
 *      information concurrently, but threads that modify the information
 *      acquires exclusive access.
 * <li> implementation of convenience methods for retrieving security entities
 *      that maintain in-memory caching of objects for fast access.
 * </ul>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @version $Id$
 */
public abstract class BaseSecurityService
        extends TurbineBaseService
        implements SecurityService
{
    /** The number of threads concurrently reading security information */
    private int readerCount = 0;

    /** The instance of UserManager the SecurityService uses */
    private UserManager userManager = null;

    /** The class of User the SecurityService uses */
    private Class userClass = null;

    /** The class of Group the SecurityService uses */
    private Class groupClass = null;

    /** The class of Permission the SecurityService uses */
    private Class permissionClass = null;

    /** The class of Role the SecurityService uses */
    private Class roleClass = null;

    /** The class of ACL the SecurityService uses */
    private Class aclClass = null;

    /** A factory to construct ACL Objects */
    private FactoryService aclFactoryService = null;

    /**
     * The Group object that represents the <a href="#global">global group</a>.
     */
    private static Group globalGroup = null;

    /** Logging */
    private static Log log = LogFactory.getLog(BaseSecurityService.class);

    /**
     * This method provides client-side encryption of passwords.
     *
     * If <code>secure.passwords</code> are enabled in TurbineResources,
     * the password will be encrypted, if not, it will be returned unchanged.
     * The <code>secure.passwords.algorithm</code> property can be used
     * to chose which digest algorithm should be used for performing the
     * encryption. <code>SHA</code> is used by default.
     *
     * @param password the password to process
     * @return processed password
     */
    public String encryptPassword(String password)
    {
        return encryptPassword(password, null);
    }

    /**
     * This method provides client-side encryption of passwords.
     *
     * If <code>secure.passwords</code> are enabled in TurbineResources,
     * the password will be encrypted, if not, it will be returned unchanged.
     * The <code>secure.passwords.algorithm</code> property can be used
     * to chose which digest algorithm should be used for performing the
     * encryption. <code>SHA</code> is used by default.
     *
     * The used algorithms must be prepared to accept null as a
     * valid parameter for salt. All algorithms in the Fulcrum Cryptoservice
     * accept this.
     *
     * @param password the password to process
     * @param salt     algorithms that needs a salt can provide one here
     * @return processed password
     */

    public String encryptPassword(String password, String salt)
    {
        if (password == null)
        {
            return null;
        }
        String secure = getConfiguration().getString(
                SecurityService.SECURE_PASSWORDS_KEY,
                SecurityService.SECURE_PASSWORDS_DEFAULT).toLowerCase();

        String algorithm = getConfiguration().getString(
                SecurityService.SECURE_PASSWORDS_ALGORITHM_KEY,
                SecurityService.SECURE_PASSWORDS_ALGORITHM_DEFAULT);

        CryptoService cs = null;
        try {
            ServiceManager serviceManager = TurbineServices.getInstance();
            cs = (CryptoService)serviceManager.getService(CryptoService.ROLE);
        }
        catch (Exception e){
            throw new RuntimeException("Could not access Crypto Service",e);
        }

        if (cs != null && (secure.equals("true") || secure.equals("yes")))
        {
            try
            {
                CryptoAlgorithm ca = cs.getCryptoAlgorithm(algorithm);

                ca.setSeed(salt);

                String result = ca.encrypt(password);

                return result;
            }
            catch (Exception e)
            {
                log.error("Unable to encrypt password: ", e);

                return null;
            }
        }
        else
        {
            return password;
        }
    }

    /**
     * Checks if a supplied password matches the encrypted password
     *
     * @param checkpw      The clear text password supplied by the user
     * @param encpw        The current, encrypted password
     *
     * @return true if the password matches, else false
     *
     */

    public boolean checkPassword(String checkpw, String encpw)
    {
        String result = encryptPassword(checkpw, encpw);

        return (result == null) ? false : result.equals(encpw);
    }

    /**
     * Initializes the SecurityService, locating the apropriate UserManager
     * This is a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init stage
     */
    public void init()
            throws InitializationException
    {
        Configuration conf = getConfiguration();

        String userManagerClassName = conf.getString(
                SecurityService.USER_MANAGER_KEY,
                SecurityService.USER_MANAGER_DEFAULT);

        String userClassName = conf.getString(
                SecurityService.USER_CLASS_KEY,
                SecurityService.USER_CLASS_DEFAULT);

        String groupClassName = conf.getString(
                SecurityService.GROUP_CLASS_KEY,
                SecurityService.GROUP_CLASS_DEFAULT);

        String permissionClassName = conf.getString(
                SecurityService.PERMISSION_CLASS_KEY,
                SecurityService.PERMISSION_CLASS_DEFAULT);

        String roleClassName = conf.getString(
                SecurityService.ROLE_CLASS_KEY,
                SecurityService.ROLE_CLASS_DEFAULT);

        String aclClassName = conf.getString(
                SecurityService.ACL_CLASS_KEY,
                SecurityService.ACL_CLASS_DEFAULT);

        try
        {
            userClass = Class.forName(userClassName);
            groupClass = Class.forName(groupClassName);
            permissionClass = Class.forName(permissionClassName);
            roleClass = Class.forName(roleClassName);
            aclClass = Class.forName(aclClassName);
        }
        catch (Exception e)
        {
            if (userClass == null)
            {
                throw new InitializationException(
                        "Failed to create a Class object for User implementation", e);
            }
            if (groupClass == null)
            {
                throw new InitializationException(
                        "Failed to create a Class object for Group implementation", e);
            }
            if (permissionClass == null)
            {
                throw new InitializationException(
                        "Failed to create a Class object for Permission implementation", e);
            }
            if (roleClass == null)
            {
                throw new InitializationException(
                        "Failed to create a Class object for Role implementation", e);
            }
            if (aclClass == null)
            {
                throw new InitializationException(
                        "Failed to create a Class object for ACL implementation", e);
            }
        }

        try
        {
            UserManager userManager =
                    (UserManager) Class.forName(userManagerClassName).newInstance();

            userManager.init(conf);

            setUserManager(userManager);
        }
        catch (Exception e)
        {
            throw new InitializationException("Failed to instantiate UserManager", e);
        }

        try
        {
            aclFactoryService = (FactoryService)TurbineServices.getInstance().getService(FactoryService.ROLE);
        }
        catch (Exception e)
        {
            throw new InitializationException(
                    "BaseSecurityService.init: Failed to get the Factory Service object", e);
        }

        setInit(true);
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of User interface.
     *
     * @return systems's chosen implementation of User interface.
     * @throws UnknownEntityException if the implementation of User interface
     *         could not be determined, or does not exist.
     */
    public Class getUserClass()
            throws UnknownEntityException
    {
        if (userClass == null)
        {
            throw new UnknownEntityException(
                    "Failed to create a Class object for User implementation");
        }
        return userClass;
    }

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public User getUserInstance()
            throws UnknownEntityException
    {
        User user;
        try
        {
            user = (User) getUserClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed instantiate an User implementation object", e);
        }
        return user;
    }

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @param userName The name of the user.
     *
     * @return an object implementing User interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public User getUserInstance(String userName)
            throws UnknownEntityException
    {
        User user = getUserInstance();
        user.setName(userName);
        return user;
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of Group interface.
     *
     * @return systems's chosen implementation of Group interface.
     * @throws UnknownEntityException if the implementation of Group interface
     *         could not be determined, or does not exist.
     */
    public Class getGroupClass()
            throws UnknownEntityException
    {
        if (groupClass == null)
        {
            throw new UnknownEntityException(
                    "Failed to create a Class object for Group implementation");
        }
        return groupClass;
    }

    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Group getGroupInstance()
            throws UnknownEntityException
    {
        Group group;
        try
        {
            group = (Group) getGroupClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Group implementation object", e);
        }
        return group;
    }

    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @param groupName The name of the Group
     *
     * @return an object implementing Group interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Group getGroupInstance(String groupName)
            throws UnknownEntityException
    {
        Group group = getGroupInstance();
        group.setName(groupName);
        return group;
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of Permission interface.
     *
     * @return systems's chosen implementation of Permission interface.
     * @throws UnknownEntityException if the implementation of Permission interface
     *         could not be determined, or does not exist.
     */
    public Class getPermissionClass()
            throws UnknownEntityException
    {
        if (permissionClass == null)
        {
            throw new UnknownEntityException(
                    "Failed to create a Class object for Permission implementation");
        }
        return permissionClass;
    }

    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Permission getPermissionInstance()
            throws UnknownEntityException
    {
        Permission permission;
        try
        {
            permission = (Permission) getPermissionClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Permission implementation object", e);
        }
        return permission;
    }

    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @param permName The name of the permission.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Permission getPermissionInstance(String permName)
            throws UnknownEntityException
    {
        Permission perm = getPermissionInstance();
        perm.setName(permName);
        return perm;
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of Role interface.
     *
     * @return systems's chosen implementation of Role interface.
     * @throws UnknownEntityException if the implementation of Role interface
     *         could not be determined, or does not exist.
     */
    public Class getRoleClass()
            throws UnknownEntityException
    {
        if (roleClass == null)
        {
            throw new UnknownEntityException(
                    "Failed to create a Class object for Role implementation");
        }
        return roleClass;
    }

    /**
     * Construct a blank Role object.
     *
     * This method calls getRoleClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Role getRoleInstance()
            throws UnknownEntityException
    {
        Role role;

        try
        {
            role = (Role) getRoleClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Role implementation object", e);
        }
        return role;
    }

    /**
     * Construct a blank Role object.
     *
     * This method calls getRoleClass, and then creates a new object using
     * the default constructor.
     *
     * @param roleName The name of the role.
     *
     * @return an object implementing Role interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public Role getRoleInstance(String roleName)
            throws UnknownEntityException
    {
        Role role = getRoleInstance();
        role.setName(roleName);
        return role;
    }

    /**
     * Return a Class object representing the system's chosen implementation of
     * of ACL interface.
     *
     * @return systems's chosen implementation of ACL interface.
     * @throws UnknownEntityException if the implementation of ACL interface
     *         could not be determined, or does not exist.
     */
    public Class getAclClass()
            throws UnknownEntityException
    {
        if (aclClass == null)
        {
            throw new UnknownEntityException(
                    "Failed to create a Class object for ACL implementation");
        }
        return aclClass;
    }

    /**
     * Construct a new ACL object.
     *
     * This constructs a new ACL object from the configured class and
     * initializes it with the supplied roles and permissions.
     *
     * @param roles The roles that this ACL should contain
     * @param permissions The permissions for this ACL
     *
     * @return an object implementing ACL interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public AccessControlList getAclInstance(Map roles, Map permissions)
            throws UnknownEntityException
    {
        Object[] objects = {roles, permissions};
        String[] signatures = {Map.class.getName(), Map.class.getName()};
        AccessControlList accessControlList;

        try
        {
            accessControlList =
                    (AccessControlList) aclFactoryService.getInstance(aclClass.getName(),
                            objects,
                            signatures);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed to instantiate an ACL implementation object", e);
        }

        return accessControlList;
    }

    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    public UserManager getUserManager()
    {
        return userManager;
    }

    /**
     * Configure a new user Manager.
     *
     * @param userManager An UserManager object
     */
    public void setUserManager(UserManager userManager)
    {
        this.userManager = userManager;
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public boolean accountExists(User user)
            throws DataBackendException
    {
        return getUserManager().accountExists(user);
    }

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public boolean accountExists(String userName)
            throws DataBackendException
    {
        return getUserManager().accountExists(userName);
    }

    /**
     * Authenticates an user, and constructs an User object to represent
     * him/her.
     *
     * @param username The user name.
     * @param password The user password.
     * @return An authenticated Turbine User.
     * @throws PasswordMismatchException if the supplied password was incorrect.
     * @throws UnknownEntityException if the user's account does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    public User getAuthenticatedUser(String username, String password)
            throws DataBackendException, UnknownEntityException,
                   PasswordMismatchException
    {
        return getUserManager().retrieve(username, password);
    }

    /**
     * Constructs an User object to represent a registered user of the
     * application.
     *
     * @param username The user name.
     * @return A Turbine User.
     * @throws UnknownEntityException if the user's account does not exist
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    public User getUser(String username)
            throws DataBackendException, UnknownEntityException
    {
        return getUserManager().retrieve(username);
    }


    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the implementation of User interface
     *         could not be determined, or does not exist.
     */
    public User getAnonymousUser()
            throws UnknownEntityException
    {
        User user = getUserInstance();
        user.setName("");
        return user;
    }

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param user An user object
     *
     * @return True if this is an anonymous user
     *
     */
    public boolean isAnonymousUser(User user)
    {
        // Either just null, the name is null or the name is the empty string
        return (user == null) || StringUtils.isEmpty(user.getName());
    }

    /**
     * Saves User's data in the permanent storage. The user account is required
     * to exist in the storage.
     *
     * @param user the User object to save
     * @throws UnknownEntityException if the user's account does not
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    public void saveUser(User user)
            throws UnknownEntityException, DataBackendException
    {
        getUserManager().store(user);
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
        userManager.saveOnSessionUnbind(user);
    }

    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @param password The password to use for the account.
     *
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void addUser(User user, String password)
            throws DataBackendException, EntityExistsException
    {
        getUserManager().createAccount(user, password);
    }

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeUser(User user)
            throws DataBackendException, UnknownEntityException
    {
        // revoke all roles form the user
        revokeAll(user);

        getUserManager().removeAccount(user);
    }

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @throws PasswordMismatchException if the supplied password was incorrect.
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    public void changePassword(User user, String oldPassword,
            String newPassword)
            throws PasswordMismatchException, UnknownEntityException,
                   DataBackendException
    {
        getUserManager().changePassword(user, oldPassword, newPassword);
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
     * @throws UnknownEntityException if the user's record does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    public void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException
    {
        getUserManager().forcePassword(user, password);
    }

    /**
     * Acquire a shared lock on the security information repository.
     *
     * Methods that read security information need to invoke this
     * method at the beginning of their body.
     */
    protected synchronized void lockShared()
    {
        readerCount++;
    }

    /**
     * Release a shared lock on the security information repository.
     *
     * Methods that read security information need to invoke this
     * method at the end of their body.
     */
    protected synchronized void unlockShared()
    {
        readerCount--;
        this.notify();
    }

    /**
     * Acquire an exclusive lock on the security information repository.
     *
     * Methods that modify security information need to invoke this
     * method at the beginning of their body. Note! Those methods must
     * be <code>synchronized</code> themselves!
     */
    protected void lockExclusive()
    {
        while (readerCount > 0)
        {
            try
            {
                this.wait();
            }
            catch (InterruptedException e)
            {
            }
        }
    }

    /**
     * Release an exclusive lock on the security information repository.
     *
     * This method is provided only for completeness. It does not really
     * do anything. Note! Methods that modify security information
     * must be <code>synchronized</code>!
     */
    protected void unlockExclusive()
    {
        // do nothing
    }

    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return a Group object that represents the global group.
     */
    public Group getGlobalGroup()
    {
        if (globalGroup == null)
        {
            synchronized (BaseSecurityService.class)
            {
                if (globalGroup == null)
                {
                    try
                    {
                        globalGroup = getAllGroups()
                                .getGroupByName(Group.GLOBAL_GROUP_NAME);
                    }
                    catch (DataBackendException e)
                    {
                        log.error("Failed to retrieve global group object: ", e);
                    }
                }
            }
        }
        return globalGroup;
    }

    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public Group getGroupByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        Group group = getAllGroups().getGroupByName(name);
        if (group == null)
        {
            throw new UnknownEntityException(
                    "The specified group does not exist");
        }
        return group;
    }

    /**
     * Retrieve a Group object with specified Id.
     *
     * @param id the id of the Group.
     * @return an object representing the Group with specified name.
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    public Group getGroupById(int id)
            throws DataBackendException, UnknownEntityException
    {
        Group group = getAllGroups().getGroupById(id);
        if (group == null)
        {
            throw new UnknownEntityException(
                    "The specified group does not exist");
        }
        return group;
    }

    /**
     * Retrieve a Role object with specified name.
     *
     * @param name the name of the Role.
     * @return an object representing the Role with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public Role getRoleByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        Role role = getAllRoles().getRoleByName(name);
        if (role == null)
        {
            throw new UnknownEntityException(
                    "The specified role does not exist");
        }
        role.setPermissions(getPermissions(role));
        return role;
    }

    /**
     * Retrieve a Role object with specified Id.
     * @param id the id of the Role.
     * @return an object representing the Role with specified name.
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    public Role getRoleById(int id)
            throws DataBackendException,
                   UnknownEntityException
    {
        Role role = getAllRoles().getRoleById(id);
        if (role == null)
        {
            throw new UnknownEntityException(
                    "The specified role does not exist");
        }
        role.setPermissions(getPermissions(role));
        return role;
    }

    /**
     * Retrieve a Permission object with specified name.
     *
     * @param name the name of the Permission.
     * @return an object representing the Permission with specified name.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public Permission getPermissionByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionByName(name);
        if (permission == null)
        {
            throw new UnknownEntityException(
                    "The specified permission does not exist");
        }
        return permission;
    }

    /**
     * Retrieve a Permission object with specified Id.
     *
     * @param id the id of the Permission.
     * @return an object representing the Permission with specified name.
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    public Permission getPermissionById(int id)
            throws DataBackendException,
                   UnknownEntityException
    {
        Permission permission = getAllPermissions().getPermissionById(id);
        if (permission == null)
        {
            throw new UnknownEntityException(
                    "The specified permission does not exist");
        }
        return permission;
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public abstract GroupSet getAllGroups()
            throws DataBackendException;

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public abstract RoleSet getAllRoles()
            throws DataBackendException;

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    public abstract PermissionSet getAllPermissions()
            throws DataBackendException;
}
