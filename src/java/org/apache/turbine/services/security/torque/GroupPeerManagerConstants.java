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

import org.apache.turbine.services.security.torque.om.TurbineGroupPeer;

/**
 * Constants for configuring the various columns and bean properties
 * for the used peer.
 *
 * <pre>
 * Default is:
 *
 * security.torque.groupPeer.class = org.apache.turbine.services.security.torque.om.TurbineGroupPeer
 * security.torque.groupPeer.column.name       = GROUP_NAME
 * security.torque.groupPeer.column.id         = GROUP_ID
 *
 * security.torque.group.class = org.apache.turbine.services.security.torque.om.TurbineGroup
 * security.torque.group.property.name       = Name
 * security.torque.group.property.id         = GroupId
 *
 * </pre>
 * If security.torque.group.class is unset, then the value of the constant CLASSNAME_DEFAULT
 * from the configured Peer is used.
 *
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */

public interface GroupPeerManagerConstants
{
    /** The key within the security service properties for the group class implementation */
    String GROUP_CLASS_KEY =
            "torque.group.class";

    /** The key within the security service properties for the group peer class implementation */
    String GROUP_PEER_CLASS_KEY =
            "torque.groupPeer.class";

    /** Group peer default class */
    String GROUP_PEER_CLASS_DEFAULT =
            TurbineGroupPeer.class.getName();

    /** The column name for the login name field. */
    String GROUP_NAME_COLUMN_KEY =
            "torque.groupPeer.column.name";

    /** The column name for the id field. */
    String GROUP_ID_COLUMN_KEY =
        "torque.groupPeer.column.id";


    /** The default value for the column name constant for the login name field. */
    String GROUP_NAME_COLUMN_DEFAULT =
        "GROUP_NAME";

    /** The default value for the column name constant for the id field. */
    String GROUP_ID_COLUMN_DEFAULT =
        "GROUP_ID";


    /** The property name of the bean property for the login name field. */
    String GROUP_NAME_PROPERTY_KEY =
        "torque.group.property.name";

    /** The property name of the bean property for the id field. */
    String GROUP_ID_PROPERTY_KEY =
        "torque.group.property.id";


    /** The default value of the bean property for the login name field. */
    String GROUP_NAME_PROPERTY_DEFAULT =
        "Name";

    /** The default value of the bean property for the id field. */
    String GROUP_ID_PROPERTY_DEFAULT =
        "GroupId";
}
