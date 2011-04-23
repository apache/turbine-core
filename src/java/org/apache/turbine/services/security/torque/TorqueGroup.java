package org.apache.turbine.services.security.torque;

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

import java.util.Iterator;

import org.apache.torque.om.Persistent;

import org.apache.turbine.om.security.Group;
import org.apache.turbine.om.security.Role;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a Group of Users in the system that are associated
 * with specific entity or resource. The users belonging to the Group may
 * have various Roles. The Permissions to perform actions upon the resource
 * depend on the Roles in the Group that they are assigned. It is separated
 * from the actual Torque peer object to be able to replace the Peer with an
 * user supplied Peer (and Object)
 *
 * <a name="global">
 * <p> Certain Roles that the Users may have in the system are not related
 * to any specific resource nor entity.
 * They are assigned within a special group named 'global' that can be
 * referenced in the code as {@link #GLOBAL_GROUP_NAME}.
 * <br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TorqueGroup
    extends TorqueObject
    implements Group,
               Comparable
{

	/** Serial Version UID */
	private static final long serialVersionUID = -2034684697021752888L;

    /**
     * Constructs a new Group.
     */
    public TorqueGroup()
    {
        super();
    }

    /**
     * Constructs a new Group with the specified name.
     *
     * @param name The name of the new object.
     */

    public TorqueGroup(String name)
    {
        super(name);
    }

    /**
     * The package private Constructor is used when the GroupPeerManager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into TorqueGroup Objects.
     * You should not use it directly!
     *
     * @param obj An Object from the peer
     */
    public TorqueGroup(Persistent obj)
    {
        super(obj);
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * Used in the GroupPeerManager when building a new Criteria.
     *
     * @return The underlying persistent object
     *
     */

    public Persistent getPersistentObj()
    {
        if (obj == null)
        {
            obj = GroupPeerManager.newPersistentInstance();
        }
        return obj;
    }

    /**
     * Returns the name of this object.
     *
     * @return The name of the object.
     */
    public String getName()
    {
        return GroupPeerManager.getGroupName(getPersistentObj());
    }

    /**
     * Sets the name of this object.
     *
     * @param name The name of the object.
     */
    public void setName(String name)
    {
        GroupPeerManager.setGroupName(getPersistentObj(), name);
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public int getId()
    {
        return GroupPeerManager.getIdAsObj(getPersistentObj()).intValue();
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public Integer getIdAsObj()
    {
        return GroupPeerManager.getIdAsObj(getPersistentObj());
    }

    /**
     * Sets the Id of this object
     *
     * @param id The new Id
     */
    public void setId(int id)
    {
        GroupPeerManager.setId(getPersistentObj(), id);
    }

    /**
     * Provides a reference to the Group object that represents the
     * <a href="#global">global group</a>.
     *
     * @return a Group object that represents the global group.
     * @deprecated Please use the method in TurbineSecurity now.
     */
    public static Group getGlobalGroup()
    {
        return TurbineSecurity.getGlobalGroup();
    }

    /**
     * Creates a new Group in the system.
     *
     * @param name The name of the new Group.
     * @return An object representing the new Group.
     * @throws TurbineSecurityException if the Group could not be created.
     * @deprecated Please use the createGroup method in TurbineSecurity now.
     */
    public static Group create(String name)
        throws TurbineSecurityException
    {
        return TurbineSecurity.createGroup(name);
    }

    // These following methods are wrappers around TurbineSecurity

    /**
     * Makes changes made to the Group attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
        throws TurbineSecurityException
    {
        TurbineSecurity.saveGroup(this);
    }

    /**
     * Removes a group from the system.
     *
     * @throws TurbineSecurityException if the Group could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        TurbineSecurity.removeGroup(this);
    }

    /**
     * Renames the role.
     *
     * @param name The new Group name.
     * @throws TurbineSecurityException if the Group could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
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
    public void grant(User user, Role role)
        throws TurbineSecurityException
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
        Iterator roles = roleSet.iterator();
        while (roles.hasNext())
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
    public void revoke(User user, Role role)
        throws TurbineSecurityException
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
        Iterator roles = roleSet.iterator();
        while (roles.hasNext())
        {
            TurbineSecurity.revoke(user, this, (Role) roles.next());
        }
    }

}

