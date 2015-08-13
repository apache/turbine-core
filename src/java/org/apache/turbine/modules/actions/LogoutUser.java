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

import org.apache.commons.configuration.Configuration;
import org.apache.fulcrum.security.util.FulcrumSecurityException;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;

/**
 * This action removes a user from the session. It makes sure to save
 * the User object in the session.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class LogoutUser
        extends Action
{
    /** Injected service instance */
    @TurbineService
    private SecurityService security;

    /** Injected configuration instance */
    @TurbineConfiguration
    private Configuration conf;

    /**
     * Clears the PipelineData user object back to an anonymous status not
     * logged in, and with a null ACL.  If the tr.props ACTION_LOGIN
     * is anything except "LogoutUser", flow is transfered to the
     * SCREEN_HOMEPAGE
     *
     * If this action name is the value of action.logout then we are
     * being run before the session validator, so we don't need to
     * set the screen (we assume that the session validator will handle
     * that). This is basically still here simply to preserve old behaviour
     * - it is recommended that action.logout is set to "LogoutUser" and
     * that the session validator does handle setting the screen/template
     * for a logged out (read not-logged-in) user.
     *
     * @param data Turbine information.
     * @exception FulcrumSecurityException a problem occurred in the security
     *            service.
     */
    @Override
    public void doPerform(PipelineData pipelineData)
            throws FulcrumSecurityException
    {
        RunData data = getRunData(pipelineData);
        User user = data.getUser();

        if (!security.isAnonymousUser(user))
        {
            // Make sure that the user has really logged in...
            if (!user.hasLoggedIn())
            {
                return;
            }

            user.setHasLoggedIn(Boolean.FALSE);
            security.saveUser(user);
        }

        data.setMessage(conf.getString(TurbineConstants.LOGOUT_MESSAGE));

        // This will cause the acl to be removed from the session in
        // the Turbine servlet code.
        data.setACL(null);

        // Retrieve an anonymous user.
        User anonymousUser = security.getAnonymousUser();
        data.setUser(anonymousUser);
        data.save();

        // In the event that the current screen or related navigations
        // require acl info, we cannot wait for Turbine to handle
        // regenerating acl.
        data.getSession().removeAttribute(TurbineConstants.ACL_SESSION_KEY);

        // If this action name is the value of action.logout then we are
        // being run before the session validator, so we don't need to
        // set the screen (we assume that the session validator will handle
        // that). This is basically still here simply to preserve old behavior
        // - it is recommended that action.logout is set to "LogoutUser" and
        // that the session validator does handle setting the screen/template
        // for a logged out (read not-logged-in) user.
        if (!conf.getString(TurbineConstants.ACTION_LOGOUT_KEY,
                            TurbineConstants.ACTION_LOGOUT_DEFAULT)
            .equals(TurbineConstants.ACTION_LOGOUT_DEFAULT))
        {
            data.setScreen(conf.getString(TurbineConstants.SCREEN_HOMEPAGE));
        }
    }
}
