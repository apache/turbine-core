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

import org.apache.commons.configuration.Configuration;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the action portion of the "Turbine classic" processing
 * pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultSessionTimeoutValve
    extends AbstractValve
    implements TurbineConstants
{
    protected int timeout;

    /**
     * Here we can setup objects that are thread safe and can be
     * reused, so we get the timeout from the configuration..
     */
    public DefaultSessionTimeoutValve()
        throws Exception
    {
        Configuration cfg = Turbine.getConfiguration();

        // Get the session timeout.

    	timeout = cfg.getInt(SESSION_TIMEOUT_KEY,
        	        SESSION_TIMEOUT_DEFAULT);

    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        RunData runData = (RunData)getRunData(pipelineData);
        // If the session is new take this opportunity to
        // set the session timeout if specified in TR.properties
        if (runData.getSession().isNew() && timeout != SESSION_TIMEOUT_DEFAULT)
        {
            runData.getSession().setMaxInactiveInterval(timeout);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
