package org.apache.turbine.om.security;


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


import org.apache.turbine.util.security.RoleSet;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents a Group of Users in the system that are associated
 * with specific entity or resource. The users belonging to the Group may have
 * various Roles. The Permissions to perform actions upon the resource depend
 * on the Roles in the Group that they are assigned.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface Group extends SecurityEntity
{
    /**
     * The name of the <a href="#global">global group</a>
     */
    String GLOBAL_GROUP_NAME = "global";

    /**
     * Makes changes made to the Group attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    void save()
        throws TurbineSecurityException;

    /**
     * Removes a group from the system.
     *
     * @throws TurbineSecurityException if the Group could not be removed.
     */
    void remove()
        throws TurbineSecurityException;

    /**
     * Renames the role.
     *
     * @param name The new Group name.
     * @throws TurbineSecurityException if the Group could not be renamed.
     */
    void rename(String name)
        throws TurbineSecurityException;

    /**
     * Grants a Role in this Group to an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Role.
     */
    void grant(User user, Role role)
        throws TurbineSecurityException;

    /**
     * Grants Roles in this Group to an User.
     *
     * @param user An User.
     * @param roleSet A RoleSet.
     * @throws TurbineSecurityException if there is a problem while assigning
     * the Roles.
     */
    void grant(User user, RoleSet roleSet)
        throws TurbineSecurityException;

    /**
     * Revokes a Role in this Group from an User.
     *
     * @param user An User.
     * @param role A Role.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Role.
     */
    void revoke(User user, Role role)
        throws TurbineSecurityException;

    /**
     * Revokes Roles in this group from an User.
     *
     * @param user An User.
     * @param roleSet a RoleSet.
     * @throws TurbineSecurityException if there is a problem while unassigning
     * the Roles.
     */
    void revoke(User user, RoleSet roleSet)
        throws TurbineSecurityException;

}
