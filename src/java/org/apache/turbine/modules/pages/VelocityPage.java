package org.apache.turbine.modules.pages;

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.turbine.services.velocity.TurbineVelocity;
import org.apache.turbine.services.velocity.VelocityService;

import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * Extends TemplatePage to set the template Context.
 *
 * @version $Id$
 */
public class VelocityPage
    extends TemplatePage
{
    /**
     * Stuffs the Context into the RunData so that it is available to
     * the Action module and the Screen module via getContext().
     *
     * @param data Turbine information.
     * @exception Exception, a generic exception.
     */
    protected void doBuildBeforeAction(RunData data)
        throws Exception
    {
        Context context = TurbineVelocity.getContext(data);
        data.getTemplateInfo()
            .setTemplateContext(VelocityService.CONTEXT, context);
    }

    /**
     * Allows the VelocityService to peform post-request actions.
     * (releases the (non-global) tools in the context for reuse later)
     */
    protected void doPostBuild(RunData data)
        throws Exception
    {
        Context context = TurbineVelocity.getContext(data);
        TurbineVelocity.requestFinished(context);
    }
}
