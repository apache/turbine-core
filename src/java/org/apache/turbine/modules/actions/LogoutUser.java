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

import org.apache.commons.configuration.Configuration;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This action removes a user from the session. It makes sure to save
 * the User object in the session.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class LogoutUser
        extends Action
{
    /**
     * Clears the RunData user object back to an anonymous status not
     * logged in, and with a null ACL.  If the tr.props ACTION_LOGIN
     * is anthing except "LogoutUser", flow is transfered to the
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
     * @exception TurbineSecurityException a problem occured in the security
     *            service.
     */
    public void doPerform(RunData data)
            throws TurbineSecurityException
    {
        User user = data.getUser();

        if (!TurbineSecurity.isAnonymousUser(user))
        {
            // Make sure that the user has really logged in...
            if (!user.hasLoggedIn())
            {
                return;
            }

            user.setHasLoggedIn(Boolean.FALSE);
            TurbineSecurity.saveUser(user);
        }

        Configuration conf = Turbine.getConfiguration();

        data.setMessage(conf.getString(TurbineConstants.LOGOUT_MESSAGE));

        // This will cause the acl to be removed from the session in
        // the Turbine servlet code.
        data.setACL(null);

        // Retrieve an anonymous user.
        data.setUser(TurbineSecurity.getAnonymousUser());
        data.save();

        // In the event that the current screen or related navigations
        // require acl info, we cannot wait for Turbine to handle
        // regenerating acl.
        data.getSession().removeAttribute(AccessControlList.SESSION_KEY);

        // If this action name is the value of action.logout then we are
        // being run before the session validator, so we don't need to
        // set the screen (we assume that the session validator will handle
        // that). This is basically still here simply to preserve old behaviour
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
