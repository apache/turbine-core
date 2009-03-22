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

import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.security.AccessControlList;

/**
 * Implements the RunData target portion of the "Turbine classic"
 * processing pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:jon@latchkey.com">Jon S. Stevens</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:mikeh@apache.org">Mike Haberman</a>
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author <a href="mailto:mpoeschl@marmot.at">Martin Poeschl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class CleanUpValve
    extends AbstractValve
{
    /**
     * Creates a new instance.
     */
    public CleanUpValve()
    {
        // empty constructor
    }

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        try
        {
            cleanUp(pipelineData);
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }

    /**
     * Perform clean up after processing the request.
     *
     * @param data The run-time data.
     */
    protected void cleanUp(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        // If a module has set data.acl = null, remove acl from
        // the session.
        if (data.getACL() == null)
        {
            try
            {
                data.getSession().removeAttribute
                    (AccessControlList.SESSION_KEY);
            }
            catch (IllegalStateException invalidatedSession)
            {
                // Web was used to shut us down. Trying to clean up
                // our stuff, but it's already been done for us.
            }
        }
    }
}
