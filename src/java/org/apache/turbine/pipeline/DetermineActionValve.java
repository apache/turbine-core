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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;
import org.apache.turbine.util.uri.URIConstants;

/**
 * This valve is responsible for setting the 'action' property of RunData based
 * on request parameter. There is no default action, since a null action is
 * perfectly valid.
 *
 * @author <a href="mailto:james@jamestaylor.org">James Taylor</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public class DetermineActionValve
    extends AbstractValve
{
    private static final Log log
        = LogFactory.getLog( DetermineActionValve.class );

    /**
     * @see org.apache.turbine.Valve#invoke(RunData, ValveContext)
     */
    public void invoke( PipelineData pipelineData, ValveContext context )
        throws IOException, TurbineException
    {
        RunData data = (RunData)getRunData(pipelineData);
        if ( ! data.hasAction() )
        {
            String action =
                data.getParameters().getString( URIConstants.CGI_ACTION_PARAM );

            if ( action != null )
            {
                data.setAction( action );

                log.debug( "Set action from request parameter" );
            }
            else
            {
                log.debug( "No action" );
            }
        }

        if ( log.isDebugEnabled() )
        {
            log.debug( "Action is now: " + data.getAction() );
        }

        // Pass control to the next Valve in the Pipeline
        context.invokeNext( pipelineData );
    }
}
