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


import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.GroupManager;
import org.apache.fulcrum.security.PermissionManager;
import org.apache.fulcrum.security.RoleManager;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.model.turbine.TurbineModelManager;
import org.apache.fulcrum.security.model.turbine.entity.TurbineRole;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.InitializationException;
import org.apache.turbine.services.ServiceManager;
import org.apache.turbine.services.TurbineBaseService;
import org.apache.turbine.services.TurbineServices;

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
public class DefaultSecurityService
        extends TurbineBaseService
        implements SecurityService
{
    /** The number of threads concurrently reading security information */
    private int readerCount = 0;

    /** The instance of UserManager the SecurityService uses */
    private UserManager userManager = null;

    /** The instance of GroupManager the SecurityService uses */
    private GroupManager groupManager;

    /** The instance of RoleManager the SecurityService uses */
    private RoleManager roleManager;

    /** The instance of PermissionManager the SecurityService uses */
    private PermissionManager permissionManager;

    /** The instance of ModelManager the SecurityService uses */
    private TurbineModelManager modelManager;

    /**
     * The Group object that represents the <a href="#global">global group</a>.
     */
    private static volatile Group globalGroup = null;

    /** Logging */
    private static Log log = LogFactory.getLog(DefaultSecurityService.class);

    /**
     * Initializes the SecurityService, locating the appropriate UserManager
     * This is a zero parameter variant which queries the Turbine Servlet
     * for its config.
     *
     * @throws InitializationException Something went wrong in the init stage
     */
    @Override
    public void init()
            throws InitializationException
    {
        ServiceManager manager = TurbineServices.getInstance();

        this.groupManager = (GroupManager)manager.getService(GroupManager.ROLE);
        this.roleManager = (RoleManager)manager.getService(RoleManager.ROLE);
        this.permissionManager = (PermissionManager)manager.getService(PermissionManager.ROLE);
        this.modelManager = (TurbineModelManager)manager.getService(TurbineModelManager.ROLE);

        Configuration conf = getConfiguration();

        String userManagerClassName = conf.getString(
                SecurityService.USER_MANAGER_KEY,
                SecurityService.USER_MANAGER_DEFAULT);

        try
        {
            this.userManager =
                    (UserManager) Class.forName(userManagerClassName).newInstance();

            userManager.init(conf);
        }
        catch (Exception e)
        {
            throw new InitializationException("Failed to instantiate UserManager", e);
        }

        setInit(true);
    }

    /**
     * Construct a blank User object.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <U extends User> U getUserInstance()
            throws UnknownEntityException
    {
        U user;
        try
        {
            user = getUserManager().getUserInstance();
        }
        catch (DataBackendException e)
        {
            throw new UnknownEntityException(
                    "Failed instantiate an User implementation object", e);
        }
        return user;
    }

    /**
     * Construct a blank User object.
     *
     * @param userName The name of the user.
     *
     * @return an object implementing User interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <U extends User> U getUserInstance(String userName)
            throws UnknownEntityException
    {
        U user;
        try
        {
            user = getUserManager().getUserInstance(userName);
        }
        catch (DataBackendException e)
        {
            throw new UnknownEntityException(
                    "Failed instantiate an User implementation object", e);
        }
        return user;
    }

    /**
     * Construct a blank Group object.
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <G extends Group> G getGroupInstance()
            throws UnknownEntityException
    {
        G group;
        try
        {
            group = groupManager.getGroupInstance();
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
     * @param groupName The name of the Group
     *
     * @return an object implementing Group interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <G extends Group> G getGroupInstance(String groupName)
            throws UnknownEntityException
    {
        G group;
        try
        {
            group = groupManager.getGroupInstance(groupName);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Group implementation object", e);
        }
        return group;
    }

    /**
     * Construct a blank Permission object.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <P extends Permission> P getPermissionInstance()
            throws UnknownEntityException
    {
        P permission;
        try
        {
            permission = permissionManager.getPermissionInstance();
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
     * @param permName The name of the permission.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <P extends Permission> P getPermissionInstance(String permName)
            throws UnknownEntityException
    {
        P permission;
        try
        {
            permission = permissionManager.getPermissionInstance(permName);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Permission implementation object", e);
        }
        return permission;
    }

    /**
     * Construct a blank Role object.
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <R extends Role> R getRoleInstance()
            throws UnknownEntityException
    {
        R role;
        try
        {
            role = roleManager.getRoleInstance();
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
     * @param roleName The name of the role.
     *
     * @return an object implementing Role interface.
     *
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    @Override
    public <R extends Role> R getRoleInstance(String roleName)
            throws UnknownEntityException
    {
        R role;
        try
        {
            role = roleManager.getRoleInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException("Failed to instantiate a Role implementation object", e);
        }
        return role;
    }

    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    @Override
    public UserManager getUserManager()
    {
        return userManager;
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
    @Override
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
    @Override
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
    @Override
    public <U extends User> U getAuthenticatedUser(String username, String password)
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
    @Override
    public <U extends User> U getUser(String username)
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
    @Override
    public <U extends User> U getAnonymousUser()
            throws UnknownEntityException
    {
        return getUserManager().getAnonymousUser();
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
    @Override
    public boolean isAnonymousUser(User user)
    {
        return getUserManager().isAnonymousUser(user);
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
    @Override
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
     * in the permData hashmap that is not mapped to a column will be saved.
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
        getUserManager().saveOnSessionUnbind(user);
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
    @Override
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
    @Override
    public void removeUser(User user)
            throws DataBackendException, UnknownEntityException
    {
        // revoke all roles form the user
        modelManager.revokeAll(user);
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
    @Override
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
    @Override
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
                // ignore
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
    @Override
    public <G extends Group> G getGlobalGroup()
    {
        if (globalGroup == null)
        {
            synchronized (DefaultSecurityService.class)
            {
                if (globalGroup == null)
                {
                    try
                    {
                        globalGroup = modelManager.getGlobalGroup();
                    }
                    catch (DataBackendException e)
                    {
                        log.error("Failed to retrieve global group object: ", e);
                    }
                }
            }
        }
        @SuppressWarnings("unchecked")
        G g = (G)globalGroup;
        return g;
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
    @Override
    public <G extends Group> G getGroupByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        return groupManager.getGroupByName(name);
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
    @Override
    public <G extends Group> G getGroupById(int id)
            throws DataBackendException, UnknownEntityException
    {
        return groupManager.getGroupById(Integer.valueOf(id));
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
    @Override
    public <R extends Role> R getRoleByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        R role = roleManager.getRoleByName(name);
        if (role instanceof TurbineRole)
        {
            ((TurbineRole)role).setPermissions(getPermissions(role));
        }
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
    @Override
    public <R extends Role> R getRoleById(int id)
            throws DataBackendException,
                   UnknownEntityException
    {
        R role = roleManager.getRoleById(Integer.valueOf(id));
        if (role instanceof TurbineRole)
        {
            ((TurbineRole)role).setPermissions(getPermissions(role));
        }
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
    @Override
    public <P extends Permission> P getPermissionByName(String name)
            throws DataBackendException, UnknownEntityException
    {
        return permissionManager.getPermissionByName(name);
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
    @Override
    public <P extends Permission> P getPermissionById(int id)
            throws DataBackendException,
                   UnknownEntityException
    {
        return permissionManager.getPermissionById(Integer.valueOf(id));
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    @Override
    public GroupSet getAllGroups() throws DataBackendException
    {
        return groupManager.getAllGroups();
    }

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    @Override
    public RoleSet getAllRoles() throws DataBackendException
    {
        return roleManager.getAllRoles();
    }

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the
     *         data backend.
     */
    @Override
    public PermissionSet getAllPermissions() throws DataBackendException
    {
        return permissionManager.getAllPermissions();
    }

    /*-----------------------------------------------------------------------
    Creation of AccessControlLists
    -----------------------------------------------------------------------*/

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     * @return The AccessControList object constructed from the user object.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account is not present.
     */
    @Override
    public <A extends AccessControlList> A getACL(User user)
        throws DataBackendException, UnknownEntityException
    {
        return getUserManager().getACL(user);
    }

    /*-----------------------------------------------------------------------
    Security management
    -----------------------------------------------------------------------*/

    /**
     * Grant an User a Role in a Group.
     *
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account, group or role is not
     *         present.
     */
    @Override
    public void grant(User user, Group group, Role role)
    throws DataBackendException, UnknownEntityException
    {
        modelManager.grant(user, group, role);
    }

    /**
     * Revoke a Role in a Group from an User.
     *
     * @param user the user.
     * @param group the group.
     * @param role the role.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account, group or role is not
     *         present.
     */
    @Override
    public void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
        modelManager.revoke(user, group, role);
    }

    /**
     * Revokes all roles from an User.
     *
     * This method is used when deleting an account.
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the account is not present.
     */
    @Override
    public void revokeAll(User user)
        throws DataBackendException, UnknownEntityException
    {
        modelManager.revokeAll(user);
    }

    /**
     * Grants a Role a Permission
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    @Override
    public void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        modelManager.grant(role, permission);
    }

    /**
     * Revokes a Permission from a Role.
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    @Override
    public void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
        modelManager.revoke(role, permission);
    }

    /**
     * Revokes all permissions from a Role.
     *
     * This method is user when deleting a Role.
     *
     * @param role the Role
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws  UnknownEntityException if the Role is not present.
     */
    @Override
    public void revokeAll(Role role)
        throws DataBackendException, UnknownEntityException
    {
        modelManager.revokeAll(role);
    }

    /**
     * Retrieves all permissions associated with a role.
     *
     * @param role the role name, for which the permissions are to be retrieved.
     * @return the Permissions for the specified role
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role is not present.
     */
    @Override
    public PermissionSet getPermissions(Role role)
            throws DataBackendException, UnknownEntityException
    {
        return ((TurbineRole)role).getPermissions();
    }

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the group already exists.
     */
    @Override
    public <G extends Group> G addGroup(G group)
            throws DataBackendException, EntityExistsException
    {
        return groupManager.addGroup(group);
    }

    /**
     * Creates a new role with specified attributes.
     *
     * @param role the objects describing the role to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the role already exists.
     */
    @Override
    public <R extends Role> R addRole(R role)
            throws DataBackendException, EntityExistsException
    {
        return roleManager.addRole(role);
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission the objects describing the permission to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the permission already exists.
     */
    @Override
    public <P extends Permission> P addPermission(P permission)
            throws DataBackendException, EntityExistsException
    {
        return permissionManager.addPermission(permission);
    }

    /**
     * Removes a Group from the system.
     *
     * @param group the object describing group to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    @Override
    public void removeGroup(Group group)
            throws DataBackendException, UnknownEntityException
    {
        groupManager.removeGroup(group);
    }

    /**
     * Removes a Role from the system.
     *
     * @param role The object describing the role to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    @Override
    public void removeRole(Role role)
            throws DataBackendException, UnknownEntityException
    {
        roleManager.removeRole(role);
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    @Override
    public void removePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        permissionManager.removePermission(permission);
    }

    /**
     * Renames an existing Group.
     *
     * @param group The object describing the group to be renamed.
     * @param name the new name for the group.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    @Override
    public void renameGroup(Group group, String name)
            throws DataBackendException, UnknownEntityException
    {
        groupManager.renameGroup(group, name);
    }

    /**
     * Renames an existing Role.
     *
     * @param role The object describing the role to be renamed.
     * @param name the new name for the role.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    @Override
    public void renameRole(Role role, String name)
            throws DataBackendException, UnknownEntityException
    {
        roleManager.renameRole(role, name);
    }

    /**
     * Renames an existing Permission.
     *
     * @param permission The object describing the permission to be renamed.
     * @param name the new name for the permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    @Override
    public void renamePermission(Permission permission, String name)
            throws DataBackendException, UnknownEntityException
    {
        permissionManager.renamePermission(permission, name);
    }
}
