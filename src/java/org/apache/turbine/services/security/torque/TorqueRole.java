package org.apache.turbine.services.security.torque;

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

import java.sql.Connection;

import java.util.Iterator;

import org.apache.turbine.services.security.TurbineSecurity;

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.om.security.Role;

import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.TurbineSecurityException;

import org.apache.torque.om.Persistent;

/**
 * This class represents a role played by the User associated with the
 * current Session. It is separated from the actual Torque peer object
 * to be able to replace the Peer with an user supplied Peer (and Object)
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:jmcnally@collab.net">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public class TorqueRole
    extends TorqueObject
    implements Role,
               Comparable,
               Persistent
{
    /** The permissions for this role. */
    private PermissionSet permissionSet = null;

    /**
     * Constructs a new Role
     */
    public TorqueRole()
    {
        super();
    }

    /**
     * Constructs a new Role with the specified name.
     *
     * @param name The name of the new object.
     */
    public TorqueRole(String name)
    {
        super(name);
    }

    /**
     * The package private Constructor is used when the RolePeerManager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into TorqueRole Objects. You should not use it directly!
     *
     * @param obj An Object from the peer
     */
    public TorqueRole(Persistent obj)
    {
        super(obj);
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * Used in the RolePeerManager when building a new Criteria.
     *
     * @return The underlying persistent object
     *
     */

    public Persistent getPersistentObj()
    {
        if (obj == null)
        {
            obj = RolePeerManager.newPersistentInstance();
        }
        return obj;
    }

    /**
     * Returns the name of this role.
     *
     * @return The name of the role.
     */
    public String getName()
    {
        return RolePeerManager.getRoleName(getPersistentObj());
    }

    /**
     * Sets the name of this Role
     *
     * @param name The name of the role.
     */
    public void setName(String name)
    {
        RolePeerManager.setRoleName(getPersistentObj(), name);
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public int getId()
    {
        return RolePeerManager.getIdAsObj(getPersistentObj()).intValue();
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public Integer getIdAsObj()
    {
        return RolePeerManager.getIdAsObj(getPersistentObj());
    }

    /**
     * Sets the Id of this object
     *
     * @param id The new Id 
     */
    public void setId(int id)
    {
        RolePeerManager.setId(getPersistentObj(), id);
    }
    /**
     * Returns the set of Permissions associated with this Role.
     *
     * @return A PermissionSet.
     *
     * @exception Exception a generic exception.
     */
    public PermissionSet getPermissions()
        throws Exception
    {
        return permissionSet;
    }

    /**
     * Sets the Permissions associated with this Role.
     *
     * @param permissionSet A PermissionSet.
     */
    public void setPermissions(PermissionSet permissionSet)
    {
        this.permissionSet = permissionSet;
    }

    // These following methods are wrappers around TurbineSecurity

    /**
     * Creates a new Role in the system.
     *
     * @param name The name of the new Role.
     * @return An object representing the new Role.
     * @throws TurbineSecurityException if the Role could not be created.
     */
    public Role create(String name)
        throws TurbineSecurityException
    {
        return TurbineSecurity.createRole(name);
    }

    /**
     * Makes changes made to the Role attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
        throws TurbineSecurityException
    {
        TurbineSecurity.saveRole(this);
    }

    /**
     * Removes a role from the system.
     *
     * @throws TurbineSecurityException if the Role could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        TurbineSecurity.removeRole(this);
    }

    /**
     * Renames the role.
     *
     * @param name The new Role name.
     * @throws TurbineSecurityException if the Role could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
    {
        TurbineSecurity.renameRole(this, name);
    }

    /**
     * Grants a Permission to this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permission.
     */
    public void grant(Permission permission)
        throws TurbineSecurityException
    {
        TurbineSecurity.grant(this, permission);
    }

    /**
     * Grants Permissions from a PermissionSet to this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permissions.
     */
    public void grant(PermissionSet permissionSet)
        throws TurbineSecurityException
    {
        Iterator permissions = permissionSet.iterator();
        while (permissions.hasNext())
        {
            TurbineSecurity.grant(this, (Permission) permissions.next());
        }
    }

    /**
     * Revokes a Permission from this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permission.
     */
    public void revoke(Permission permission)
        throws TurbineSecurityException
    {
        TurbineSecurity.revoke(this, permission);
    }

    /**
     * Revokes Permissions from a PermissionSet from this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permissions.
     */
    public void revoke(PermissionSet permissionSet)
        throws TurbineSecurityException
    {
        Iterator permissions = permissionSet.iterator();
        while (permissions.hasNext())
        {
            TurbineSecurity.revoke(this, (Permission) permissions.next());
        }
    }
}
