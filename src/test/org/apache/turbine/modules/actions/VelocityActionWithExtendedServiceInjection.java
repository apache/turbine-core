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
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.ServiceWithServiceInjection;
import org.apache.velocity.context.Context;

/**
 * Annnotating even an assembler as TurbineService on class level we could omit annotations for fields if class is a Turbine service.
 */
@TurbineService
public class VelocityActionWithExtendedServiceInjection extends VelocityAction
{
    private static Log log = LogFactory.getLog(VelocityActionWithExtendedServiceInjection.class);

    // Test for class level SERVICE_NAME in ServiceWithServiceInjection
    // Annotation could be omitted as the class is annotated
    // @TurbineService
    private ServiceWithServiceInjection serviceWithServiceInjection;
    

    /**
     *  Default action is nothing.
     *
     * @param  pipelineData           Current RunData information
     * @param  context        Context to populate
     * @throws  Exception  Thrown on error
     */
    @Override
    public void doPerform(PipelineData pipelineData, Context context) throws Exception
    {
        log.debug("Calling doPerform(PipelineData)");
        assertNotNull("field injected serviceWithServiceInjection object was Null.", serviceWithServiceInjection);
        serviceWithServiceInjection.callService();
        context.put("mykey","x");
    }
}
