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

import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This action is used for testing the Turbine 2 Legacy method signatures.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 */
@SuppressWarnings("deprecation")
public class Turbine2LegacyAction extends LegacyVelocityAction
{
    public static int numberOfCalls;

    /**
     *  Default action is throw an exception.
     *
     * @param  data           Current RunData information
     * @param  context        Context to populate
     * @throws  Exception  Thrown on error
     */
    @Override
    public void doPerform(RunData data, Context context) throws Exception
    {
        log.debug("Calling doPerform(RunData)");
		Turbine2LegacyAction.numberOfCalls++;
		assertNotNull("RunData object was Null.", data);
    }

}
