package org.apache.turbine.modules.screens;

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

import org.apache.turbine.Turbine;
import org.apache.turbine.TurbineConstants;

import org.apache.turbine.util.RunData;

/**
 * Directs errors at the Jsp error template defined in template.error.
 *
 * @version $Id$
 */
public class JspErrorScreen
    extends BaseJspScreen
{
    /**
     * @param data Turbine information.
     * @exception Exception a generic exception.
     */
    protected void doBuildTemplate(RunData data)
        throws Exception
    {
        String errorTemplate = Turbine.getConfiguration()
            .getString(TurbineConstants.TEMPLATE_ERROR_KEY,
                       TurbineConstants.TEMPLATE_ERROR_JSP);

        setTemplate(data, errorTemplate);
    }
}
