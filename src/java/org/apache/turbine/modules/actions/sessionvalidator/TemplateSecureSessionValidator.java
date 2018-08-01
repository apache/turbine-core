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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * SessionValidator that requires login for use with Template Services
 * like Velocity or WebMacro.
 *
 * <br>
 *
 * Templating services requires a different Session Validator
 * because of the way it handles screens.  If you use the WebMacro or
 * Velocity Service with the {@link DefaultSessionValidator}, users will be able to
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


    @TurbineConfiguration( TurbineConstants.LOGIN_MESSAGE )
    private String loginMessage;

    @TurbineConfiguration( TurbineConstants.TEMPLATE_LOGIN )
    private String templateLogin;


    /**
     * doPerform is virtually identical to DefaultSessionValidator
     * except that it calls template methods instead of bare screen
     * methods. For example, it uses <code>setScreenTemplate</code> to
     * load the tr.props TEMPLATE_LOGIN instead of the default's
     * setScreen to TurbineConstants.SCREEN_LOGIN.
     *
     * @see DefaultSessionValidator
     * @param pipelineData Turbine information.
     * @throws Exception The anonymous user could not be obtained
     *         from the security service
     */
    @Override
    public void doPerform(PipelineData pipelineData)
    throws Exception
    {
        RunData data = getRunData(pipelineData);
        // Pull user from session.
        data.populate();

        // The user may have not logged in, so create a "guest/anonymous" user.
        if (data.getUser() == null)
        {
            log.debug("Fixing up empty User Object!");
            User anonymousUser = security.getAnonymousUser();
            data.setUser(anonymousUser);
            data.save();
        }

        // This is the secure session validator, so user must be logged in.
        if (!data.getUser().hasLoggedIn())
        {
            log.debug("User is not logged in!");

            // only set the message if nothing else has already set it
            // (e.g. the LogoutUser action).
            if (StringUtils.isEmpty(data.getMessage()))
            {
                data.setMessage(loginMessage);
            }

            // Set the screen template to the login page.
            log.debug("Sending User to the Login Screen ("
                    + templateLogin + ")");
            data.getTemplateInfo().setScreenTemplate(templateLogin);

            // We're not doing any actions buddy! (except action.login which
            // will have been performed already)
            data.setAction(null);
        }

        log.debug("Login Check finished!");

        // Make sure we have some way to return a response.
        if (!data.hasScreen() && StringUtils.isEmpty(
                data.getTemplateInfo().getScreenTemplate()))
        {
            if (StringUtils.isNotEmpty(templateHomepage))
            {
                data.getTemplateInfo().setScreenTemplate(templateHomepage);
            }
            else
            {
                data.setScreen(screenHomepage);
            }
        } else {
            handleFormCounterToken(data, false);
        }

        // We do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if (data.getTemplateInfo().getScreenTemplate() != null)
        {
            data.setScreen(null);
        }

        // Comply with Turbine 4.0 standards
        pipelineData.get(Turbine.class).put(User.class, data.getUser());
    }
}
