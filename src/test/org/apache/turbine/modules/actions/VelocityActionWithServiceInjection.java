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

import org.apache.fulcrum.factory.FactoryService;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.velocity.context.Context;
/**
 * This action is used in testing the injection of services.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class VelocityActionWithServiceInjection extends VelocityAction
{
    // Test for explicit service name
    @TurbineService( RunDataService.SERVICE_NAME )
    private RunDataService runDataService;

    // Test for implicit SERVICE_NAME
    @TurbineService
    private RunDataService runDataService2;

    // Test for implicit ROLE
    @TurbineService
    private FactoryService factory;

    /**
     *  Default action is nothing.
     *
     * @param  pipelineData           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    @Override
    public void doPerform(PipelineData pipelineData, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
		assertNotNull("runDataService object was Null.", runDataService);
        log.debug("Injected service is " + runDataService.getName());
        assertNotNull("runDataService2 object was Null.", runDataService2);
        log.debug("Injected service is " + runDataService2.getName());
        assertNotNull("factory object was Null.", factory);
        log.debug("Injected service is " + factory.getClass());
    }
}
