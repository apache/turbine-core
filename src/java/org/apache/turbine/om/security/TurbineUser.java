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

import org.apache.turbine.services.security.torque.TorqueUser;

/**
 * A generic implementation of User interface.
 *
 * This basic implementation contains the functionality that is
 * expected to be common among all User implementations.
 * You are welcome to extend this class if you wish to have
 * custom functionality in your user objects (like accessor methods
 * for custom attributes). <b>Note</b> that implementing a different scheme
 * of user data storage normally involves writing an implementation of
 * {@link org.apache.turbine.services.security.UserManager} interface.
 *
 * @author <a href="mailto:josh@stonecottage.com">Josh Lucas</a>
 * @author <a href="mailto:jon@collab.net">Jon S. Stevens</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:frank.kim@clearink.com">Frank Y. Kim</a>
 * @author <a href="mailto:cberry@gluecode.com">Craig D. Berry</a>
 * @author <a href="mailto:epugh@upstate.com">Eric Pugh</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 *
 * @deprecated Use {@link org.apache.turbine.services.security.torque.TorqueUser}
 * instead.
 *
 * @version $Id$
 */
public class TurbineUser extends TorqueUser
{
    /** Serial Version UID */
    private static final long serialVersionUID = -6090627713197456117L;
}
