package org.apache.turbine.modules.actions;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Turbine" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.resources.TurbineResources;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.AccessControlList;

/**
 * This action removes a user from the session. It makes sure to save
 * the User object in the session.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public class LogoutUser extends Action
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
     * @exception Exception, a generic exception.
     */
    public void doPerform(RunData data)
            throws Exception
    {
        User user = data.getUser();

        if (user != null)
        {
            // Make sure that the user has really logged in...
            if (!user.hasLoggedIn())
            {
                return;
            }

            user.setHasLoggedIn(new Boolean(false));
            TurbineSecurity.saveUser(user);
        }

        data.setMessage(TurbineResources.getString(
                TurbineConstants.LOGOUT_MESSAGE));

        // This will cause the acl to be removed from the session in
        // the Turbine servlet code.
        data.setACL(null);

        // Retrieve an anonymous user.
        data.setUser(TurbineSecurity.getAnonymousUser());
        data.save();

        // In the event that the current screen or related navigations
        // require acl info, we cannot wait for Turbine to handle
        // regenerating acl.
        data.getSession().removeValue(AccessControlList.SESSION_KEY);

        // If this action name is the value of action.logout then we are
        // being run before the session validator, so we don't need to
        // set the screen (we assume that the session validator will handle
        // that). This is basically still here simply to preserve old behaviour
        // - it is recommended that action.logout is set to "LogoutUser" and
        // that the session validator does handle setting the screen/template
        // for a logged out (read not-logged-in) user.
        if (!TurbineResources.getString(TurbineConstants.ACTION_LOGOUT, "")
                .equals("LogoutUser"))
        {
            data.setScreen(TurbineResources.getString(
                    TurbineConstants.SCREEN_HOMEPAGE));
        }
    }
}
