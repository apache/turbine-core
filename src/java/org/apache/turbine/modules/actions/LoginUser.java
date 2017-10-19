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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fulcrum.security.util.DataBackendException;
import org.apache.fulcrum.security.util.FulcrumSecurityException;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * This is where we authenticate the user logging into the system
 * against a user in the database. If the user exists in the database
 * that users last login time will be updated.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:quintonm@bellsouth.net">Quinton McCombs</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class LoginUser
        extends Action
{
    /** CGI Parameter for the user name */
    public static final String CGI_USERNAME = "username";

    /** CGI Parameter for the password */
    public static final String CGI_PASSWORD = "password";

    /** Logging */
    private static Log log = LogFactory.getLog(LoginUser.class);

    /** Injected service instance */
    @TurbineService
    private SecurityService security;

    @TurbineConfiguration( TurbineConstants.LOGIN_ERROR )
    private String loginError = "";

    @TurbineConfiguration( TurbineConstants.TEMPLATE_LOGIN )
    private String templateLogin;

    @TurbineConfiguration( TurbineConstants.SCREEN_LOGIN )
    private String screenLogin;

    /**
     * Updates the user's LastLogin timestamp, sets their state to
     * "logged in" and calls RunData.setUser() .  If the user cannot
     * be authenticated (database error?) the user is assigned
     * anonymous status and, if tr.props contains a TEMPLATE_LOGIN,
     * the screenTemplate is set to this, otherwise the screen is set
     * to SCREEN_LOGIN
     *
     * @param     pipelineData Turbine information.
     * @throws FulcrumSecurityException could not get instance of the
     *            anonymous user
     */
    @Override
    public void doPerform(PipelineData pipelineData)
            throws FulcrumSecurityException
    {
        RunData data = getRunData(pipelineData);
        String username = data.getParameters().getString(CGI_USERNAME, "");
        String password = data.getParameters().getString(CGI_PASSWORD, "");

        if (StringUtils.isEmpty(username))
        {
            return;
        }

        try
        {
        	if (username.equals(security.getAnonymousUser().getName()))
            {
                throw new TurbineException("Anonymous user cannot login");
            }

            // Authenticate the user and get the object.
            User user = security.getAuthenticatedUser(username, password);

            // Store the user object.
            data.setUser(user);

            // Mark the user as being logged in.
            user.setHasLoggedIn(Boolean.TRUE);

            // Set the last_login date in the database.
            user.updateLastLogin();

            // This only happens if the user is valid; otherwise, we
            // will get a valueBound in the User object when we don't
            // want to because the username is not set yet.  Save the
            // User object into the session.
            data.save();

            /*
             * If the setPage("template.vm") method has not
             * been used in the template to authenticate the
             * user (usually Login.vm), then the user will
             * be forwarded to the template that is specified
             * by the "template.home" property as listed in
             * TR.props for the webapp.
             */

        }
        catch (Exception e)
        {
            if (e instanceof DataBackendException)
            {
                log.error(e);
            }

            // Set Error Message and clean out the user.
            data.setMessage(loginError);
            User anonymousUser = security.getAnonymousUser();
            data.setUser(anonymousUser);

            if (StringUtils.isNotEmpty(templateLogin))
            {
                // We're running in a templating solution
                data.setScreenTemplate(templateLogin);
            }
            else
            {
                data.setScreen(screenLogin);
            }
        }
    }

}
