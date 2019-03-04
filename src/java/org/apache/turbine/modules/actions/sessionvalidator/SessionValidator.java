package org.apache.turbine.modules.actions.sessionvalidator;

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.annotation.TurbineService;

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

import org.apache.turbine.modules.Action;
import org.apache.turbine.services.security.SecurityService;
import org.apache.turbine.util.RunData;

/**
 * The SessionValidator attempts to retrieve the User object from the
 * Servlet API session that is associated with the request.  If the
 * data cannot be retrieved, it is handled here.  If the user has not
 * been marked as being logged into the system, the user is rejected
 * and the screen is set to the screen.homepage value in
 * TurbineResources.properties.
 *
 * <p>
 * Other systems generally have a database table which stores this
 * information, but we take advantage of the Servlet API here to save
 * a hit to the database for each and every connection that a user
 * makes.
 * </p>
 *
 * <p>
 * This action is special in that it should only be executed by the
 * Turbine servlet.
 * </p>
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @version $Id$
 */
public abstract class SessionValidator implements Action
{

    @TurbineService
    protected SecurityService security;

    @TurbineConfiguration( TurbineConstants.TEMPLATE_HOMEPAGE )
    protected String templateHomepage;

    @TurbineConfiguration( TurbineConstants.SCREEN_HOMEPAGE )
    protected String screenHomepage;

    @TurbineConfiguration( TurbineConstants.TEMPLATE_INVALID_STATE )
    protected String templateInvalidState;

    @TurbineConfiguration( TurbineConstants.SCREEN_INVALID_STATE )
    protected String screenInvalidState;

    // the session_access_counter can be placed as a hidden field in
    // forms.  This can be used to prevent a user from using the
    // browsers back button and submitting stale data.
    /**
     *
     * @param data RunData object
     * @param screenOnly {@link DefaultSessionValidator}
     */
    protected void handleFormCounterToken( RunData data, boolean screenOnly )
    {
        if (data.getParameters().containsKey("_session_access_counter"))
        {
            if (screenOnly) {
                // See comments in screens.error.InvalidState.
                if (data.getParameters().getInt("_session_access_counter")
                        < (((Integer) data.getUser().getTemp(
                        "_session_access_counter")).intValue() - 1))
                {
                    data.getUser().setTemp("prev_screen", data.getScreen());
                    data.getUser().setTemp("prev_parameters", data.getParameters());
                    data.setScreen(screenInvalidState);
                    data.setAction("");
                }
            } else {
                if (!security.isAnonymousUser(data.getUser()))
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
                            data.getTemplateInfo().setScreenTemplate(templateInvalidState);
                        }
                        else
                        {
                            data.getUser().setTemp("prev_screen",
                                                   data.getScreen().replace('/', ','));
                            data.setScreen(screenInvalidState);
                        }
                        data.getUser().setTemp("prev_parameters", data.getParameters());
                        data.setAction("");
                    }
                }
            }
        }

    }
    // empty
}
