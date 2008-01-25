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
import org.apache.turbine.modules.actions.AccessController;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Implements the action portion of the "Turbine classic" processing
 * pipeline (from the Turbine 2.x series).
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DefaultACLCreationValve
    extends AbstractValve
    implements TurbineConstants
{
    protected AccessController accessController = null;

    /**
     * Here we can setup objects that are thread safe and can be
     * reused. We setup the session validator and the access
     * controller.
     */
    public DefaultACLCreationValve()
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
            // Put the Access Control List into the RunData object, so
            // it is easily available to modules.  It is also placed
            // into the session for serialization.  Modules can null
            // out the ACL to force it to be rebuilt based on more
            // information.
            ActionLoader.getInstance().exec(
                    pipelineData, Turbine.getConfiguration().getString(ACTION_ACCESS_CONTROLLER_KEY,
                            ACTION_ACCESS_CONTROLLER_DEFAULT));
        }
        catch (Exception e)
        {
            throw new TurbineException(e);
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
