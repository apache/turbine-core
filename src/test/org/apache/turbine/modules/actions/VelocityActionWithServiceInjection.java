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


import junit.framework.Assert;

import org.apache.turbine.annotation.InjectService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.rundata.RunDataService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;
/**
 * This action is used in testing the injection of services.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
public class VelocityActionWithServiceInjection extends VelocityAction
{
    @InjectService( RunDataService.SERVICE_NAME )
    private RunDataService runDataService;

    /**
     *  Default action is nothing.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(RunData data, Context context) throws Exception
    {
        log.debug("Calling doPerform");
    }

    /**
     *  Default action is nothing.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @exception  Exception  Thrown on error
     */
    public void doPerform(PipelineData pipelineData, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
		Assert.assertNotNull("runDataService object was Null.", runDataService);
        log.debug("Injected service is " + runDataService.getName());
    }
}
