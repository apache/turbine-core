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
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.util.RunData;
import org.apache.turbine.util.TurbineException;

/**
 * Set defaultEncoding of the request
 *
 * This valve must be situated in the pipeline before any access to the
 * {@link org.apache.fulcrum.parser.ParameterParser} to take effect.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class DefaultSetEncodingValve
    extends AbstractValve
{
    private static final Log log = LogFactory.getLog(DefaultSetEncodingValve.class);

    @TurbineConfiguration( TurbineConstants.PARAMETER_ENCODING_KEY )
    protected String defaultEncoding = TurbineConstants.PARAMETER_ENCODING_DEFAULT;

    /**
     * @see org.apache.turbine.pipeline.Valve#invoke(PipelineData, ValveContext)
     */
    @Override
    public void invoke(PipelineData pipelineData, ValveContext context)
        throws IOException, TurbineException
    {
        HttpServletRequest req = pipelineData.get(Turbine.class, HttpServletRequest.class);

        // If the servlet container gives us no clear indication about the
        // encoding of the contents, set it to our default value.
        String requestEncoding = req.getCharacterEncoding();

        if (requestEncoding == null)
        {
            requestEncoding = defaultEncoding;

            if (log.isDebugEnabled())
            {
                log.debug("Changing Input Encoding to " + requestEncoding);
            }

            try
            {
                req.setCharacterEncoding(requestEncoding);
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new TurbineException("Could not change request encoding to " + requestEncoding, uee);
            }
        }

        // Copy encoding charset to RunData to set a reasonable default for the response
        RunData data = getRunData(pipelineData);
        data.setCharSet(requestEncoding);

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(pipelineData);
    }
}
