package org.apache.turbine.modules.screens;

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

import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Support Turbine 2 screens
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @deprecated Use VelocityScreen directly
 */
@Deprecated
public class LegacyVelocityScreen
    extends VelocityScreen
{
    /**
     * Velocity Screens extending this class should override this
     * method to perform any particular business logic and add
     * information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @throws Exception a generic exception.
     */
    protected void doBuildTemplate(RunData data, Context context)
            throws Exception
    {
        // empty
    }

    /**
     * Adapter method
     *
     * @param pipelineData Turbine information.
     * @param context Context for web pages.
     * @throws Exception a generic exception.
     */
    @Override
    protected void doBuildTemplate(PipelineData pipelineData,
                                   Context context)
            throws Exception
    {
        doBuildTemplate(pipelineData.getRunData(), context);
    }
}
