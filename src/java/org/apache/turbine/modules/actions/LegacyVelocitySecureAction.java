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


import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.RunData;

/**
 * VelocitySecure action.
 *
 * Always performs a Security Check that you've defined before
 * executing the doPerform().  You should extend this class and
 * add the specific security check needed.  If you have a number of
 * actions that need to perform the same check, you could make a base
 * action by extending this class and implementing the isAuthorized().
 * Then each action that needs to perform the same check could extend
 * your base action.
 *
 * @author <a href="mailto:tv@apache.org">Thomas Vandahl</a>
 * @deprecated Use VelocitySecureAction directly
 */
@Deprecated
public abstract class LegacyVelocitySecureAction extends LegacyVelocityAction
{
    /**
     * This method overrides the method in VelocityAction to
     * perform a security check first.
     *
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    @Override
    public void perform(PipelineData pipelineData) throws Exception
    {
        if (isAuthorized(getRunData(pipelineData)))
        {
            super.perform(pipelineData);
        }
    }

    /**
     * Implement this method to perform the security check needed.
     * You should set the template in this method that you want the
     * user to be sent to if they're unauthorized.
     *
     * @param data Turbine information.
     * @return True if the user is authorized to access the screen.
     * @throws Exception a generic exception.
     */
    protected abstract boolean isAuthorized(RunData data) throws Exception;
}
