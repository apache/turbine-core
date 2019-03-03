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

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the Redirect Requested portion of the "Turbine classic"
 * processing pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:epugh@opensourceConnections.com">Eric Pugh</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DetermineRedirectRequestedValve
    extends AbstractValve
{
    private static final Logger log = LogManager.getLogger(DetermineRedirectRequestedValve.class);

    /**
     * Creates a new instance.
     */
    public DetermineRedirectRequestedValve()
    {
        // empty constructor
    }

    /**
     * @see org.apache.turbine.pipeline.Valve#invoke(PipelineData, ValveContext)
     */
    @Override
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        redirectRequested(pipelineData);

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }

    /**
     * Perform clean up after processing the request.
     *
     * @param pipelineData The run-time data.
     *
     * @throws IOException if sending the redirect fails
     */
    protected void redirectRequested(PipelineData pipelineData)
        throws IOException
    {
        RunData data = getRunData(pipelineData);
        // handle a redirect request
        boolean requestRedirected = StringUtils.isNotEmpty(data.getRedirectURI());
        if (requestRedirected)
        {
            if (data.getResponse().isCommitted())
            {
                log.warn("redirect requested, response already committed: {}", data.getRedirectURI());
            }
            else
            {
                data.getResponse().sendRedirect(data.getRedirectURI());
            }
        }
    }
}
