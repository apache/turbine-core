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

import org.apache.turbine.util.security.PermissionSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a role played by the User associated with the
 * current Session.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface Role extends SecurityEntity
{
    /**
     * Returns the set of Permissions associated with this Role.
     *
     * @return A PermissionSet.
     * @exception Exception A generic exception.
     */
    PermissionSet getPermissions()
        throws Exception;

    /**
     * Sets the Permissions associated with this Role.
     *
     * @param permissionSet A PermissionSet.
     */
    void setPermissions(PermissionSet permissionSet);

    // These following methods are wrappers around TurbineSecurity

    /**
     * Creates a new Role in the system.
     *
     * @param name The name of the new Role.
     * @return An object representing the new Role.
     * @throws TurbineSecurityException if the Role could not be created.
     */
    Role create(String name)
        throws TurbineSecurityException;

    /**
     * Makes changes made to the Role attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    void save()
        throws TurbineSecurityException;

    /**
     * Removes a role from the system.
     *
     * @throws TurbineSecurityException if the Role could not be removed.
     */
    void remove()
        throws TurbineSecurityException;

    /**
     * Renames the role.
     *
     * @param name The new Role name.
     * @throws TurbineSecurityException if the Role could not be renamed.
     */
    void rename(String name)
        throws TurbineSecurityException;

    /**
     * Grants a Permission to this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permission.
     */
    void grant(Permission permission)
        throws TurbineSecurityException;

    /**
     * Grants Permissions from a PermissionSet to this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Permissions.
     */
    void grant(PermissionSet permissionSet)
        throws TurbineSecurityException;

    /**
     * Revokes a Permission from this Role.
     *
     * @param permission A Permission.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permission.
     */
    void revoke(Permission permission)
        throws TurbineSecurityException;

    /**
     * Revokes Permissions from a PermissionSet from this Role.
     *
     * @param permissionSet A PermissionSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Permissions.
     */
    void revoke(PermissionSet permissionSet)
        throws TurbineSecurityException;
}
