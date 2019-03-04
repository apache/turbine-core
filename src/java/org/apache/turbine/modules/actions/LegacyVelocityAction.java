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

import org.apache.fulcrum.parser.ParameterParser;
import org.apache.turbine.Turbine;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * This class provides a methods for Turbine2 Velocity Actions to use. Since
 * this class is abstract, it should only be extended and not used directly.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @deprecated Use VelocityAction directly
 */
@Deprecated
public abstract class LegacyVelocityAction extends VelocityAction
{
    /**
     * You SHOULD override this method and implement it in your action.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @throws Exception a generic exception.
     */
    public abstract void doPerform(RunData data, Context context)
            throws Exception;

    /**
     * Adapter method for legacy signature
     *
     * @param pipelineData Turbine information.
     * @param context Context for web pages.
     * @throws Exception a generic exception.
     */
    @Override
    public void doPerform(PipelineData pipelineData, Context context)
            throws Exception
    {
        doPerform(pipelineData.getRunData(), context);
    }

    /**
     * This overrides the default Action.doPerform() to execute the
     * doEvent() method.  If that fails, then it will execute the
     * doPerform() method instead.
     *
     * @param pipelineData A Turbine RunData object.
     * @throws Exception a generic exception.
     */
    @Override
    public void doPerform(PipelineData pipelineData)
            throws Exception
    {
        if (!initialized)
        {
            initialize();
        }

        RunData data = pipelineData.getRunData();
        ParameterParser pp = pipelineData.get(Turbine.class, ParameterParser.class);
        Context context = velocity.getContext(pipelineData);
        executeEvents(pp, new Class<?>[]{ RunData.class, Context.class },
                new Object[]{ data, context });
    }
}
