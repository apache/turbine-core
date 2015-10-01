package org.apache.turbine.modules.pages;


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


import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.velocity.VelocityService;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

/**
 * Extends TemplatePage to set the template Context.
 *
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class VelocityPage
    extends TemplatePage
{
    /** Injected service instance */
    @TurbineService
    private VelocityService velocity;

    /**
     * Stuffs the Context into the PipelineData so that it is available to
     * the Action module and the Screen module via getContext().
     *
     * @param pipelineData Turbine information.
     * @exception Exception, a generic exception.
     */
    @Override
    protected void doBuildBeforeAction(PipelineData pipelineData)
        throws Exception
    {
        RunData data = getRunData(pipelineData);
        Context context = velocity.getContext(pipelineData);
        data.getTemplateInfo()
            .setTemplateContext(VelocityService.CONTEXT, context);
    }

    /**
     * Allows the VelocityService to perform post-request actions.
     * (releases the (non-global) tools in the context for reuse later)
     */
    @Override
    protected void doPostBuild(PipelineData pipelineData)
        throws Exception
    {
        Context context = velocity.getContext(pipelineData);
        velocity.requestFinished(context);
    }
}
