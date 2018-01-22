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


import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.entity.Group;
import org.apache.fulcrum.security.entity.Permission;
import org.apache.fulcrum.security.entity.Role;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.EntityExistsException;
import org.apache.fulcrum.security.util.GroupSet;
import org.apache.fulcrum.security.util.PasswordMismatchException;
import org.apache.fulcrum.security.util.PermissionSet;
import org.apache.fulcrum.security.util.RoleSet;
import org.apache.fulcrum.security.util.UnknownEntityException;
import org.apache.turbine.om.security.DefaultUserImpl;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.Service;
import org.apache.turbine.services.security.passive.PassiveUserManager;

/**
 * The Security Service manages Users, Groups Roles and Permissions in the
 * system.
 *
 * The task performed by the security service include creation and removal of
 * accounts, groups, roles, and permissions; assigning users roles in groups;
 * assigning roles specific permissions and construction of objects
 * representing these logical entities.
 *
 * <p> Because of pluggable nature of the Services, it is possible to create
 * multiple implementations of SecurityService, for example employing database
 * and directory server as the data backend.<br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public interface SecurityService
        extends Service
{
    /** The name of the service */
    String SERVICE_NAME = "SecurityService";

    /**
     * the key within services's properties for user manager implementation
     * classname (user.manager)
     */
    String USER_MANAGER_KEY = "user.manager";

    /**
     * the default implementation of UserManager interface
     * (org.apache.turbine.services.security.passive.PassiveUserManager)
     */
    String USER_MANAGER_DEFAULT
            = PassiveUserManager.class.getName();

    /**
     * the key within services's properties for user implementation
     * classname (wrapper.class)
     */
    String USER_WRAPPER_KEY = "wrapper.class";

    /**
     * the default implementation of {@link User} interface
     * (org.apache.turbine.om.security.DefaultUserImpl)
     */
    String USER_WRAPPER_DEFAULT
            = DefaultUserImpl.class.getName();


    /*-----------------------------------------------------------------------
      Management of User objects
      -----------------------------------------------------------------------*/

    /**
     * Construct a blank User object.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <U extends User> U getUserInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank User object.
     *
     * @param userName The name of the user.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <U extends User> U getUserInstance(String userName)
            throws UnknownEntityException;

    /**
     * Construct a blank Group object.
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <G extends Group> G getGroupInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Group object.
     *
     * @param groupName The name of the Group
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <G extends Group> G getGroupInstance(String groupName)
            throws UnknownEntityException;

    /**
     * Construct a blank Permission object.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <P extends Permission> P getPermissionInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Permission object.
     *
     * @param permName The name of the Permission
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <P extends Permission> P getPermissionInstance(String permName)
            throws UnknownEntityException;

    /**
     * Construct a blank Role object.
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <R extends Role> R getRoleInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Role object.
     *
     * @param roleName The name of the Role
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    <R extends Role> R getRoleInstance(String roleName)
            throws UnknownEntityException;

    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    UserManager getUserManager();

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param userName The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    boolean accountExists(String userName)
            throws DataBackendException;

    /**
     * Check whether a specified user's account exists.
     * An User object is used for looking up the account.
     *
     * @param user The user object to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    boolean accountExists(User user)
            throws DataBackendException;

    /**
     * Authenticates an user, and constructs an User object to represent
     * him/her.
     *
     * @param username The user name.
     * @param password The user password.
     * @return An authenticated Turbine User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account is not present.
     * @throws PasswordMismatchException if the supplied password was incorrect.
     */
    <U extends User> U getAuthenticatedUser(String username, String password)
            throws DataBackendException, UnknownEntityException,
            PasswordMismatchException;

    /**
     * Constructs an User object to represent a registered user of the
     * application.
     *
     * @param username The user name.
     * @return A Turbine User.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account is not present.
     */
    <U extends User> U getUser(String username)
            throws DataBackendException, UnknownEntityException;

    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the anonymous User object couldn't be
     *         constructed.
     */
    <U extends User> U getAnonymousUser()
            throws UnknownEntityException;

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param u a user object
     *
     * @return True if this is an anonymous user
     *
     */
    boolean isAnonymousUser(User u);

    /**
     * Saves User's data in the permanent storage. The user account is required
     * to exist in the storage.
     *
     * @param user the user object to save
     * @throws UnknownEntityException if the user's account does not
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the storage.
     */
    void saveUser(User user)
            throws UnknownEntityException, DataBackendException;

    /**
     * Saves User data when the session is unbound. The user account is required
     * to exist in the storage.
     *
     * LastLogin, AccessCounter, persistent pull tools, and any data stored
     * in the permData hashtable that is not mapped to a column will be saved.
     *
     * @param user the user object
     *
     * @throws UnknownEntityException if the user's account does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    void saveOnSessionUnbind(User user)
            throws UnknownEntityException, DataBackendException;

    /*-----------------------------------------------------------------------
      Account management
      -----------------------------------------------------------------------*/

    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @param password The password to use.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the user account already exists.
     * @throws UnknownEntityException  if the provided user does not exist (is null)
     */
    void addUser(User user, String password)
            throws DataBackendException, EntityExistsException, UnknownEntityException;

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    void removeUser(User user)
            throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Management of passwords
      -----------------------------------------------------------------------*/

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
    void changePassword(User user, String oldPassword,
                        String newPassword)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException;

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
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException;

    /*-----------------------------------------------------------------------
      Retrieval of security information
      -----------------------------------------------------------------------*/

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     * @return A new AccessControlList object.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    <A extends AccessControlList> A getACL(User user)
            throws DataBackendException, UnknownEntityException;

    /**
     * Retrieves all permissions associated with a role.
     *
     * @param role the role name, for which the permissions are to be retrieved.
     * @return the permissions associated with the role
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role is not present.
     */
    PermissionSet getPermissions(Role role)
            throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Manipulation of security information
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
    void grant(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException;

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
    void revoke(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException;

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
    void revokeAll(User user)
            throws DataBackendException, UnknownEntityException;

    /**
     * Grants a Role a Permission
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    void grant(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException;

    /**
     * Revokes a Permission from a Role.
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    void revoke(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException;

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
    void revokeAll(Role role)
            throws DataBackendException, UnknownEntityException;
    
    /**
     * Revokes by default all permissions from a Role and if flag is set
     * all groups and users for this role
     * 
     * This method is used when deleting a Role.
     * 
     * @param role
     *            the Role
     * @param cascadeDelete
     *             if <code>true </code> removes all groups and user for this role.
     * @throws DataBackendException
     *             if there was an error accessing the data backend.
     * @throws UnknownEntityException
     *             if the Role is not present.
     */
    void revokeAll( Role role, boolean cascadeDelete )
                    throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Retrieval & storage of SecurityObjects
      -----------------------------------------------------------------------*/

    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return A Group object that represents the global group.
     */
    <G extends Group> G getGlobalGroup();

    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    <G extends Group> G getGroupByName(String name)
            throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a Group object with specified Id.
     *
     * @param id the id of the Group.
     *
     * @return an object representing the Group with specified name.
     *
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    <G extends Group> G getGroupById(int id)
            throws DataBackendException,
                   UnknownEntityException;

    /**
     * Retrieve a Role object with specified name.
     *
     * @param name the name of the Role.
     * @return an object representing the Role with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    <R extends Role> R getRoleByName(String name)
            throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a Role object with specified Id.
     *
     * @param id the id of the Role.
     *
     * @return an object representing the Role with specified name.
     *
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    <R extends Role> R getRoleById(int id)
            throws DataBackendException,
                   UnknownEntityException;

    /**
     * Retrieve a Permission object with specified name.
     *
     * @param name the name of the Permission.
     * @return an object representing the Permission with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    <P extends Permission> P getPermissionByName(String name)
            throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a Permission object with specified Id.
     *
     * @param id the id of the Permission.
     *
     * @return an object representing the Permission with specified name.
     *
     * @throws UnknownEntityException if the permission does not
     *            exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *            storage.
     */
    <P extends Permission> P getPermissionById(int id)
            throws DataBackendException,
                   UnknownEntityException;

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    GroupSet getAllGroups()
            throws DataBackendException;

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    RoleSet getAllRoles()
            throws DataBackendException;

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    PermissionSet getAllPermissions()
            throws DataBackendException;

    /*-----------------------------------------------------------------------
      Group/Role/Permission management
      -----------------------------------------------------------------------*/

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @return the new Group object.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the group already exists.
     */
    <G extends Group> G addGroup(G group)
            throws DataBackendException, EntityExistsException;

    /**
     * Creates a new role with specified attributes.
     *
     * @param role The object describing the role to be created.
     * @return the new Role object.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the role already exists.
     */
    <R extends Role> R addRole(R role)
            throws DataBackendException, EntityExistsException;

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission The object describing the permission to be created.
     * @return the new Permission object.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the permission already exists.
     */
    <P extends Permission> P addPermission(P permission)
            throws DataBackendException, EntityExistsException;

    /**
     * Removes a Group from the system.
     *
     * @param group The object describing the group to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    void removeGroup(Group group)
            throws DataBackendException, UnknownEntityException;

    /**
     * Removes a Role from the system.
     *
     * @param role The object describing the role to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    void removeRole(Role role)
            throws DataBackendException, UnknownEntityException;

    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    void removePermission(Permission permission)
            throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Group.
     *
     * @param group The object describing the group to be renamed.
     * @param name the new name for the group.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    void renameGroup(Group group, String name)
            throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Role.
     *
     * @param role The object describing the role to be renamed.
     * @param name the new name for the role.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    void renameRole(Role role, String name)
            throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Permission.
     *
     * @param permission The object describing the permission to be renamed.
     * @param name the new name for the permission.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    void renamePermission(Permission permission, String name)
            throws DataBackendException, UnknownEntityException;
    /**
     * Replaces transactionally the first given role with the second role for the given user. 
     * 
     * @param user the user.
     * @param role the old role
     * @param newRole the new role
     * 
     * @throws DataBackendException
     * @throws UnknownEntityException
     */
    void replaceRole( User user, Role role, Role newRole )
        throws DataBackendException, UnknownEntityException;

}
