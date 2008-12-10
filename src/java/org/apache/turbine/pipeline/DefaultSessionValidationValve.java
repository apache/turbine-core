package org.apache.turbine.pipeline;


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


import java.io.IOException;

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.modules.ActionLoader;
import org.apache.turbine.modules.actions.sessionvalidator.SessionValidator;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the action portion of the "Turbine classic" processing
 * pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>\
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultSessionValidationValve
    extends AbstractValve
{
    protected SessionValidator sessionValidator = null;


    public DefaultSessionValidationValve()
        throws Exception
    {

    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        try
        {
            // This is where the validation of the Session information
            // is performed if the user has not logged in yet, then
            // the screen is set to be Login. This also handles the
            // case of not having a screen defined by also setting the
            // screen to Login. If you want people to go to another
            // screen other than Login, you need to change that within
            // TurbineResources.properties...screen.homepage; or, you
            // can specify your own SessionValidator action.
            ActionLoader.getInstance().exec(pipelineData,
                    Turbine.getConfiguration().getString(
                            TurbineConstants.ACTION_SESSION_VALIDATOR_KEY,
                            TurbineConstants.ACTION_SESSION_VALIDATOR_DEFAULT));
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
