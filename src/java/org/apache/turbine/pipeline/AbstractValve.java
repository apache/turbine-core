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

/**
 * Valve that can be used as the basis of Valve implementations.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class AbstractValve
    implements Valve
{
    /**
     * Initialize this valve for use in a pipeline.
     *
     * @throws Exception
     */
    public void initialize()
        throws Exception
    {
        // empty
    }

    /**
     * @see org.apache.turbine.Valve#invoke(PipelineData, ValveContext)
     */
    public abstract void invoke(PipelineData data, ValveContext context)
        throws IOException, TurbineException;


    /**
     * utility for getting RunData out of the pielineData object.
     * @param pipelineData
     * @return
     */
    public final RunData getRunData(PipelineData pipelineData)
    {
        if(!(pipelineData instanceof RunData)){
            throw new RuntimeException("Can't cast pipelineData to rundata");
        }
        return (RunData)pipelineData;
    }

}
