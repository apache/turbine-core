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


import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineSecurityException;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * This is a Facade class for SecurityService.
 *
 * This class provides static methods that call related methods of the
 * implementation of SecurityService used by the System, according to
 * the settings in TurbineResources.
 * <br>
 *
 * <a name="global">
 * <p> Certain Roles that the Users may have in the system may are not related
 * to any specific resource nor entity. They are assigned within a special group
 * named 'global' that can be referenced in the code as
 * {@link org.apache.turbine.om.security.Group#GLOBAL_GROUP_NAME}.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public abstract class TurbineSecurity
{
    /**
     * Retrieves an implementation of SecurityService, base on the settings in
     * TurbineResources.
     *
     * @return an implementation of SecurityService.
     */
    public static SecurityService getService()
    {
        return (SecurityService) TurbineServices.getInstance().
                getService(SecurityService.SERVICE_NAME);
    }

    /*-----------------------------------------------------------------------
      Management of User objects
      -----------------------------------------------------------------------*/

    /**
     * This method provides client-side encryption of passwords.
     *
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties.
     *
     * @param password the password to process
     * @return processed password
     */
    public static String encryptPassword(String password)
    {
        return getService().encryptPassword(password);
    }

    /**
     * This method provides client-side encryption of passwords.
     *
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties.
     *
     * @param password the password to process
     * @param salt the supplied salt to encrypt the password
     * @return processed password
     */
    public static String encryptPassword(String password, String salt)
    {
        return getService().encryptPassword(password, salt);
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

    public static boolean checkPassword(String checkpw, String encpw)
    {
        return getService().checkPassword(checkpw, encpw);
    }

    /*-----------------------------------------------------------------------
      Getting Object Classes
      -----------------------------------------------------------------------*/

    /**
     * Returns the Class object for the implementation of User interface
     * used by the system.
     *
     * @return the implementation of User interface used by the system.
     * @throws UnknownEntityException if the system's implementation of User
     *         interface could not be determined.
     */
    public static Class getUserClass()
            throws UnknownEntityException
    {
        return getService().getUserClass();
    }

    /**
     * Returns the Class object for the implementation of Group interface
     * used by the system.
     *
     * @return the implementation of Group interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Group
     *         interface could not be determined.
     */
    public static Class getGroupClass()
        throws UnknownEntityException
    {
        return getService().getGroupClass();
    }

    /**
     * Returns the Class object for the implementation of Permission interface
     * used by the system.
     *
     * @return the implementation of Permission interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Permission
     *         interface could not be determined.
     */
    public static Class getPermissionClass()
        throws UnknownEntityException
    {
        return getService().getPermissionClass();
    }

    /**
     * Returns the Class object for the implementation of Role interface
     * used by the system.
     *
     * @return the implementation of Role interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Role
     *         interface could not be determined.
     */
    public static Class getRoleClass()
        throws UnknownEntityException
    {
        return getService().getRoleClass();
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
    public static User getUserInstance()
            throws UnknownEntityException
    {
        return getService().getUserInstance();
    }

    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    public static UserManager getUserManager()
    {
        return getService().getUserManager();
    }

    /**
     * Configure a new user Manager.
     *
     * @param userManager An UserManager object
     */
    public void setUserManager(UserManager userManager)
    {
        getService().setUserManager(userManager);
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
    public static boolean accountExists(User user)
            throws DataBackendException
    {
        return getService().accountExists(user);
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
    public static boolean accountExists(String userName)
            throws DataBackendException
    {
        return getService().accountExists(userName);
    }

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
    public static User getAuthenticatedUser(String username, String password)
            throws DataBackendException, UnknownEntityException,
            PasswordMismatchException
    {
        return getService().getAuthenticatedUser(username, password);
    }

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
    public static User getUser(String username)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getUser(username);
    }

    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the anonymous User object couldn't be
     *         constructed.
     */
    public static User getAnonymousUser()
            throws UnknownEntityException
    {
        return getService().getAnonymousUser();
    }

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured service
     *
     * @param user A user object
     * @return True if this is an anonymous user
     */
    public static boolean isAnonymousUser(User user)
    {
        return getService().isAnonymousUser(user);
    }

    /**
     * Saves User's data in the permanent storage. The user account is required
     * to exist in the storage.
     *
     * @param user The User object to save.
     * @throws UnknownEntityException if the user's account does not
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public static void saveUser(User user)
            throws UnknownEntityException, DataBackendException
    {
        getService().saveUser(user);
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
    public static void saveOnSessionUnbind(User user)
            throws UnknownEntityException, DataBackendException
    {
        getService().saveOnSessionUnbind(user);
    }

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @throws PasswordMismatchException if the supplied password was
     *         incorrect.
     * @throws UnknownEntityException if the user's record does not
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public static void changePassword(User user, String oldPassword,
                                      String newPassword)
            throws PasswordMismatchException, UnknownEntityException,
            DataBackendException
    {
        getService().changePassword(user, oldPassword, newPassword);
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
     *         exist in the database.
     * @throws DataBackendException if there is a problem accessing the
     *         storage.
     */
    public static void forcePassword(User user, String password)
            throws UnknownEntityException, DataBackendException
    {
        getService().forcePassword(user, password);
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
    public static AccessControlList getACL(User user)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getACL(user);
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
    public static void grant(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException
    {
        getService().grant(user, group, role);
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
    public static void revoke(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException
    {
        getService().revoke(user, group, role);
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
    public static void revokeAll(User user)
            throws DataBackendException, UnknownEntityException
    {
        getService().revokeAll(user);
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
    public static void grant(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        getService().grant(role, permission);
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
    public static void revoke(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        getService().revoke(role, permission);
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
    public static void revokeAll(Role role)
            throws DataBackendException, UnknownEntityException
    {
        getService().revokeAll(role);
    }

    /*-----------------------------------------------------------------------
      Account management
      -----------------------------------------------------------------------*/

    /**
     * Creates new user account with specified attributes.
     *
     * <strong>TODO</strong> throw more specific exception<br>
     *
     * @param user the object describing account to be created.
     * @param password password for the new user
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public static void addUser(User user, String password)
            throws DataBackendException, EntityExistsException
    {
        getService().addUser(user, password);
    }

    /**
     * Removes an user account from the system.
     *
     * <strong>TODO</strong> throw more specific exception<br>
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public static void removeUser(User user)
            throws DataBackendException, UnknownEntityException
    {
        getService().removeUser(user);
    }

    /*-----------------------------------------------------------------------
      Group/Role/Permission management
      -----------------------------------------------------------------------*/
    /**
     * Provides a reference to the Group object that represents the
     * <a name="global">global group</a>.
     *
     * @return a Group object that represents the global group.
     */
    public static Group getGlobalGroup()
    {
        return getService().getGlobalGroup();
    }

    /**
     * Creates a new Group in the system. This is a convenience
     * method.
     *
     * @param name The name of the new Group.
     * @return An object representing the new Group.
     * @throws TurbineSecurityException if the Group could not be created.
     */
    public static Group createGroup(String name)
            throws TurbineSecurityException
    {
        return getService().addGroup(getGroupInstance(name));
    }

    /**
     * Creates a new Permission in the system. This is a convenience
     * method.
     *
     * @param name The name of the new Permission.
     * @return An object representing the new Permission.
     * @throws TurbineSecurityException if the Permission could not be created.
     */
    public static Permission createPermission(String name)
            throws TurbineSecurityException
    {
        return getService().addPermission(getPermissionInstance(name));
    }

    /**
     * Creates a new Role in the system. This is a convenience
     * method.
     *
     * @param name The name of the Role.
     *
     * @return An object representing the new Role.
     *
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public static Role createRole(String name)
        throws TurbineSecurityException
    {
        return getService().addRole(getRoleInstance(name));
    }

    /**
     * Retrieve a Group object with specified name.
     *
     * @param groupName The name of the Group to be retrieved.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the Group is not present.
     */
    public static Group getGroupByName(String groupName)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getGroupByName(groupName);
    }

    /**
     * Retrieve a Group object with specified Id.
     *
     * @param name the name of the Group.
     *
     * @return an object representing the Group with specified name.
     *
     * @exception UnknownEntityException if the permission does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static Group getGroupById(int groupId)
            throws DataBackendException,
                   UnknownEntityException
    {
        return getService().getGroupById(groupId);
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
    public static Group getGroupInstance(String groupName)
            throws UnknownEntityException
    {
        return getService().getGroupInstance(groupName);
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
    public static Role getRoleInstance(String roleName)
            throws UnknownEntityException
    {
        return getService().getRoleInstance(roleName);
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
    public static Permission getPermissionInstance(String permName)
            throws UnknownEntityException
    {
        return getService().getPermissionInstance(permName);
    }

    /**
     * Retrieve a Role object with specified name.
     *
     * @param roleName The name of the Role to be retrieved.
     * @return an object representing the Role with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the Role is not present.
     */
    public static Role getRoleByName(String roleName)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getRoleByName(roleName);
    }

    /**
     * Retrieve a Role object with specified Id.
     *
     * @param name the name of the Role.
     *
     * @return an object representing the Role with specified name.
     *
     * @exception UnknownEntityException if the permission does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static Role getRoleById(int roleId)
            throws DataBackendException,
                   UnknownEntityException
    {
        return getService().getRoleById(roleId);
    }

    /**
     * Retrieve a Permission object with specified name.
     *
     * @param permissionName The name of the Permission to be retrieved.
     * @return an object representing the Permission with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the Permission is not present.
     */
    public static Permission getPermissionByName(String permissionName)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getPermissionByName(permissionName);
    }

    /**
     * Retrieve a Permission object with specified Id.
     *
     * @param name the name of the Permission.
     *
     * @return an object representing the Permission with specified name.
     *
     * @exception UnknownEntityException if the permission does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public static Permission getPermissionById(int permissionId)
            throws DataBackendException,
                   UnknownEntityException
    {
        return getService().getPermissionById(permissionId);
    }

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param criteria A Criteria of Group selection.
     * @return a set of Groups that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static GroupSet getGroups(Object criteria)
            throws DataBackendException
    {
        return getService().getGroups(criteria);
    }

    /**
     * Retrieve a set of Roles that meet the specified Criteria.
     *
     * @param criteria a Criteria of Roles selection.
     * @return a set of Roles that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static RoleSet getRoles(Object criteria)
            throws DataBackendException
    {
        return getService().getRoles(criteria);
    }

    /**
     * Retrieve a set of Permissions that meet the specified Criteria.
     *
     * @param criteria a Criteria of Permissions selection.
     * @return a set of Permissions that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static PermissionSet getPermissions(Object criteria)
            throws DataBackendException
    {
        return getService().getPermissions(criteria);
    }

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static GroupSet getAllGroups()
            throws DataBackendException
    {
        return getService().getAllGroups();
    }

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static RoleSet getAllRoles()
            throws DataBackendException
    {
        return getService().getAllRoles();
    }

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public static PermissionSet getAllPermissions()
            throws DataBackendException
    {
        return getService().getAllPermissions();
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
    public static PermissionSet getPermissions(Role role)
            throws DataBackendException, UnknownEntityException
    {
        return getService().getPermissions(role);
    }

    /**
     * Stores Group's attributes. The Groups is required to exist in the system.
     *
     * @param group The Group to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public static void saveGroup(Group group)
            throws DataBackendException, UnknownEntityException
    {
        getService().saveGroup(group);
    }

    /**
     * Stores Role's attributes. The Roles is required to exist in the system.
     *
     * @param role The Role to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public static void saveRole(Role role)
            throws DataBackendException, UnknownEntityException
    {
        getService().saveRole(role);
    }

    /**
     * Stores Permission's attributes. The Permissions is required to exist in
     * the system.
     *
     * @param permission The Permission to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public static void savePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        getService().savePermission(permission);
    }

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the group already exists.
     */
    public static void addGroup(Group group)
            throws DataBackendException, EntityExistsException
    {
        getService().addGroup(group);
    }

    /**
     * Creates a new role with specified attributes.
     *
     * @param role the objects describing the role to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the role already exists.
     */
    public static void addRole(Role role)
            throws DataBackendException, EntityExistsException
    {
        getService().addRole(role);
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission the objects describing the permission to be created.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the permission already exists.
     */
    public static void addPermission(Permission permission)
            throws DataBackendException, EntityExistsException
    {
        getService().addPermission(permission);
    }

    /**
     * Removes a Group from the system.
     *
     * @param group the object describing group to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public static void removeGroup(Group group)
            throws DataBackendException, UnknownEntityException
    {
        getService().removeGroup(group);
    }

    /**
     * Removes a Role from the system.
     *
     * @param role The object describing the role to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public static void removeRole(Role role)
            throws DataBackendException, UnknownEntityException
    {
        getService().removeRole(role);
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public static void removePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        getService().removePermission(permission);
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
    public static void renameGroup(Group group, String name)
            throws DataBackendException, UnknownEntityException
    {
        getService().renameGroup(group, name);
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
    public static void renameRole(Role role, String name)
            throws DataBackendException, UnknownEntityException
    {
        getService().renameRole(role, name);
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
    public static void renamePermission(Permission permission, String name)
            throws DataBackendException, UnknownEntityException
    {
        getService().renamePermission(permission, name);
    }
}
