package org.apache.turbine.om.security;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.sql.Connection;
import java.util.Iterator;

import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a Group of Users in the system that are associated
 * with specific entity or resource. The users belonging to the Group may have
 * various Roles. The Permissions to perform actions upon the resource depend
 * on the Roles in the Group that they are assigned.
 *
 * <a name="global">
 * <p> Certain Roles that the Users may have in the system may are not related
 * to any specific resource nor entity.
 * They are assigned within a special group named 'global' that can be
 * referenced in the code as {@link #GLOBAL_GROUP_NAME}.
 * <br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * 
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueGroup}
 * instead.
 * 
 * @version $Id$
 */
public class TurbineGroup extends SecurityObject implements Group
{
    /** Serial Version UID */
    private static final long serialVersionUID = -6034684697021752649L;

    /**
     * Constructs a new Group.
     */
    public TurbineGroup()
    {
        super();
    }

    /**
     * Constructs a new Group with the specified name.
     *
     * @param name The name of the new object.
     */
    public TurbineGroup(String name)
    {
        super(name);
    }

    // These following methods are wrappers around TurbineSecurity

    /**
     * Makes changes made to the Group attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while saving data.
     */
    public void save() throws TurbineSecurityException
    {
        TurbineSecurity.saveGroup(this);
    }

    /**
     * not implemented
     *
     * @param conn
     * @throws Exception
     */
    public void save(Connection conn) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * not implemented
     *
     * @param dbname
     * @throws Exception
     */
    public void save(String dbname) throws Exception
    {
        throw new Exception("not implemented");
    }

    /**
     * Removes a group from the system.
     *
     * @throws TurbineSecurityException if the Group could not be removed.
     */
    public void remove() throws TurbineSecurityException
    {
        TurbineSecurity.removeGroup(this);
    }

    /**
     * Renames the role.
     *
     * @param name The new Group name.
     * @throws TurbineSecurityException if the Group could not be renamed.
     */
    public void rename(String name) throws TurbineSecurityException
    {
        TurbineSecurity.renameGroup(this, name);
    }

    /**
     * Grants a Role in this Group to an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Role.
     */
    public void grant(User user, Role role) throws TurbineSecurityException
    {
        TurbineSecurity.grant(user, this, role);
    }

    /**
     * Grants Roles in this Group to an User.
     *
     * @param user An User.
     * @param roleSet A RoleSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Roles.
     */
    public void grant(User user, RoleSet roleSet)
            throws TurbineSecurityException
    {
        for (Iterator roles = roleSet.iterator(); roles.hasNext();)
        {
            TurbineSecurity.grant(user, this, (Role) roles.next());
        }
    }

    /**
     * Revokes a Role in this Group from an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Role.
     */
    public void revoke(User user, Role role) throws TurbineSecurityException
    {
        TurbineSecurity.revoke(user, this, role);
    }

    /**
     * Revokes Roles in this group from an User.
     *
     * @param user An User.
     * @param roleSet a RoleSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Roles.
     */
    public void revoke(User user, RoleSet roleSet)
            throws TurbineSecurityException
    {
        for (Iterator roles = roleSet.iterator(); roles.hasNext();)
        {
            TurbineSecurity.revoke(user, this, (Role) roles.next());
        }
    }
}
