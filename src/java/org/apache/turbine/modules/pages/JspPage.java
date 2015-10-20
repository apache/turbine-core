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


import javax.servlet.http.HttpServletResponse;

import org.apache.turbine.Turbine;
import org.apache.turbine.annotation.TurbineService;
import org.apache.turbine.pipeline.PipelineData;
import org.apache.turbine.services.jsp.JspService;

/**
 * Extends TemplatePage to add some convenience objects to the request.
 *
 * @author <a href="mailto:john.mcnally@clearink.com">John D. McNally</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Revision$
 */
public class JspPage
    extends TemplatePage
{
    /** Injected service instance */
    @TurbineService
    private JspService jspService;

    /**
     * Stuffs some useful objects into the request so that
     * it is available to the Action module and the Screen module
     */
    @Override
    protected void doBuildBeforeAction(PipelineData pipelineData)
        throws Exception
    {
        jspService.addDefaultObjects(pipelineData);

        try
        {
            HttpServletResponse response = pipelineData.get(Turbine.class, HttpServletResponse.class);
            //We try to set the buffer size from defaults
            response.setBufferSize(jspService.getDefaultBufferSize());
        }
        catch (IllegalStateException ise)
        {
            // If the response was already committed, we die silently
            // No logger here?
        }
    }

}
