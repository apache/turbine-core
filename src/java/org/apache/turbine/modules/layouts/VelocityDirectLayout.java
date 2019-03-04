package org.apache.turbine.modules.layouts;

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

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.util.template.TemplateNavigation;
import org.apache.turbine.util.template.TemplateScreen;
import org.apache.velocity.context.Context;

/**
 * This Layout module allows Velocity templates
 * to be used as layouts. It will stream directly the output of
 * the layout and navigation templates to the output writer without
 * using a screen. Use this if you have a large page to output
 * and won't buffer it in the memory.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:mbryson@mont.mindspring.com">Dave Bryson</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class VelocityDirectLayout extends VelocityLayout
{
    /**
     * @see org.apache.turbine.modules.layouts.VelocityLayout#populateContext(org.apache.turbine.pipeline.PipelineData, org.apache.velocity.context.Context)
     */
    @Override
    protected void populateContext(PipelineData pipelineData, Context context) throws Exception
    {
        // variable for the screen in the layout template
        context.put(TurbineConstants.SCREEN_PLACEHOLDER,
                    new TemplateScreen(pipelineData));

        // variable to reference the navigation screen in the layout template
        context.put(TurbineConstants.NAVIGATION_PLACEHOLDER,
                    new TemplateNavigation(pipelineData));
    }
}
