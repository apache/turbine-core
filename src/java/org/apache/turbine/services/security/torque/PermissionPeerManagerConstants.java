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

import org.apache.turbine.services.security.torque.om.TurbinePermissionPeer;

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.torque.permissionPeer.class = org.apache.turbine.services.security.torque.om.TurbinePermissionPeer
 * security.torque.permissionPeer.column.name       = PERMISSION_NAME
 * security.torque.permissionPeer.column.id         = PERMISSION_ID
 *
 * security.torque.permission.class = org.apache.turbine.services.security.torque.om.TurbinePermission
 * security.torque.permission.property.name       = Name
 * security.torque.permission.property.id         = PermissionId
 *
 * </pre>
 * If security.torque.permission.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface PermissionPeerManagerConstants
{
    /** The key within the security service properties for the permission class implementation */
    String PERMISSION_CLASS_KEY =
        "torque.permission.class";

    /** The key within the security service properties for the permission peer class implementation */
    String PERMISSION_PEER_CLASS_KEY =
        "torque.permissionPeer.class";

    /** Permission peer default class */
    String PERMISSION_PEER_CLASS_DEFAULT =
        TurbinePermissionPeer.class.getName();

    /** The column name for the login name field. */
    String PERMISSION_NAME_COLUMN_KEY =
        "torque.permissionPeer.column.name";

    /** The column name for the id field. */
    String PERMISSION_ID_COLUMN_KEY =
        "torque.permissionPeer.column.id";


    /** The default value for the column name constant for the login name field. */
    String PERMISSION_NAME_COLUMN_DEFAULT =
        "PERMISSION_NAME";

    /** The default value for the column name constant for the id field. */
    String PERMISSION_ID_COLUMN_DEFAULT =
        "PERMISSION_ID";


    /** The property name of the bean property for the login name field. */
    String PERMISSION_NAME_PROPERTY_KEY =
        "torque.permission.property.name";

    /** The property name of the bean property for the id field. */
    String PERMISSION_ID_PROPERTY_KEY =
        "torque.permission.property.id";


    /** The default value of the bean property for the login name field. */
    String PERMISSION_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    String PERMISSION_ID_PROPERTY_DEFAULT =
        "PermissionId";

}
