package org.apache.turbine.modules.actions;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.modules.Action;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.TurbineSecurityException;

import org.apache.turbine.om.security.User;

/**
 * This action doPerforms an Access Control List and places it into
 * the RunData object, so it is easily available to modules.  The ACL
 * is also placed into the session.  Modules can null out the ACL to
 * force it to be rebuilt based on more information.
 *
 * <p>
 *
 * Turbine uses a User-Role-Permission arrangement for access control.
 * Users are assigned Roles.  Roles are assigned Permissions.  Turbine
 * modules then check the Permission required for an action or
 * information with the set of Permissions currently associated with
 * the session (which are dependent on the user associated with the
 * session.)
 *
 * <p>
 *
 * The criteria for assigning Roles/Permissions is application
 * dependent, in some cases an application may change a User's Roles
 * during the session.  To achieve flexibility, the ACL takes an
 * Object parameter, which the application can use to doPerform the
 * ACL.
 *
 * <p>
 *
 * This action is special in that it should only be executed by the
 * Turbine servlet.
 *
 * @version $Id$
 */
public class AccessController
        extends Action
{

    /** Logging */
    private static Log log = LogFactory.getLog(AccessController.class);

    /**
     * If there is a user and the user is logged in, doPerform will
     * set the RunData ACL.  The list is first sought from the current
     * session, otherwise it is loaded through
     * <code>TurbineSecurity.getACL()</code> and added to the current
     * session.
     *
     * @see org.apache.turbine.services.security.TurbineSecurity
     * @param data Turbine information.
     * @exception TurbineSecurityException problem with the security service.
     */
    public void doPerform(RunData data)
            throws TurbineSecurityException
    {
        User user = data.getUser();

        if (!TurbineSecurity.isAnonymousUser(user)
            && user.hasLoggedIn())
        {
            log.debug("Fetching ACL for " + user.getName());
            AccessControlList acl = (AccessControlList)
                    data.getSession().getAttribute(
                            AccessControlList.SESSION_KEY);
            if (acl == null)
            {
                log.debug("No ACL found in Session, building fresh ACL");
                acl = TurbineSecurity.getACL(user);
                data.getSession().setAttribute(
                        AccessControlList.SESSION_KEY, acl);

                log.debug("ACL is " + acl);
            }
            data.setACL(acl);
        }
    }
}
