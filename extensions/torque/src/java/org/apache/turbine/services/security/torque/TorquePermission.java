package org.apache.turbine.services.security.torque;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
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


import org.apache.torque.om.Persistent;

import org.apache.turbine.om.security.Permission;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a permission given to a Role associated with the
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

public class TorquePermission
    extends TorqueObject
    implements Permission,
               Comparable,
               Persistent
{
    /**
     * Constructs a Permission
     */
    public TorquePermission()
    {
      super();
    }

    /**
     * Constructs a new Permission with the sepcified name.
     *
     * @param name The name of the new object.
     */
    public TorquePermission(String name)
    {
        super(name);
    }

    /**
     * The package private Constructor is used when the PermissionPeerManager
     * has retrieved a list of Database Objects from the peer and
     * must 'wrap' them into TorquePermission Objects.
     * You should not use it directly!
     *
     * @param obj An Object from the peer
     */

    public TorquePermission(Persistent obj)
    {
        super(obj);
    }

    /**
     * Returns the underlying Object for the Peer
     *
     * Used in the PermissionPeerManager when building a new Criteria.
     *
     * @return The underlying Persistent Object
     *
     */

    public Persistent getPersistentObj()
    {
        if (obj == null)
        {
            obj = PermissionPeerManager.newPersistentInstance();
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
        return PermissionPeerManager.getPermissionName(getPersistentObj());
    }

    /**
     * Sets the name of this object.
     *
     * @param name The name of the object.
     */
    public void setName(String name)
    {
        PermissionPeerManager.setPermissionName(getPersistentObj(), name);
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public int getId()
    {
        return PermissionPeerManager.getIdAsObj(getPersistentObj()).intValue();
    }

    /**
     * Gets the Id of this object
     *
     * @return The Id of the object
     */
    public Integer getIdAsObj()
    {
        return PermissionPeerManager.getIdAsObj(getPersistentObj());
    }

    /**
     * Sets the Id of this object
     *
     * @param id The new Id
     */
    public void setId(int id)
    {
        PermissionPeerManager.setId(getPersistentObj(), id);
    }

    /**
     * Creates a new Permission in the system.
     *
     * @param name The name of the new Permission.
     * @return An object representing the new Permission.
     * @throws TurbineSecurityException if the Permission could not be created.
     * @deprecated Please use the createPermission method in TurbineSecurity.
     */
    public static Permission create(String name)
        throws TurbineSecurityException
    {
        return TurbineSecurity.createPermission(name);
    }

    /**
     * Makes changes made to the Permission attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    public void save()
        throws TurbineSecurityException
    {
        TurbineSecurity.savePermission(this);
    }

    /**
     * Removes a permission from the system.
     *
     * @throws TurbineSecurityException if the Permission could not be removed.
     */
    public void remove()
        throws TurbineSecurityException
    {
        TurbineSecurity.removePermission(this);
    }

    /**
     * Renames the permission.
     *
     * @param name The new Permission name.
     * @throws TurbineSecurityException if the Permission could not be renamed.
     */
    public void rename(String name)
        throws TurbineSecurityException
    {
        TurbineSecurity.renamePermission(this, name);
    }
}



