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

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.util.RunData;

import org.apache.velocity.context.Context;

/**
 * VelocityErrorScreen screen - directs errors at the velocity
 * error template defined in template.error.
 *
 * @author <a href="mailto:gonzalo.diethelm@sonda.com">Gonzalo Diethelm</a>
 * @author <a href="mailto:hps@intermeta.de">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class VelocityErrorScreen
    extends VelocityScreen
{
    /**
     * Implement this to add information to the context.
     *
     * @param data Turbine information.
     * @param context Context for web pages.
     * @exception Exception a generic exception.
     */
    protected void doBuildTemplate(RunData data, Context context)
            throws Exception
    {
        context.put (TurbineConstants.PROCESSING_EXCEPTION_PLACEHOLDER,
                     data.getStackTraceException().toString());
        context.put (TurbineConstants.STACK_TRACE_PLACEHOLDER,
                     data.getStackTrace());

        String errorTemplate = Turbine.getConfiguration()
            .getString(TurbineConstants.TEMPLATE_ERROR_KEY,
                       TurbineConstants.TEMPLATE_ERROR_VM);

        setTemplate(data, errorTemplate);
    }
}
