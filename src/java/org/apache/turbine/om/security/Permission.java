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

import java.io.Serializable;

import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This class represents the permissions that a Role has to access
 * certain pages/functions within the system.  The class implements
 * Comparable so that when Permissions are added to a Set, they will
 * be in alphabetical order by name.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public interface Permission extends SecurityEntity, Serializable
{
    /**
     * Makes changes made to the Permission attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while
     *  saving data.
     */
    void save()
        throws TurbineSecurityException;

    /**
     * Removes a permission from the system.
     *
     * @throws TurbineSecurityException if the Permission could not be removed.
     */
    void remove()
        throws TurbineSecurityException;

    /**
     * Renames the permission.
     *
     * @param name The new Permission name.
     * @throws TurbineSecurityException if the Permission could not be renamed.
     */
    void rename(String name)
        throws TurbineSecurityException;
}
