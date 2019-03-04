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
import java.io.Writer;

import org.apache.turbine.util.TurbineException;

/**
 * <code>Valve</code> implementation use for testing purposes.
 *
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id$
 */
class SimpleValve implements Valve
{
    private String value;

    private Writer writer;

    /**
     * @param value The value for the associated <code>Writer</code> to write.
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    protected void setWriter(Writer writer)
    {
        this.writer = writer;
    }

    /**
     * @see org.apache.turbine.pipeline.Valve#invoke(PipelineData, ValveContext)
     */
    @Override
    public void invoke(PipelineData data, ValveContext context)
        throws IOException, TurbineException
    {
        // Perform our actions
        writer.write(value);

        // Pass control to the next Valve in the Pipeline
        context.invokeNext(data);
    }
}
