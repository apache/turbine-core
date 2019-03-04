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

import org.apache.turbine.util.TurbineException;

/**
 * <p>A <b>ValveContext</b> is the mechanism by which a Valve can trigger the
 * execution of the next Valve in a Pipeline, without having to know anything
 * about the internal implementation mechanisms.  An instance of a class
 * implementing this interface is passed as a parameter to the
 * <code>Valve.invoke()</code> method of each executed Valve.</p>
 *
 * <p><strong>IMPLEMENTATION NOTE</strong>: It is up to the implementation of
 * ValveContext to ensure that simultaneous requests being processed (by
 * separate threads) through the same Pipeline do not interfere with each
 * other's flow of control.</p>
 *
 * @author Craig R. McClanahan
 * @author Gunnar Rjnning
 * @author Peter Donald
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Revision$ $Date$
 */
public interface ValveContext
{
    /**
     * <p>Cause the <code>invoke()</code> method of the next Valve
     * that is part of the Pipeline currently being processed (if any)
     * to be executed, passing on the specified request and response
     * objects plus this <code>ValveContext</code> instance.
     * Exceptions thrown by a subsequently executed Valve will be
     * passed on to our caller.</p>
     *
     * <p>If there are no more Valves to be executed, execution of
     * this method will result in a no op.</p>
     *
     * @param pipelineData The run-time information, including the servlet
     * request and response we are processing.
     *
     * @throws IOException Thrown by a subsequent Valve.
     * @throws TurbineException Thrown by a subsequent Valve.
     * @throws TurbineException No further Valves configured in the
     * Pipeline currently being processed.
     */
    void invokeNext(PipelineData pipelineData)
        throws IOException, TurbineException;
}
