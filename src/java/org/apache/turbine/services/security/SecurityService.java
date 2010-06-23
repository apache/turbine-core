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


import java.util.List;
import java.util.Map;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.TurbineGroup;
import org.apache.turbine.om.security.TurbinePermission;
import org.apache.turbine.om.security.TurbineRole;
import org.apache.turbine.om.security.TurbineUser;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.Service;
import org.apache.turbine.services.security.passive.PassiveUserManager;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineAccessControlList;
import org.apache.turbine.util.security.UnknownEntityException;

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
     * the key within services's properties for user implementation
     * classname (user.class)
     */
    String USER_CLASS_KEY = "user.class";

    /**
     * the default implementation of User interface
     * (org.apache.turbine.om.security.TurbineUser)
     */
    String USER_CLASS_DEFAULT
            = TurbineUser.class.getName();

    /**
     * The key within services' properties for the GROUP
     * implementation classname (group.class)
     */
    String GROUP_CLASS_KEY = "group.class";

    /**
     * The default implementation of the Group interface
     * (org.apache.turbine.om.security.TurbineGroup)
     */
    String GROUP_CLASS_DEFAULT
            = TurbineGroup.class.getName();

    /**
     * The key within services' properties for the PERMISSION
     * implementation classname (permission.class)
     */
    String PERMISSION_CLASS_KEY = "permission.class";

    /**
     * The default implementation of the Permissions interface
     * (org.apache.turbine.om.security.TurbinePermission)
     */
    String PERMISSION_CLASS_DEFAULT
            = TurbinePermission.class.getName();

    /**
     * The key within services' properties for the ROLE
     * implementation classname (role.class)
     */
    String ROLE_CLASS_KEY = "role.class";

    /**
     * The default implementation of the Role Interface
     * (org.apache.turbine.om.security.TurbineRole)
     */
    String ROLE_CLASS_DEFAULT
            = TurbineRole.class.getName();

    /**
     * The key within services' properties for the
     * ACL implementation classname (acl.class)
     */
    String ACL_CLASS_KEY = "acl.class";

    /**
     * The default implementation of the Acl Interface
     * (org.apache.turbine.util.security.TurbineAccessControlList)
     */
    String ACL_CLASS_DEFAULT
            = TurbineAccessControlList.class.getName();

    /**
     * the key within services's properties for user implementation
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
     * the key within services's properties for secure passwords flag
     * (secure.passwords)
     */
    String SECURE_PASSWORDS_KEY = "secure.passwords";

    /** the value of secure passwords flag (false) */
    String SECURE_PASSWORDS_DEFAULT = "false";

    /**
     * the key within services's properties for secure passwords algorithm
     * (secure.passwords.algorithm)
     */
    String SECURE_PASSWORDS_ALGORITHM_KEY
            = "secure.passwords.algorithm";

    /** the default algorithm for password encryption (SHA) */
    String SECURE_PASSWORDS_ALGORITHM_DEFAULT = "SHA";

    /*-----------------------------------------------------------------------
      Management of User objects
      -----------------------------------------------------------------------*/

    /**
     * Returns the Class object for the implementation of User interface
     * used by the system.
     *
     * @return the implementation of User interface used by the system.
     * @throws UnknownEntityException if the system's implementation of User
     *         interface could not be determined.
     */
    Class getUserClass()
            throws UnknownEntityException;

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    User getUserInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank User object.
     *
     * This method calls getUserClass, and then creates a new object using
     * the default constructor.
     *
     * @param userName The name of the user.
     *
     * @return an object implementing User interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    User getUserInstance(String userName)
            throws UnknownEntityException;

    /**
     * Returns the Class object for the implementation of Group interface
     * used by the system.
     *
     * @return the implementation of Group interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Group
     *         interface could not be determined.
     */
    Class getGroupClass()
            throws UnknownEntityException;

    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Group getGroupInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Group object.
     *
     * This method calls getGroupClass, and then creates a new object using
     * the default constructor.
     *
     * @param groupName The name of the Group
     *
     * @return an object implementing Group interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Group getGroupInstance(String groupName)
            throws UnknownEntityException;

    /**
     * Returns the Class object for the implementation of Permission interface
     * used by the system.
     *
     * @return the implementation of Permission interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Permission
     *         interface could not be determined.
     */
    Class getPermissionClass()
            throws UnknownEntityException;

    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Permission getPermissionInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Permission object.
     *
     * This method calls getPermissionClass, and then creates a new object using
     * the default constructor.
     *
     * @param permName The name of the Permission
     *
     * @return an object implementing Permission interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Permission getPermissionInstance(String permName)
            throws UnknownEntityException;

    /**
     * Returns the Class object for the implementation of Role interface
     * used by the system.
     *
     * @return the implementation of Role interface used by the system.
     * @throws UnknownEntityException if the system's implementation of Role
     *         interface could not be determined.
     */
    Class getRoleClass()
            throws UnknownEntityException;

    /**
     * Construct a blank Role object.
     *
     * This method calls getRoleClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Role getRoleInstance()
            throws UnknownEntityException;

    /**
     * Construct a blank Role object.
     *
     * This method calls getRoleClass, and then creates a new object using
     * the default constructor.
     *
     * @param roleName The name of the Role
     *
     * @return an object implementing Role interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    Role getRoleInstance(String roleName)
            throws UnknownEntityException;

    /**
     * Returns the Class object for the implementation of AccessControlList interface
     * used by the system.
     *
     * @return the implementation of AccessControlList interface used by the system.
     * @throws UnknownEntityException if the system's implementation of AccessControlList
     *         interface could not be determined.
     */
    Class getAclClass()
            throws UnknownEntityException;

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
    AccessControlList getAclInstance(Map roles, Map permissions)
            throws UnknownEntityException;

    /**
     * Returns the configured UserManager.
     *
     * @return An UserManager object
     */
    UserManager getUserManager();

    /**
     * Configure a new user Manager.
     *
     * @param userManager An UserManager object
     */
    void setUserManager(UserManager userManager);

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
    User getAuthenticatedUser(String username, String password)
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
    User getUser(String username)
            throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a set of users that meet the specified criteria.
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
    List getUserList(Object criteria)
            throws DataBackendException;

    /**
     * Constructs an User object to represent an anonymous user of the
     * application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the anonymous User object couldn't be
     *         constructed.
     */
    User getAnonymousUser()
            throws UnknownEntityException;

    /**
     * Checks whether a passed user object matches the anonymous user pattern
     * according to the configured user manager
     *
     * @param An user object
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
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
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
     */
    void addUser(User user, String password)
            throws DataBackendException, EntityExistsException;

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
     * This method provides client-side encryption mechanism for passwords.
     *
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties.
     *
     * @param password the password to process
     * @return processed password
     */
    String encryptPassword(String password);

    /**
     * This method provides client-side encryption mechanism for passwords.
     *
     * This is an utility method that is used by other classes to maintain
     * a consistent approach to encrypting password. The behavior of the
     * method can be configured in service's properties.
     *
     * Algorithms that must supply a salt for encryption
     * can use this method to provide it.
     *
     * @param password the password to process
     * @param salt Salt parameter for some crypto algorithms
     *
     * @return processed password
     */
    String encryptPassword(String password, String salt);

    /**
     * Checks if a supplied password matches the encrypted password
     * when using the current encryption algorithm
     *
     * @param checkpw      The clear text password supplied by the user
     * @param encpw        The current, encrypted password
     *
     * @return true if the password matches, else false
     *
     */
    boolean checkPassword(String checkpw, String encpw);

    /**
     * Change the password for an User.
     *
     * @param user an User to change password for.
     * @param oldPassword the current password supplied by the user.
     * @param newPassword the current password requested by the user.
     * @exception PasswordMismatchException if the supplied password was
     *            incorrect.
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
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
     * @exception UnknownEntityException if the user's record does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
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
    AccessControlList getACL(User user)
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

    /*-----------------------------------------------------------------------
      Retrieval & storage of SecurityObjects
      -----------------------------------------------------------------------*/

    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return A Group object that represents the global group.
     */
    Group getGlobalGroup();

    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    Group getGroupByName(String name)
            throws DataBackendException, UnknownEntityException;

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
    Group getGroupById(int id)
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
    Role getRoleByName(String name)
            throws DataBackendException, UnknownEntityException;

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
    Role getRoleById(int id)
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
    Permission getPermissionByName(String name)
            throws DataBackendException, UnknownEntityException;

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
    Permission getPermissionById(int id)
            throws DataBackendException,
                   UnknownEntityException;

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param criteria a Criteria of Group selection.
     * @return a set of Groups that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    GroupSet getGroups(Object criteria)
            throws DataBackendException;

    /**
     * Retrieve a set of Roles that meet the specified Criteria.
     *
     * @param criteria a Criteria of Roles selection.
     * @return a set of Roles that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    RoleSet getRoles(Object criteria)
            throws DataBackendException;

    /**
     * Retrieve a set of Permissions that meet the specified Criteria.
     *
     * @param criteria a Criteria of Permissions selection.
     * @return a set of Permissions that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    PermissionSet getPermissions(Object criteria)
            throws DataBackendException;

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

    /**
     * Stores Group's attributes. The Groups is required to exist in the system.
     *
     * @param group The Group to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    void saveGroup(Group group)
            throws DataBackendException, UnknownEntityException;

    /**
     * Stores Role's attributes. The Roles is required to exist in the system.
     *
     * @param role The Role to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    void saveRole(Role role)
            throws DataBackendException, UnknownEntityException;

    /**
     * Stores Permission's attributes. The Permission is required to exist in
     * the system.
     *
     * @param permission The Permission to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    void savePermission(Permission permission)
            throws DataBackendException, UnknownEntityException;

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
    Group addGroup(Group group)
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
    Role addRole(Role role)
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
    Permission addPermission(Permission permission)
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
}
