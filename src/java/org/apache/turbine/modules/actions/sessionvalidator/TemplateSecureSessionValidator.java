package org.apache.turbine.modules.actions.sessionvalidator;

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

import org.apache.commons.lang.StringUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.TurbineSecurity;

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * SessionValidator that requires login for use with Template Services
 * like Velocity or WebMacro.
 *
 * <br>
 *
 * Templating services requires a different Session Validator
 * because of the way it handles screens.  If you use the WebMacro or
 * Velocity Service with the DefaultSessionValidator, users will be able to
 * bypass login by directly addressing the template using
 * template/index.wm.  This is because the Page class looks for the
 * keyword "template" in the Path information and if it finds it will
 * reset the screen using it's lookup mechanism and thereby bypass
 * Login.
 *
 * Note that you will need to set the template.login property to the
 * login template.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TemplateSecureSessionValidator
    extends SessionValidator
{
    /** Logging */
    private static Log log = LogFactory.getLog(
            TemplateSecureSessionValidator.class);

    /**
     * doPerform is virtually identical to DefaultSessionValidator
     * except that it calls template methods instead of bare screen
     * methods. For example, it uses <code>setScreenTemplate</code> to
     * load the tr.props TEMPLATE_LOGIN instead of the default's
     * setScreen to TurbineConstants.SCREEN_LOGIN.
     * @deprecated Use PipelineData version instead.
     * @see DefaultSessionValidator
     * @param data Turbine information.
     * @throws TurbineException The anonymous user could not be obtained
     *         from the security service
     */
    public void doPerform(RunData data)
            throws TurbineException
    {
        Configuration conf = Turbine.getConfiguration();

        // Pull user from session.
        data.populate();

        // The user may have not logged in, so create a "guest/anonymous" user.
        if (data.getUser() == null)
        {
            log.debug("Fixing up empty User Object!");
            data.setUser(TurbineSecurity.getAnonymousUser());
            data.save();
        }

        // This is the secure sessionvalidator, so user must be logged in.
        if (!data.getUser().hasLoggedIn())
        {
            log.debug("User is not logged in!");

            // only set the message if nothing else has already set it
            // (e.g. the LogoutUser action).
            if (StringUtils.isEmpty(data.getMessage()))
            {
                data.setMessage(conf.getString(TurbineConstants.LOGIN_MESSAGE));
            }

            // Set the screen template to the login page.
            String loginTemplate =
                conf.getString(TurbineConstants.TEMPLATE_LOGIN);

            log.debug("Sending User to the Login Screen ("
                    + loginTemplate + ")");
            data.getTemplateInfo().setScreenTemplate(loginTemplate);

            // We're not doing any actions buddy! (except action.login which
            // will have been performed already)
            data.setAction(null);
        }

        log.debug("Login Check finished!");

        // Make sure we have some way to return a response.
        if (!data.hasScreen() && StringUtils.isEmpty(
                data.getTemplateInfo().getScreenTemplate()))
        {
            String template = conf.getString(
                    TurbineConstants.TEMPLATE_HOMEPAGE);

            if (StringUtils.isNotEmpty(template))
            {
                data.getTemplateInfo().setScreenTemplate(template);
            }
            else
            {
                data.setScreen(conf.getString(
                        TurbineConstants.SCREEN_HOMEPAGE));
            }
        }

        // The session_access_counter can be placed as a hidden field in
        // forms.  This can be used to prevent a user from using the
        // browsers back button and submitting stale data.
        // FIXME!! a template needs to be written to use this with templates.

        if (data.getParameters().containsKey("_session_access_counter")
                && !TurbineSecurity.isAnonymousUser(data.getUser()))
        {
            // See comments in screens.error.InvalidState.
            if (data.getParameters().getInt("_session_access_counter")
                    < (((Integer) data.getUser().getTemp(
                    "_session_access_counter")).intValue() - 1))
            {
                if (data.getTemplateInfo().getScreenTemplate() != null)
                {
                    data.getUser().setTemp("prev_template",
                            data.getTemplateInfo().getScreenTemplate()
                            .replace('/', ','));
                    data.getTemplateInfo().setScreenTemplate(conf.getString(
                            TurbineConstants.TEMPLATE_INVALID_STATE));
                }
                else
                {
                    data.getUser().setTemp("prev_screen",
                                           data.getScreen().replace('/', ','));
                    data.setScreen(conf.getString(
                            TurbineConstants.SCREEN_INVALID_STATE));
                }
                data.getUser().setTemp("prev_parameters", data.getParameters());
                data.setAction("");
            }
        }

        // We do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if (data.getTemplateInfo().getScreenTemplate() != null)
        {
            data.setScreen(null);
        }
    }

    /**
     * doPerform is virtually identical to DefaultSessionValidator
     * except that it calls template methods instead of bare screen
     * methods. For example, it uses <code>setScreenTemplate</code> to
     * load the tr.props TEMPLATE_LOGIN instead of the default's
     * setScreen to TurbineConstants.SCREEN_LOGIN.
     *
     * @see DefaultSessionValidator
     * @param pipelineData Turbine information.
     * @throws TurbineException The anonymous user could not be obtained
     *         from the security service
     */
    public void doPerform(PipelineData pipelineData)
    throws TurbineException
    {
        RunData data = (RunData) getRunData(pipelineData);
        doPerform(data);
    }


}
