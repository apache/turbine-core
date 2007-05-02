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
import org.apache.turbine.util.db.map.TurbineMapBuilder;

/**
 * This class handles all database access for the VISITOR_ROLE table.
 * This table contains all the roles that a given user can play.
 *
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * 
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 * 
 * @version $Id$
 */
public class UserGroupRolePeer extends BasePeer
{
    /** Serial Version UID */
    private static final long serialVersionUID = -9097451661613525751L;

    /** The map builder for this Peer. */
    private static final TurbineMapBuilder MAP_BUILDER;

    /** The table name for this peer. */
    public static final String TABLE_NAME;

    /** The column name for the visitor id field. */
    public static final String USER_ID;

    /** The column name for the group id field. */
    public static final String GROUP_ID;

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

        TABLE_NAME = MAP_BUILDER.getTableUserGroupRole();
        USER_ID = MAP_BUILDER.getUserGroupRole_UserId();
        GROUP_ID = MAP_BUILDER.getUserGroupRole_GroupId();
        ROLE_ID = MAP_BUILDER.getUserGroupRole_RoleId();
    }
    
    /**
     * Get the name of this table.
     *
     * @return A String with the name of the table.
     */
    public static String getTableName()
    {
        return TABLE_NAME;
    }

    /**
     * Returns the full name of a column.
     *
     * @param name name of a column
     * @return A String with the full name of the column.
     */
    public static String getColumnName(String name)
    {
        StringBuffer sb = new StringBuffer();
        sb.append(TABLE_NAME);
        sb.append(".");
        sb.append(name);
        return sb.toString();
    }
}
