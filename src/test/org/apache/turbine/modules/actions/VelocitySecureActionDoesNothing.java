package org.apache.turbine.modules.actions;

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


import static org.junit.Assert.assertNotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
/**
 * This action is used to test the secure action methods.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class VelocitySecureActionDoesNothing extends VelocitySecureAction
{
    private static Log log = LogFactory.getLog(VelocitySecureActionDoesNothing.class);
    public static int numberOfCalls;
    public static int pipelineDataCalls;
    public static int isAuthorizedCalls;

    /**
     *  Default action is throw an exception.
     *
     * @param  pipelineData           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    @Override
    public void doPerform(PipelineData pipelineData, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
		VelocitySecureActionDoesNothing.numberOfCalls++;
        RunData rd = (RunData)pipelineData;
		assertNotNull("PipelineData object was Null.", rd);
		VelocitySecureActionDoesNothing.pipelineDataCalls++;
    }

    /**
     * @see org.apache.turbine.modules.actions.VelocitySecureAction#isAuthorized(org.apache.turbine.pipeline.PipelineData)
     */
    @Override
    protected boolean isAuthorized(PipelineData pipelineData) throws Exception
    {
        log.debug("Calling isAuthorized(PipelineData)");
        VelocitySecureActionDoesNothing.isAuthorizedCalls++;
        return true;
    }
}
