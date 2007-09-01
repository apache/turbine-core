package org.apache.turbine.services.security.db;

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

import org.apache.turbine.om.security.peer.UserPeer;
import org.apache.turbine.services.security.torque.TorqueSecurityService;
import org.apache.turbine.util.security.UnknownEntityException;

/**
 * An implementation of SecurityService that uses a database as backend.
 *
 * @author <a href="mailto:Rafal.Krzewski@e-point.pl">Rafal Krzewski</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:marco@intermeta.de">Marco Kn&uuml;ttel</a>
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
 * instead.
 * @version $Id$
 */
public class DBSecurityService
        extends TorqueSecurityService
{
    /**
     * The key within services's properties for user implementation
     * classname (user.class)  - Leandro
     */
    public static final String USER_PEER_CLASS_KEY = "userPeer.class";

    /**
     * The default implementation of User interface
     * (org.apache.turbine.om.security.DBUser)
     */
    public static final String USER_PEER_CLASS_DEFAULT =
            "org.apache.turbine.om.security.peer.TurbineUserPeer";

    /* Service specific implementation methods */

    /**
     * Returns the Class object for the implementation of UserPeer interface
     * used by the system (defined in TR.properties)
     *
     * @return the implementation of UserPeer interface used by the system.
     * @throws UnknownEntityException if the system's implementation of UserPeer
     *         interface could not be determined.
     * @deprecated No replacement. Use 
     * {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
     * instead.
     */
    public Class getUserPeerClass() throws UnknownEntityException
    {
        String userPeerClassName = getConfiguration().getString(
                USER_PEER_CLASS_KEY, USER_PEER_CLASS_DEFAULT);
        try
        {
            return Class.forName(userPeerClassName);
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed create a Class object for UserPeer implementation", e);
        }
    }

    /**
     * Construct a UserPeer object.
     *
     * This method calls getUserPeerClass, and then creates a new object using
     * the default constructor.
     *
     * @return an object implementing UserPeer interface.
     * @throws UnknownEntityException if the object could not be instantiated.
     * @deprecated No replacement. Use 
     * {@link org.apache.turbine.services.security.torque.TorqueSecurityService}
     * instead.
     */
    public UserPeer getUserPeerInstance() throws UnknownEntityException
    {
        UserPeer up;
        try
        {
            up = (UserPeer) getUserPeerClass().newInstance();
        }
        catch (Exception e)
        {
            throw new UnknownEntityException(
                    "Failed instantiate an UserPeer implementation object", e);
        }
        return up;
    }
}
