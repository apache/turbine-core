package org.apache.turbine.services.security.db;

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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.apache.torque.om.BaseObject;
import org.apache.torque.om.NumberKey;
import org.apache.torque.util.Criteria;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.SecurityObject;
import org.apache.turbine.om.security.TurbineGroup;
import org.apache.turbine.om.security.TurbinePermission;
import org.apache.turbine.om.security.TurbineRole;
import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.peer.GroupPeer;
import org.apache.turbine.om.security.peer.PermissionPeer;
import org.apache.turbine.om.security.peer.RolePeer;
import org.apache.turbine.om.security.peer.RolePermissionPeer;
import org.apache.turbine.om.security.peer.UserGroupRolePeer;
import org.apache.turbine.om.security.peer.UserPeer;
import org.apache.turbine.services.security.BaseSecurityService;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class DBSecurityService extends BaseSecurityService
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
                                             + user.getUserName() + "' does not exist");
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
            for (Iterator groupsIterator = getAllGroups().elements(); 
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
                for (Iterator rolesIterator = groupRoles.elements();
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
            throw new DataBackendException("Failed to build ACL for user '" +
                                           user.getUserName() + "'" , e);
        }
        finally
        {
            // notify the state modifiers that we are done creating the snapshot.
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
                        ((TurbineRole) role).getPrimaryKey());
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
                    + user.getUserName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '"
                    + ((SecurityObject) group).getName() + "'");
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
                        ((TurbineRole) role).getPrimaryKey());
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
                    + user.getUserName() + "'");
        }
        if (!groupExists)
        {
            throw new UnknownEntityException("Unknown group '"
                    + ((SecurityObject) group).getName() + "'");
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
                // The following would not work, due to an annoying misfeature
                // of Village. Village allows only a single row to be deleted at
                // a time. I wish that it was possible to disable this
                // behaviour!

                // Criteria criteria = new Criteria();
                // criteria.add(UserGroupRolePeer.USER_ID,
                //         ((BaseObject) user).getPrimaryKey());
                // UserGroupRolePeer.doDelete(criteria);
                int id = ((NumberKey) ((BaseObject) user)
                        .getPrimaryKey()).intValue();
                UserGroupRolePeer.deleteAll(UserGroupRolePeer.TABLE_NAME,
                        UserGroupRolePeer.USER_ID, id);
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
                + user.getUserName() + "'");
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
                        ((TurbineRole) role).getPrimaryKey());
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
                    + ((SecurityObject) permission).getName() + "'");
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
                        ((TurbineRole) role).getPrimaryKey());
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
                    + ((SecurityObject) permission).getName() + "'");
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
                // The following would not work, due to an annoying misfeature
                // of Village. see revokeAll( user )

                // Criteria criteria = new Criteria();
                // criteria.add(RolePermissionPeer.ROLE_ID,
                //         role.getPrimaryKey());
                // RolePermissionPeer.doDelete(criteria);

                int id = ((NumberKey) ((TurbineRole) role)
                        .getPrimaryKey()).intValue();
                RolePermissionPeer.deleteAll(RolePermissionPeer.TABLE_NAME,
                        RolePermissionPeer.ROLE_ID, id);
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
     * @param criteria Criteria of Group selection.
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
     * @param criteria Criteria of Roles selection.
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
     * @param criteria Criteria of Permissions selection.
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
     * @return the Permissions
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
     * Retrieves a new Group. It creates
     * a new Group based on the Services Group implementation. It does not
     * create a new Group in the system though. Use create for that.
     *
     * @param groupName The name of the Group to be retrieved.
     * @return the Group object
     */
    public Group getNewGroup(String groupName)
    {
        return (Group) new TurbineGroup(groupName);
    }

    /**
     * Retrieves a new Role. It creates
     * a new Role based on the Services Role implementation. It does not
     * create a new Role in the system though. Use create for that.
     *
     * @param roleName The name of the Role to be retrieved.
     * @return the Role object
     */
    public Role getNewRole(String roleName)
    {
        return (Role) new TurbineRole(roleName);
    }

    /**
     * Retrieves a new Permission. It creates a new Permission based on the
     * Services Permission implementation. It does not
     * create a new Permission in the system though. Use create for that.
     *
     * @param permissionName The name of the Permission to be retrieved.
     * @return the Permission object
     */
    public Permission getNewPermission(String permissionName)
    {
        return (Permission) new TurbinePermission(permissionName);
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
        throws DataBackendException, EntityExistsException
    {
        boolean groupExists = false;
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
                        ((SecurityObject) group).getName());
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
                        ((SecurityObject) permission).getName());
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
     * @param group object describing group to be removed.
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
     * @param role object describing role to be removed.
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
     * @param permission object describing permission to be removed.
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
     * @param group object describing the group to be renamed.
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
                ((SecurityObject) group).setName(name);
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
     * @param role object describing the role to be renamed.
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
     * @param permission object describing the permission to be renamed.
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
                ((SecurityObject) permission).setName(name);
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
     * @throws Exception a generic exception.
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
     * @throws Exception a generic exception.
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
     * @throws Exception a generic exception.
     */
    protected boolean checkExists(Permission permission)
        throws DataBackendException, Exception
    {
        return PermissionPeer.checkExists(permission);
    }

}
