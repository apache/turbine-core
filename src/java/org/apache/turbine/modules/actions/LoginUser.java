package org.apache.turbine.modules.actions;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
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

import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.Action;
import org.apache.turbine.om.security.User;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.security.DataBackendException;
import org.apache.turbine.util.security.TurbineSecurityException;

/**
 * This is where we authenticate the user logging into the system
 * against a user in the database. If the user exists in the database
 * that users last login time will be updated.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
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

    /**
     * Updates the user's LastLogin timestamp, sets their state to
     * "logged in" and calls RunData.setUser() .  If the user cannot
     * be authenticated (database error?) the user is assigned
     * anonymous status and, if tr.props contains a TEMPLATE_LOGIN,
     * the screenTemplate is set to this, otherwise the screen is set
     * to SCREEN_LOGIN
     *
     * <p><em>Note: Turbine clears the session before calling this
     * method</em></p>
     *
     * @param     data Turbine information.
     * @exception TurbineSecurityException could not get instance of the
     *            anonymous user
     */
    public void doPerform(RunData data)
            throws TurbineSecurityException
    {
        // This prevents a db hit on second Action call during page
        // generation.  Turbine removes everything from the Session
        // before calling this method, so in this case we should
        // continue on with the Login procedure.
        if (data.getUserFromSession() != null)
        {
            return;
        }

        String username = data.getParameters().getString(CGI_USERNAME, "");
        String password = data.getParameters().getString(CGI_PASSWORD, "");

        try
        {
            // Authenticate the user and get the object.
            User user = TurbineSecurity.getAuthenticatedUser(username, password);

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
            Configuration conf = Turbine.getConfiguration();

            if (e instanceof DataBackendException)
            {
                log.error(e);
            }

            // Set Error Message and clean out the user.
            data.setMessage(conf.getString(TurbineConstants.LOGIN_ERROR, ""));
            data.setUser (TurbineSecurity.getAnonymousUser());

            String loginTemplate = conf.getString(TurbineConstants.TEMPLATE_LOGIN);

            if (StringUtils.isNotEmpty(loginTemplate))
            {
                // We're running in a templating solution
                data.setScreenTemplate(loginTemplate);
            }
            else
            {
                data.setScreen(conf.getString(TurbineConstants.SCREEN_LOGIN));
            }
        }
    }
}
