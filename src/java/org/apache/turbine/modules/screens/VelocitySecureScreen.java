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
import org.apache.velocity.context.Context;

/**
 * VelocitySecureScreen
 *
 * Always performs a Security Check that you've defined before
 * executing the doBuildTemplate().  You should extend this class and
 * add the specific security check needed.  If you have a number of
 * screens that need to perform the same check, you could make a base
 * screen by extending this class and implementing the isAuthorized().
 * Then each screen that needs to perform the same check could extend
 * your base screen.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public abstract class VelocitySecureScreen
        extends VelocityScreen
{
    /**
     * Implement this to add information to the context.
     *
     * @param pipelineData Turbine information.
     * @param context Context for web pages.
     * @exception Exception, a generic exception.
     */
    @Override
    protected abstract void doBuildTemplate(PipelineData pipelineData,
                                            Context context)
            throws Exception;

    /**
     * This method overrides the method in VelocityScreen to
     * perform a security check first.
     *
     * @param pipelineData Turbine information.
     * @exception Exception, a generic exception.
     */
    @Override
    protected void doBuildTemplate(PipelineData pipelineData)
        throws Exception
    {
        if (isAuthorized(pipelineData))
        {
            doBuildTemplate(pipelineData, velocity.getContext(pipelineData));
        }
    }

    /**
     * Implement this method to perform the security check needed.
     * You should set the template in this method that you want the
     * user to be sent to if they're unauthorized.  See the
     * VelocitySecurityCheck utility.
     *
     * @param pipelineData Turbine information.
     * @return True if the user is authorized to access the screen.
     * @exception Exception, a generic exception.
     */
    protected abstract boolean isAuthorized(PipelineData pipelineData)
            throws Exception;
}
