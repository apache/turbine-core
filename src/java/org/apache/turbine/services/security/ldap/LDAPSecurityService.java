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
import java.util.Vector;

import org.apache.turbine.services.InitializationException;
import org.apache.turbine.util.RunData;

import org.apache.turbine.om.security.User;
import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.peer.GroupPeer;
import org.apache.turbine.om.security.peer.RolePeer;
import org.apache.turbine.om.security.peer.PermissionPeer;
import org.apache.turbine.om.security.peer.UserGroupRolePeer;
import org.apache.turbine.om.security.peer.RolePermissionPeer;
import org.apache.turbine.om.security.peer.TurbineUserPeer;

import org.apache.turbine.util.Log;
import org.apache.turbine.util.db.Criteria;

import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.GroupSet;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.UnknownEntityException;
import org.apache.turbine.util.security.EntityExistsException;
import org.apache.turbine.util.security.PasswordMismatchException;

import org.apache.turbine.services.security.BaseSecurityService;

/**
 * An implementation of SecurityService that uses LDAP as a backend.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:tadewunmi@gluecode.com">Tracy M. Adewunmi </a>
 * @author <a href="mailto:lflournoy@gluecode.com">Leonard J. Flournoy </a>
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @version $Id$
 */
public class LDAPSecurityService extends BaseSecurityService
{
    /*
     * -----------------------------------------------------------------------
     *  C R E A T I O N  O F  A C C E S S  C O N T R O L  L I S T
     * -----------------------------------------------------------------------
     */

    /**
     * Constructs an AccessControlList for a specific user.
     *
     * This method creates a snapshot of the state of security information
     * concerning this user, at the moment of invocation and stores it
     * into an AccessControlList object.
     *
     * @param user the user for whom the AccessControlList are to be retrieved
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account is not present.
     */
    public AccessControlList getACL(User user)
            throws DataBackendException, UnknownEntityException
    {
        /*
         * This is severely lacking [jvz].
         */
        Hashtable roles = new Hashtable();
        Hashtable permissions = new Hashtable();
        return new AccessControlList(roles, permissions);
    }

    /*
     * -----------------------------------------------------------------------
     * S E C U R I T Y  M A N A G E M E N T
     * -----------------------------------------------------------------------
     */

    /**
     * Grant an User a Role in a Group.
     *
     * @param User the user.
     * @param Group the group.
     * @param Role the role.
     * @throws DataBackendException if there was an error accessing the data backend.
     * @throws UnknownEntityException if user account, group or role is not present.
     */
    public synchronized void grant(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Revoke a Role in a Group from an User.
      *
      * @param User the user.
      * @param Group the group.
      * @param Role the role.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if user account, group or role is not present.
      */
    public synchronized void revoke(User user, Group group, Role role)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Grants a Role a Permission
      *
      * @param role the Role.
      * @param permission the Permission.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if role or permission is not present.
      */
    public synchronized void grant(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Revokes a Permission from a Role.
      *
      * @param role the Role.
      * @param permission the Permission.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if role or permission is not present.
      */
    public synchronized void revoke(Role role, Permission permission)
        throws DataBackendException, UnknownEntityException
    {
    }

    /*
     * -----------------------------------------------------------------------
     * G R O U P / R O L E / P E R M I S S I O N  M A N A G E M E N T
     * -----------------------------------------------------------------------
     */

    /**
     * Retrieves a new Group. It creates
     * a new Group based on the Services Group implementation. It does not
     * create a new Group in the system though. Use addGroup for that.
     * <strong>Not implemented</strong>
     *
     * @param groupName The name of the Group to be retrieved.
     */
    public Group getNewGroup( String groupName )
    {
        // Not implemented
        return null;
    }

    /**
     * Retrieves a new Role. It creates
     * a new Role based on the Services Role implementation. It does not
     * create a new Role in the system though. Use addRole for that.
     * <strong>Not implemented</strong>
     *
     * @param groupName The name of the Group to be retrieved.
     */
    public Role getNewRole(String roleName)
    {
        // Not implemented
        return null;
    }

    /**
     * Retrieves a new Permission. It creates
     * a new Permission based on the Services Permission implementation. It does not
     * create a new Permission in the system though. Use create for that.
     * <strong>Not implemented</strong>
     *
     * @param permissionName The name of the Permission to be retrieved.
     */
    public Permission getNewPermission( String permissionName )
    {
        // Not implemented
        return null;
    }

    /**
     * Retrieve a set of Groups that meet the specified Criteria.
     *
     * @param a Criteria of Group selection.
     * @return a set of Groups that meet the specified Criteria.
     */
    public GroupSet getGroups(Criteria criteria) throws DataBackendException
    {
        Vector groups = new Vector(0);
        return new GroupSet(groups);
    }

    /**
      * Retrieve a set of Roles that meet the specified Criteria.
      *
      * @param a Criteria of Roles selection.
      * @return a set of Roles that meet the specified Criteria.
      */
    public RoleSet getRoles(Criteria criteria) throws DataBackendException
    {
        Vector roles = new Vector(0);
        return new RoleSet(roles);
    }

    /**
      * Retrieve a set of Permissions that meet the specified Criteria.
      *
      * @param a Criteria of Permissions selection.
      * @return a set of Permissions that meet the specified Criteria.
      */
    public PermissionSet getPermissions(Criteria criteria)
            throws DataBackendException
    {
        Vector permissions = new Vector(0);
        return new PermissionSet(permissions);
    }

    /**
      * Retrieves all permissions associated with a role.
      *
      * @param role the role name, for which the permissions are to be retrieved.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the role is not present.
      */
    public PermissionSet getPermissions(Role role)
            throws DataBackendException, UnknownEntityException
    {
        return new PermissionSet();
    }

    /**
      * Stores Group's attributes. The Groups is required to exist in the system.
      *
      * @param group The Group to be stored.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the group does not exist.
      */
    public void saveGroup(Group group) throws DataBackendException,
    UnknownEntityException
    {
    }

    /**
      * Stores Role's attributes. The Roles is required to exist in the system.
      *
      * @param role The Role to be stored.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the role does not exist.
      */
    public void saveRole(Role role) throws DataBackendException,
    UnknownEntityException
    {
    }

    /**
      * Stores Permission's attributes. The Permissions is required to exist in the system.
      *
      * @param permission The Permission to be stored.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the permission does not exist.
      */
    public void savePermission(Permission permission)
            throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Creates a new group with specified attributes.
      * <strong>Not implemented</strong>
      *
      * @param group the object describing the group to be created.
      * @return a new Group object that has id set up properly.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws EntityExistsException if the group already exists.
      */
    public synchronized Group addGroup(Group group)
        throws DataBackendException, EntityExistsException
    {
        // Not implemented
        return null;
    }

    /**
      * Creates a new role with specified attributes.
      *
      * @param role the object describing the role to be created.
      * @return a new Role object that has id set up properly.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws EntityExistsException if the role already exists.
      */
    public synchronized Role addRole(Role role)
        throws DataBackendException, EntityExistsException
    {
        return null;
        //return new Role();
    }

    /**
      * Creates a new permission with specified attributes.
      * <strong>Not implemented</strong>
      *
      * @param permission the object describing the permission to be created.
      * @return a new Permission object that has id set up properly.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws EntityExistsException if the permission already exists.
      */
    public synchronized Permission addPermission(Permission permission)
        throws DataBackendException, EntityExistsException
    {
        // Not implemented
        return null;
    }

    /**
      * Removes a Group from the system.
      *
      * @param the object describing group to be removed.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the group does not exist.
      */
    public synchronized void removeGroup(Group group)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Removes a Role from the system.
      *
      * @param the object describing role to be removed.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the role does not exist.
      */
    public synchronized void removeRole(Role role)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Removes a Permission from the system.
      *
      * @param the object describing permission to be removed.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the permission does not exist.
      */
    public synchronized void removePermission(Permission permission)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Renames an existing Group.
      *
      * @param the object describing the group to be renamed.
      * @param name the new name for the group.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the group does not exist.
      */
    public synchronized void renameGroup(Group group, String name)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Renames an existing Role.
      *
      * @param the object describing the role to be renamed.
      * @param name the new name for the role.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the role does not exist.
      */
    public synchronized void renameRole(Role role, String name)
        throws DataBackendException, UnknownEntityException
    {
    }

    /**
      * Renames an existing Permission.
      *
      * @param the object describing the permission to be renamed.
      * @param name the new name for the permission.
      * @throws DataBackendException if there was an error accessing the data backend.
      * @throws UnknownEntityException if the permission does not exist.
      */
    public synchronized void renamePermission(Permission permission,
            String name)
        throws DataBackendException, UnknownEntityException
    {
    }

    //just to satisify the interface requirements
    public void revokeAll(User user)
    {
    }

    //just to satisify the interface requirements
    public void revokeAll(Role role)
    {
    }

    //just to satisify the interface requirements
    public void revokeAll(Group group)
    {
    }
}
