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
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.security.TurbineSecurity;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * SessionValidator for use with the Template Service, the
 * TemplateSessionValidator is virtually identical to the
 * TemplateSecureValidator except that it does not transfer to the
 * login page when it detects a null user (or a user not logged in).
 *
 * <p>The Template Service requires a different Session Validator
 * because of the way it handles screens.
 *
 * <p>Note that you will need to set the template.login property to the
 * login template.
 *
 * @see TemplateSecureSessionValidator
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class TemplateSessionValidator
    extends SessionValidator
{
    /** Logging */
    private static Log log = LogFactory.getLog(TemplateSessionValidator.class);

    /**
     * Execute the action.
     *
     * @deprecated Use PipelineData version instead.
     * @param data Turbine information.
     * @exception TurbineException The anonymous user could not be obtained
     *         from the security service
     */
    @Deprecated
    @Override
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

        // make sure we have some way to return a response
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
        // the session_access_counter can be placed as a hidden field in
        // forms.  This can be used to prevent a user from using the
        // browsers back button and submitting stale data.
        else if (data.getParameters().containsKey("_session_access_counter")
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

        // we do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if (data.getTemplateInfo().getScreenTemplate() != null)
        {
            data.setScreen(null);
        }
    }

    /**
     * Execute the action.
     *
     * @param pipelineData Turbine information.
     * @exception TurbineException The anonymous user could not be obtained
     *         from the security service
     */
    @Override
    public void doPerform(PipelineData pipelineData)
    throws TurbineException
    {
        RunData data = getRunData(pipelineData);
        doPerform(data);

        // Comply with Turbine 4.0 standards
        pipelineData.get(Turbine.class).put(User.class, data.getUser());
    }
}
