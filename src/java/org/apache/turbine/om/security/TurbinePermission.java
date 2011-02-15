package org.apache.turbine.om.security;


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


import java.sql.Connection;

import org.apache.turbine.services.security.TurbineSecurity;
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
 * @version $Id$
 */
public class TurbinePermission extends SecurityObject<Permission> implements Permission
{
    /**
     * Constructs a new TurbinePermission.
     */
    public TurbinePermission()
    {
        super();
    }

    /**
     * Constructs a new TurbinePermission with the sepcified name.
     *
     * @param name The name of the new object.
     */
    public TurbinePermission(String name)
    {
        super(name);
    }

    /**
     * Makes changes made to the Permission attributes permanent.
     *
     * @throws TurbineSecurityException if there is a problem while saving data.
     */
    public void save() throws TurbineSecurityException
    {
        TurbineSecurity.savePermission(this);
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
     * Removes a permission from the system.
     *
     * @throws TurbineSecurityException if the Permission could not be removed.
     */
    public void remove() throws TurbineSecurityException
    {
        TurbineSecurity.removePermission(this);
    }

    /**
     * Renames the permission.
     *
     * @param name The new Permission name.
     * @throws TurbineSecurityException if the Permission could not be renamed.
     */
    public void rename(String name) throws TurbineSecurityException
    {
        TurbineSecurity.renamePermission(this, name);
    }
}
