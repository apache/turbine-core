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

import org.apache.turbine.services.security.torque.om.TurbineRolePeer;

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.torque.rolePeer.class = org.apache.turbine.services.security.torque.om.TurbineRolePeer
 * security.torque.rolePeer.column.name       = ROLE_NAME
 * security.torque.rolePeer.column.id         = ROLE_ID
 *
 * security.torque.role.class = org.apache.turbine.services.security.torque.om.TurbineRole
 * security.torque.role.property.name       = Name
 * security.torque.role.property.id         = RoleId
 *
 * </pre>
 * If security.torque.role.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface RolePeerManagerConstants
{
    /** The key within the security service properties for the role class implementation */
    String ROLE_CLASS_KEY =
        "torque.role.class";

    /** The key within the security service properties for the role peer class implementation */
    String ROLE_PEER_CLASS_KEY =
        "torque.rolePeer.class";

    /** Role peer default class */
    String ROLE_PEER_CLASS_DEFAULT =
            TurbineRolePeer.class.getName();

    /** The column name for the login name field. */
    String ROLE_NAME_COLUMN_KEY =
        "torque.rolePeer.column.name";

    /** The column name for the id field. */
    String ROLE_ID_COLUMN_KEY =
        "torque.rolePeer.column.id";


    /** The default value for the column name constant for the login name field. */
    String ROLE_NAME_COLUMN_DEFAULT =
        "ROLE_NAME";

    /** The default value for the column name constant for the id field. */
    String ROLE_ID_COLUMN_DEFAULT =
        "ROLE_ID";


    /** The property name of the bean property for the login name field. */
    String ROLE_NAME_PROPERTY_KEY =
        "torque.role.property.name";

    /** The property name of the bean property for the id field. */
    String ROLE_ID_PROPERTY_KEY =
        "torque.role.property.id";


    /** The default value of the bean property for the login name field. */
    String ROLE_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    String ROLE_ID_PROPERTY_DEFAULT =
        "RoleId";

}
