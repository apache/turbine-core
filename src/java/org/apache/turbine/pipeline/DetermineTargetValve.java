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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

/**
 * This valve is responsible for setting the 'target' property of the RunData.
 * If it is not already set it attempts to get the target from the request
 * parameter 'template'. If the parameter is not set, we use the homepage
 * specified by the configuration property Turbine.TEMPLATE_HOMEPAGE.
 *
 * FIXME: The request parameter which determines the template should be
 *        configurable.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class DetermineTargetValve implements Valve
{
    private static final Logger log
        = LogManager.getLogger(DetermineTargetValve.class);

    /**
     * @see org.apache.turbine.pipeline.Valve#invoke(PipelineData, ValveContext)
     */
    @Override
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        RunData runData = pipelineData.getRunData();
        if (!runData.hasScreen())
        {
            String target = runData.getParameters().getString(URIConstants.CGI_SCREEN_PARAM);

            if (target != null)
            {
                runData.setScreen(target);
                log.debug("Set screen target from request parameter");
            }
            else
            {
				log.debug("No target screen");
            }
        }

        log.debug("Screen Target is now: {}", runData::getScreen);

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
