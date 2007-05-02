package org.apache.turbine.services.security.db;

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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.om.BaseObject;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.peer.GroupPeer;
import org.apache.turbine.om.security.peer.PermissionPeer;
import org.apache.turbine.om.security.peer.RolePeer;
import org.apache.turbine.om.security.peer.RolePermissionPeer;
import org.apache.turbine.om.security.peer.UserGroupRolePeer;
import org.apache.turbine.om.security.peer.UserPeer;
import org.apache.turbine.services.security.BaseSecurityService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * An implementation of SecurityService that uses a database as backend.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 * @version $Id$
 */
public class DBSecurityService
        extends BaseSecurityService
{
    /** Logging */
    private static Log log = LogFactory.getLog(DBSecurityService.class);

    /**
     * The key within services's properties for user implementation
     * classname (user.class)  - Leandro
     */
    public static final String USER_PEER_CLASS_KEY = "userPeer.class";

    /**
     * The default implementation of User interface
     * (org.apache.turbine.om.security.DBUser)
     */
    public static final String USER_PEER_CLASS_DEFAULT =
            "org.apache.turbine.om.security.peer.TurbineUserPeer";

    /*-----------------------------------------------------------------------
      Creation of AccessControlLists
      -----------------------------------------------------------------------*/

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * This method creates a snapshot of the state of security information
     * concerning this user, at the moment of invocation and stores it
     * into an AccessControlList object.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     * @return A new AccessControlList object.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public AccessControlList getACL(User user)
            throws DataBackendException, UnknownEntityException
    {
        if (!TurbineSecurity.accountExists(user))
        {
            throw new UnknownEntityException("The account '"
                    + user.getName() + "' does not exist");
        }
        try
        {
            Hashtable roles = new Hashtable();
            Hashtable permissions = new Hashtable();
            // notify the state modifiers (writers) that we want to create
            // the snapshot.
            lockShared();

            // construct the snapshot:

            // foreach group in the system
            for (Iterator groupsIterator = getAllGroups().iterator();
                 groupsIterator.hasNext();)
            {
                Group group = (Group) groupsIterator.next();
                // get roles of user in the group
                RoleSet groupRoles = RolePeer.retrieveSet(user, group);
                // put the Set into roles(group)
                roles.put(group, groupRoles);
                // collect all permissions in this group
                PermissionSet groupPermissions = new PermissionSet();
                // foreach role in Set
                for (Iterator rolesIterator = groupRoles.iterator();
                     rolesIterator.hasNext();)
                {
                    Role role = (Role) rolesIterator.next();
                    // get permissions of the role
                    PermissionSet rolePermissions
                            = PermissionPeer.retrieveSet(role);
                    groupPermissions.add(rolePermissions);
                }
                // put the Set into permissions(group)
                permissions.put(group, groupPermissions);
            }
            return getAclInstance(roles, permissions);
        }
        catch (Exception e)
        {
            throw new DataBackendException("Failed to build ACL for user '"
                    + user.getName() + "'", e);
        }
        finally
        {
            // notify the state modifiers that we are done creating the snapshot
            unlockShared();
        }
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
    public synchronized void grant(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = TurbineSecurity.accountExists(user);
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID,
                        ((BaseObject) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID,
                        ((BaseObject) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID,
                        ((BaseObject) role).getPrimaryKey());
                UserGroupRolePeer.doInsert(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(User,Group,Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '"
                    + user.getName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '"
                    + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '"
                    + role.getName() + "'");
        }
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
    public synchronized void revoke(User user, Group group, Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        boolean groupExists = false;
        boolean roleExists = false;
        try
        {
            lockExclusive();
            userExists = TurbineSecurity.accountExists(user);
            groupExists = checkExists(group);
            roleExists = checkExists(role);
            if (userExists && groupExists && roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID,
                        ((BaseObject) user).getPrimaryKey());
                criteria.add(UserGroupRolePeer.GROUP_ID,
                        ((BaseObject) group).getPrimaryKey());
                criteria.add(UserGroupRolePeer.ROLE_ID,
                        ((BaseObject) role).getPrimaryKey());
                UserGroupRolePeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(User,Role,Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!userExists)
        {
            throw new UnknownEntityException("Unknown user '"
                    + user.getName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '"
                    + group.getName() + "'");
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '"
                    + role.getName() + "'");
        }
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
    public synchronized void revokeAll(User user)
            throws DataBackendException, UnknownEntityException
    {
        boolean userExists = false;
        try
        {
            lockExclusive();
            userExists = TurbineSecurity.accountExists(user);
            if (userExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(UserGroupRolePeer.USER_ID,
                        ((BaseObject) user).getPrimaryKey());
                UserGroupRolePeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(User) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown user '"
                + user.getName() + "'");
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
    public synchronized void grant(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            permissionExists = checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID,
                        ((BaseObject) role).getPrimaryKey());
                criteria.add(RolePermissionPeer.PERMISSION_ID,
                        ((BaseObject) permission).getPrimaryKey());
                UserGroupRolePeer.doInsert(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("grant(Role,Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '"
                    + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '"
                    + permission.getName() + "'");
        }
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
    public synchronized void revoke(Role role, Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            permissionExists = checkExists(permission);
            if (roleExists && permissionExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID,
                        ((BaseObject) role).getPrimaryKey());
                criteria.add(RolePermissionPeer.PERMISSION_ID,
                        ((BaseObject) permission).getPrimaryKey());
                RolePermissionPeer.doDelete(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revoke(Role,Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        if (!roleExists)
        {
            throw new UnknownEntityException("Unknown role '"
                    + role.getName() + "'");
        }
        if (!permissionExists)
        {
            throw new UnknownEntityException("Unknown permission '"
                    + permission.getName() + "'");
        }
    }

    /**
     * Revokes all permissions from a Role.
     *
     * This method is user when deleting a Role.
     *
     * @param role the Role
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the Role is not present.
     */
    public synchronized void revokeAll(Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (roleExists)
            {
                Criteria criteria = new Criteria();
                criteria.add(RolePermissionPeer.ROLE_ID,
                        ((BaseObject) role).getPrimaryKey());
                RolePermissionPeer.doDelete(criteria);

                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("revokeAll(Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown role '"
                + role.getName() + "'");
    }

    /*-----------------------------------------------------------------------
      Group/Role/Permission management
      -----------------------------------------------------------------------*/

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param criteria A Criteria of Group selection.
     * @return a set of Groups that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public GroupSet getGroups(Criteria criteria)
            throws DataBackendException
    {
        Criteria dbCriteria = new Criteria();
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            dbCriteria.put(GroupPeer.getColumnName(key), criteria.get(key));
        }
        List groups = new ArrayList(0);
        try
        {
            groups = GroupPeer.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("getGroups(Criteria) failed", e);
        }
        return new GroupSet(groups);
    }

    /**
     * Retrieve a set of Roles that meet the specified Criteria.
     *
     * @param criteria A Criteria of Roles selection.
     * @return a set of Roles that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public RoleSet getRoles(Criteria criteria)
            throws DataBackendException
    {
        Criteria dbCriteria = new Criteria();
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            dbCriteria.put(RolePeer.getColumnName(key), criteria.get(key));
        }
        List roles = new ArrayList(0);
        try
        {
            roles = RolePeer.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException("getRoles(Criteria) failed", e);
        }
        return new RoleSet(roles);
    }

    /**
     * Retrieve a set of Permissions that meet the specified Criteria.
     *
     * @param criteria A Criteria of Permissions selection.
     * @return a set of Permissions that meet the specified Criteria.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     */
    public PermissionSet getPermissions(Criteria criteria)
            throws DataBackendException
    {
        Criteria dbCriteria = new Criteria();
        Iterator keys = criteria.keySet().iterator();
        while (keys.hasNext())
        {
            String key = (String) keys.next();
            dbCriteria.put(PermissionPeer.getColumnName(key),
                    criteria.get(key));
        }
        List permissions = new Vector(0);
        try
        {
            permissions = PermissionPeer.doSelect(criteria);
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                    "getPermissions(Criteria) failed", e);
        }
        return new PermissionSet(permissions);
    }

    /**
     * Retrieves all permissions associated with a role.
     *
     * @param role the role name, for which the permissions are to be retrieved.
     * @return A Permission set for the Role.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role is not present.
     */
    public PermissionSet getPermissions(Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockShared();
            roleExists = checkExists(role);
            if (roleExists)
            {
                return PermissionPeer.retrieveSet(role);
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("getPermissions(Role) failed", e);
        }
        finally
        {
            unlockShared();
        }
        throw new UnknownEntityException("Unknown role '"
                + role.getName() + "'");
    }

    /**
     * Stores Group's attributes. The Groups is required to exist in the system.
     *
     * @param group The Group to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public void saveGroup(Group group)
            throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            groupExists = checkExists(group);
            if (groupExists)
            {
                Criteria criteria = GroupPeer.buildCriteria(group);
                GroupPeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("saveGroup(Group) failed", e);
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
    }

    /**
     * Stores Role's attributes. The Roles is required to exist in the system.
     *
     * @param role The Role to be stored.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public void saveRole(Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            roleExists = checkExists(role);
            if (roleExists)
            {
                Criteria criteria = RolePeer.buildCriteria(role);
                RolePeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("saveRole(Role) failed", e);
        }
        throw new UnknownEntityException("Unknown role '" + role + "'");
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
    public void savePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                Criteria criteria = PermissionPeer.buildCriteria(permission);
                PermissionPeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                    "savePermission(Permission) failed", e);
        }
        throw new UnknownEntityException("Unknown permission '"
                + permission + "'");
    }

    /**
     * Creates a new group with specified attributes.
     *
     * @param group the object describing the group to be created.
     * @return a new Group object that has id set up properly.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the group already exists.
     */
    public synchronized Group addGroup(Group group)
            throws DataBackendException,
            EntityExistsException
    {
        boolean groupExists = false;

        if (StringUtils.isEmpty(group.getName()))
        {
            throw new DataBackendException("Could not create "
                    + "a group with empty name!");
        }

        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (!groupExists)
            {
                // add a row to the table
                Criteria criteria = GroupPeer.buildCriteria(group);
                GroupPeer.doInsert(criteria);
                // try to get the object back using the name as key.
                criteria = new Criteria();
                criteria.add(GroupPeer.NAME,
                        group.getName());
                List results = GroupPeer.doSelect(criteria);
                if (results.size() != 1)
                {
                    throw new DataBackendException(
                            "Internal error - query returned "
                            + results.size() + " rows");
                }
                Group newGroup = (Group) results.get(0);
                // add the group to system-wide cache
                getAllGroups().add(newGroup);
                // return the object with correct id
                return newGroup;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addGroup(Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        // the only way we could get here without return/throw tirggered
        // is that the groupExists was true.
        throw new EntityExistsException("Group '" + group + "' already exists");
    }

    /**
     * Creates a new role with specified attributes.
     *
     * @param role the object describing the role to be created.
     * @return a new Role object that has id set up properly.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the role already exists.
     */
    public synchronized Role addRole(Role role)
            throws DataBackendException, EntityExistsException
    {
        boolean roleExists = false;

        if (StringUtils.isEmpty(role.getName()))
        {
            throw new DataBackendException("Could not create "
                    + "a role with empty name!");
        }

        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (!roleExists)
            {
                // add a row to the table
                Criteria criteria = RolePeer.buildCriteria(role);
                RolePeer.doInsert(criteria);
                // try to get the object back using the name as key.
                criteria = new Criteria();
                criteria.add(RolePeer.NAME, role.getName());
                List results = RolePeer.doSelect(criteria);
                if (results.size() != 1)
                {
                    throw new DataBackendException(
                            "Internal error - query returned "
                            + results.size() + " rows");
                }
                Role newRole = (Role) results.get(0);
                // add the role to system-wide cache
                getAllRoles().add(newRole);
                // return the object with correct id
                return newRole;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("addRole(Role) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        // the only way we could get here without return/throw tirggered
        // is that the roleExists was true.
        throw new EntityExistsException("Role '" + role + "' already exists");
    }

    /**
     * Creates a new permission with specified attributes.
     *
     * @param permission the object describing the permission to be created.
     * @return a new Permission object that has id set up properly.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws EntityExistsException if the permission already exists.
     */
    public synchronized Permission addPermission(Permission permission)
            throws DataBackendException, EntityExistsException
    {
        boolean permissionExists = false;

        if (StringUtils.isEmpty(permission.getName()))
        {
            throw new DataBackendException("Could not create "
                    + "a permission with empty name!");
        }

        try
        {
            lockExclusive();
            permissionExists = checkExists(permission);
            if (!permissionExists)
            {
                // add a row to the table
                Criteria criteria = PermissionPeer.buildCriteria(permission);
                PermissionPeer.doInsert(criteria);
                // try to get the object back using the name as key.
                criteria = new Criteria();
                criteria.add(PermissionPeer.NAME,
                        permission.getName());
                List results = PermissionPeer.doSelect(criteria);
                if (results.size() != 1)
                {
                    throw new DataBackendException(
                            "Internal error - query returned "
                            + results.size() + " rows");
                }
                Permission newPermission = (Permission) results.get(0);
                // add the permission to system-wide cache
                getAllPermissions().add(newPermission);
                // return the object with correct id
                return newPermission;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                    "addPermission(Permission) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        // the only way we could get here without return/throw tirggered
        // is that the permissionExists was true.
        throw new EntityExistsException("Permission '" + permission
                + "' already exists");
    }

    /**
     * Removes a Group from the system.
     *
     * @param group The object describing the group to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the group does not exist.
     */
    public synchronized void removeGroup(Group group)
            throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (groupExists)
            {
                Criteria criteria = GroupPeer.buildCriteria(group);
                GroupPeer.doDelete(criteria);
                getAllGroups().remove(group);
                return;
            }
        }
        catch (Exception e)
        {
            log.error("Failed to delete a Group");
            log.error(e);
            throw new DataBackendException("removeGroup(Group) failed", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
    }

    /**
     * Removes a Role from the system.
     *
     * @param role The object describing the role to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the role does not exist.
     */
    public synchronized void removeRole(Role role)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (roleExists)
            {
                // revoke all permissions from the role to be deleted
                revokeAll(role);
                Criteria criteria = RolePeer.buildCriteria(role);
                RolePeer.doDelete(criteria);
                getAllRoles().remove(role);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("removeRole(Role)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown role '" + role + "'");
    }

    /**
     * Removes a Permission from the system.
     *
     * @param permission The object describing the permission to be removed.
     * @throws DataBackendException if there was an error accessing the data
     *         backend.
     * @throws UnknownEntityException if the permission does not exist.
     */
    public synchronized void removePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                Criteria criteria = PermissionPeer.buildCriteria(permission);
                PermissionPeer.doDelete(criteria);
                getAllPermissions().remove(permission);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("removePermission(Permission)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown permission '"
                + permission + "'");
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
    public synchronized void renameGroup(Group group, String name)
            throws DataBackendException, UnknownEntityException
    {
        boolean groupExists = false;
        try
        {
            lockExclusive();
            groupExists = checkExists(group);
            if (groupExists)
            {
                group.setName(name);
                Criteria criteria = GroupPeer.buildCriteria(group);
                GroupPeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameGroup(Group,String)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown group '" + group + "'");
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
    public synchronized void renameRole(Role role, String name)
            throws DataBackendException, UnknownEntityException
    {
        boolean roleExists = false;
        try
        {
            lockExclusive();
            roleExists = checkExists(role);
            if (roleExists)
            {
                role.setName(name);
                Criteria criteria = RolePeer.buildCriteria(role);
                RolePeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException("renameRole(Role,String)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown role '" + role + "'");
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
    public synchronized void renamePermission(Permission permission,
                                              String name)
            throws DataBackendException, UnknownEntityException
    {
        boolean permissionExists = false;
        try
        {
            lockExclusive();
            permissionExists = checkExists(permission);
            if (permissionExists)
            {
                permission.setName(name);
                Criteria criteria = PermissionPeer.buildCriteria(permission);
                PermissionPeer.doUpdate(criteria);
                return;
            }
        }
        catch (Exception e)
        {
            throw new DataBackendException(
                    "renamePermission(Permission,name)", e);
        }
        finally
        {
            unlockExclusive();
        }
        throw new UnknownEntityException("Unknown permission '"
                + permission + "'");
    }

    /* Service specific implementation methods */

    /**
     * Returns the Class object for the implementation of UserPeer interface
     * used by the system (defined in TR.properties)
     *
     * @return the implementation of UserPeer interface used by the system.
     * @throws UnknownEntityException if the system's implementation of UserPeer
     *         interface could not be determined.
     */
    public Class getUserPeerClass() throws UnknownEntityException
    {
        String userPeerClassName = getConfiguration().getString(
                USER_PEER_CLASS_KEY, USER_PEER_CLASS_DEFAULT);
        try
        {
            return Class.forName(userPeerClassName);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed create a Class object for UserPeer implementation", e);
        }
    }

    /**
     * Construct a UserPeer object.
     *
     * This method calls getUserPeerClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing UserPeer interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     */
    public UserPeer getUserPeerInstance() throws UnknownEntityException
    {
        UserPeer up;
        try
        {
            up = (UserPeer) getUserPeerClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed instantiate an UserPeer implementation object", e);
        }
        return up;
    }

    /**
     * Determines if the <code>Group</code> exists in the security system.
     *
     * @param group a <code>Group</code> value
     * @return true if the group exists in the system, false otherwise
     * @throws DataBackendException when more than one Group with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    protected boolean checkExists(Group group)
            throws DataBackendException, Exception
    {
        return GroupPeer.checkExists(group);
    }

    /**
     * Determines if the <code>Role</code> exists in the security system.
     *
     * @param role a <code>Role</code> value
     * @return true if the role exists in the system, false otherwise
     * @throws DataBackendException when more than one Role with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    protected boolean checkExists(Role role)
            throws DataBackendException, Exception
    {
        return RolePeer.checkExists(role);
    }

    /**
     * Determines if the <code>Permission</code> exists in the security system.
     *
     * @param permission a <code>Permission</code> value
     * @return true if the permission exists in the system, false otherwise
     * @throws DataBackendException when more than one Permission with
     *         the same name exists.
     * @throws Exception A generic exception.
     */
    protected boolean checkExists(Permission permission)
            throws DataBackendException, Exception
    {
        return PermissionPeer.checkExists(permission);
    }

}
