package org.apache.turbine.services.security;

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

import org.apache.turbine.services.Service;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.peer.UserPeer;

import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.AccessControlList;

import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.UnknownEntityException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;
import org.apache.turbine.util.security.TurbineSecurityException;

import org.apache.torque.util.Criteria;

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
 * @version $Id$
 */
public interface SecurityService
    extends Service
{
    /** The name of the service */
    public static final String SERVICE_NAME = "SecurityService";

    /** the key within services's properties for user implementation classname (user.class) */
    public static final String USER_CLASS_KEY = "user.class";

    /** the default implementation of User interface (org.apache.turbine.om.security.DBUser) */
    public static final String USER_CLASS_DEFAULT = "org.apache.turbine.om.security.TurbineUser";

    /** the key within services's properties for user implementation classname (user.manager) */
    public static final String USER_MANAGER_KEY = "user.manager";

    /** the default implementation of UserManager interface (org.apache.turbine.services.security.DBUserManager) */
    public static final String USER_MANAGER_DEFAULT = "org.apache.turbine.services.security.DBUserManager";

    /** the key within services's properties for secure passwords flag (secure.passwords) */
    public static final String SECURE_PASSWORDS_KEY = "secure.passwords";

    /** the value of secure passwords flag (false) */
    public static final String SECURE_PASSWORDS_DEFAULT = "false";

    /** the key within services's properties for secure passwords algorithm (secure.passwords.algorithm) */
    public static final String SECURE_PASSWORDS_ALGORITHM_KEY = "secure.passwords.algorithm";

    /** the default algorithm for password encryption (SHA) */
    public static final String SECURE_PASSWORDS_ALGORITHM_DEFAULT = "SHA";

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
    public Class getUserClass()
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
    public User getUserInstance()
        throws UnknownEntityException;

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param user The user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public boolean accountExists( String username )
        throws DataBackendException;

    /**
     * Check whether a specified user's account exists.
     *
     * The login name is used for looking up the account.
     *
     * @param usename The name of the user to be checked.
     * @return true if the specified account exists
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public boolean accountExists( User user )
        throws DataBackendException;

    /**
     * Authenticates an user, and constructs an User object to represent him/her.
     *
     * @param username The user name.
     * @param password The user password.
     * @return An authenticated Turbine User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     * @throws PasswordMismatchException if the supplied password was incorrect.
     */
    public User getAuthenticatedUser( String username, String password )
        throws DataBackendException, UnknownEntityException, PasswordMismatchException;

    /**
     * Constructs an User object to represent a registered user of the application.
     *
     * @param username The user name.
     * @return A Turbine User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public User getUser( String username )
        throws DataBackendException, UnknownEntityException;

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
    public User[] getUsers( Criteria criteria )
        throws DataBackendException;

    /**
     * Constructs an User object to represent an anonymous user of the application.
     *
     * @return An anonymous Turbine User.
     * @throws UnknownEntityException if the anonymous User object couldn't be
     *         constructed.
     */
    public User getAnonymousUser()
        throws UnknownEntityException;

    /**
     * Saves User's data in the permanent storage. The user account is required
     * to exist in the storage.
     *
     * @exception UnknownEntityException if the user's account does not
     *            exist in the database.
     * @exception DataBackendException if there is a problem accessing the
     *            storage.
     */
    public void saveUser( User user )
        throws UnknownEntityException, DataBackendException;

    /*-----------------------------------------------------------------------
      Account management
      -----------------------------------------------------------------------*/

    /**
     * Creates new user account with specified attributes.
     *
     * @param user the object describing account to be created.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the user account already exists.
     */
    public void addUser( User user, String password )
        throws DataBackendException, EntityExistsException;

    /**
     * Removes an user account from the system.
     *
     * @param user the object describing the account to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the user account is not present.
     */
    public void removeUser( User user )
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
    public String encryptPassword( String password );

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
    public void changePassword( User user, String oldPassword, String newPassword )
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
    public void forcePassword( User user, String password )
        throws UnknownEntityException, DataBackendException;

    /*-----------------------------------------------------------------------
      Retrieval of security information
      -----------------------------------------------------------------------*/

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public AccessControlList getACL( User user )
        throws DataBackendException, UnknownEntityException;

    /**
     * Retrieves all permissions associated with a role.
     *
     * @param role the role name, for which the permissions are to be retrieved.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role is not present.
     */
    public PermissionSet getPermissions( Role role )
        throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Manipulation of security information
      -----------------------------------------------------------------------*/

    /**
     * Grant an User a Role in a Group.
     *
     * @param User the user.
     * @param Group the group.
     * @param Role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    public void grant( User user, Group group, Role role )
        throws DataBackendException, UnknownEntityException;

    /**
     * Revoke a Role in a Group from an User.
     *
     * @param User the user.
     * @param Group the group.
     * @param Role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    public void revoke( User user, Group group, Role role )
        throws DataBackendException, UnknownEntityException;

    /**
     * Revokes all roles from an User.
     *
     * This method is used when deleting an account.
     *
     * @param user the User.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the account is not present.
     */
    public void revokeAll( User user )
        throws DataBackendException, UnknownEntityException;

    /**
     * Grants a Role a Permission
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public void grant( Role role, Permission permission )
        throws DataBackendException, UnknownEntityException;

    /**
     * Revokes a Permission from a Role.
     *
     * @param role the Role.
     * @param permission the Permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if role or permission is not present.
     */
    public void revoke( Role role, Permission permission )
        throws DataBackendException, UnknownEntityException;

    /**
     * Revokes all permissions from a Role.
     *
     * This method is user when deleting a Role.
     *
     * @param role the Role
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws  UnknownEntityException if the Role is not present.
     */
    public void revokeAll( Role role )
        throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Retrieval & storage of SecurityObjects
      -----------------------------------------------------------------------*/

    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return a Group object that represents the global group.
     */
    public Group getGlobalGroup();

    /**
     * Retrieves a new Group. It creates
     * a new Group based on the Services Group implementation. It does not
     * create a new Group in the system though. Use addGroup for that.
     *
     * @param groupName The name of the Group to be retrieved.
     */
    public Group getNewGroup( String groupName );

    /**
     * Retrieves a new Role. It creates
     * a new Group based on the Services Role implementation. It does not
     * create a new Role in the system though. Use addRole for that.
     *
     * @param roleName The name of the Role to be retrieved.
     */
    public Role getNewRole( String roleName );

    /**
     * Retrieves a new Permission. It creates
     * a new Permission based on the Services Permission implementation. It does not
     * create a new Permission in the system though. Use addPermission for that.
     *
     * @param permissionName The name of the Permission to be retrieved.
     */
    public Permission getNewPermission( String permissionName );

    /**
     * Retrieve a Group object with specified name.
     *
     * @param name the name of the Group.
     * @return an object representing the Group with specified name.
     */
    public Group getGroup( String name )
        throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a Role object with specified name.
     *
     * @param name the name of the Role.
     * @return an object representing the Role with specified name.
     */
    public Role getRole( String name )
        throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a Permission object with specified name.
     *
     * @param name the name of the Permission.
     * @return an object representing the Permission with specified name.
     */
    public Permission getPermission( String name )
        throws DataBackendException, UnknownEntityException;

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param a Criteria of Group selection.
     * @return a set of Groups that meet the specified Criteria.
     */
    public GroupSet getGroups( Criteria criteria )
        throws DataBackendException;

    /**
     * Retrieve a set of Roles that meet the specified Criteria.
     *
     * @param a Criteria of Roles selection.
     * @return a set of Roles that meet the specified Criteria.
     */
    public RoleSet getRoles( Criteria criteria )
        throws DataBackendException;

    /**
     * Retrieve a set of Permissions that meet the specified Criteria.
     *
     * @param a Criteria of Permissions selection.
     * @return a set of Permissions that meet the specified Criteria.
     */
    public PermissionSet getPermissions( Criteria criteria )
        throws DataBackendException;

    /**
     * Retrieves all groups defined in the system.
     *
     * @return the names of all groups defined in the system.
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public GroupSet getAllGroups()
        throws DataBackendException;

    /**
     * Retrieves all roles defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public RoleSet getAllRoles()
        throws DataBackendException;

    /**
     * Retrieves all permissions defined in the system.
     *
     * @return the names of all roles defined in the system.
     * @throws DataBackendException if there was an error accessing the data backend.
     */
    public PermissionSet getAllPermissions()
        throws DataBackendException;
     /**
     * Stores Group's attributes. The Groups is required to exist in the system.
     *
     * @param group The Group to be stored.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public void saveGroup( Group group )
        throws DataBackendException, UnknownEntityException;

    /**
     * Stores Role's attributes. The Roles is required to exist in the system.
     *
     * @param role The Role to be stored.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void saveRole( Role role )
        throws DataBackendException, UnknownEntityException;

    /**
     * Stores Permission's attributes. The Permissions is required to exist in the system.
     *
     * @param permission The Permission to be stored.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public void savePermission( Permission permission )
        throws DataBackendException, UnknownEntityException;

    /*-----------------------------------------------------------------------
      Group/Role/Permission management
      -----------------------------------------------------------------------*/

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @return the new Group object.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the group already exists.
     */
    public Group addGroup( Group group )
        throws DataBackendException, EntityExistsException;

    /**
     * Creates a new role with specified attributes.
     *
     * @param group the objects describing the group to be created.
     * @return the new Role object.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the role already exists.
     */
    public Role addRole( Role role )
        throws DataBackendException, EntityExistsException;

    /**
     * Creates a new permission with specified attributes.
     *
     * @param group the objects describing the group to be created.
     * @return the new Permission object.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws EntityExistsException if the permission already exists.
     */
    public Permission addPermission( Permission permission )
        throws DataBackendException, EntityExistsException;

    /**
     * Removes a Group from the system.
     *
     * @param the object describing group to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public void removeGroup( Group group )
        throws DataBackendException, UnknownEntityException;

    /**
     * Removes a Role from the system.
     *
     * @param the object describing role to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void removeRole( Role role )
        throws DataBackendException, UnknownEntityException;

    /**
     * Removes a Permission from the system.
     *
     * @param the object describing permission to be removed.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public void removePermission( Permission permission )
        throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Group.
     *
     * @param the object describing the group to be renamed.
     * @param name the new name for the group.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public void renameGroup( Group group, String name )
        throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Role.
     *
     * @param the object describing the role to be renamed.
     * @param name the new name for the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void renameRole( Role role, String name )
        throws DataBackendException, UnknownEntityException;

    /**
     * Renames an existing Permission.
     *
     * @param the object describing the permission to be renamed.
     * @param name the new name for the permission.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public void renamePermission( Permission permission, String name )
        throws DataBackendException, UnknownEntityException;
}
