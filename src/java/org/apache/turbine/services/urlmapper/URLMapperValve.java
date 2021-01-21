package org.apache.turbine.services.urlmapper;


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

import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.pipeline.Valve;
import org.apache.turbine.pipeline.ValveContext;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * This valve is responsible for parsing parameters out of
 * simplified URLs.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class URLMapperValve
    implements Valve
{
    /** Injected service instance */
    @TurbineService
    private URLMapperService urlMapperService;

    /**
     * @see org.apache.turbine.pipeline.Valve#invoke(PipelineData, ValveContext)
     */
    @Override
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        RunData data = pipelineData.getRunData();
        String uri = data.getRequest().getRequestURI();

        urlMapperService.mapFromURL(uri, data.getParameters());

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
