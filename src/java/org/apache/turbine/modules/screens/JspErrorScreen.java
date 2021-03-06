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

import org.apache.turbine.TurbineConstants;
import org.apache.turbine.annotation.TurbineConfiguration;
import org.apache.turbine.pipeline.PipelineData;

/**
 * Directs errors at the Jsp error template defined in template.error.
 *
 * @author <a href="mailto:ingo@raleigh.ibm.com">Ingo Schuster</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @author <a href="mailto:peter@courcoux.biz">Peter Courcoux</a>
 * @version $Id$
 */
public class JspErrorScreen
    extends BaseJspScreen
{
    @TurbineConfiguration( TurbineConstants.TEMPLATE_ERROR_KEY )
    private String templateError = TurbineConstants.TEMPLATE_ERROR_JSP;

    /**
     * @param pipelineData Turbine information.
     * @throws Exception a generic exception.
     */
    @Override
    protected void doBuildTemplate(PipelineData pipelineData)
        throws Exception
    {
        setTemplate(pipelineData, templateError);
    }
}
