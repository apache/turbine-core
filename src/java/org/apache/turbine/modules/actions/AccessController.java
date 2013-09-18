package org.apache.turbine.modules.actions;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.acl.AccessControlList;
import org.apache.fulcrum.security.util.FulcrumSecurityException;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;

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
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:bmclaugh@algx.net">Brett McLaughlin</a>
 * @author <a href="quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class AccessController
        extends Action
{

    /** Logging */
    private static Log log = LogFactory.getLog(AccessController.class);

    /** Injected service instance */
    @TurbineService
    private SecurityService security;

    /**
     * If there is a user and the user is logged in, doPerform will
     * set the RunData ACL.  The list is first sought from the current
     * session, otherwise it is loaded through
     * <code>TurbineSecurity.getACL()</code> and added to the current
     * session.
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception FulcrumSecurityException problem with the security service.
     */
    @Deprecated
    @Override
    public void doPerform(RunData data)
            throws FulcrumSecurityException
    {
        User user = data.getUser();

        if (!security.isAnonymousUser(user)
            && user.hasLoggedIn())
        {
            log.debug("Fetching ACL for " + user.getName());
            AccessControlList acl = (AccessControlList)
                    data.getSession().getAttribute(
                            TurbineConstants.ACL_SESSION_KEY);
            if (acl == null)
            {
                log.debug("No ACL found in Session, building fresh ACL");
                acl = security.getACL(user);
                data.getSession().setAttribute(
                        TurbineConstants.ACL_SESSION_KEY, acl);

                log.debug("ACL is " + acl);
            }
            data.setACL(acl);
        }
    }

    /**
     * If there is a user and the user is logged in, doPerform will
     * set the RunData ACL.  The list is first sought from the current
     * session, otherwise it is loaded through
     * <code>TurbineSecurity.getACL()</code> and added to the current
     * session.
     *
     * @param data Turbine information.
     * @exception FulcrumSecurityException problem with the security service.
     */
    @Override
    public void doPerform(PipelineData pipelineData)
    	throws FulcrumSecurityException
    {
        RunData data = getRunData(pipelineData);
        doPerform(data);

        // Comply with Turbine 4.0 standards
        pipelineData.get(Turbine.class).put(AccessControlList.class, data.getACL());
    }
}
