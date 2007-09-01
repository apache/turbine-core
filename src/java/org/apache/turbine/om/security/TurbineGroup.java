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

import org.apache.turbine.services.security.torque.TorqueGroup;

/**
 * This class represents a Group of Users in the system that are associated
 * with specific entity or resource. The users belonging to the Group may have
 * various Roles. The Permissions to perform actions upon the resource depend
 * on the Roles in the Group that they are assigned.
 *
 * <a name="global">
 * <p> Certain Roles that the Users may have in the system may are not related
 * to any specific resource nor entity.
 * They are assigned within a special group named 'global' that can be
 * referenced in the code as {@link #GLOBAL_GROUP_NAME}.
 * <br>
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 *
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueGroup}
 * instead.
 *
 * @version $Id$
 */
public class TurbineGroup extends TorqueGroup
{
    /** Serial Version UID */
    private static final long serialVersionUID = -6034684697021752649L;
}
