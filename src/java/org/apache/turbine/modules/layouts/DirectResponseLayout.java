package org.apache.turbine.modules.layouts;


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


import org.apache.turbine.modules.Layout;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * This layout allows an action to manipulate the ServletOutputStream directly.
 * It requires that data.declareDirectResponse() has been called to indicate
 * that the OutputStream is being handled elsewhere.
 *
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class DirectResponseLayout implements Layout
{
    /**
     * Ensures that a direct response has been declared.
     *
     * @param pipelineData Turbine information.
     * @throws TurbineException if a direct response has not been declared.
     */
    @Override
    public void doBuild(PipelineData pipelineData)
            throws Exception
    {
        RunData data = pipelineData.getRunData();
        if (!data.isOutSet())
        {
            throw new TurbineException(
                "data.declareDirectResponse() has not been called");
        }
    }
}
