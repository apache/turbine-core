package org.apache.turbine.om.security.peer;

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

import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.turbine.util.db.map.TurbineMapBuilder;

/**
 * This class handles all database access for the
 * ROLE_PERMISSION table.  This table contains all
 * the permissions for a given role.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * 
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 * 
 * @version $Id$
 */
public class RolePermissionPeer extends BasePeer
{
    /** Serial Version UID */
    private static final long serialVersionUID = 4149656810524167640L;

   /** The map builder for this Peer. */
    private static final TurbineMapBuilder MAP_BUILDER;

    /** The table name for this peer. */
    public static final String TABLE_NAME;

    /** The column name for the permission id field. */
    public static final String PERMISSION_ID;

    /** The column name for the role id field. */
    public static final String ROLE_ID;


    static
    {
        try
        {
            MAP_BUILDER = (TurbineMapBuilder)/* Torque. */getMapBuilder(TurbineMapBuilder.class.getName());
        }
        catch (TorqueException e)
        {
            log.error("Could not initialize Peer", e);
            throw new RuntimeException(e);
        }

        TABLE_NAME = MAP_BUILDER.getTableRolePermission();
        PERMISSION_ID = MAP_BUILDER.getRolePermission_PermissionId();
        ROLE_ID = MAP_BUILDER.getRolePermission_RoleId();
    }
    
    /**
     * Deletes the mappings for a role_id.
     *
     * @param role_id An int with the role id.
     * @exception Exception a generic exception.
     */
    public static void deleteRole(int role_id) throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(ROLE_ID, role_id);
        doDelete(criteria);
    }

    /**
     * Deletes the mappings for a permission_id.
     *
     * @param permission_id An int with the permission id.
     * @exception Exception a generic exception.
     */
    public static void deletePermission(int permission_id) throws Exception
    {
        Criteria criteria = new Criteria();
        criteria.add(PERMISSION_ID, permission_id);
        doDelete(criteria);
    }
}
