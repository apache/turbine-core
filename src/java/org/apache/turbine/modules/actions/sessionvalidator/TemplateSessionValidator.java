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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.turbine.Turbine;
import org.apache.turbine.om.security.User;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * SessionValidator for use with the Template Service, the
 * TemplateSessionValidator is virtually identical to the
 * {@link TemplateSecureSessionValidator} except that it does not transfer to the
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
    private static Logger log = LogManager.getLogger(TemplateSessionValidator.class);

    /**
     * Execute the action.
     *
     * @param pipelineData Turbine information.
     * @throws Exception The anonymous user could not be obtained
     *         from the security service
     */
    @Override
    public void doPerform(PipelineData pipelineData) throws Exception
    {
        RunData data = getRunData(pipelineData);
        // Pull user from session.
        data.populate();

        // The user may have not logged in, so create a "guest/anonymous" user.
        if (data.getUser() == null)
        {
            log.debug("Creating an anonymous user object!");
            User anonymousUser = security.getAnonymousUser();
            data.setUser(anonymousUser);
            data.save();
        }

        // make sure we have some way to return a response
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

        // we do not want to allow both a screen and template parameter.
        // The template parameter is dominant.
        if (data.getTemplateInfo().getScreenTemplate() != null)
        {
            data.setScreen(null);
        }

        // Comply with Turbine 4.0 standards
        pipelineData.get(Turbine.class).put(User.class, data.getUser());
    }
}
