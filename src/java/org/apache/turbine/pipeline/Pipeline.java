package org.apache.turbine.pipeline;


/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import java.io.IOException;

import org.apache.turbine.util.TurbineException;

/**
 * The idea of a pipeline is being taken from Catalina
 * in its entirety :-)
 *
 * I would like to take the idea further and implement
 * Valves instead of hardcoding particular methods
 * in a pipeline.
 *
 * It would be more flexible to specify Valves for
 * a pipeline in an XML file (we can also rip off the
 * digester rules from T4) and have invoke() as part
 * of the interface.
 *
 * So a set of Valves would be added to the pipeline
 * and the pipeline would 'invoke' each valve. In the
 * case Turbine each Valve would produce some output
 * to be sent out the pipe. I think with another days
 * work this can be fully working. The first pipeline
 * to be fully implemented will the ClassicPipeline
 * will emulate the Turbine 2.1 way of doing things.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 */
public interface Pipeline
{
    /**
     * Initializes this instance.  Called once by the Turbine servlet.
     */
    public void initialize()
        throws Exception;

    /**
     * <p>Add a new Valve to the end of the pipeline.</p>
     *
     * @param valve Valve to be added.
     *
     * @exception IllegalStateException If the pipeline has not been
     * initialized.
     */
    public void addValve(Valve valve);

    /**
     * Return the set of all Valves in the pipeline.  If there are no
     * such Valves, a zero-length array is returned.
     *
     * @return An array of valves.
     */
    public Valve[] getValves();

    /**
     * <p>Cause the specified request and response to be processed by
     * the sequence of Valves associated with this pipeline, until one
     * of these Valves decides to end the processing.</p>
     *
     * <p>The implementation must ensure that multiple simultaneous
     * requests (on different threads) can be processed through the
     * same Pipeline without interfering with each other's control
     * flow.</p>
     *
     * @param data The run-time information, including the servlet
     * request and response we are processing.
     *
     * @exception IOException an input/output error occurred.
     */
    public void invoke(PipelineData data)
        throws TurbineException, IOException;

    /**
     * Remove the specified Valve from the pipeline, if it is found;
     * otherwise, do nothing.
     *
     * @param valve Valve to be removed.
     */
    public void removeValve(Valve valve);
}
