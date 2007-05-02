package org.apache.turbine.om.security.peer;

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

/**
 * Peer classes for an User object need to implement this interface.
 *
 * The purpose of this class is to decouple DBSecurityService from
 * the default User implementation and it's related Peer class.
 *
 * @author <a href="mailto:leandro@ibnetwork.com.br">Leandro Rodrigo Saad Cruz</a>
 *
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 *
 * @version $Id$
 */
public interface UserPeer
{
    /** The key name for the visitor id field. */
    String USERNAME = "LOGIN_NAME";

    /** The key name for the username field. */
    String USER_ID = "USER_ID";

    /**
     * Returns the full name of a column.
     *
     * @param name name of a column
     * @return A String with the full name of the column.
     */
    String getFullColumnName(String name);
}
